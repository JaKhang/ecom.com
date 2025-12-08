package com.nlu.store.core.jawire;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class TodoService {
    private List<TodoItem> db = new ArrayList<>();
    private AtomicInteger idCounter = new AtomicInteger(1);

    public TodoService() {
        // Dữ liệu mẫu
        add("Học Java Core");
        add("Tìm hiểu JaWire");
    }

    public List<TodoItem> getAll() {
        return new ArrayList<>(db);
    }

    public void add(String title) {
        db.add(new TodoItem(idCounter.getAndIncrement(), title, false));
    }

    public void remove(int id) {
        db.removeIf(item -> item.getId() == id);
    }

    public void toggle(int id) {
        db.stream().filter(i -> i.getId() == id).findFirst()
                .ifPresent(i -> i.setCompleted(!i.isCompleted()));
    }
}
