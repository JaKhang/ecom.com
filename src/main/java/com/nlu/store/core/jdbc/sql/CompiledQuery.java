package com.nlu.store.core.jdbc.sql;

import java.util.Collections;
import java.util.List;

public record CompiledQuery(
        String joinClause,      // Chuỗi JOIN (vd: INNER JOIN...)
        String whereClause,     // Chuỗi WHERE (vd: WHERE age > ?)
        List<Object> parameters  // Danh sách tham số (vd: [18])
) {
    public static CompiledQuery empty() {
        return new CompiledQuery("", "", Collections.emptyList());
    }
}
