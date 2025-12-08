<jsp:useBean id="component" scope="request" type="com.nlu.store.core.jawire.TodoComponent"/>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- CSS Inline cho gọn -->
<style>
    .todo-box { max-width: 500px; margin: 20px auto; font-family: sans-serif; border: 1px solid #ddd; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
    .todo-input { width: 70%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; }
    .btn { padding: 10px 15px; cursor: pointer; border: none; border-radius: 4px; color: white; }
    .btn-primary { background: #007bff; }
    .btn-danger { background: #dc3545; font-size: 0.8em; }
    .task-item { display: flex; justify-content: space-between; align-items: center; padding: 10px 0; border-bottom: 1px solid #eee; }
    .completed { text-decoration: line-through; color: #888; }
    .filters { margin-bottom: 15px; }
    .filters button { background: #eee; color: #333; margin-right: 5px; }
    .filters button.active { background: #007bff; color: white; }
</style>

<div class="todo-box">
    <h2 style="text-align: center">JaWire Todo List </h2>

    <!-- 1. Input thêm mới -->
    <div style="display: flex; gap: 10px; margin-bottom: 20px;">
        <!-- jw-model: Khi gõ, biến newTaskInput trong Java sẽ tự update -->
        <!-- jw-click (Enter key trick): Nếu muốn Enter để submit, cần thêm JS xử lý keydown, ở đây dùng button cho đơn giản -->
        <input type="text"
               class="todo-input"
               placeholder="Nhập công việc..."
               name="newTaskInput"
               > <!-- Value phải bind ngược lại để khi reset biến Java, input cũng rỗng -->

        <button class="btn btn-primary" jw-click="addTask($('newTaskInput'))">Thêm</button>
    </div>

    <!-- 2. Bộ lọc -->
    <div class="filters">
        <button class="btn ${component.filterMode == 'ALL' ? 'active' : ''}"
                jw-click="setFilter('ALL')">Tất cả</button>

        <button class="btn ${component.filterMode == 'ACTIVE' ? 'active' : ''}"
                jw-click="setFilter('ACTIVE')">Đang làm</button>

        <button class="btn ${component.filterMode == 'COMPLETED' ? 'active' : ''}"
                jw-click="setFilter('COMPLETED')">Đã xong</button>
    </div>

    <!-- 3. Danh sách -->
    <div>
        <c:if test="${empty component.tasks}">
            <p style="text-align: center; color: #777">Không có công việc nào.</p>
        </c:if>

        <c:forEach items="${component.tasks}" var="item">
            <div class="task-item">
                <div style="display: flex; align-items: center; gap: 10px;">
                    <!-- Checkbox gọi toggleTask(id) -->
                    <input type="checkbox"
                        ${item.completed ? 'checked' : ''}
                           jw-click="toggleTask(${item.id})">

                    <span class="${item.completed ? 'completed' : ''}">
                            ${item.title}
                    </span>
                </div>

                <!-- Nút xóa gọi deleteTask(id) -->
                <button class="btn btn-danger" jw-click="deleteTask(${item.id})">Xóa</button>
            </div>
        </c:forEach>
    </div>

</div>
