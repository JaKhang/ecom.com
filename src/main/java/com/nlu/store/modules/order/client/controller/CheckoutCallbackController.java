package com.nlu.store.modules.order.client.controller;

import com.nlu.store.core.web.AbstractController;
import com.nlu.store.core.web.HttpContext;
import com.nlu.store.modules.order.services.CheckoutService;
import com.nlu.store.modules.payment.models.PaymentResult;
import com.nlu.store.modules.payment.models.PaymentService;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/checkout/callback")
public class CheckoutCallbackController extends AbstractController {
    @Inject
    private  CheckoutService checkoutService;
    @Inject
    private PaymentService paymentService;

    @Override
    protected void doGet(HttpContext ctx) {
        PaymentResult result = paymentService.verify(null, ctx.getRequest());
        if (result.isPaid()){
            checkoutService.paymentSuccess(result.getTransactionId(), result.getOrderRef());
            ctx.alertSuccess("checkout.success");
            ctx.redirect("/");
            return;
        }

        super.doGet(ctx);
    }
}
