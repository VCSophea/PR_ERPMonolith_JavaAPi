const { execSync } = require("child_process");
const { existsSync, rmSync, mkdirSync, copyFileSync, readdirSync, readFileSync, writeFileSync } = require("fs");
const path = require("path");

const root = path.resolve(__dirname, "..");
const run = (cmd) => execSync(cmd, { stdio: "inherit", cwd: root });
const die = (msg) => {
  console.error(`❌ ${msg}`);
  process.exit(1);
};

// * Validate Environment arg
const env = process.argv[2];
if (!["qa", "prod", "docker-local", "docker-prod"].includes(env)) die("Usage: node scripts/build.js <qa|prod|docker-local|docker-prod>");

console.log(`🚀 Starting Build for ${env}...`);

// * 1. Read Configuration from .env and package.json
let config = {};
try {
  // Read package.json
  const pkg = JSON.parse(readFileSync(path.join(root, "package.json"), "utf8"));
  config.artifactId = pkg.name;
  config.description = pkg.description;
  config.version = pkg.version; // default

  // Read .env
  if (existsSync(path.join(root, ".env"))) {
    const envText = readFileSync(path.join(root, ".env"), "utf8");
    const envVersion = envText.match(/^APP_VERSION=(.*)/m)?.[1]?.trim();
    const envProjectName = envText.match(/^PROJECT_NAME=(.*)/m)?.[1]?.trim();

    if (envVersion) config.version = envVersion;
    if (envProjectName) config.name = envProjectName;
    else config.name = pkg.name; // fallback
  } else {
    config.name = pkg.name;
  }
} catch (e) {
  die("Failed to read configuration: " + e.message);
}

console.log(`📋 Configuration:`);
console.log(`   ArtifactId:  ${config.artifactId}`);
console.log(`   Name:        ${config.name}`);
console.log(`   Version:     ${config.version}`);
console.log(`   Description: ${config.description}`);

// * 2. Update pom.xml
try {
  console.log("📝 Updating pom.xml...");
  const pomPath = path.join(root, "pom.xml");
  let pom = readFileSync(pomPath, "utf8");

  // Helper to replace tag content securely (skipping parent)
  // We assume the project definition follows the parent block
  // Strategy: Split by </parent>, process the second part, join back.

  const parentEndTag = "</parent>";
  const parts = pom.split(parentEndTag);

  if (parts.length < 2) {
    // No parent tag, replace globally (be careful with regex)
    // If there IS a parent tag, split ensures we only touch the project part
    // If no parent tag, 'parts[0]' is the whole file.
    // But user has parent tag in this project.
    console.warn("⚠️ <parent> tag not found or split failed. Parsing strictly...");
  }

  let projectContent = parts.length > 1 ? parts[1] : parts[0];

  // Regex replacers (only replaces the FIRST occurrence in the projectContent)
  projectContent = projectContent.replace(/<artifactId>.*?<\/artifactId>/, `<artifactId>${config.artifactId}</artifactId>`);
  projectContent = projectContent.replace(/<version>.*?<\/version>/, `<version>${config.version}</version>`);
  projectContent = projectContent.replace(/<name>.*?<\/name>/, `<name>${config.name}</name>`);
  projectContent = projectContent.replace(/<description>.*?<\/description>/, `<description>${config.description}</description>`);

  const newPom = parts.length > 1 ? parts[0] + parentEndTag + projectContent : projectContent;

  writeFileSync(pomPath, newPom, "utf8");
  console.log("✅ pom.xml updated.");
} catch (e) {
  die("Failed to update pom.xml: " + e.message);
}

const releaseName = `${config.artifactId}##V${config.version}`;
const releaseDir = path.join(root, "release", releaseName);

// * 3. Verify Java
try {
  const v = execSync("java -version 2>&1", { encoding: "utf8" });
  if (!v.match(/(25\.|23\.|21\.|17\.)/)) {
    console.warn("⚠️ Recommended Java version (17/21/23/25) not detected. Proceeding...");
  }
} catch (e) {
  console.warn("⚠️ Java check failed: " + e.message);
}

// * 4. Build
console.log(`📦 Running Maven Build...`);
if (existsSync(releaseDir)) rmSync(releaseDir, { recursive: true, force: true });
mkdirSync(releaseDir, { recursive: true });

try {
  // Clean install
  run("mvn clean install -DskipTests -q");
} catch (e) {
  die("Maven build failed.");
}

// * 5. Copy Artifact
const target = path.join(root, "target");
if (existsSync(target)) {
  const jar = readdirSync(target).find((f) => f.endsWith(".jar") && !f.includes("original"));
  if (jar) {
    copyFileSync(path.join(target, jar), path.join(releaseDir, `${config.artifactId}.jar`));
    // * Prepare for Docker
    copyFileSync(path.join(target, jar), path.join(target, "docker.jar"));
    console.log(`✅ Artifact copied: ${config.artifactId}.jar`);
  } else {
    die("No JAR file found in target.");
  }
} else {
  die("Target directory not found.");
}

// * 6. Zip (Cross-platform)
try {
  console.log("📦 Zipping...");
  const zipName = `${releaseName}.zip`;
  const zipPath = path.join(root, "release", zipName);
  if (existsSync(zipPath)) rmSync(zipPath);

  if (process.platform === "win32") {
    run(`powershell -Command "Compress-Archive -Path '${releaseDir}' -DestinationPath '${zipPath}' -Force"`);
  } else {
    // using -j to junk paths if we wanted flat, but here preserving folder structure usually ok
    // Actually typically we want the folder inside? Or just contents?
    // User script implies folder content.
    // 'zip -r ...' on the folder
    run(`zip -r "${zipPath}" "${releaseDir}"`);
    // Optimization: zip the CONTENTS relative to release dir or the dir itself?
    // Previous script: execSync(`zip -r ../${zipName} .`, { stdio: "inherit", cwd: releaseDir });
    // Let's stick to that pattern for Mac
    execSync(`zip -r "..\/${zipName}" .`, { stdio: "inherit", cwd: releaseDir });
  }

  // Cleanup
  if (!env.includes("docker")) {
    rmSync(releaseDir, { recursive: true, force: true });
    run("mvn clean -q");
    console.log(`✅ Build Success: release/${zipName}`);
  }
} catch (e) {
  die("Packaging failed: " + e.message);
}

// * 7. Docker Build (Optional)
if (env.includes("docker")) {
  try {
    console.log("🐳 Building Docker Image...");
    const imageName = config.artifactId;
    const tag = env === "docker-prod" ? config.version : "latest";
    const fullImageName = `${imageName}:${tag}`;

    // Build
    run(`docker build -t ${fullImageName} .`);
    console.log(`✅ Docker Image Built: ${fullImageName}`);

    // Cleanup after docker build
    run("mvn clean -q");
  } catch (e) {
    die("Docker build failed: " + e.message);
  }
}
