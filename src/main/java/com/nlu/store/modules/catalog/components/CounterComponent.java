package com.nlu.store.modules.catalog.components;

import com.nlu.store.core.jawire.Component;
import com.nlu.store.core.jawire.Model;
import com.nlu.store.core.jdbc.ResultSetReader;
import com.nlu.store.core.jdbc.RowMapper;
import com.nlu.store.core.jdbc.query.LotusClient;
import com.nlu.store.modules.catalog.dao.mappers.SimpleCategoryMapper;
import com.nlu.store.modules.user.models.User;
import jakarta.enterprise.context.RequestScoped;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;

@RequestScoped
public class CounterComponent extends Component {
    @Model()
    @Getter
    @Setter
    private int counter;


    public void increase(){

        this.counter  += 2;
    }

    public void decrease(){
        this.counter -= 2;
    }


    @Override
    public String view() {
        return "counter";
    }
}
