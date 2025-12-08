package com.nlu.store.core.jawire;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.List;

@Named
@RequestScoped
public class TodoComponent extends Component {

    @Inject
    private TodoService todoService;

    // --- STATE (Dữ liệu hiển thị) ---
    private List<TodoItem> tasks;
    private String filterMode = "ALL"; // ALL, ACTIVE, COMPLETED

    // --- LIFECYCLE ---
    @Override
    public String view() {
        return "todo";
    }

    // Chạy khi khởi tạo hoặc sau khi Hydrate xong
    // Dùng để load dữ liệu mới nhất từ DB lên giao diện
    @Override
    public void rendering() {
        List<TodoItem> all = todoService.getAll();

        // Logic lọc dữ liệu
        if ("ACTIVE".equals(filterMode)) {
            this.tasks = all.stream().filter(i -> !i.isCompleted()).collect(java.util.stream.Collectors.toList());
        } else if ("COMPLETED".equals(filterMode)) {
            this.tasks = all.stream().filter(TodoItem::isCompleted).collect(java.util.stream.Collectors.toList());
        } else {
            this.tasks = all;
        }
    }



    // --- ACTIONS ---

    public void addTask(String newTaskInput) {
        if (newTaskInput != null && !newTaskInput.trim().isEmpty()) {
            todoService.add(newTaskInput);
        }
    }

    public void deleteTask(int id) {
        todoService.remove(id);
    }

    public void toggleTask(int id) {
        todoService.toggle(id);
    }

    public void setFilter(String mode) {
        this.filterMode = mode;
    }



    public List<TodoItem> getTasks() {
        return tasks;
    }

    public void setTasks(List<TodoItem> tasks) {
        this.tasks = tasks;
    }



    public String getFilterMode() {
        return filterMode;
    }

    @Override
    protected void clear() {

    }

    @Override
    public void updated(String field, Object value) {
        System.out.println(field);
        System.out.println(value);
    }
}
