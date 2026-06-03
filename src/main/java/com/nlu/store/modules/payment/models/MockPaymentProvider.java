package com.nlu.store.modules.payment.models;

import com.nlu.store.modules.payment.models.PaymentProvider;
import com.nlu.store.modules.payment.models.PaymentRequest;
import com.nlu.store.modules.payment.models.PaymentResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class MockPaymentProvider implements PaymentProvider {

    private static final String PROVIDER_NAME = "MOCK";
    private static final String MOCK_SECRET_KEY = "day-la-khoa-bi-mat-cua-mock-provider";

    @Override
    public boolean supports(String provider) {
        return true;
    }

    @Override
    public String name() {
        return PROVIDER_NAME;
    }

    @Override
    public String requestPayment(PaymentRequest request) {
        String rawData = "amount=" + request.getAmount() + "&orderRef=" + request.getOrderRefence();
        String signature = hmacSHA256(rawData, MOCK_SECRET_KEY);
        return "/mock-payment-page?amount=%s&orderRef=%s&signature=%s"
                .formatted(request.getAmount(), request.getOrderRefence(), signature);
    }

    @Override
    public PaymentResult handleCallback(HttpServletRequest request) {
        // 1. Lấy các tham số quan trọng từ URL callback
        String status = request.getParameter("status");       // SUCCESS hoặc FAILED
        String orderRef = request.getParameter("orderRef");
        String amount = request.getParameter("amount");
        String receivedSignature = request.getParameter("signature"); // Chữ ký do "Cổng thanh toán" gửi về

        // 2. Kiểm tra dữ liệu đầu vào
        if (status == null || receivedSignature == null) {
            return buildResult(false, "Dữ liệu callback không hợp lệ (thiếu tham số)", null, orderRef);
        }

        // 3. Tái tạo chuỗi dữ liệu để kiểm tra chữ ký
        // Quy ước: data = "status={status}&orderRef={orderRef}&amount={amount}"
        // Thứ tự ghép chuỗi PHẢI KHỚP với thứ tự bên trang tạo link callback
        String rawData = "status=" + status + "&orderRef=" + orderRef + "&amount=" + amount;

        String calculatedSignature = hmacSHA256(rawData, MOCK_SECRET_KEY);

        if (!calculatedSignature.equals(receivedSignature)) {
            return buildResult(false, "CẢNH BÁO: Chữ ký không hợp lệ! Dữ liệu có thể bị giả mạo.", null, orderRef);
        }

        // 6. Nếu chữ ký đúng -> Xử lý kết quả thanh toán
        boolean isPaid = "SUCCESS".equalsIgnoreCase(status);
        String note = isPaid ? "Thanh toán thành công (Verified)" : "Thanh toán thất bại (Verified)";

        // Thu thập params
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((k, v) -> params.put(k, v[0]));

        return buildResult(isPaid, note, params, orderRef);
    }

    // --- Helper Methods ---

    private PaymentResult buildResult(boolean paid, String note, Map<String, String> params, String o) {
        return PaymentResult.builder()
                .paid(paid)
                .orderRef(o)
                .transactionId("MOCK-" + UUID.randomUUID().toString().substring(0, 8))
                .note(note)
                .params(params)
                .build();
    }

    /**
     * Hàm tạo chữ ký HMAC-SHA256
     */
    public static String hmacSHA256(String data, String key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKeySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return toHexString(rawHmac);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Lỗi tạo chữ ký: " + e.getMessage());
        }
    }

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }
}
