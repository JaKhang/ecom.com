package com.nlu.store.core.jdbc.sql;

import com.nlu.store.core.data.specification.Criteria;

public interface CriteriaCompiler {
    CompiledQuery compile(Criteria criteria);
}
