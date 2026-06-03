package com.nlu.store.modules.payment.models;

import com.nlu.store.core.web.AbstractController; // Giả sử bạn có class này
import com.nlu.store.core.web.HttpContext;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/mock-payment-page") // URL này phải khớp với return của MockPaymentProvider.requestPayment
public class MockPaymentController extends AbstractController {

    // KHÓA BÍ MẬT: Phải khớp tuyệt đối với khóa trong MockPaymentProvider
    private static final String MOCK_SECRET_KEY = "day-la-khoa-bi-mat-cua-mock-provider";

    // URL xử lý kết quả thanh toán (nơi MockPaymentProvider.handleCallback lắng nghe)
    private static final String CALLBACK_URL = "/checkout/callback";

    @Override
    protected void doGet(HttpContext ctx) {
        // 1. Lấy dữ liệu từ URL
        String amount = ctx.getParam("amount").orElse("");
        String orderRef = ctx.getParam("orderRef").orElse("");
        String receivedSignature = ctx.getParam("signature").orElse("");

        // 2. Kiểm tra dữ liệu đầu vào
        if (amount.isEmpty() || orderRef.isEmpty() || receivedSignature.isEmpty()) {
            ctx.sendError(400, "Thiếu thông tin thanh toán (amount, orderRef, signature)");
            return;
        }

        // 3. VERIFY SIGNATURE (Bảo mật)
        // Tái tạo chuỗi data theo quy ước: amount={amount}&orderRef={orderRef}
        String rawData = "amount=" + amount + "&orderRef=" + orderRef;
        String calculatedSignature = MockPaymentProvider.hmacSHA256(rawData, MOCK_SECRET_KEY);

        if (!calculatedSignature.equals(receivedSignature)) {
            // Nếu chữ ký không khớp -> Có dấu hiệu giả mạo URL
            ctx.sendError(403, "CẢNH BÁO BẢO MẬT: Chữ ký không hợp lệ! Vui lòng không sửa đổi URL.");
            return;
        }

        // 4. Tạo Link Callback cho nút bấm (Cũng phải ký tên bảo mật)
        String contextPath = ctx.getRequest().getContextPath();
        String baseUrl = contextPath + CALLBACK_URL;

        // --- Link THÀNH CÔNG ---
        // Data: status=SUCCESS&orderRef=...&amount=...
        String successData = "status=SUCCESS&orderRef=" + orderRef + "&amount=" + amount;
        String successSig = MockPaymentProvider.hmacSHA256(successData, MOCK_SECRET_KEY);
        String linkSuccess = baseUrl + "?" + successData + "&signature=" + successSig;

        // --- Link THẤT BẠI ---
        // Data: status=FAILED&orderRef=...&amount=...
        String failData = "status=FAILED&orderRef=" + orderRef + "&amount=" + amount;
        String failSig = MockPaymentProvider.hmacSHA256(failData, MOCK_SECRET_KEY);
        String linkFail = baseUrl + "?" + failData + "&signature=" + failSig;

        // 5. Truyền dữ liệu sang View
        ctx.setAttribute("linkSuccess", linkSuccess);
        ctx.setAttribute("linkFail", linkFail);

        // Lưu ý: amount và orderRef đã có sẵn trong param scope (dùng ${param.amount} ở JSP)
        // nên không cần setAttribute lại, trừ khi muốn format lại.

        ctx.view("mock/payment");
    }
}

