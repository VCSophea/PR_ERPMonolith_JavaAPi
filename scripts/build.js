const { execSync } = require("child_process");
const { existsSync, rmSync, mkdirSync, copyFileSync, readdirSync, readFileSync, writeFileSync } = require("fs");
const path = require("path");

const root = path.resolve(__dirname, "..");
const run = (cmd) => {
  try {
    execSync(cmd, { stdio: "inherit", cwd: root });
  } catch (e) {
    process.exit(1);
  }
};
const die = (msg) => {
  console.error(`❌ ${msg}`);
  process.exit(1);
};

const isDocker = process.argv[2] === "docker";

// * 1. Configuration
let config = { ...JSON.parse(readFileSync(path.join(root, "package.json"), "utf8")) }; // loads name, version
try {
  if (existsSync(path.join(root, ".env"))) {
    const envData = readFileSync(path.join(root, ".env"), "utf8");
    config.version = envData.match(/^APP_VERSION=(.*)/m)?.[1]?.trim() || config.version;
    config.projectName = envData.match(/^PROJECT_NAME=(.*)/m)?.[1]?.trim() || config.name; // Use projectName for <name>
    config.appEnv = envData.match(/^APP_ENV=(.*)/m)?.[1]?.trim() || "dev";
  }
} catch (e) {
  die("Config load failed: " + e.message);
}

console.log(` Build: ${config.projectName} (Artifact: ${config.name}, Version: ${config.version}) [${config.appEnv}]`);

// * 2. Sync pom.xml
try {
  const pomPath = path.join(root, "pom.xml");
  const parts = readFileSync(pomPath, "utf8").split("</parent>");
  const projectBlock = (parts.length > 1 ? parts[1] : parts[0])
    .replace(/<artifactId>.*?<\/artifactId>/, `<artifactId>${config.name}</artifactId>`)
    .replace(/<version>.*?<\/version>/, `<version>${config.version}</version>`)
    .replace(/<name>.*?<\/name>/, `<name>${config.projectName}</name>`)
    .replace(/<description>.*?<\/description>/, `<description>${config.description}</description>`);

  writeFileSync(pomPath, parts.length > 1 ? parts[0] + "</parent>" + projectBlock : projectBlock);
  console.log("✅ pom.xml updated");
} catch (e) {
  die("POM update failed: " + e.message);
}

// * 3. Build & Package
const releaseName = `${config.name}##V${config.version}`;
const releaseDir = path.join(root, "release", releaseName);
const target = path.join(root, "target");

if (existsSync(releaseDir)) rmSync(releaseDir, { recursive: true, force: true });
mkdirSync(releaseDir, { recursive: true });

console.log("📦 Maven Build...");
run("mvn clean install -DskipTests -q");

// * 4. Artifact Handling
const jar = readdirSync(target).find((f) => f.endsWith(".jar") && !f.includes("original"));
if (!jar) die("Build failed: No JAR found.");

copyFileSync(path.join(target, jar), path.join(releaseDir, `${config.name}.jar`));
copyFileSync(path.join(target, jar), path.join(target, "docker.jar")); // Standardize for Docker
console.log(`✅ Jar created: ${config.name}.jar`);

// * 5. Handle Next Steps
if (isDocker) {
  console.log("🐳 Starting Docker containers...");
  run(`COMPOSE_PROJECT_NAME=${config.name} docker compose up -d --build`);
  console.log("✅ Docker is up and running!");
} else {
  // * 6. Zip Release
  console.log("📦 Zipping Release...");
  const zipName = `${releaseName}.zip`;
  const zipPath = path.join(root, "release", zipName);

  if (process.platform === "win32") {
    execSync(`powershell -Command "Compress-Archive -Path '${releaseDir}' -DestinationPath '${zipPath}' -Force"`, { stdio: "inherit" });
  } else {
    execSync(`zip -r "../${zipName}" .`, { stdio: "inherit", cwd: releaseDir });
  }

  // Cleanup
  rmSync(releaseDir, { recursive: true, force: true });
  run("mvn clean -q");
  console.log(`✅ Release Ready: release/${zipName}`);
}
