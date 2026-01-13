<%--
  Created by IntelliJ IDEA.
  User: PC
  Date: 08/12/2025
  Time: 11:34 CH
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- ID thay đổi -> Component cũ bị xóa (chạy destroy), Component mới được tạo (chạy init) -->
<div id="timer-${component.countdown}"
     x-data="{
        timeLeft: ${component.countdown},
        interval: null,

        init() {
            if (this.timeLeft > 0) {
                this.interval = setInterval(() => {
                    this.timeLeft--;
                    if (this.timeLeft <= 0) clearInterval(this.interval);
                }, 1000);
            }
        },

        destroy() {
            // CỰC KỲ QUAN TRỌNG:
            // Khi ID đổi từ timer-60 sang timer-59 (ví dụ vậy),
            // cái cũ bị xóa, phải clear interval để không bị chạy trùng.
        }
     }">
    <button jw-click="resend()">Resend</button>

    <span id="display-${component.countdown}" x-text="timeLeft"></span>
</div>

