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
}
