package com.udaya.repository;

import com.udaya.model.Module;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
@RequiredArgsConstructor
public class ModuleRepository {

	private final DSLContext dsl;

	// * Find All Modules
	public List<Module> findAll() {
		return dsl.selectFrom(table("modules")).orderBy(field("ordering")).fetchInto(Module.class);
	}

	// * Find by Module Type
	public List<Module> findByModuleTypeId(Long moduleTypeId) {
		return dsl.selectFrom(table("modules")).where(field("module_type_id").eq(moduleTypeId)).orderBy(field("ordering")).fetchInto(Module.class);
	}

	// * Find by Status
	public List<Module> findByStatus(Integer status) {
		return dsl.selectFrom(table("modules")).where(field("status").eq(status)).orderBy(field("ordering")).fetchInto(Module.class);
	}

	// * Find by Name
	public Optional<Module> findByName(String name) {
		return dsl.selectFrom(table("modules")).where(field("name").likeIgnoreCase(name)).fetchOptionalInto(Module.class);
	}

	// * Find by ID
	public Optional<Module> findById(Long id) {
		return dsl.selectFrom(table("modules")).where(field("id").eq(id)).fetchOptionalInto(Module.class);
	}
}
