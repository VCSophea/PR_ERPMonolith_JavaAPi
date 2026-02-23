package com.udaya.repository;

import com.udaya.model.ModuleType;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
@RequiredArgsConstructor
public class ModuleTypeRepository {

	private final DSLContext dsl;

	public List<ModuleType> findAll() {
		return dsl.selectFrom(table("module_types")).orderBy(field("ordering")).fetchInto(ModuleType.class);
	}

	public Long save(ModuleType type) {
		return dsl.insertInto(table("module_types")).set(field("name"), type.getName()).set(field("ordering"), type.getOrdering()).set(field("status"), 1).returningResult(field("id", Long.class)).fetchOne(field("id", Long.class));
	}
}
