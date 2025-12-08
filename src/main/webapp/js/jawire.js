document.addEventListener('DOMContentLoaded', () => {
    initJWire();
});

function initJWire() {
    // 1. Lắng nghe sự kiện CLICK (jw-click)
    document.addEventListener('click', (e) => {
        const target = e.target.closest('[jw-click]');
        if (target) {
            e.preventDefault();

            // Tìm component cha (Root)
            const root = target.closest('[jw-snapshot]');
            if (!root) return;

            const actionString = target.getAttribute('jw-click');

            // Thêm hiệu ứng loading
            target.classList.add('jw-loading');

            // --- FIX: Truyền root vào parseAction để scope selector ---
            const actionData = parseAction(actionString, root);

            sendRequest(root, {
                action: actionData,
                updates: {}
            }, () => {
                target.classList.remove('jw-loading');
            });
        }
    });

    // 2. Lắng nghe sự kiện INPUT (jw-model)
    document.addEventListener('input', debounce((e) => {
        const target = e.target;
        if (target.hasAttribute('jw-model')) {
            const field = target.getAttribute('jw-model');
            const value = target.type === 'checkbox' ? target.checked : target.value;
            const root = target.closest('[jw-snapshot]');

            if (root) {
                sendRequest(root, {
                    action: null,
                    updates: { [field]: value }
                });
            }
        }
    }, 300));
}

// --- CORE FUNCTION: Gửi Request lên Servlet ---
function sendRequest(rootElement, payloadData, onComplete = null) {
    const snapshotRaw = rootElement.getAttribute('jw-snapshot');
    if (!snapshotRaw) return;

    const snapshot = JSON.parse(snapshotRaw);

    // Lấy ID từ DOM hiện tại để gửi lên server (quan trọng cho Nested Component)
    const componentId = rootElement.id;

    const payload = {
        componentId: componentId, // Gửi ID để server biết instance nào
        snapshot: snapshot,
        updates: payloadData.updates || {},
        action: payloadData.action || null
    };

    fetch('/jawire/update', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: JSON.stringify(payload)
    })
        .then(response => {
            if (!response.ok) throw new Error('JWire Error');
            return response.text();
        })
        .then(html => {
            // Morphdom sẽ tìm element dựa trên ID hoặc thay thế chính rootElement
            // Lưu ý: html trả về từ server phải có thẻ bao ngoài trùng ID với rootElement
            morphdom(rootElement, html);

            if (onComplete) onComplete();
        })
        .catch(err => {
            console.error('JWire Update Failed:', err);
            // alert('Có lỗi xảy ra, vui lòng tải lại trang.');
            if (onComplete) onComplete();
        });
}

// --- HELPER: Parse Action String ---
// FIX: Thêm tham số componentRoot
function parseAction(actionString, componentRoot) {
    const match = actionString.match(/^([a-zA-Z0-9_]+)(\((.*)\))?$/);
    if (!match) return { method: actionString, params: [] };

    const methodName = match[1];
    const paramsRaw = match[3];

    let params = [];
    if (paramsRaw) {
        try {
            // --- HÀM $ ĐƯỢC SCOPE TRONG COMPONENT ---
            const getValue = (selector) => {
                let el = null;

                // 1. Tìm theo NAME (Chỉ trong componentRoot)
                // a. Radio: Tìm cái checked bên trong component này
                const radioChecked = componentRoot.querySelector(`input[name="${selector}"][type="radio"]:checked`);
                if (radioChecked) return radioChecked.value;

                // b. Input thường theo name
                if (!el) el = componentRoot.querySelector(`[name="${selector}"]`);

                // 2. Fallback: Tìm theo ID hoặc CSS Selector
                if (!el) {
                    // Nếu selector không bắt đầu bằng # hay ., thử tìm theo ID
                    // Lưu ý: Dùng querySelector bên trong root thay vì document.getElementById
                    if (!selector.match(/^[#.]/)) {
                        el = componentRoot.querySelector('#' + selector);
                    }

                    // Cuối cùng thử querySelector raw (ví dụ: .my-class)
                    if (!el) {
                        try { el = componentRoot.querySelector(selector); } catch(e){}
                    }
                }

                if (!el) {
                    console.warn(`Jawire: Không tìm thấy element '${selector}' trong component.`);
                    return null;
                }

                // 3. Lấy giá trị
                if (el.type === 'checkbox') {
                    return el.checked;
                }
                return el.value;
            };

            // Tạo function dynamic
            const fn = new Function("$", "return [" + paramsRaw + "]");
            params = fn(getValue);

        } catch (e) {
            console.error("Jawire: Error parsing params for action '" + actionString + "'", e);
        }
    }

    return { method: methodName, params: params };
}

// --- HELPER: Debounce ---
function debounce(func, wait) {
    let timeout;
    return function(...args) {
        const context = this;
        clearTimeout(timeout);
        timeout = setTimeout(() => func.apply(context, args), wait);
    };
}
