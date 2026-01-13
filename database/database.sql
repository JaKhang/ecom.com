-- ============================================================
-- 0. GLOBAL SETTINGS & CLEANUP
-- ============================================================
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP DATABASE IF EXISTS phone_store;
CREATE DATABASE phone_store;
USE phone_store;
-- ============================================================
-- 1. AUTHENTICATION (USERS & ROLES)
-- ============================================================
DROP TABLE IF EXISTS `users_roles`;
DROP TABLE IF EXISTS `roles`;
DROP TABLE IF EXISTS `users`;

CREATE TABLE `users`
(
    `id`                              CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    `email`                           VARCHAR(255)                                          NOT NULL,
    `password_hash`                   VARCHAR(255)                                          NOT NULL,
    `full_name`                       VARCHAR(255)                                          NOT NULL,
    `avatar`                          VARCHAR(255)                                          NULL     DEFAULT NULL,
    `is_active`                       BOOLEAN                                               NOT NULL DEFAULT TRUE,
    `verify_token`                    VARCHAR(64)                                           NULL     DEFAULT NULL,
    `verify_token_expired_at`         TIMESTAMP                                             NULL     DEFAULT NULL,
    `verified_at`                     TIMESTAMP                                             NULL     DEFAULT NULL,
    `reset_password_token`            VARCHAR(64)                                           NULL     DEFAULT NULL,
    `reset_password_token_expired_at` TIMESTAMP                                             NULL     DEFAULT NULL,
    `created_at`                      TIMESTAMP                                             NULL     DEFAULT CURRENT_TIMESTAMP,
    `updated_at`                      TIMESTAMP                                             NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at`                      TIMESTAMP                                             NULL     DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_users_email_active` (`email`, (IF(deleted_at IS NULL, 1, NULL)))
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `roles`
(
    `id`         CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    `name`       VARCHAR(255)                                          NOT NULL,
    `code`       VARCHAR(50)                                           NOT NULL,
    `created_at` TIMESTAMP                                             NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP                                             NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at` TIMESTAMP                                             NULL     DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_roles_code` (`code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `users_roles`
(
    `user_id` CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    `role_id` CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    PRIMARY KEY (`user_id`, `role_id`),
    CONSTRAINT `fk_ur_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_ur_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


-- ============================================================
-- 2. CATALOG BASE (BRANDS & CATEGORIES)
-- ============================================================
DROP TABLE IF EXISTS `brands`;
CREATE TABLE `brands`
(
    `id`             CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    `name`           VARCHAR(64)                                           NOT NULL,
    `slug`           VARCHAR(128)                                          NOT NULL,
    `logo`           VARCHAR(255)                                          NULL     DEFAULT NULL,
    `sort_order`     INT                                                   NOT NULL DEFAULT 0,
    `products_count` INT UNSIGNED                                          NOT NULL DEFAULT 0,
    `is_active`      TINYINT(1)                                            NOT NULL DEFAULT 1,
    `created_at`     TIMESTAMP                                             NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     TIMESTAMP                                             NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at`     TIMESTAMP                                             NULL     DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_brands_slug_active` (`slug`, (IF(deleted_at IS NULL, 1, NULL)))
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories`
(
    `id`             CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    `name`           VARCHAR(64)                                           NOT NULL,
    `slug`           VARCHAR(128)                                          NOT NULL,
    `icon`           VARCHAR(255)                                          NULL     DEFAULT NULL,
    `parent_id`      CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NULL     DEFAULT NULL,
    `sort_order`     INT                                                   NOT NULL DEFAULT 0,
    `is_active`      TINYINT(1)                                            NOT NULL DEFAULT 1,
    `products_count` INT UNSIGNED                                          NOT NULL DEFAULT 0,
    `created_at`     TIMESTAMP                                             NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     TIMESTAMP                                             NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at`     TIMESTAMP                                             NULL     DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_categories_slug_active` (`slug`, (IF(deleted_at IS NULL, 1, NULL))),
    KEY `idx_categories_parent` (`parent_id`),
    CONSTRAINT `fk_categories_parent` FOREIGN KEY (`parent_id`) REFERENCES `categories` (`id`) ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


-- ============================================================
-- 3. PRODUCTS CORE
-- ============================================================
DROP TABLE IF EXISTS `products_categories`;
DROP TABLE IF EXISTS `products`;

CREATE TABLE `products`
(
    `id`                CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    `name`              VARCHAR(128)                                          NOT NULL,
    `slug`              VARCHAR(255)                                          NOT NULL,
    `thumbnail`         VARCHAR(255)                                          NULL     DEFAULT NULL,
    `brand_id`          CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NULL     DEFAULT NULL,
    `description`       TEXT                                                  NULL,
    `short_description` VARCHAR(512)                                          NULL,
    `keywords`          TEXT                                                  NULL,
    `status`            VARCHAR(32)                                           NOT NULL DEFAULT 'draft',
    `is_featured`       TINYINT(1)                                            NOT NULL DEFAULT 0,
    `specs_snapshot`    JSON                                                  NULL,
    `min_price`         DECIMAL(12, 2)                                        NULL     DEFAULT 0.00,
    `max_price`         DECIMAL(12, 2)                                        NULL     DEFAULT 0.00,
    `rating_avg`        DECIMAL(3, 2)                                         NOT NULL DEFAULT 0.00,
    `reviews_count`     INT UNSIGNED                                          NOT NULL DEFAULT 0,
    `release_date`      DATE                                                  NULL     DEFAULT NULL,
    `created_at`        TIMESTAMP                                             NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        TIMESTAMP                                             NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at`        TIMESTAMP                                             NULL     DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_products_slug_active` (`slug`, (IF(deleted_at IS NULL, 1, NULL))),
    KEY `idx_products_brand` (`brand_id`),
    KEY `idx_products_status` (`status`),
    FULLTEXT KEY `idx_products_fulltext` (`name`, `keywords`),
    CONSTRAINT `fk_products_brand` FOREIGN KEY (`brand_id`) REFERENCES `brands` (`id`) ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `products_categories`
(
    `product_id`  CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    `category_id` CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    `is_primary`  TINYINT(1)                                            NOT NULL DEFAULT 0,
    PRIMARY KEY (`product_id`, `category_id`),
    KEY `idx_pc_category` (`category_id`),
    CONSTRAINT `fk_pc_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_pc_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


-- ============================================================
-- 4. ATTRIBUTE SYSTEM (EAV) - REORDERED FOR DEPENDENCIES
-- ============================================================

DROP TABLE IF EXISTS product_value_swatches; -- [NEW]
DROP TABLE IF EXISTS product_attribute_definitions;
DROP TABLE IF EXISTS attribute_values;
DROP TABLE IF EXISTS `attributes`;
DROP TABLE IF EXISTS `attribute_groups`;

-- 4.1. Groups (Parent)
CREATE TABLE `attribute_groups`
(
    `id`         CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    `name`       VARCHAR(64)                                           NOT NULL,
    `code`       VARCHAR(64)                                           NOT NULL,
    `sort_order` INT                                                   NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_attr_groups_code` (`code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- 4.2. Attributes (Child of Groups)
CREATE TABLE `attributes`
(
    `id`              CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    `group_id`        CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NULL     DEFAULT NULL,
    `name`            VARCHAR(64)                                           NOT NULL,
    `code`            VARCHAR(64)                                           NOT NULL,
    `type`            VARCHAR(32)                                           NOT NULL DEFAULT 'text',
    `unit`            VARCHAR(32)                                           NULL     DEFAULT NULL,
    `is_variant_axis` TINYINT(1)                                            NOT NULL DEFAULT 0,
    `is_filterable`   TINYINT(1)                                            NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_attributes_code` (`code`),
    KEY `idx_attributes_group` (`group_id`),
    CONSTRAINT `fk_attributes_group` FOREIGN KEY (`group_id`) REFERENCES `attribute_groups` (`id`) ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- 4.3. Attribute Options (Values like Red, Blue, S, M)
CREATE TABLE attribute_values
(
    `id`           CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    `attribute_id` CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    `value`        VARCHAR(255)                                          NOT NULL,
    `visual_value` VARCHAR(255)                                          NULL     DEFAULT NULL, -- Default HEX or Icon
    `visual_type`  ENUM ('select', 'button', 'color', 'image', 'radio')  NULL,
    `sort_order`   INT                                                   NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_attr_options_attr` (`attribute_id`),
    CONSTRAINT `fk_attr_options_attr` FOREIGN KEY (`attribute_id`) REFERENCES `attributes` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- 4.4. Product Options (Mapping Product <-> Attribute + UI Type)
CREATE TABLE product_attribute_definitions
(
    `product_id`    CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    `attribute_id`  CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    -- 'select', 'button', 'color', 'image', 'radio'
    `visual_type`   ENUM ('select', 'button', 'color', 'image', 'radio')  NOT NULL DEFAULT 'select',
    `position`      INT                                                   NOT NULL DEFAULT 0,
    `is_renderable` TINYINT(1)                                            NOT NULL DEFAULT 0,
    PRIMARY KEY (`product_id`, `attribute_id`),
    CONSTRAINT `fk_po_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_po_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `attributes` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- 4.5. [NEW] Product Option Swatches (Override values per Product)
-- Bảng này cho phép Product A dùng màu #F00, Product B dùng ảnh texture.jpg cho cùng 1 option "Red"
CREATE TABLE product_value_swatches
(
    `product_id` CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    `option_id`  CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL, -- Link to attribute_options

    `type`       ENUM ('color', 'image')                               NOT NULL DEFAULT 'color',
    `value`      VARCHAR(255)                                          NOT NULL, -- Hex Code OR Image URL

    PRIMARY KEY (`product_id`, `option_id`),

    CONSTRAINT `fk_pos_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_pos_option` FOREIGN KEY (`option_id`) REFERENCES attribute_values (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


-- ============================================================
-- 5. SPECIFICATIONS (NON-VARIANT ATTRIBUTES)
-- ============================================================
DROP TABLE IF EXISTS `product_specs`;

CREATE TABLE `product_specs`
(
    `id`              CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    `product_id`      CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    `attribute_id`    CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    `value_option_id` CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NULL     DEFAULT NULL,
    `raw_value`       VARCHAR(255)                                          NULL     DEFAULT NULL,
    `unit`            VARCHAR(32)                                           NULL     DEFAULT NULL,
    `is_highlight`    TINYINT(1)                                            NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_ps_unique_attr` (`product_id`, `attribute_id`),
    KEY `idx_ps_attribute` (`attribute_id`),
    CONSTRAINT `fk_ps_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_ps_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `attributes` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_ps_option` FOREIGN KEY (`value_option_id`) REFERENCES attribute_values (`id`) ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


-- ============================================================
-- 6. VARIANTS (SKUS & PIVOT)
-- ============================================================
DROP TABLE IF EXISTS `variant_attribute_value`;
DROP TABLE IF EXISTS `product_variants`;

CREATE TABLE `product_variants`
(
    `id`             CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    `product_id`     CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    `name`           VARCHAR(128)                                          NOT NULL,
    `sku`            VARCHAR(128)                                          NOT NULL,
    `thumbnail`      VARCHAR(255)                                          NULL,
    `price`          DECIMAL(12, 2)                                        NOT NULL,
    `original_price` DECIMAL(12, 2)                                        NULL     DEFAULT NULL,
    `stock_quantity` INT                                                   NOT NULL DEFAULT 0,
    `weight_g`       INT                                                   NULL     DEFAULT NULL,
    `is_active`      TINYINT(1)                                            NOT NULL DEFAULT 1,
    `is_default`     TINYINT(1)                                            NOT NULL DEFAULT 0,
    `created_at`     TIMESTAMP                                             NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     TIMESTAMP                                             NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at`     TIMESTAMP                                             NULL     DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_variants_sku_active` (`sku`, (IF(deleted_at IS NULL, 1, NULL))),
    KEY `idx_variants_product` (`product_id`),
    CONSTRAINT `fk_variants_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `variant_attribute_value`
(
    `variant_id`      CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    `attribute_id`    CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    `value_option_id` CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    PRIMARY KEY (`variant_id`, `attribute_id`),
    KEY `idx_pva_option` (`value_option_id`),
    CONSTRAINT `fk_pva_variant` FOREIGN KEY (`variant_id`) REFERENCES `product_variants` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_pva_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `attributes` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_pva_option` FOREIGN KEY (`value_option_id`) REFERENCES attribute_values (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


-- ============================================================
-- 7. IMAGES (NORMALIZED)
-- ============================================================
DROP TABLE IF EXISTS `product_value_images`;
DROP TABLE IF EXISTS `product_images`;

CREATE TABLE `product_images`
(
    `id`           CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    `product_id`   CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    `url`          VARCHAR(500)                                          NOT NULL,
    `alt_text`     VARCHAR(255)                                          NULL     DEFAULT NULL,
    `is_thumbnail` TINYINT(1)                                            NOT NULL DEFAULT 0,
    `sort_order`   INT                                                   NOT NULL DEFAULT 0,
    `created_at`   TIMESTAMP                                             NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_images_product` (`product_id`),
    CONSTRAINT `fk_images_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `product_value_images`
(
    `attribute_value_id` CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    `image_id`           CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,

    PRIMARY KEY (`attribute_value_id`, `image_id`),

    CONSTRAINT `fk_pvi_value` FOREIGN KEY (`attribute_value_id`)
        REFERENCES attribute_values (`id`) ON DELETE CASCADE,

    CONSTRAINT `fk_pvi_image` FOREIGN KEY (`image_id`)
        REFERENCES `product_images` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;



-- ============================================================
-- 8. REVIEWS
-- ============================================================
DROP TABLE IF EXISTS `product_reviews`;

CREATE TABLE `product_reviews`
(
    `id`                   CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    `product_id`           CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    `user_id`              CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    `rating`               TINYINT UNSIGNED                                      NOT NULL,
    `comment`              TEXT                                                  NULL,
    `is_verified_purchase` TINYINT(1)                                            NOT NULL DEFAULT 0,
    `status`               VARCHAR(32)                                           NOT NULL DEFAULT 'pending',
    `created_at`           TIMESTAMP                                             NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`           TIMESTAMP                                             NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_reviews_product_status` (`product_id`, `status`),
    KEY `idx_reviews_user` (`user_id`),
    CONSTRAINT `fk_reviews_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_reviews_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `chk_rating_range` CHECK (`rating` >= 1 AND `rating` <= 5)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 9. STORED PROCEDURES
-- ============================================================
DELIMITER ;;

DROP PROCEDURE IF EXISTS `sp_recalc_brand_count`;;
CREATE PROCEDURE `sp_recalc_brand_count`(IN `p_brand_id` CHAR(26) CHARACTER SET ascii)
BEGIN
    UPDATE `brands`
    SET `products_count` = (SELECT COUNT(*) FROM `products` WHERE `brand_id` = p_brand_id AND `deleted_at` IS NULL)
    WHERE `id` = p_brand_id;
END;;

DROP PROCEDURE IF EXISTS `sp_recalc_category_count`;;
CREATE PROCEDURE `sp_recalc_category_count`(IN `p_category_id` CHAR(26) CHARACTER SET ascii)
BEGIN
    UPDATE `categories`
    SET `products_count` = (SELECT COUNT(*)
                            FROM `products_categories` pc
                                     JOIN `products` p ON pc.product_id = p.id
                            WHERE pc.category_id = p_category_id
                              AND p.deleted_at IS NULL)
    WHERE `id` = p_category_id;
END;;

DROP PROCEDURE IF EXISTS `sp_recalc_product_rating`;;
CREATE PROCEDURE `sp_recalc_product_rating`(IN `p_product_id` CHAR(26) CHARACTER SET ascii)
BEGIN
    UPDATE `products`
    SET `reviews_count` = (SELECT COUNT(*)
                           FROM `product_reviews`
                           WHERE `product_id` = p_product_id
                             AND `status` = 'approved'),
        `rating_avg`    = (SELECT COALESCE(AVG(`rating`), 0)
                           FROM `product_reviews`
                           WHERE `product_id` = p_product_id
                             AND `status` = 'approved')
    WHERE `id` = p_product_id;
END;;

DROP PROCEDURE IF EXISTS `sp_recalc_product_snapshot`;;
CREATE PROCEDURE `sp_recalc_product_snapshot`(IN `p_product_id` CHAR(26) CHARACTER SET ascii)
BEGIN
    UPDATE `products`
    SET `specs_snapshot` = (SELECT JSON_OBJECTAGG(attr.name, TRIM(CONCAT_WS(' ', COALESCE(opt.value, ps.raw_value),
                                                                            NULLIF(ps.unit, ''))))
                            FROM `product_specs` ps
                                     JOIN `attributes` attr ON ps.attribute_id = attr.id
                                     LEFT JOIN attribute_values opt ON ps.value_option_id = opt.id
                            WHERE ps.product_id = p_product_id
                              AND ps.is_highlight = 1)
    WHERE `id` = p_product_id;
END;;

DELIMITER ;

-- ============================================================
-- 12. ORDERS SYSTEM (OPTIMIZED)
-- ============================================================
DROP TABLE IF EXISTS `order_history`;
DROP TABLE IF EXISTS `order_items`;
DROP TABLE IF EXISTS `order_shipping`;
DROP TABLE IF EXISTS `orders`;

-- 1. Bảng Orders: Quản lý trạng thái, tài chính và đối soát
CREATE TABLE `orders` (
                          `id`                CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
                          `code`              VARCHAR(32) NOT NULL, -- VD: #ORD-2401-X92A
                          `user_id`           CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NULL,

    -- Trạng thái đơn hàng
                          `status`            ENUM('pending', 'confirmed', 'processing', 'shipping', 'delivered', 'cancelled', 'returned', 'refunded') NOT NULL DEFAULT 'pending',
                          `cancel_reason`     VARCHAR(255) NULL, -- Lưu lý do hủy đơn (VD: Khách đổi ý, Hết hàng)

    -- Thanh toán & Đối soát
                          `payment_method`    VARCHAR(50) NOT NULL, -- 'cod', 'banking', 'vnpay', 'momo'
                          `payment_status`    ENUM('unpaid', 'paid', 'refunded', 'partial_refund') NOT NULL DEFAULT 'unpaid',
                          `transaction_ref`   VARCHAR(100) NULL, -- Mã giao dịch từ cổng thanh toán (VD: VNPAY_123456)
                          `paid_at`           TIMESTAMP NULL,    -- Thời điểm thanh toán thành công

    -- Tài chính
                          `currency`          CHAR(3) NOT NULL DEFAULT 'VND',
                          `sub_total`         DECIMAL(15, 2) NOT NULL DEFAULT 0, -- Tổng tiền hàng
                          `shipping_fee`      DECIMAL(15, 2) NOT NULL DEFAULT 0,
                          `discount_amount`   DECIMAL(15, 2) NOT NULL DEFAULT 0,
                          `coupon_code`       VARCHAR(50) NULL, -- Mã giảm giá đã dùng (để tracking marketing)
                          `grand_total`       DECIMAL(15, 2) NOT NULL DEFAULT 0, -- = Sub + Ship - Discount

    -- Thông tin bổ sung
                          `note`              TEXT NULL, -- Ghi chú khách hàng
                          `ip_address`        VARCHAR(45) NULL, -- IP đặt hàng (chống spam/fraud)
                          `user_agent`        VARCHAR(255) NULL, -- Thiết bị đặt hàng

                          `created_at`        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          `updated_at`        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                          PRIMARY KEY (`id`),
                          UNIQUE KEY `idx_orders_code` (`code`),
                          KEY `idx_orders_user` (`user_id`),
                          KEY `idx_orders_status` (`status`),
                          KEY `idx_orders_created` (`created_at`), -- Index cho báo cáo doanh thu theo ngày
                          KEY `idx_orders_transaction` (`transaction_ref`), -- Tìm kiếm nhanh theo mã giao dịch
                          CONSTRAINT `fk_orders_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Bảng Shipping: Tách biệt để quản lý Logistics
CREATE TABLE `order_shipping` (
                                  `id`                CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
                                  `order_id`          CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,

    -- Thông tin người nhận
                                  `contact_name`      VARCHAR(100) NOT NULL,
                                  `contact_phone`     VARCHAR(20) NOT NULL,
                                  `contact_email`     VARCHAR(255) NULL,

    -- Địa chỉ (Snapshot text để in phiếu giao hàng nhanh)
                                  `address_detail`    VARCHAR(255) NOT NULL,
                                  `ward`              VARCHAR(100) NOT NULL,
                                  `district`          VARCHAR(100) NOT NULL,
                                  `province`          VARCHAR(100) NOT NULL,
                                  `full_address`      TEXT NOT NULL, -- Ghép sẵn để hiển thị: "144 Xuân Thủy, Cầu Giấy..."

    -- Mã định danh địa lý (để tính phí ship lại nếu cần)
                                  `province_code`     VARCHAR(20) NULL,
                                  `district_code`     VARCHAR(20) NULL,
                                  `ward_code`         VARCHAR(20) NULL,

    -- Tracking vận chuyển (Logistics)
                                  `carrier_name`      VARCHAR(50) NULL, -- VD: GHTK, GHN, ViettelPost
                                  `tracking_code`     VARCHAR(50) NULL, -- Mã vận đơn
                                  `estimated_delivery_date` DATE NULL,
                                  `shipping_note`     VARCHAR(255) NULL, -- Ghi chú cho shipper (VD: Gọi trước khi giao)

                                  PRIMARY KEY (`id`),
                                  UNIQUE KEY `idx_os_order` (`order_id`),
                                  KEY `idx_os_tracking` (`tracking_code`),
                                  CONSTRAINT `fk_os_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Bảng Order Items: Lưu vết sản phẩm tại thời điểm mua
CREATE TABLE `order_items` (
                               `id`                CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
                               `order_id`          CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
                               `product_id`        CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NULL,
                               `variant_id`        CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NULL,

    -- Snapshot thông tin sản phẩm (Quan trọng khi SP bị xóa/sửa sau này)
                               `product_name`      VARCHAR(255) NOT NULL,
                               `sku`               VARCHAR(128) NOT NULL,
                               `thumbnail`         VARCHAR(255) NULL,

    -- Snapshot thuộc tính biến thể (Quan trọng)
    -- VD: {"Color": "Titanium", "Storage": "256GB", "Ram": "8GB"}
                               `variant_snapshot`  JSON NULL,

                               `quantity`          INT NOT NULL DEFAULT 1,
                               `price`             DECIMAL(15, 2) NOT NULL, -- Giá bán tại thời điểm mua
                               `total_price`       DECIMAL(15, 2) NOT NULL, -- = quantity * price (đã trừ discount dòng nếu có)

                               PRIMARY KEY (`id`),
                               KEY `idx_order_items_order` (`order_id`),
                               CONSTRAINT `fk_oi_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
                               CONSTRAINT `fk_oi_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Bảng History: Audit log
CREATE TABLE `order_history` (
                                 `id`            CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
                                 `order_id`      CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,

                                 `action`        VARCHAR(50) NOT NULL, -- create, payment_success, ship, cancel
                                 `prev_status`   VARCHAR(50) NULL,
                                 `new_status`    VARCHAR(50) NULL,
                                 `note`          VARCHAR(255) NULL, -- VD: "Hệ thống tự động hủy do quá hạn thanh toán"

                                 `created_by`    CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NULL, -- NULL = System/Customer, ID = Admin/Staff
                                 `created_at`    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                 PRIMARY KEY (`id`),
                                 KEY `idx_oh_order` (`order_id`),
                                 CONSTRAINT `fk_oh_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
