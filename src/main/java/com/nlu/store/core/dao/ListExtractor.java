package com.nlu.store.core.dao;




import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ListExtractor<T> implements ResultSetExtractor<List<T>>{

    private final RowMapper<T> mapper;

    public ListExtractor(RowMapper<T> mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<T> extractData(ResultSetReader reader) throws SQLException {
        List<T> list = new ArrayList<>();
        for (int i = 1; reader.next(); i++){
            var entity = mapper.mapRow(reader, i);
            if(entity != null){
                list.add(entity);
            }
        }
        return list;
    }
}
