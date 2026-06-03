package com.nlu.store.modules.order.services;

import com.nlu.store.core.data.ULID;
import com.nlu.store.core.exceptions.ResourceNotFoundException;
import com.nlu.store.core.web.Authentication;
import com.nlu.store.modules.catalog.model.cart.Cart;
import com.nlu.store.modules.catalog.model.cart.CartItem;
import com.nlu.store.modules.catalog.model.details.ProductVariant;
import com.nlu.store.modules.catalog.model.details.VariantValue;
import com.nlu.store.modules.catalog.services.ProductService;
import com.nlu.store.modules.order.dao.OrderDao;
import com.nlu.store.modules.order.dto.CheckoutRequest;
import com.nlu.store.modules.order.models.*;
import com.nlu.store.modules.payment.models.PaymentRequest;
import com.nlu.store.modules.payment.models.PaymentService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class DefaultCheckoutService implements CheckoutService {

    private final OrderDao orderDao;
    private final ProductService productService;
    private final PaymentService paymentService;

    @Inject
    public DefaultCheckoutService(OrderDao orderDao, ProductService productService, PaymentService paymentService) {
        this.orderDao = orderDao;
        this.productService = productService;
        this.paymentService = paymentService;
    }

    @Override
    public CheckoutResult checkout(Cart cart, CheckoutRequest request, Authentication authentication) {
        // 1. SECURITY: Fetch fresh data from DB.
        // Never trust prices or stock levels sent from the client-side (Cart).
        List<ProductVariant> dbVariants = productService.findVariantsByIds(cart.getVariantIds());

        // Convert list to map for O(1) lookup performance
        Map<ULID, ProductVariant> variantMap = dbVariants.stream()
                .collect(Collectors.toMap(ProductVariant::getId, Function.identity()));

        List<OrderItem> orderItems = new LinkedList<>();
        BigDecimal subTotal = BigDecimal.ZERO; // Accumulator for raw product value

        // 2. PROCESS CART ITEMS
        for (CartItem cartItem : cart) {
            ProductVariant dbVariant = variantMap.get(cartItem.getVariantId());

            // Validate existence
            if (dbVariant == null) {
                throw new ResourceNotFoundException("Variant not found: " + cartItem.getVariantId());
            }

            // Validate stock availability
            if (dbVariant.getStocks() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + dbVariant.getName());
            }

            // Calculate item total based on DB price
            BigDecimal itemTotal = this.calculateItemTotal(dbVariant, cartItem.getQuantity());
            subTotal = subTotal.add(itemTotal);

            // Build OrderItem using reliable DB data
            OrderItem orderItem = OrderItem.builder()
                    .sku(dbVariant.getSku())
                    .price(dbVariant.getPrice())
                    .productName(dbVariant.getName())
                    .thumbnail(cartItem.getThumbnail())
                    .productId(cartItem.getProductId())
                    .variantId(dbVariant.getId())
                    .totalPrice(itemTotal)
                    .quantity(cartItem.getQuantity())
                    .variantSnapshot(getVariantSnapshot(dbVariant.getValues()))
                    .build();

            orderItems.add(orderItem);
        }

        // 3. FINANCIAL CALCULATIONS
        // Placeholder logic for shipping and discounts (should be replaced by actual services)
        BigDecimal shippingFee = BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;

        // Final amount to be paid
        BigDecimal grandTotal = subTotal.add(shippingFee).subtract(discountAmount);

        // 4. CONSTRUCT SHIPPING DETAILS
        // Normalize address data into the ShippingDetails entity
        ShippingDetails shippingDetails = ShippingDetails.builder()
                // Contact Info
                .contactName(request.getFullName())
                .contactPhone(request.getPhoneNumber())
                .contactEmail(request.getEmail())

                // Structured Address (Useful for shipping APIs like GHN/GHTK)
                .province(request.getProvince())
                .district(request.getDistrict())
                .ward(request.getWard())
                .addressDetail(request.getAddressDetail())

                // Full Address Snapshot
                .fullAddress(String.format("%s, %s, %s, %s",
                        request.getAddressDetail(), request.getWard(), request.getDistrict(), request.getProvince()))
                .build();

        // 5. BUILD THE ORDER
        Order order = Order.builder()
                .code(generateOrderCode()) // Generate human-readable code
                .items(orderItems)
                .shippingDetails(shippingDetails) // Attach shipping info
                .userId(authentication.id())

                // Status & Payment
                .status(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.UNPAID)
                .paymentMethod(request.getPaymentMethod())

                // Financials
                .currency("VND")
                .subTotal(subTotal)
                .shippingFee(shippingFee)
                .discountAmount(discountAmount)
                .grandTotal(grandTotal)

                // Meta & Audit
                .note(request.getNote())
                .createdAt(LocalDateTime.now())
                .build();

        // 6. PERSISTENCE
        // Save the order to the database
        orderDao.createOrderAndDecreaseStock(order);

        String paymentGateway = paymentService.requestPayment(order.getPaymentMethod(), PaymentRequest.builder()
                .amount(order.getGrandTotal())
                .orderRefence(order.getCode())
                .build());
        return CheckoutResult.builder()
                .paymentGetWay(paymentGateway)
                .orderCode(order.getCode())
                .build();
    }

    @Override
    public void paymentSuccess(String transactionId, String orderRef) {
        orderDao.paymentSuccess(transactionId, orderRef);
    }



    /**
     * Safe calculation of item total price.
     */
    private BigDecimal calculateItemTotal(ProductVariant variant, int quantity) {
        if (variant == null || variant.getPrice() == null) {
            return BigDecimal.ZERO;
        }
        return variant.getPrice().multiply(BigDecimal.valueOf(quantity));
    }


    private String generateOrderCode() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int randomNum = ThreadLocalRandom.current().nextInt(100, 999);
        return timestamp + randomNum;
    }

    /**
     * Safe calculation of total price.
     */
    private BigDecimal getTotalPrice(ProductVariant variant, int quantity) {
        if (variant == null || variant.getPrice() == null) {
            return BigDecimal.ZERO;
        }
        return variant.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * Converts variant values (Color, Size) into a snapshot Map for JSON storage.
     */
    private Map<String, String> getVariantSnapshot(List<VariantValue> values) {
        Map<String, String> snapshot = new HashMap<>();
        if (values != null) {
            for (VariantValue val : values) {
                if (val.getAttributeLabel() != null && val.getOptionValue() != null) {
                    snapshot.put(val.getAttributeLabel(), val.getOptionValue());
                }
            }
        }
        return snapshot;
    }
}
