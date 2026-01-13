document.addEventListener('alpine:init', () => {

    Alpine.data('cartAction', (initial = {}) => ({
        message: initial.message || {
            remove: {
                success: "",
                error: ""
            },
            add: {
                success: "",
                error: ""
            },
            update: {
                success: "",
                error: ""
            }
        },

        add(variantId, quantity) {
            JWireInstance.dispatch('cart', 'addOrUpdate', variantId, quantity)
                .then(() => {
                    this.toastSuccess(initial.message.add.success)
                })
                .catch((e) => {
                    this.toastError(e.message || this.message.add.error)
                })
        },
        remove(variantId, componentId) {
            JWireInstance.dispatch('cart', 'remove', variantId)
                .then(() => {
                    Toastify({
                        text: initial.message.remove.success,
                        className: 'bg-success',
                        style: {background: 'unset'},
                        duration: 3000
                    }).showToast();
                })
                .then(() => JWireInstance.dispatch(componentId, 'refresh'))
                .catch((e) => {
                    Toastify({
                        text: e.message || initial.message.remove.success,
                        className: 'bg-danger',
                        style: {background: 'unset'},
                        duration: 3000
                    }).showToast();
                });
        },
        addWithDefault(productId) {
            JWireInstance.dispatch('cart', 'addDefault', productId)
                .then(() => {
                    this.toastSuccess(initial.message.add.success)
                })
                .catch((e) => {
                    this.toastError(e.message || initial.message.add.error)

                });
        },
        toastSuccess(message) {
            Toastify({
                text: message,
                className: 'bg-success',
                style: {
                    background: 'unset'
                },
                duration: 3000
            }).showToast()
        },
        toastError(message) {
            Toastify({
                text: message,
                className: 'bg-danger',
                style: {
                    background: 'unset'
                },
                duration: 3000
            }).showToast()
        }
    }))
})