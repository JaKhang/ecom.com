package com.nlu.store.core.jawire;

import lombok.Data;

@Data
public class TodoItem {
    private int id;
    private String title;
    private boolean completed;

    public TodoItem() {} // Cần thiết cho Jackson

    public TodoItem(int id, String title, boolean completed) {
        this.id = id;
        this.title = title;
        this.completed = completed;
    }
}
