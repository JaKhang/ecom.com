<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:useBean id="component" scope="request" type="com.nlu.store.modules.user.controllers.ResendVerifyComponent"/>


<!-- Thêm lastUpdated vào ID để đảm bảo ID luôn mới mỗi khi server render lại -->
<div id="timer-box-${component.countdown}"
     x-data="{
        timeLeft: ${component.countdown},
        interval: null,

        init() {
            console.log('Timer Init:', this.timeLeft);
            if (this.timeLeft > 0) {
                this.startTimer();
            }
        },

        startTimer() {
            if (this.interval) clearInterval(this.interval);
            this.interval = setInterval(() => {
                this.timeLeft--;
                if (this.timeLeft <= 0) {
                    this.timeLeft = 0;
                    clearInterval(this.interval);
                }
            }, 1000);
        },

        destroy() {
            if (this.interval) clearInterval(this.interval);
        }
    }"
>
    <!-- Nội dung button giữ nguyên -->
    <button class="btn btn-buy w-auto font-md-bold"
            type="button"
            jw-click="resend()"
            :class="{ 'disabled': timeLeft > 0 || $el.classList.contains('jw-loading') }"
            :disabled="timeLeft > 0"
            :style="timeLeft > 0 ? 'opacity: 0.6; cursor: not-allowed;' : ''"
    >
        <span x-show="timeLeft === 0">
            <fmt:message key="verify.pending.btn_resend"/>
        </span>

        <span x-show="timeLeft > 0" x-cloak>
            <fmt:message key="verify.pending.btn_resend"/>
            (<span x-text="timeLeft"></span>s)
        </span>
    </button>
</div>
