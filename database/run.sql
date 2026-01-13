ALTER TABLE `products`
    ADD COLUMN `thumbnail` VARCHAR(255) NULL DEFAULT NULL AFTER `slug`;

UPDATE `products`
SET `thumbnail` = 'https://res.cloudinary.com/ddojgtm2r/image/upload/v1753723110/thumbnail.jpg'
WHERE `id` IN ('01JEQF1X1A1B2C3D4E5F6G7H8J', '01JEQF1X2B2C3D4E5F6G7H8J9K');

ALTER TABLE `products`
    ADD COLUMN `rating`  DECIMAL(3, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN `reviews` INT UNSIGNED  NOT NULL DEFAULT 0;

UPDATE `products` p
SET `specs_snapshot` = (SELECT JSON_OBJECTAGG(attr.name, COALESCE(opt.value, ps.raw_value))
                        FROM `product_specs` ps
                                 JOIN `attributes` attr ON ps.attribute_id = attr.id
                                 LEFT JOIN `attribute_options` opt ON ps.value_option_id = opt.id
                        WHERE ps.product_id = p.id
                          AND ps.is_highlight = 1 -- Chỉ lấy thông số nổi bật
                        GROUP BY ps.product_id);


# chua them va db
INSERT INTO `attributes` (`id`, `group_id`, `name`, `code`, `type`, `unit`, `is_variant_axis`, `is_filterable`)
VALUES ('01JEQB2K8H8J9K0L1M2N3P4Q5S', '01JEQC1G3C3D4E5F6G7H8J9K0L', 'Chipset', 'chipset', 'text', NULL, 0, 1);
INSERT INTO `product_specs` (`id`, `product_id`, `attribute_id`, `value_option_id`, `raw_value`, `unit`, `is_highlight`)
VALUES
-- iPhone 15 Pro Max
('01JEQG1Y8H8J9K0L1M2N3P4Q5T', '01JEQF1X1A1B2C3D4E5F6G7H8J', '01JEQB2K8H8J9K0L1M2N3P4Q5S', NULL, 'Apple A17 Pro', NULL,
 1),

-- Samsung S24 Ultra
('01JEQG1Y9J9K0L1M2N3P4Q5U6V', '01JEQF1X2B2C3D4E5F6G7H8J9K', '01JEQB2K8H8J9K0L1M2N3P4Q5S', NULL,
 'Snapdragon 8 Gen 3 for Galaxy', NULL, 1);
INSERT INTO `attribute_groups` (`id`, `name`, `code`, `sort_order`)
VALUES ('01JEQC1G4D4E5F6G7H8J9K0L1M', 'Camera', 'camera', 4);
INSERT INTO `attributes` (`id`, `group_id`, `name`, `code`, `type`, `unit`, `is_variant_axis`, `is_filterable`)
VALUES
-- Nhóm Hiệu năng (Performance)
('01JEQB2K9J0K1L2M3N4P5Q6R7S', '01JEQC1G3C3D4E5F6G7H8J9K0L', 'Dung lượng Pin', 'battery_capacity', 'int', 'mAh', 0, 1),

-- Nhóm Chung (General)
('01JEQB2KA0K1L2M3N4P5Q6R7S8', '01JEQC1G1A1B2C3D4E5F6G7H8J', 'Hệ điều hành', 'os', 'text', NULL, 0, 1),
('01JEQB2KB1L2M3N4P5Q6R7S8T9', '01JEQC1G1A1B2C3D4E5F6G7H8J', 'Trọng lượng', 'weight', 'int', 'g', 0, 0),

-- Nhóm Camera (New Group)
('01JEQB2KC2M3N4P5Q6R7S8T9U0', '01JEQC1G4D4E5F6G7H8J9K0L1M', 'Camera sau', 'main_camera', 'text', 'MP', 0, 0),
('01JEQB2KD3N4P5Q6R7S8T9U0V1', '01JEQC1G4D4E5F6G7H8J9K0L1M', 'Camera trước', 'front_camera', 'text', 'MP', 0, 0);
INSERT INTO `product_specs` (`id`, `product_id`, `attribute_id`, `raw_value`, `unit`, `is_highlight`)
VALUES
-- ==========================================
-- 🍎 iPhone 15 Pro Max
-- ==========================================
('01JEQG1YA0B1C2D3E4F5G6H7J8', '01JEQF1X1A1B2C3D4E5F6G7H8J', '01JEQB2K9J0K1L2M3N4P5Q6R7S', '4441', 'mAh', 1),      -- Pin
('01JEQG1YB1C2D3E4F5G6H7J8K9', '01JEQF1X1A1B2C3D4E5F6G7H8J', '01JEQB2KA0K1L2M3N4P5Q6R7S8', 'iOS 17', NULL, 0),     -- OS
('01JEQG1YC2D3E4F5G6H7J8K9L0', '01JEQF1X1A1B2C3D4E5F6G7H8J', '01JEQB2KC2M3N4P5Q6R7S8T9U0', '48MP + 12MP + 12MP', NULL,
 1),                                                                                                               -- Cam sau
('01JEQG1YD3E4F5G6H7J8K9L0M1', '01JEQF1X1A1B2C3D4E5F6G7H8J', '01JEQB2KD3N4P5Q6R7S8T9U0V1', '12', 'MP', 0),         -- Cam trước
('01JEQG1YE4F5G6H7J8K9L0M1N2', '01JEQF1X1A1B2C3D4E5F6G7H8J', '01JEQB2KB1L2M3N4P5Q6R7S8T9', '221', 'g',
 0),                                                                                                               -- Trọng lượng

-- ==========================================
-- 🤖 Samsung Galaxy S24 Ultra
-- ==========================================
('01JEQG1YF5G6H7J8K9L0M1N2P3', '01JEQF1X2B2C3D4E5F6G7H8J9K', '01JEQB2K9J0K1L2M3N4P5Q6R7S', '5000', 'mAh', 1),      -- Pin
('01JEQG1YG6H7J8K9L0M1N2P3Q4', '01JEQF1X2B2C3D4E5F6G7H8J9K', '01JEQB2KA0K1L2M3N4P5Q6R7S8', 'Android 14', NULL, 0), -- OS
('01JEQG1YH7J8K9L0M1N2P3Q4R5', '01JEQF1X2B2C3D4E5F6G7H8J9K', '01JEQB2KC2M3N4P5Q6R7S8T9U0', '200MP + 50MP + 12MP + 10MP',
 NULL, 1),                                                                                                         -- Cam sau
('01JEQG1YJ8K9L0M1N2P3Q4R5S6', '01JEQF1X2B2C3D4E5F6G7H8J9K', '01JEQB2KD3N4P5Q6R7S8T9U0V1', '12', 'MP', 0),         -- Cam trước
('01JEQG1YK9L0M1N2P3Q4R5S6T7', '01JEQF1X2B2C3D4E5F6G7H8J9K', '01JEQB2KB1L2M3N4P5Q6R7S8T9', '232', 'g', 0);
-- Trọng lượng

-- Tạo User mới
INSERT INTO `users` (`id`, `email`, `password_hash`, `full_name`, `is_active`, `verified_at`)
VALUES ('01JEQP9X1A1B2C3D4E5F6G7H8J', 'khachhang@example.com',
        '$2a$10$EwX9xJlUgdWaKNbFrz/atOhzUwBtIqFQusuhBoyS/PuMD.HVB4smy', 'Nguyễn Văn A', 1, CURRENT_TIMESTAMP);


INSERT INTO `product_reviews` (`id`, `product_id`, `user_id`, `rating`, `comment`, `is_verified_purchase`, `status`)
VALUES
-- ==========================================
-- 🍎 iPhone 15 Pro Max (ID: ...G7H8J)
-- ==========================================
-- Review 1: 5 Sao (User Nguyễn Văn A)
('01JEQR1Y1A1B2C3D4E5F6G7H8J', '01JEQF1X1A1B2C3D4E5F6G7H8J', '01JEQP9X1A1B2C3D4E5F6G7H8J', 5,
 'Màu Titan tự nhiên bên ngoài đẹp hơn trong ảnh nhiều. Cầm rất nhẹ.', 1, 'approved'),

-- Review 2: 4 Sao (User Admin)
('01JEQR1Y2B2C3D4E5F6G7H8J9K', '01JEQF1X1A1B2C3D4E5F6G7H8J', '01KBY8QMDZ4609C11CBNJ694R8', 4,
 'Máy mạnh nhưng khi chơi game nặng vẫn hơi ấm nhẹ.', 1, 'approved'),

-- Review 3: 5 Sao (User Nguyễn Văn A)
('01JEQR1Y3C3D4E5F6G7H8J9K0L', '01JEQF1X1A1B2C3D4E5F6G7H8J', '01JEQP9X1A1B2C3D4E5F6G7H8J', 5,
 'Giao hàng nhanh, đóng gói kỹ. Camera chụp đêm đỉnh.', 1, 'approved'),


-- ==========================================
-- 🤖 Samsung S24 Ultra (ID: ...J9K)
-- ==========================================
-- Review 4: 5 Sao (User Nguyễn Văn A)
('01JEQR1Y4D4E5F6G7H8J9K0L1M', '01JEQF1X2B2C3D4E5F6G7H8J9K', '01JEQP9X1A1B2C3D4E5F6G7H8J', 5,
 'Tính năng AI dịch trực tiếp quá tiện lợi cho công việc.', 1, 'approved'),

-- Review 5: 3 Sao (User Admin)
('01JEQR1Y5E5F6G7H8J9K0L1M2N', '01JEQF1X2B2C3D4E5F6G7H8J9K', '01KBY8QMDZ4609C11CBNJ694R8', 3,
 'Máy hơi to và cấn tay khi cầm lâu. Màn hình phẳng dán cường lực dễ hơn đời trước.', 1, 'approved');


SELECT p.id                AS p_id,
       p.name              AS p_name,
       p.slug              AS p_slug,
       p.thumbnail         AS p_thumbnail,
       p.brand_id          AS p_brand_id,
       p.description       AS p_description,
       p.short_description AS p_short_description,
       p.status            AS p_status,
       p.is_featured       AS p_is_featured,
       p.specs_snapshot    AS p_specs_snapshot,
       p.min_price         AS p_min_price,
       p.max_price         AS p_max_price,
       p.rating            AS p_rating,
       p.reviews           AS p_reviews,
       p.created_at        AS p_created_at,
       p.updated_at        AS p_updated_at,
       b.id                AS b_id,
       b.name              AS b_name,
       b.slug              AS b_slug,
       b.logo              AS b_logo
FROM products p
         LEFT JOIN brands b ON p.brand_id = b.id






-- Thêm cột number_of_products nếu chưa có
-- Lưu ý: Nếu cột đã tồn tại, lệnh này sẽ báo lỗi (Duplicate column name).
ALTER TABLE `categories`
    ADD COLUMN `number_of_products` INT UNSIGNED NOT NULL DEFAULT 0
        COMMENT 'Cache: Số lượng sản phẩm'
        AFTER `is_active`;
-- ============================================================
-- Trigger 1: Khi THÊM sản phẩm vào danh mục -> Tăng +1
-- ============================================================
CREATE TRIGGER `trg_category_count_insert`
    AFTER INSERT ON `products_categories`
    FOR EACH ROW
BEGIN
    UPDATE `categories`
    SET `number_of_products` = `number_of_products` + 1
    WHERE `id` = NEW.category_id;
END;;

-- ============================================================
-- Trigger 2: Khi XÓA sản phẩm khỏi danh mục -> Giảm -1
-- ============================================================
CREATE TRIGGER `trg_category_count_delete`
    AFTER DELETE ON `products_categories`
    FOR EACH ROW
BEGIN
    UPDATE `categories`
    SET `number_of_products` = GREATEST(0, `number_of_products` - 1) -- Đảm bảo không bị âm
    WHERE `id` = OLD.category_id;
END;;

-- ============================================================
-- Trigger 3: Khi CHUYỂN sản phẩm sang danh mục khác
-- ============================================================
CREATE TRIGGER `trg_category_count_update`
    AFTER UPDATE ON `products_categories`
    FOR EACH ROW
BEGIN
    -- Nếu category_id thay đổi
    IF OLD.category_id != NEW.category_id THEN
        -- Giảm ở danh mục cũ
        UPDATE `categories`
        SET `number_of_products` = GREATEST(0, `number_of_products` - 1)
        WHERE `id` = OLD.category_id;

        -- Tăng ở danh mục mới
        UPDATE `categories`
        SET `number_of_products` = `number_of_products` + 1
        WHERE `id` = NEW.category_id;
    END IF;
END;;

DELIMITER ;



UPDATE `categories` c
SET `number_of_products` = (
    SELECT COUNT(*)
    FROM `products_categories` pc
    WHERE pc.category_id = c.id
);
ALTER TABLE `categories`
    ADD COLUMN `sort_order` INT NOT NULL DEFAULT 0
        COMMENT 'Thứ tự hiển thị (Số nhỏ xếp trước, VD: 0, 1, 2...)'
        AFTER `parent_id`;

CREATE INDEX `idx_categories_parent_sort` ON `categories` (`parent_id`, `sort_order`);


ALTER TABLE `categories`
    ADD COLUMN `sort_order` INT NOT NULL DEFAULT 0
        COMMENT 'Thứ tự hiển thị (Số nhỏ xếp trước, VD: 0, 1, 2...)'
        AFTER `parent_id`;

CREATE INDEX `idx_categories_parent_sort` ON `categories` (`parent_id`, `sort_order`);