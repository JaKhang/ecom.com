/**
 * JWire Core - Lightweight Server-Driven UI Utility
 * Dependencies: Alpine.js (with Morph plugin)
 *
 * Features:
 * - Server-side rendering updates via AJAX
 * - Alpine.js Morphing for DOM diffing
 * - Event delegation (Click, Input, Change)
 * - Dynamic Debounce per input
 * - External Triggering API
 */

const JW_CONFIG = {
    selectors: {
        click: '[jw-click]',
        model: '[jw-model]',
        snapshot: '[jw-snapshot]',
        loading: 'jw-loading',
        replace: 'jw-replace',
        ignore: 'jw-ignore',
        childOnly: 'jw-childOnly',
        update: 'jw-update',

        debounce: 'jw-debounce', // Custom delay attribute
    },
    endpoints: {
        update: '/jawire/update' // Server request path
    },
    defaultDebounce: 300 // Default debounce time in ms
};

class JWire {
    constructor() {
        this.inputTimers = new Map();
        this.init();
    }

    init() {
        if (!window.Alpine) {
            console.error('JWire: Alpine.js is required but not found.');
            return;
        }
        this.bindEvents();
    }

    /**
     * Set up event delegation
     */
    bindEvents() {
        document.addEventListener('click', this.onClickHandler.bind(this));
        document.addEventListener('input', this.onInputHandler.bind(this));
        // Lắng nghe sự kiện change dành riêng cho thẻ Select
        document.addEventListener('change', this.onChangeHandler.bind(this));
        window.addEventListener('jawire:dispatch', this.onExternalTrigger.bind(this));
    }

    onExternalTrigger(e) {
        if (e.detail) {
            const {componentId, method, params} = e.detail;
            this.dispatch(componentId, method, ...(params || []));
        }
    }

    /**
     * Manually trigger an update from external sources
     */
    async dispatch(componentId, methodName, ...params) {
        const root = document.getElementById(componentId);
        if (!root) {
            console.warn(`JWire: Component with ID '${componentId}' not found.`);
            return;
        }

        return this.sendRequest(root, {
            action: {method: methodName, params},
            updates: {}
        });
    }


    async handle(e,methodName, ...params) {
        const root = this.getClosestSnapshot(e.current.target);
        if (!root) {
            return;
        }

        return this.sendRequest(root, {
            action: {method: methodName, params},
            updates: {}
        });
    }

    onClickHandler(e) {
        const target = e.target.closest(JW_CONFIG.selectors.click);
        if (!target) return;

        e.preventDefault();

        const root = this.getClosestSnapshot(target);
        if (!root) return;

        const actionString = target.getAttribute('jw-click');
        target.classList.add(JW_CONFIG.selectors.loading);

        const actionData = this.parseAction(actionString, root);
        this.sendRequest(root, {
            action: actionData,
            updates: {}
        }).finally(() => {
            target.classList.remove(JW_CONFIG.selectors.loading);
        });
    }

    /**
     * Handler cho sự kiện Input (Text, Checkbox, Radio...)
     * Bỏ qua thẻ SELECT vì nó dùng sự kiện Change
     */
    onInputHandler(e) {
        const target = e.target;
        console.log(target)
        if (!target.hasAttribute('jw-model') || target.tagName === 'SELECT') return;

        const root = this.getClosestSnapshot(target);
        if (!root) return;

        const field = target.getAttribute('jw-model');
        let value;

        // --- LOGIC MỚI CHO CHECKBOX ---
        if (target.type === 'checkbox') {
            // Tìm tất cả checkbox có cùng jw-model trong component này
            const group = root.querySelectorAll(`input[type="checkbox"][jw-model="${field}"]`);

            if (group.length > 1) {
                // Nếu là một nhóm (List), trả về mảng các value được checked
                value = Array.from(group)
                    .filter(el => el.checked)
                    .map(el => el.value);
            } else {
                // Nếu chỉ có 1 checkbox lẻ, trả về boolean
                value = target.checked;
            }
        } else {
            // Các input khác (Text, Radio...)
            value = target.value;
        }
        // ------------------------------

        const delay = this.getDebounceDelay(target);
        this.setDebounceTimer(target, root, field, value, delay);
    }

    /**
     * Handler cho sự kiện Change (Dành riêng cho Select/Dropdown)
     */
    onChangeHandler(e) {
        const target = e.target;
        // Chỉ xử lý nếu có jw-model và là thẻ SELECT
        if (!target.hasAttribute('jw-model') || target.tagName !== 'SELECT') return;

        const root = this.getClosestSnapshot(target);
        if (!root) return;

        const field = target.getAttribute('jw-model');

        // Xử lý giá trị (hỗ trợ multiple select)
        let value;
        if (target.multiple) {
            value = Array.from(target.selectedOptions).map(option => option.value);
        } else {
            value = value === 'null' ? null : target.value;
        }

        const delay = this.getDebounceDelay(target);
        this.setDebounceTimer(target, root, field, value, delay);
    }

    getClosestSnapshot(element) {
        return element.closest(JW_CONFIG.selectors.snapshot);
    }

    getDebounceDelay(target) {
        let delay = JW_CONFIG.defaultDebounce;
        if (target.hasAttribute(JW_CONFIG.selectors.debounce)) {
            const attrVal = target.getAttribute(JW_CONFIG.selectors.debounce);
            delay = attrVal === '' ? 150 : parseInt(attrVal, 10);
            if (isNaN(delay)) delay = JW_CONFIG.defaultDebounce;
        }
        return delay;
    }

    setDebounceTimer(target, root, field, value, delay) {
        if (this.inputTimers.has(target)) {
            clearTimeout(this.inputTimers.get(target));
        }

        const timerId = setTimeout(() => {
            this.sendRequest(root, {
                action: null,
                updates: {[field]: value}
            });
            this.inputTimers.delete(target);
        }, delay);

        this.inputTimers.set(target, timerId);
    }

    async sendRequest(rootElement, payloadData) {
        const snapshotRaw = rootElement.getAttribute('jw-snapshot');
        if (!snapshotRaw) return;

        try {
            const snapshot = JSON.parse(snapshotRaw);
            const componentId = rootElement.id;

            const payload = {
                componentId,
                snapshot,
                updates: payloadData.updates || {},
                action: payloadData.action || null
            };

            const response = await fetch(JW_CONFIG.endpoints.update, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: JSON.stringify(payload)
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Có lỗi xảy ra');
            }

            const html = await response.text();
            window.Alpine.morph(rootElement, html, this.getMorphConfig());

        } catch (err) {
            console.log(err.message)
            throw err;
        }
    }

    getMorphConfig() {
        return {
            updating(el, toEl, childrenOnly, skip) {
                if (el.nodeType === Node.ELEMENT_NODE && el.hasAttribute(JW_CONFIG.selectors.ignore)) {
                    skip();
                }

                if (el.nodeType === Node.ELEMENT_NODE && el.hasAttribute(JW_CONFIG.selectors.childOnly)) {
                    childrenOnly();
                }
            },
            key(el) {
                return el.id;
            },
            lookahead: true,
        };
    }

    parseAction(actionString, componentRoot) {
        const match = actionString.match(/^([a-zA-Z0-9_]+)(\((.*)\))?$/);
        if (!match) return {method: actionString, params: []};

        const [, methodName, , paramsRaw] = match;
        let params = [];

        if (paramsRaw) {
            try {
                const $ = (selector) => this.resolveValue(selector, componentRoot);
                const fn = new Function("$", `return [${paramsRaw}]`);
                params = fn($);
            } catch (e) {
                console.error(`JWire: Error parsing params for '${actionString}'`, e);
            }
        }

        return {method: methodName, params};
    }

    resolveValue(selector, root) {
        // 1. Xử lý Radio Group
        const radio = root.querySelector(`input[name="${selector}"][type="radio"]:checked`);
        if (radio) return radio.value;

        // 2. LOGIC MỚI: Xử lý Checkbox Group (theo name hoặc jw-model)
        // Tìm các checkbox có name="selector" hoặc jw-model="selector"
        const checkboxes = root.querySelectorAll(`
            input[name="${selector}"][type="checkbox"],
            input[jw-model="${selector}"][type="checkbox"]
        `);

        if (checkboxes.length > 1) {
            // Trả về mảng giá trị của các ô đã chọn
            return Array.from(checkboxes)
                .filter(cb => cb.checked)
                .map(cb => cb.value);
        }

        // 3. Xử lý Element đơn lẻ (Text, Single Checkbox, Select)
        const el = root.querySelector(`[name="${selector}"]`) ||
            (selector.match(/^[#.]/) ? root.querySelector(selector) : root.querySelector(`#${selector}`)) ||
            root.querySelector(`[jw-model="${selector}"]`) || // Fallback tìm theo model
            root.querySelector(selector);

        if (!el) {
            console.warn(`JWire: Element '${selector}' not found.`);
            return null;
        }

        if (el.tagName === 'SELECT' && el.multiple) {
            return Array.from(el.selectedOptions).map(opt => opt.value);
        }

        return el.type === 'checkbox' ? el.checked : el.value;
    }
}

// Initialize and assign to window for global access
document.addEventListener('DOMContentLoaded', () => {
    window.JWireInstance = new JWire();
});
