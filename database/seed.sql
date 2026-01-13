-- ============================================================
-- 0. PREPARE TABLE STRUCTURE
-- ============================================================
-- Thêm cột release_date nếu chưa có
-- ALTER TABLE `products` ADD COLUMN `release_date` DATE NULL AFTER `status`;

-- ============================================================
-- 1. AUTHENTICATION (ROLES & USERS)
-- ============================================================

-- 1.1. Roles
INSERT INTO `roles` (`id`, `created_at`, `updated_at`, `name`, `code`, `deleted_at`)
VALUES ('01KCX7P5BFSG5RRSQC0QK1BDMY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Administrator', 'ROLE_ADMIN', NULL),
       ('01KCX7P5BVZV3N2VTMHNYRPWNG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Customer', 'ROLE_CUSTOMER', NULL);

-- 1.2. Users
-- Password: password123
INSERT INTO `users` (`id`, `created_at`, `updated_at`, `email`, `password_hash`, `full_name`, `is_active`, `deleted_at`,
                     `verified_at`)
VALUES ('01KCX7P5BV6K3N7CWCVWQZMH7F', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin@example.com',
        '$2a$10$EwX9xJlUgdWaKNbFrz/atOhzUwBtIqFQusuhBoyS/PuMD.HVB4smy', 'Admin User', TRUE, NULL, CURRENT_TIMESTAMP),
       ('01KCX7P5BVX9646SZWMSZWT51R', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'khachhang@example.com',
        '$2a$10$EwX9xJlUgdWaKNbFrz/atOhzUwBtIqFQusuhBoyS/PuMD.HVB4smy', 'Nguyễn Văn A', TRUE, NULL, CURRENT_TIMESTAMP);

-- 1.3. Assign Role
INSERT INTO `users_roles` (`role_id`, `user_id`)
VALUES ('01KCX7P5BFSG5RRSQC0QK1BDMY', '01KCX7P5BV6K3N7CWCVWQZMH7F');


-- ============================================================
-- 2. CATALOG (BRANDS & CATEGORIES)
-- ============================================================

-- 2.1. Brands
INSERT INTO `brands` (`id`, `name`, `slug`, `logo`, `is_active`)
VALUES ('01KCX7P5BVM7FYB5MTAQBFD771', 'Apple', 'apple', 'https://res.cloudinary.com/demo/image/upload/apple_logo.png',
        1),
       ('01KCX7P5BV5W7FAW3BY61BXJZ3', 'Samsung', 'samsung',
        'https://res.cloudinary.com/demo/image/upload/samsung_logo.png', 1),
       ('01KCX7P5BVFHA1EGY8GB0THTRJ', 'Xiaomi', 'xiaomi',
        'https://res.cloudinary.com/demo/image/upload/xiaomi_logo.png', 1),
       ('01KCX7P5BV51VHXGKF2YSS7Y0Y', 'Sony', 'sony', 'https://res.cloudinary.com/demo/image/upload/sony_logo.png', 1);

-- 2.2. Categories
INSERT INTO `categories` (`id`, `name`, `slug`, `icon`, `parent_id`)
VALUES
-- Parent
('01KCX7P5BVV62CW3V1XCGCQ7X3', 'Điện tử & Công nghệ', 'dien-tu-cong-nghe', NULL, NULL),
('01KCX7P5BV2XWYPADZZXAK88KD', 'Thời trang', 'thoi-trang', NULL, NULL),
-- Children
('01KCX7P5BVEPE3KG1SRKV5XFVP', 'Điện thoại & Phụ kiện', 'dien-thoai-phu-kien', NULL, '01KCX7P5BVV62CW3V1XCGCQ7X3'),
('01KCX7P5BV94FCF73XNC9BC1NA', 'Máy tính & Laptop', 'may-tinh-laptop', NULL, '01KCX7P5BVV62CW3V1XCGCQ7X3'),
('01KCX7P5BV8SNJVHQRFAD16VE2', 'Thời trang Nam', 'thoi-trang-nam', NULL, '01KCX7P5BV2XWYPADZZXAK88KD');


-- ============================================================
-- 3. ATTRIBUTES (GROUPS, ATTRIBUTES, OPTIONS)
-- ============================================================

-- 3.1. Groups
INSERT INTO `attribute_groups` (`id`, `name`, `code`, `sort_order`)
VALUES ('01KCX7P5BV8J0DQGPNCA5X0YQN', 'Thông tin chung', 'general', 1),
       ('01KCX7P5BVBE3V90Z6PEKXDNJJ', 'Màn hình & Hiển thị', 'display', 2),
       ('01KCX7P5BV0AS5JP6YWXJXATR3', 'Cấu hình & Hiệu năng', 'performance', 3),
       ('01KCX7P5BVN95WMYQZCAKC7QWX', 'Camera', 'camera', 4);

-- 3.2. Attributes
INSERT INTO `attributes` (`id`, `group_id`, `name`, `code`, `type`, `unit`, `is_variant_axis`, `is_filterable`)
VALUES
-- Axis = 1 (Variants)
('01KCX7P5BVBYZCF5AMSYCJZ2Q0', '01KCX7P5BV8J0DQGPNCA5X0YQN', 'Màu sắc', 'color', 'select', NULL, 1, 1),
('01KCX7P5BVW2B4QZYSFMJ8RWJM', '01KCX7P5BV0AS5JP6YWXJXATR3', 'Bộ nhớ trong', 'storage_rom', 'select', 'GB', 1, 1),
-- Axis = 0 (Specs)
('01KCX7P5BVBX2Z9X9C64GTY65C', '01KCX7P5BV8J0DQGPNCA5X0YQN', 'Chất liệu', 'material', 'text', NULL, 0, 1),
('01KCX7P5BVJXABATPD5HMATG8P', '01KCX7P5BV8J0DQGPNCA5X0YQN', 'Bảo hành', 'warranty', 'int', 'Tháng', 0, 1),
('01KCX7P5BV2BV8AST177G3682X', '01KCX7P5BV8J0DQGPNCA5X0YQN', 'Hệ điều hành', 'os', 'text', NULL, 0, 1),
('01KCX7P5BV8PGKT5TEZS9FWNBP', '01KCX7P5BV8J0DQGPNCA5X0YQN', 'Trọng lượng', 'weight', 'int', 'g', 0, 0),
('01KCX7P5BV6DSSK9BZEJ4AYB76', '01KCX7P5BVBE3V90Z6PEKXDNJJ', 'Kích thước màn hình', 'screen_size', 'decimal', 'inch', 0,
 1),
('01KCX7P5BVAX4GHVJNTHJNQHAS', '01KCX7P5BV0AS5JP6YWXJXATR3', 'RAM', 'memory_ram', 'int', 'GB', 0, 1),
('01KCX7P5BV726X3E1JYS41QSWZ', '01KCX7P5BV0AS5JP6YWXJXATR3', 'Chipset', 'chipset', 'text', NULL, 0, 1),
('01KCX7P5BVDNSHX5981D8ZXDXD', '01KCX7P5BV0AS5JP6YWXJXATR3', 'Dung lượng Pin', 'battery_capacity', 'int', 'mAh', 0, 1),
('01KCX7P5BVNN4ZXG216MQ9G8DE', '01KCX7P5BVN95WMYQZCAKC7QWX', 'Camera sau', 'main_camera', 'text', 'MP', 0, 0),
('01KCX7P5BVFPFZFR5VN81S6H38', '01KCX7P5BVN95WMYQZCAKC7QWX', 'Camera trước', 'front_camera', 'text', 'MP', 0, 0);

-- 3.3. Options
INSERT INTO `attribute_values` (`id`, `attribute_id`, `value`, `visual_value`, `sort_order`)
VALUES
-- Màu cũ
('01KCX7P5BV1DMBHVD4GHXRS3WE', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', 'Đen Titan', '#000000', 1),
('01KCX7P5BVMHRVNKEX1W9AQRH6', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', 'Trắng Titan', '#FFFFFF', 2),
('01KCX7P5BV2R02J8RNAJQHHX24', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', 'Xanh Titan', '#0000FF', 3),
('01KCX7P5BVNCW0SEX7DRPX11Y3', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', 'Xám Titan', '#808080', 4),
('01KCX7P5BV9RZPNVF329K4BAGW', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', 'Tím Titan', '#800080', 5),
-- Màu mới (Macbook/Sony/Xiaomi)
('01KCX7P5BVBH3234YMB226VHVW', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', 'Bạc (Silver)', '#C0C0C0', 8),
('01KCX7P5BVCFY43EF886JXFR99', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', 'Kem (Cream)', '#FFFDD0', 9),
('01KCX7P5BV5PXSF2Q6A8FDD1FG', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', 'Xám Không Gian', '#505050', 10),
-- Bộ nhớ
('01KCX7P5BVZFVJ615ER41WTYJ2', '01KCX7P5BVW2B4QZYSFMJ8RWJM', '256', NULL, 1),
('01KCX7P5BVD494NEAENTERFHJ9', '01KCX7P5BVW2B4QZYSFMJ8RWJM', '512', NULL, 2),
('01KCX7P5BV78FRNAFX1YJRE2RA', '01KCX7P5BVW2B4QZYSFMJ8RWJM', '1TB', NULL, 3);


-- ============================================================
-- 4. PRODUCTS CORE (ALL 7 PRODUCTS)
-- ============================================================
-- Đã thêm cột `release_date`

INSERT INTO `products` (`id`, `name`, `slug`, `thumbnail`, `brand_id`, `status`, `min_price`, `max_price`,
                        `short_description`, `release_date`)
VALUES
-- 1. iPhone 15 Pro Max
('01KCX7P5BVRQWX561MMZS4XXED', 'iPhone 15 Pro Max', 'iphone-15-pro-max',
 'https://cdn2.fptshop.com.vn/unsafe/750x0/filters:format(webp):quality(75)/2023_9_20_638307992305419305_iphone-15-promax-xanh-vang-1.jpg',
 '01KCX7P5BVM7FYB5MTAQBFD771', 'active', 29990000, 45990000, 'Thiết kế Titan chuẩn hàng không vũ trụ.', '2023-09-22'),

-- 2. Samsung S24 Ultra
('01KCX7P5BVFV1VN8Q10WJWFS5G', 'Samsung Galaxy S24 Ultra', 'samsung-galaxy-s24-ultra',
 'https://cdn2.fptshop.com.vn/unsafe/750x0/filters:format(webp):quality(75)/2024_1_15_638409395341919374_samsung-galaxy-s24-ultra-den-1.png',
 '01KCX7P5BV5W7FAW3BY61BXJZ3', 'active', 26990000, 38990000, 'Quyền năng Galaxy AI đỉnh cao.', '2024-01-24'),

-- 3. MacBook Pro 14 M3
('01KCX7P5BVNBSEWH74NHWYN6PY', 'MacBook Pro 14 inch M3', 'macbook-pro-14-m3',
 'https://res.cloudinary.com/ddojgtm2r/image/upload/v1753723110/thumbnail.jpg',
 '01KCX7P5BVM7FYB5MTAQBFD771', 'active', 39990000, 45990000, 'Chip M3 mạnh mẽ, màn hình Liquid Retina XDR.',
 '2023-11-07'),

-- 4. Sony WH-1000XM5
('01KCX7P5BV7GC3ZV90GRDAGKPV', 'Sony WH-1000XM5 Wireless', 'sony-wh-1000xm5',
 'https://res.cloudinary.com/ddojgtm2r/image/upload/v1753723110/thumbnail.jpg',
 '01KCX7P5BV51VHXGKF2YSS7Y0Y', 'active', 6490000, 6990000, 'Chống ồn chủ động hàng đầu thế giới.', '2022-05-20'),

-- 5. Samsung Z Fold6
('01KCX7P5BVXDTZ2TEFJ889SM80', 'Samsung Galaxy Z Fold6', 'samsung-z-fold-6',
 'https://cdn2.fptshop.com.vn/unsafe/750x0/filters:format(webp):quality(75)/samsung_galaxy_z_fold6_gray_a413f785af.png',
 '01KCX7P5BV5W7FAW3BY61BXJZ3', 'active', 41990000, 45990000, 'Quyền năng AI trên màn hình gập cực đại.', '2024-07-10'),

-- 6. iPad Pro M4
('01KCX7P5BV41VPJ6H8QKNM41NW', 'iPad Pro 11 inch M4', 'ipad-pro-m4-11',
 'https://res.cloudinary.com/ddojgtm2r/image/upload/v1753723110/thumbnail.jpg',
 '01KCX7P5BVM7FYB5MTAQBFD771', 'active', 28990000, 34990000, 'Thiết kế siêu mỏng, màn hình OLED Tandem.', '2024-05-15'),

-- 7. Xiaomi Book 14
('01KCX7P5BVS5DXAC105C0W1A1S', 'Xiaomi Book 14 2024', 'xiaomi-book-14',
 'https://res.cloudinary.com/ddojgtm2r/image/upload/v1753723110/thumbnail.jpg',
 '01KCX7P5BVFHA1EGY8GB0THTRJ', 'active', 18990000, 21990000, 'Hiệu năng vượt trội trong tầm giá.', '2024-02-22');

-- Link Categories
INSERT INTO `products_categories` (`product_id`, `category_id`, `is_primary`)
VALUES ('01KCX7P5BVRQWX561MMZS4XXED', '01KCX7P5BVEPE3KG1SRKV5XFVP', 1), -- iPhone -> Điện thoại
       ('01KCX7P5BVFV1VN8Q10WJWFS5G', '01KCX7P5BVEPE3KG1SRKV5XFVP', 1), -- Samsung S24 -> Điện thoại
       ('01KCX7P5BVNBSEWH74NHWYN6PY', '01KCX7P5BV94FCF73XNC9BC1NA', 1), -- MacBook -> Laptop
       ('01KCX7P5BV7GC3ZV90GRDAGKPV', '01KCX7P5BVEPE3KG1SRKV5XFVP', 1), -- Sony -> Phụ kiện
       ('01KCX7P5BVXDTZ2TEFJ889SM80', '01KCX7P5BVEPE3KG1SRKV5XFVP', 1), -- Fold6 -> Điện thoại
       ('01KCX7P5BV41VPJ6H8QKNM41NW', '01KCX7P5BVV62CW3V1XCGCQ7X3', 1), -- iPad -> Điện tử
       ('01KCX7P5BVS5DXAC105C0W1A1S', '01KCX7P5BV94FCF73XNC9BC1NA', 1);
-- Xiaomi Book -> Laptop


-- ============================================================
-- 5. PRODUCT SPECS (ALL PRODUCTS)
-- ============================================================

INSERT INTO `product_specs` (`id`, `product_id`, `attribute_id`, `value_option_id`, `raw_value`, `unit`, `is_highlight`)
VALUES
-- --- iPhone 15 Pro Max ---
('01KCX7P5BVVQQQV7WA10F4867D', '01KCX7P5BVRQWX561MMZS4XXED', '01KCX7P5BVAX4GHVJNTHJNQHAS', NULL, '8', 'GB', 1),
('01KCX7P5BVBRBPZCQFYGRY1G28', '01KCX7P5BVRQWX561MMZS4XXED', '01KCX7P5BV6DSSK9BZEJ4AYB76', NULL, '6.7', 'inch', 1),
('01KCX7P5BV4M4HVKK696P935J7', '01KCX7P5BVRQWX561MMZS4XXED', '01KCX7P5BVBX2Z9X9C64GTY65C', NULL, 'Titanium Grade 5',
 NULL, 0),
('01KCX7P5BVXD6PVZMAS06W6QFP', '01KCX7P5BVRQWX561MMZS4XXED', '01KCX7P5BV726X3E1JYS41QSWZ', NULL, 'Apple A17 Pro', NULL,
 1),
('01KCX7P5BVZJ8ZEW7035BT6TKG', '01KCX7P5BVRQWX561MMZS4XXED', '01KCX7P5BVDNSHX5981D8ZXDXD', NULL, '4441', 'mAh', 1),
('01KCX7P5BV4WY9AJE1C02JP8N3', '01KCX7P5BVRQWX561MMZS4XXED', '01KCX7P5BVNN4ZXG216MQ9G8DE', NULL, '48MP + 12MP + 12MP',
 NULL, 1),

-- --- Samsung S24 Ultra ---
('01KCX7P5BVPG43XBRTXGPYH7XE', '01KCX7P5BVFV1VN8Q10WJWFS5G', '01KCX7P5BVAX4GHVJNTHJNQHAS', NULL, '12', 'GB', 1),
('01KCX7P5BVERJPQ30XPTAA0PEW', '01KCX7P5BVFV1VN8Q10WJWFS5G', '01KCX7P5BV6DSSK9BZEJ4AYB76', NULL, '6.8', 'inch', 1),
('01KCX7P5BVTESXPNNFAZMDRQ8M', '01KCX7P5BVFV1VN8Q10WJWFS5G', '01KCX7P5BV726X3E1JYS41QSWZ', NULL, 'Snapdragon 8 Gen 3',
 NULL, 1),
('01KCX7P5BVD28YZH7G8T8MBVJ3', '01KCX7P5BVFV1VN8Q10WJWFS5G', '01KCX7P5BVDNSHX5981D8ZXDXD', NULL, '5000', 'mAh', 1),
('01KCX7P5BV86FWZNY0GS8NXGMS', '01KCX7P5BVFV1VN8Q10WJWFS5G', '01KCX7P5BVNN4ZXG216MQ9G8DE', NULL, '200MP + 50MP + 12MP',
 NULL, 1),

-- --- MacBook Pro 14 M3 ---
('01KCX7P5BVX8BZQZA7N7YBK4TS', '01KCX7P5BVNBSEWH74NHWYN6PY', '01KCX7P5BV726X3E1JYS41QSWZ', NULL,
 'Apple M3 (8-core CPU)', NULL, 1),
('01KCX7P5BVG9JY0H39G15HENTJ', '01KCX7P5BVNBSEWH74NHWYN6PY', '01KCX7P5BVAX4GHVJNTHJNQHAS', NULL, '8', 'GB', 1),
('01KCX7P5BVJJ3BSMZ6KPTJRGBZ', '01KCX7P5BVNBSEWH74NHWYN6PY', '01KCX7P5BV6DSSK9BZEJ4AYB76', NULL, '14.2', 'inch', 1),
('01KCX7P5BV5AVF1Z837X8PDZNJ', '01KCX7P5BVNBSEWH74NHWYN6PY', '01KCX7P5BVDNSHX5981D8ZXDXD', NULL, '70Wh (22h sử dụng)',
 NULL, 1),

-- --- Sony WH-1000XM5 ---
('01KCX7P5BV4BGM86GTMSQMAQ30', '01KCX7P5BV7GC3ZV90GRDAGKPV', '01KCX7P5BVDNSHX5981D8ZXDXD', NULL, '30 giờ (NC On)', NULL,
 1),
('01KCX7P5BVP6Z873ESRW861Q1Z', '01KCX7P5BV7GC3ZV90GRDAGKPV', '01KCX7P5BV726X3E1JYS41QSWZ', NULL,
 'Processor V1 & HD QN1', NULL, 1),
('01KCX7P5BV6DDCB3X63YWHWHBK', '01KCX7P5BV7GC3ZV90GRDAGKPV', '01KCX7P5BVJXABATPD5HMATG8P', NULL, '12', 'Tháng', 1),

-- --- Samsung Z Fold6 ---
('01KCX7P5BVDZ3KX9MYPX5H0KN3', '01KCX7P5BVXDTZ2TEFJ889SM80', '01KCX7P5BVAX4GHVJNTHJNQHAS', NULL, '12', 'GB', 1),
('01KCX7P5BVQQYVBNSWGNJJD0QD', '01KCX7P5BVXDTZ2TEFJ889SM80', '01KCX7P5BV6DSSK9BZEJ4AYB76', NULL, '7.6', 'inch', 1),
('01KCX7P5BV954NH8CTVVW4PP0X', '01KCX7P5BVXDTZ2TEFJ889SM80', '01KCX7P5BV726X3E1JYS41QSWZ', NULL, 'Snapdragon 8 Gen 3',
 NULL, 1),
('01KCX7P5BVGYX2PJPSNQQF91D6', '01KCX7P5BVXDTZ2TEFJ889SM80', '01KCX7P5BVNN4ZXG216MQ9G8DE', NULL, '50MP + 12MP + 10MP',
 NULL, 1),

-- --- iPad Pro M4 ---
('01KCX7P5BVQ0BHES6CKF8GASCQ', '01KCX7P5BV41VPJ6H8QKNM41NW', '01KCX7P5BV726X3E1JYS41QSWZ', NULL, 'Apple M4 (9-core)',
 NULL, 1),
('01KCX7P5BWM2N668ET4EVZE9EP', '01KCX7P5BV41VPJ6H8QKNM41NW', '01KCX7P5BV6DSSK9BZEJ4AYB76', NULL, '11', 'inch', 1),
('01KCX7P5BW6PXGQ3F5ZSX3AJ4E', '01KCX7P5BV41VPJ6H8QKNM41NW', '01KCX7P5BV8PGKT5TEZS9FWNBP', NULL, '444', 'g', 1),

-- --- Xiaomi Book 14 ---
('01KCX7P5BWF3C5VAC69C2AJT84', '01KCX7P5BVS5DXAC105C0W1A1S', '01KCX7P5BVAX4GHVJNTHJNQHAS', NULL, '16', 'GB', 1),
('01KCX7P5BWV2QSMX8312PEN2RS', '01KCX7P5BVS5DXAC105C0W1A1S', '01KCX7P5BV726X3E1JYS41QSWZ', NULL, 'Intel Core i5-13500H',
 NULL, 1),
('01KCX7P5BWQQWQ7VY0DHG0R19P', '01KCX7P5BVS5DXAC105C0W1A1S', '01KCX7P5BV6DSSK9BZEJ4AYB76', NULL, '14', 'inch', 1),
('01KCX7P5BWAB7N1RJG4X9F8VX5', '01KCX7P5BVS5DXAC105C0W1A1S', '01KCX7P5BVDNSHX5981D8ZXDXD', NULL, '56Wh', NULL, 1);


-- ============================================================
-- 6. PRODUCT VARIANTS (SKU)
-- ============================================================

INSERT INTO `product_variants` (`id`, `product_id`, `name`, `sku`, `price`, `original_price`, `stock_quantity`,
                                `is_default`)
VALUES
-- iPhone
('01KCX7P5BW8DW0KWSSSSA60MND', '01KCX7P5BVRQWX561MMZS4XXED', 'iPhone 15 Pro Max - Đen - 256GB', 'IP15PM-BLK-256',
 29990000, 34990000, 50, 1),
('01KCX7P5BW3CA3RSVTPA3MXG2Q', '01KCX7P5BVRQWX561MMZS4XXED', 'iPhone 15 Pro Max - Trắng - 512GB', 'IP15PM-WHT-512',
 35990000, 40990000, 20, 0),
('01KCX7P5BW0N2PWPJFTP0HFJVT', '01KCX7P5BVRQWX561MMZS4XXED', 'iPhone 15 Pro Max - Xanh - 1TB', 'IP15PM-BLU-1TB',
 44990000, 48990000, 10, 0),
-- Samsung
('01KCX7P5BWEJW7E7W6MAA5J1KS', '01KCX7P5BVFV1VN8Q10WJWFS5G', 'Samsung S24 Ultra - Xám - 256GB', 'S24U-GRY-256',
 26990000, 33990000, 100, 1),
('01KCX7P5BWYZTWWVB6VXH82ENR', '01KCX7P5BVFV1VN8Q10WJWFS5G', 'Samsung S24 Ultra - Tím - 512GB', 'S24U-PUR-512',
 30990000, 37990000, 45, 0),
-- MacBook
('01KCX7P5BW01M4YHQH57V566Y7', '01KCX7P5BVNBSEWH74NHWYN6PY', 'MacBook Pro M3 - Xám - 512GB', 'MBP14-GRY-512', 39990000,
 41990000, 20, 1),
('01KCX7P5BWMHN0AE39PZCPX22M', '01KCX7P5BVNBSEWH74NHWYN6PY', 'MacBook Pro M3 - Bạc - 512GB', 'MBP14-SLV-512', 39990000,
 41990000, 10, 0),
-- Sony
('01KCX7P5BWVGR905BR6N93FZ8F', '01KCX7P5BV7GC3ZV90GRDAGKPV', 'Sony XM5 - Đen', 'XM5-BLK', 6490000, 6990000, 50, 1),
('01KCX7P5BW5NE4JGBX5J783KZV', '01KCX7P5BV7GC3ZV90GRDAGKPV', 'Sony XM5 - Kem', 'XM5-CRM', 6490000, 6990000, 30, 0),
-- Fold6
('01KCX7P5BWKA22CEWSPRB4MS0B', '01KCX7P5BVXDTZ2TEFJ889SM80', 'Z Fold6 - Xanh - 256GB', 'ZFOLD6-BLU-256', 41990000,
 45990000, 15, 1),
-- iPad
('01KCX7P5BW3YXAG0C8FH3B7N4F', '01KCX7P5BV41VPJ6H8QKNM41NW', 'iPad Pro M4 - Bạc - 256GB', 'IPADM4-SLV-256', 28990000,
 30990000, 25, 1),
-- Xiaomi
('01KCX7P5BWDP7C53DXTCCFNVV9', '01KCX7P5BVS5DXAC105C0W1A1S', 'Xiaomi Book - Xám - 512GB', 'MIBOOK-GRY-512', 18990000,
 21990000, 40, 1);


-- ============================================================
-- 7. VARIANT ATTRIBUTES (PIVOT)
-- ============================================================

INSERT INTO variant_attribute_value (`variant_id`, `attribute_id`, `value_option_id`)
VALUES
-- iPhone
('01KCX7P5BW8DW0KWSSSSA60MND', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', '01KCX7P5BV1DMBHVD4GHXRS3WE'),
('01KCX7P5BW8DW0KWSSSSA60MND', '01KCX7P5BVW2B4QZYSFMJ8RWJM', '01KCX7P5BVZFVJ615ER41WTYJ2'),
('01KCX7P5BW3CA3RSVTPA3MXG2Q', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', '01KCX7P5BVMHRVNKEX1W9AQRH6'),
('01KCX7P5BW3CA3RSVTPA3MXG2Q', '01KCX7P5BVW2B4QZYSFMJ8RWJM', '01KCX7P5BVD494NEAENTERFHJ9'),
('01KCX7P5BW0N2PWPJFTP0HFJVT', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', '01KCX7P5BV2R02J8RNAJQHHX24'),
('01KCX7P5BW0N2PWPJFTP0HFJVT', '01KCX7P5BVW2B4QZYSFMJ8RWJM', '01KCX7P5BV78FRNAFX1YJRE2RA'),
-- Samsung
('01KCX7P5BWEJW7E7W6MAA5J1KS', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', '01KCX7P5BVNCW0SEX7DRPX11Y3'),
('01KCX7P5BWEJW7E7W6MAA5J1KS', '01KCX7P5BVW2B4QZYSFMJ8RWJM', '01KCX7P5BVZFVJ615ER41WTYJ2'),
('01KCX7P5BWYZTWWVB6VXH82ENR', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', '01KCX7P5BV9RZPNVF329K4BAGW'),
('01KCX7P5BWYZTWWVB6VXH82ENR', '01KCX7P5BVW2B4QZYSFMJ8RWJM', '01KCX7P5BVD494NEAENTERFHJ9'),
-- MacBook
('01KCX7P5BW01M4YHQH57V566Y7', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', '01KCX7P5BV5PXSF2Q6A8FDD1FG'),
('01KCX7P5BW01M4YHQH57V566Y7', '01KCX7P5BVW2B4QZYSFMJ8RWJM', '01KCX7P5BVD494NEAENTERFHJ9'),
('01KCX7P5BWMHN0AE39PZCPX22M', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', '01KCX7P5BVBH3234YMB226VHVW'),
('01KCX7P5BWMHN0AE39PZCPX22M', '01KCX7P5BVW2B4QZYSFMJ8RWJM', '01KCX7P5BVD494NEAENTERFHJ9'),
-- Sony
('01KCX7P5BWVGR905BR6N93FZ8F', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', '01KCX7P5BV1DMBHVD4GHXRS3WE'),
('01KCX7P5BW5NE4JGBX5J783KZV', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', '01KCX7P5BVCFY43EF886JXFR99'),
-- Fold6
('01KCX7P5BWKA22CEWSPRB4MS0B', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', '01KCX7P5BV2R02J8RNAJQHHX24'),
('01KCX7P5BWKA22CEWSPRB4MS0B', '01KCX7P5BVW2B4QZYSFMJ8RWJM', '01KCX7P5BVZFVJ615ER41WTYJ2'),
-- iPad
('01KCX7P5BW3YXAG0C8FH3B7N4F', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', '01KCX7P5BVBH3234YMB226VHVW'),
('01KCX7P5BW3YXAG0C8FH3B7N4F', '01KCX7P5BVW2B4QZYSFMJ8RWJM', '01KCX7P5BVZFVJ615ER41WTYJ2'),
-- Xiaomi
('01KCX7P5BWDP7C53DXTCCFNVV9', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', '01KCX7P5BV5PXSF2Q6A8FDD1FG'),
('01KCX7P5BWDP7C53DXTCCFNVV9', '01KCX7P5BVW2B4QZYSFMJ8RWJM', '01KCX7P5BVD494NEAENTERFHJ9');


-- ============================================================
-- 8. REVIEWS
-- ============================================================

INSERT INTO `product_reviews` (`id`, `product_id`, `user_id`, `rating`, `comment`, `is_verified_purchase`, `status`)
VALUES ('01KCX7P5BWSHTC0A9TGTN7DV08', '01KCX7P5BVRQWX561MMZS4XXED', '01KCX7P5BV6K3N7CWCVWQZMH7F', 5,
        'Tuyệt vời, giao hàng nhanh!', 1, 'approved'),
       ('01KCX7P5BWS879MCR1GTAJ7X7A', '01KCX7P5BVRQWX561MMZS4XXED', '01KCX7P5BV6K3N7CWCVWQZMH7F', 4,
        'Sản phẩm tốt nhưng hộp hơi móp.', 1, 'approved');
-- ============================================================
-- 1. INSERT NEW BRANDS (Thương hiệu mới)
-- ============================================================
-- Logo dùng chung link ảnh thumbnail theo yêu cầu
INSERT INTO `brands` (`id`, `name`, `slug`, `logo`, `is_active`)
VALUES ('01KCX7P5BWK8VA76E9KP5XZ31C', 'Google', 'google',
        'https://res.cloudinary.com/ddojgtm2r/image/upload/v1753723110/thumbnail.jpg', 1),
       ('01KCX7P5BWP9T21FZZ6FWNVXG9', 'Dell', 'dell',
        'https://res.cloudinary.com/ddojgtm2r/image/upload/v1753723110/thumbnail.jpg', 1),
       ('01KCX7P5BWVK9F3QF8B8ZAFSCS', 'Asus', 'asus',
        'https://res.cloudinary.com/ddojgtm2r/image/upload/v1753723110/thumbnail.jpg', 1),
       ('01KCX7P5BWYB5VZ5Y0P4QKZF4S', 'Logitech', 'logitech',
        'https://res.cloudinary.com/ddojgtm2r/image/upload/v1753723110/thumbnail.jpg', 1);

-- ============================================================
-- 2. INSERT NEW OPTIONS (Màu sắc mới)
-- ============================================================
INSERT INTO `attribute_values` (`id`, `attribute_id`, `value`, `visual_value`, `sort_order`)
VALUES ('01KCX7P5BW9JZ1B7RYZCR459EM', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', 'Hồng (Rose)', '#FFC0CB', 11),
       ('01KCX7P5BWH1B6AHE52JFZG0CW', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', 'Xanh Mint', '#98FF98', 12),
       ('01KCX7P5BW27V7T7RH424MM7GW', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', 'Titan Tự Nhiên', '#B3B3B1', 13);

-- ============================================================
-- 3. INSERT 5 NEW PRODUCTS
-- ============================================================
-- Thumbnail dùng chung link ảnh theo yêu cầu
INSERT INTO `products` (`id`, `name`, `slug`, `thumbnail`, `brand_id`, `status`, `min_price`, `max_price`,
                        `short_description`, `release_date`)
VALUES
-- 8. Google Pixel 9 Pro XL
('01KCX7P5BW9MD9ZJSBB54ADDX1', 'Google Pixel 9 Pro XL', 'google-pixel-9-pro-xl',
 'https://res.cloudinary.com/ddojgtm2r/image/upload/v1753723110/thumbnail.jpg',
 '01KCX7P5BWK8VA76E9KP5XZ31C', 'active', 28990000, 32990000, 'Camera AI dẫn đầu, chip Tensor G4.', '2024-08-13'),

-- 9. Dell XPS 13 9340
('01KCX7P5BWZRZ154P84N4F9RCS', 'Dell XPS 13 9340 (2024)', 'dell-xps-13-9340',
 'https://res.cloudinary.com/ddojgtm2r/image/upload/v1753723110/thumbnail.jpg',
 '01KCX7P5BWP9T21FZZ6FWNVXG9', 'active', 42990000, 48990000, 'Thiết kế tương lai, chip Intel Core Ultra.',
 '2024-02-20'),

-- 10. Apple Watch Ultra 2
('01KCX7P5BW90A27D7Y6F9Y56EH', 'Apple Watch Ultra 2', 'apple-watch-ultra-2',
 'https://res.cloudinary.com/ddojgtm2r/image/upload/v1753723110/thumbnail.jpg',
 '01KCX7P5BVM7FYB5MTAQBFD771', 'active', 19990000, 21990000, 'Vỏ Titan, màn hình sáng nhất lịch sử Apple.',
 '2023-09-22'),

-- 11. Logitech MX Master 3S
('01KCX7P5BW28PHS81NGCGKJ4XF', 'Logitech MX Master 3S', 'logitech-mx-master-3s',
 'https://res.cloudinary.com/ddojgtm2r/image/upload/v1753723110/thumbnail.jpg',
 '01KCX7P5BWYB5VZ5Y0P4QKZF4S', 'active', 2490000, 2690000, 'Chuột công thái học tốt nhất cho coder.', '2022-05-24'),

-- 12. Asus ROG Zephyrus G14
('01KCX7P5BW7R735EBD7WAFT3NP', 'Asus ROG Zephyrus G14', 'asus-rog-zephyrus-g14',
 'https://res.cloudinary.com/ddojgtm2r/image/upload/v1753723110/thumbnail.jpg',
 '01KCX7P5BWVK9F3QF8B8ZAFSCS', 'active', 49990000, 55990000, 'Laptop gaming mỏng nhẹ, màn hình OLED.', '2024-01-09');

-- ============================================================
-- 4. LINK CATEGORIES
-- ============================================================
INSERT INTO `products_categories` (`product_id`, `category_id`, `is_primary`)
VALUES ('01KCX7P5BW9MD9ZJSBB54ADDX1', '01KCX7P5BVEPE3KG1SRKV5XFVP', 1), -- Pixel -> Điện thoại
       ('01KCX7P5BWZRZ154P84N4F9RCS', '01KCX7P5BV94FCF73XNC9BC1NA', 1), -- Dell -> Laptop
       ('01KCX7P5BW90A27D7Y6F9Y56EH', '01KCX7P5BVEPE3KG1SRKV5XFVP', 1), -- Watch -> Phụ kiện
       ('01KCX7P5BW28PHS81NGCGKJ4XF', '01KCX7P5BVEPE3KG1SRKV5XFVP', 1), -- Mouse -> Phụ kiện
       ('01KCX7P5BW7R735EBD7WAFT3NP', '01KCX7P5BV94FCF73XNC9BC1NA', 1);
-- Asus -> Laptop

-- ============================================================
-- 5. PRODUCT SPECS (Mỗi sản phẩm 3 Specs)
-- ============================================================
INSERT INTO `product_specs` (`id`, `product_id`, `attribute_id`, `raw_value`, `unit`, `is_highlight`)
VALUES

-- Google Pixel 9 Pro XL (3 Specs)
('01KCX7P5BW08FHECJQNG4A80XV', '01KCX7P5BW9MD9ZJSBB54ADDX1', '01KCX7P5BV726X3E1JYS41QSWZ', 'Google Tensor G4', NULL,
 1),                                                                                                            -- Chip
('01KCX7P5BWVW7ZAWZGCPMJ0JA7', '01KCX7P5BW9MD9ZJSBB54ADDX1', '01KCX7P5BVAX4GHVJNTHJNQHAS', '16', 'GB', 1),      -- RAM
('01KCX7P5BW33W0P4C37V62ZH9T', '01KCX7P5BW9MD9ZJSBB54ADDX1', '01KCX7P5BVDNSHX5981D8ZXDXD', '5060', 'mAh', 1),   -- Pin

-- Dell XPS 13 (3 Specs)
('01KCX7P5BW8EJXYTBSK3K95DRS', '01KCX7P5BWZRZ154P84N4F9RCS', '01KCX7P5BV726X3E1JYS41QSWZ', 'Intel Core Ultra 7 155H',
 NULL, 1),                                                                                                      -- Chip
('01KCX7P5BWJQQ05QHX28Y5KMMF', '01KCX7P5BWZRZ154P84N4F9RCS', '01KCX7P5BV6DSSK9BZEJ4AYB76', '13.4', 'inch', 1),  -- Screen
('01KCX7P5BWEZYXERDH2NZ5XZA7', '01KCX7P5BWZRZ154P84N4F9RCS', '01KCX7P5BV8PGKT5TEZS9FWNBP', '1190', 'g', 1),     -- Weight

-- Apple Watch Ultra 2 (3 Specs)
('01KCX7P5BWHQQQPCMGF2JQHJWF', '01KCX7P5BW90A27D7Y6F9Y56EH', '01KCX7P5BVBX2Z9X9C64GTY65C', 'Titanium Grade 5', NULL,
 1),                                                                                                            -- Material
('01KCX7P5BWT2Y6B8RBAM6HP5KT', '01KCX7P5BW90A27D7Y6F9Y56EH', '01KCX7P5BVDNSHX5981D8ZXDXD', '36 giờ', NULL, 1),  -- Pin
('01KCX7P5BWVHQJDYDW8YPWNHVW', '01KCX7P5BW90A27D7Y6F9Y56EH', '01KCX7P5BV6DSSK9BZEJ4AYB76', '49mm', 'case', 1),  -- Size

-- Logitech MX Master 3S (3 Specs)
('01KCX7P5BWSCD0Q34Z9DA3Y3NF', '01KCX7P5BW28PHS81NGCGKJ4XF', '01KCX7P5BVDNSHX5981D8ZXDXD', '70 ngày', NULL, 1), -- Pin
('01KCX7P5BWAX4X43C2CR0ZK21A', '01KCX7P5BW28PHS81NGCGKJ4XF', '01KCX7P5BV8PGKT5TEZS9FWNBP', '141', 'g', 1),      -- Weight
('01KCX7P5BW30NWGTDYBK4CK81W', '01KCX7P5BW28PHS81NGCGKJ4XF', '01KCX7P5BV726X3E1JYS41QSWZ', 'Cảm biến Darkfield 8K',
 NULL, 1),                                                                                                      -- Sensor

-- Asus ROG Zephyrus G14 (3 Specs)
('01KCX7P5BW7KFE7EA6EEH80BKR', '01KCX7P5BW7R735EBD7WAFT3NP', '01KCX7P5BV726X3E1JYS41QSWZ', 'AMD Ryzen 9 8945HS', NULL,
 1),                                                                                                            -- Chip
('01KCX7P5BWKKWQ3WTE25C8TJ9T', '01KCX7P5BW7R735EBD7WAFT3NP', '01KCX7P5BVAX4GHVJNTHJNQHAS', '32', 'GB', 1),      -- RAM
('01KCX7P5BWQGAMSCR44K85F44S', '01KCX7P5BW7R735EBD7WAFT3NP', '01KCX7P5BV6DSSK9BZEJ4AYB76', '14', 'inch', 1);
-- Screen

-- ============================================================
-- 6. VARIANTS
-- ============================================================
INSERT INTO `product_variants` (`id`, `product_id`, `name`, `sku`, `price`, `original_price`, `stock_quantity`,
                                `is_default`)
VALUES
-- Pixel
('01KCX7P5BW2JBP2824CSCXNWDK', '01KCX7P5BW9MD9ZJSBB54ADDX1', 'Pixel 9 Pro XL - Hồng - 256GB', 'PXL9-PNK-256', 28990000,
 30990000, 10, 1),
-- Dell
('01KCX7P5BWEGX52D2TCFFX352F', '01KCX7P5BWZRZ154P84N4F9RCS', 'Dell XPS 13 - Bạc - 512GB', 'XPS13-SLV-512', 42990000,
 45990000, 20, 1),
-- Watch
('01KCX7P5BWJNZ28V7CCQW6Q56D', '01KCX7P5BW90A27D7Y6F9Y56EH', 'Apple Watch Ultra 2 - Titan', 'AWU2-TI-GPS', 19990000,
 21990000, 50, 1),
-- Mouse
('01KCX7P5BW22W7ZTGZ56E4DEEE', '01KCX7P5BW28PHS81NGCGKJ4XF', 'MX Master 3S - Đen', 'MX3S-BLK', 2490000, 2690000, 100,
 1),
-- Asus
('01KCX7P5BW47TGEP52P5VT8NR2', '01KCX7P5BW7R735EBD7WAFT3NP', 'ROG G14 - Trắng - 1TB', 'G14-WHT-1TB', 49990000, 55990000,
 15, 1);

-- ============================================================
-- 7. VARIANT ATTRIBUTES
-- ============================================================
INSERT INTO variant_attribute_value (`variant_id`, `attribute_id`, `value_option_id`)
VALUES
-- Pixel Hồng
('01KCX7P5BW2JBP2824CSCXNWDK', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', '01KCX7P5BW9JZ1B7RYZCR459EM'),
('01KCX7P5BW2JBP2824CSCXNWDK', '01KCX7P5BVW2B4QZYSFMJ8RWJM', '01KCX7P5BVZFVJ615ER41WTYJ2'),
-- Dell Bạc
('01KCX7P5BWEGX52D2TCFFX352F', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', '01KCX7P5BVBH3234YMB226VHVW'),
('01KCX7P5BWEGX52D2TCFFX352F', '01KCX7P5BVW2B4QZYSFMJ8RWJM', '01KCX7P5BVD494NEAENTERFHJ9'),
-- Watch Titan
('01KCX7P5BWJNZ28V7CCQW6Q56D', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', '01KCX7P5BW27V7T7RH424MM7GW'),
-- Mouse Đen
('01KCX7P5BW22W7ZTGZ56E4DEEE', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', '01KCX7P5BV1DMBHVD4GHXRS3WE'),
-- Asus Trắng
('01KCX7P5BW47TGEP52P5VT8NR2', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', '01KCX7P5BVMHRVNKEX1W9AQRH6'),
('01KCX7P5BW47TGEP52P5VT8NR2', '01KCX7P5BVW2B4QZYSFMJ8RWJM', '01KCX7P5BV78FRNAFX1YJRE2RA');



-- ============================================================
-- 1. INSERT 5 PRODUCTS (Sản phẩm mới từ Brand cũ)
-- ============================================================

INSERT INTO `products` (`id`, `name`, `slug`, `thumbnail`, `brand_id`, `status`, `min_price`, `max_price`,
                        `short_description`, `release_date`)
VALUES

-- 13. Xiaomi 14 Ultra (Brand: Xiaomi)
('01KCX7P5BWRNK8NQ7P1Z1F96YN', 'Xiaomi 14 Ultra', 'xiaomi-14-ultra',
 'https://res.cloudinary.com/ddojgtm2r/image/upload/v1753723110/thumbnail.jpg',
 '01KCX7P5BVFHA1EGY8GB0THTRJ', 'active', 29990000, 32990000, 'Đỉnh cao nhiếp ảnh Leica huyền thoại.', '2024-02-22'),

-- 14. Samsung Galaxy Tab S9 (Brand: Samsung)
('01KCX7P5BWCV5M6MXMB0WM5SW6', 'Samsung Galaxy Tab S9 WiFi', 'samsung-galaxy-tab-s9',
 'https://res.cloudinary.com/ddojgtm2r/image/upload/v1753723110/thumbnail.jpg',
 '01KCX7P5BV5W7FAW3BY61BXJZ3', 'active', 19990000, 22990000, 'Máy tính bảng Android tốt nhất thế giới.', '2023-07-26'),

-- 15. Sony Alpha A7 IV (Brand: Sony)
('01KCX7P5BWXASWD4EQTHDNRC7Z', 'Sony Alpha A7 IV Body', 'sony-alpha-a7-iv',
 'https://res.cloudinary.com/ddojgtm2r/image/upload/v1753723110/thumbnail.jpg',
 '01KCX7P5BV51VHXGKF2YSS7Y0Y', 'active', 59990000, 62990000, 'Chuẩn mực mới cho máy ảnh Hybrid.', '2021-10-21'),

-- 16. Dell XPS 15 9530 (Brand: Dell)
('01KCX7P5BW836FBRYNBC10HT8G', 'Dell XPS 15 9530 (2023)', 'dell-xps-15-9530',
 'https://res.cloudinary.com/ddojgtm2r/image/upload/v1753723110/thumbnail.jpg',
 '01KCX7P5BWP9T21FZZ6FWNVXG9', 'active', 45990000, 52990000, 'Hiệu năng mạnh mẽ trong thân hình siêu mẫu.',
 '2023-05-10'),

-- 17. Asus ROG Phone 8 (Brand: Asus)
('01KCX7P5BW7CGQA5SCJS35KQRD', 'Asus ROG Phone 8', 'asus-rog-phone-8',
 'https://res.cloudinary.com/ddojgtm2r/image/upload/v1753723110/thumbnail.jpg',
 '01KCX7P5BWVK9F3QF8B8ZAFSCS', 'active', 24990000, 28990000, 'Gaming phone thiết kế tối giản, hiệu năng tối đa.',
 '2024-01-08');


-- ============================================================
-- 2. LINK CATEGORIES
-- ============================================================
INSERT INTO `products_categories` (`product_id`, `category_id`, `is_primary`)
VALUES ('01KCX7P5BWRNK8NQ7P1Z1F96YN', '01KCX7P5BVEPE3KG1SRKV5XFVP', 1), -- Xiaomi -> Điện thoại
       ('01KCX7P5BWCV5M6MXMB0WM5SW6', '01KCX7P5BVV62CW3V1XCGCQ7X3', 1), -- Tab S9 -> Điện tử (Tablet)
       ('01KCX7P5BWXASWD4EQTHDNRC7Z', '01KCX7P5BVV62CW3V1XCGCQ7X3', 1), -- Sony Cam -> Điện tử
       ('01KCX7P5BW836FBRYNBC10HT8G', '01KCX7P5BV94FCF73XNC9BC1NA', 1), -- Dell -> Laptop
       ('01KCX7P5BW7CGQA5SCJS35KQRD', '01KCX7P5BVEPE3KG1SRKV5XFVP', 1);
-- ROG Phone -> Điện thoại


-- ============================================================
-- 3. PRODUCT SPECS (3 Highlight Specs/Product)
-- ============================================================
-- Lưu ý: Trigger mới sẽ tự động nối raw_value + unit (VD: "5000" + "mAh" = "5000 mAh")

INSERT INTO `product_specs` (`id`, `product_id`, `attribute_id`, `raw_value`, `unit`, `is_highlight`)
VALUES

-- Xiaomi 14 Ultra
('01KCX7P5BWMP3N2QKYVW2SEPEX', '01KCX7P5BWRNK8NQ7P1Z1F96YN', '01KCX7P5BVNN4ZXG216MQ9G8DE', '50MP Leica Quad-Cam', NULL,
 1),                                                                                                             -- Cam
('01KCX7P5BW0AK7F3499FBQEQ5B', '01KCX7P5BWRNK8NQ7P1Z1F96YN', '01KCX7P5BV726X3E1JYS41QSWZ', 'Snapdragon 8 Gen 3', NULL,
 1),                                                                                                             -- Chip
('01KCX7P5BW0NTP4HJR4VR2GE3V', '01KCX7P5BWRNK8NQ7P1Z1F96YN', '01KCX7P5BVDNSHX5981D8ZXDXD', '5000', 'mAh', 1),    -- Pin

-- Samsung Tab S9
('01KCX7P5BW1A1C9KC1NRG2P5BS', '01KCX7P5BWCV5M6MXMB0WM5SW6', '01KCX7P5BV6DSSK9BZEJ4AYB76', '11', 'inch', 1),     -- Screen
('01KCX7P5BW1N9F0ZS1DJA8VT9J', '01KCX7P5BWCV5M6MXMB0WM5SW6', '01KCX7P5BV726X3E1JYS41QSWZ', 'Snapdragon 8 Gen 2', NULL,
 1),                                                                                                             -- Chip
('01KCX7P5BWSYHYAMWZVTV0HJDY', '01KCX7P5BWCV5M6MXMB0WM5SW6', '01KCX7P5BVDNSHX5981D8ZXDXD', '8400', 'mAh', 1),    -- Pin

-- Sony Alpha A7 IV
('01KCX7P5BWPNQ6N287YEGYD3GW', '01KCX7P5BWXASWD4EQTHDNRC7Z', '01KCX7P5BVNN4ZXG216MQ9G8DE', '33MP Full-frame', NULL,
 1),                                                                                                             -- Cam (Sensor)
('01KCX7P5BWN091HYEQFW33246F', '01KCX7P5BWXASWD4EQTHDNRC7Z', '01KCX7P5BV726X3E1JYS41QSWZ', 'BIONZ XR', NULL, 1), -- Chip
('01KCX7P5BWQ1SJ17JXPFPAZJZW', '01KCX7P5BWXASWD4EQTHDNRC7Z', '01KCX7P5BV8PGKT5TEZS9FWNBP', '658', 'g', 1),       -- Weight

-- Dell XPS 15
('01KCX7P5BWPWFPKDBNZRP59Q9H', '01KCX7P5BW836FBRYNBC10HT8G', '01KCX7P5BV726X3E1JYS41QSWZ', 'Intel Core i7-13700H', NULL,
 1),                                                                                                             -- Chip
('01KCX7P5BWGDYKVSY7JWTXPG6E', '01KCX7P5BW836FBRYNBC10HT8G', '01KCX7P5BVAX4GHVJNTHJNQHAS', '16', 'GB', 1),       -- RAM
('01KCX7P5BWS4GW5RMW8GY5DQ5X', '01KCX7P5BW836FBRYNBC10HT8G', '01KCX7P5BV6DSSK9BZEJ4AYB76', '15.6', 'inch', 1),   -- Screen

-- Asus ROG Phone 8
('01KCX7P5BW2MTJ1GXZYFCY3AS8', '01KCX7P5BW7CGQA5SCJS35KQRD', '01KCX7P5BV726X3E1JYS41QSWZ', 'Snapdragon 8 Gen 3', NULL,
 1),                                                                                                             -- Chip
('01KCX7P5BW9V0R2V70H38EPQ8H', '01KCX7P5BW7CGQA5SCJS35KQRD', '01KCX7P5BVAX4GHVJNTHJNQHAS', '16', 'GB', 1),       -- RAM
('01KCX7P5BWVMPM8GC7G1E73R0F', '01KCX7P5BW7CGQA5SCJS35KQRD', '01KCX7P5BV6DSSK9BZEJ4AYB76', '6.78', 'inch', 1);
-- Screen


-- ============================================================
-- 4. VARIANTS
-- ============================================================
INSERT INTO `product_variants` (`id`, `product_id`, `name`, `sku`, `price`, `original_price`, `stock_quantity`,
                                `is_default`)
VALUES
-- Xiaomi
('01KCX7P5BW7XJYVRZFV4Y7EA01', '01KCX7P5BWRNK8NQ7P1Z1F96YN', 'Xiaomi 14 Ultra - Đen - 512GB', 'MI14U-BLK-512', 29990000,
 32990000, 25, 1),
-- Tab S9
('01KCX7P5BWGWN9MBZ95JY8R6Q5', '01KCX7P5BWCV5M6MXMB0WM5SW6', 'Tab S9 - Xám - 128GB', 'TABS9-GRY-128', 19990000,
 21990000, 30, 1),
-- Sony
('01KCX7P5BWDYAB6337VCDGRPK9', '01KCX7P5BWXASWD4EQTHDNRC7Z', 'Sony A7 IV - Đen', 'A7M4-BODY', 59990000, 62990000, 10,
 1),
-- Dell
('01KCX7P5BWE91YG0CPR2G8B15Y', '01KCX7P5BW836FBRYNBC10HT8G', 'Dell XPS 15 - Bạc - 512GB', 'XPS15-SLV-512', 45990000,
 48990000, 15, 1),
-- ROG
('01KCX7P5BW6ABACH617PDAVHRH', '01KCX7P5BW7CGQA5SCJS35KQRD', 'ROG Phone 8 - Đen - 256GB', 'ROG8-BLK-256', 24990000,
 26990000, 40, 1);


-- ============================================================
-- 5. VARIANT ATTRIBUTES
-- ============================================================
INSERT INTO variant_attribute_value (`variant_id`, `attribute_id`, `value_option_id`)
VALUES
-- Xiaomi Đen + 512GB
('01KCX7P5BW7XJYVRZFV4Y7EA01', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', '01KCX7P5BV1DMBHVD4GHXRS3WE'), -- Đen
('01KCX7P5BW7XJYVRZFV4Y7EA01', '01KCX7P5BVW2B4QZYSFMJ8RWJM', '01KCX7P5BVD494NEAENTERFHJ9'), -- 512GB

-- Tab S9 Xám + 256GB (Dùng tạm ID 256GB cũ)
('01KCX7P5BWGWN9MBZ95JY8R6Q5', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', '01KCX7P5BV5PXSF2Q6A8FDD1FG'), -- Xám KG
('01KCX7P5BWGWN9MBZ95JY8R6Q5', '01KCX7P5BVW2B4QZYSFMJ8RWJM', '01KCX7P5BVZFVJ615ER41WTYJ2'), -- 256GB (Map tạm cho 128)

-- Sony Đen
('01KCX7P5BWDYAB6337VCDGRPK9', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', '01KCX7P5BV1DMBHVD4GHXRS3WE'), -- Đen

-- Dell Bạc + 512GB
('01KCX7P5BWE91YG0CPR2G8B15Y', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', '01KCX7P5BVBH3234YMB226VHVW'), -- Bạc
('01KCX7P5BWE91YG0CPR2G8B15Y', '01KCX7P5BVW2B4QZYSFMJ8RWJM', '01KCX7P5BVD494NEAENTERFHJ9'), -- 512GB

-- ROG Đen + 256GB
('01KCX7P5BW6ABACH617PDAVHRH', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', '01KCX7P5BV1DMBHVD4GHXRS3WE'), -- Đen
('01KCX7P5BW6ABACH617PDAVHRH', '01KCX7P5BVW2B4QZYSFMJ8RWJM', '01KCX7P5BVZFVJ615ER41WTYJ2');
-- 256GB


-- ============================================================
-- 8. PRODUCT OPTIONS CONFIGURATION (CẤU HÌNH UI)
-- ============================================================
-- Quy ước:
-- 1. Điện thoại cao cấp (iPhone, Samsung S24, Fold, Pixel): Color hiển thị dạng 'image' (Ảnh nhỏ)
-- 2. Laptop, Phụ kiện: Color hiển thị dạng 'color' (Chấm tròn màu)
-- 3. Dung lượng (Storage): Luôn hiển thị dạng 'button'

-- ID Attribute Màu sắc: 01KCX7P5BVBYZCF5AMSYCJZ2Q0
-- ID Attribute Bộ nhớ: 01KCX7P5BVW2B4QZYSFMJ8RWJM

INSERT INTO `product_attribute_definitions` (`product_id`, `attribute_id`, `visual_type`, `position`)
VALUES
-- 1. iPhone 15 Pro Max (Image + Button)
('01KCX7P5BVRQWX561MMZS4XXED', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', 'image', 0),
('01KCX7P5BVRQWX561MMZS4XXED', '01KCX7P5BVW2B4QZYSFMJ8RWJM', 'button', 1),

-- 2. Samsung S24 Ultra (Image + Button)
('01KCX7P5BVFV1VN8Q10WJWFS5G', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', 'image', 0),
('01KCX7P5BVFV1VN8Q10WJWFS5G', '01KCX7P5BVW2B4QZYSFMJ8RWJM', 'button', 1),

-- 3. MacBook Pro M3 (Color + Button) -> Laptop dùng chấm màu
('01KCX7P5BVNBSEWH74NHWYN6PY', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', 'color', 0),
('01KCX7P5BVNBSEWH74NHWYN6PY', '01KCX7P5BVW2B4QZYSFMJ8RWJM', 'button', 1),

-- 4. Sony WH-1000XM5 (Color only)
('01KCX7P5BV7GC3ZV90GRDAGKPV', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', 'color', 0),

-- 5. Samsung Z Fold6 (Image + Button)
('01KCX7P5BVXDTZ2TEFJ889SM80', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', 'image', 0),
('01KCX7P5BVXDTZ2TEFJ889SM80', '01KCX7P5BVW2B4QZYSFMJ8RWJM', 'button', 1),

-- 6. iPad Pro M4 (Color + Button)
('01KCX7P5BV41VPJ6H8QKNM41NW', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', 'color', 0),
('01KCX7P5BV41VPJ6H8QKNM41NW', '01KCX7P5BVW2B4QZYSFMJ8RWJM', 'button', 1),

-- 7. Xiaomi Book 14 (Color + Button)
('01KCX7P5BVS5DXAC105C0W1A1S', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', 'color', 0),
('01KCX7P5BVS5DXAC105C0W1A1S', '01KCX7P5BVW2B4QZYSFMJ8RWJM', 'button', 1),

-- 8. Google Pixel 9 Pro XL (Image + Button)
('01KCX7P5BW9MD9ZJSBB54ADDX1', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', 'image', 0),
('01KCX7P5BW9MD9ZJSBB54ADDX1', '01KCX7P5BVW2B4QZYSFMJ8RWJM', 'button', 1),

-- 9. Dell XPS 13 (Color + Button)
('01KCX7P5BWZRZ154P84N4F9RCS', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', 'color', 0),
('01KCX7P5BWZRZ154P84N4F9RCS', '01KCX7P5BVW2B4QZYSFMJ8RWJM', 'button', 1),

-- 10. Apple Watch Ultra 2 (Image only - Dây đeo nhìn ảnh mới rõ)
('01KCX7P5BW90A27D7Y6F9Y56EH', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', 'image', 0),

-- 11. Logitech MX Master 3S (Color only)
('01KCX7P5BW28PHS81NGCGKJ4XF', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', 'color', 0),

-- 12. Asus ROG Zephyrus (Color + Button)
('01KCX7P5BW7R735EBD7WAFT3NP', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', 'color', 0),
('01KCX7P5BW7R735EBD7WAFT3NP', '01KCX7P5BVW2B4QZYSFMJ8RWJM', 'button', 1),

-- 13. Xiaomi 14 Ultra (Image + Button)
('01KCX7P5BWRNK8NQ7P1Z1F96YN', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', 'image', 0),
('01KCX7P5BWRNK8NQ7P1Z1F96YN', '01KCX7P5BVW2B4QZYSFMJ8RWJM', 'button', 1),

-- 14. Tab S9 (Color + Button)
('01KCX7P5BWCV5M6MXMB0WM5SW6', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', 'color', 0),
('01KCX7P5BWCV5M6MXMB0WM5SW6', '01KCX7P5BVW2B4QZYSFMJ8RWJM', 'button', 1),

-- 15. Sony Alpha A7 IV (Color only - Body máy ảnh thường chỉ có màu đen)
('01KCX7P5BWXASWD4EQTHDNRC7Z', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', 'color', 0),

-- 16. Dell XPS 15 (Color + Button)
('01KCX7P5BW836FBRYNBC10HT8G', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', 'color', 0),
('01KCX7P5BW836FBRYNBC10HT8G', '01KCX7P5BVW2B4QZYSFMJ8RWJM', 'button', 1),

-- 17. Asus ROG Phone 8 (Image + Button)
('01KCX7P5BW7CGQA5SCJS35KQRD', '01KCX7P5BVBYZCF5AMSYCJZ2Q0', 'image', 0),
('01KCX7P5BW7CGQA5SCJS35KQRD', '01KCX7P5BVW2B4QZYSFMJ8RWJM', 'button', 1);


-- ============================================================
-- 9. PRODUCT OPTION SWATCHES (OVERRIDES - TÙY CHỈNH NÂNG CAO)
-- ============================================================
-- Đây là ví dụ về việc ghi đè giá trị mặc định.
-- Mặc định hệ thống sẽ lấy Mã màu Hex hoặc Ảnh đại diện Variant.
-- Bảng này dùng khi bạn muốn một hình ảnh texture cụ thể hoặc mã màu khác biệt cho riêng sản phẩm đó.

INSERT INTO `product_value_swatches` (`product_id`, `option_id`, `type`, `value`)
VALUES

-- Ví dụ 1: Samsung S24 Ultra (ID: ...J9K)
-- Màu "Tím Titan" (ID Option: ...R6S)
-- Thay vì lấy ảnh variant (cả cái điện thoại), ta muốn hiển thị một miếng texture kim loại màu tím.
('01KCX7P5BVFV1VN8Q10WJWFS5G', '01KCX7P5BV9RZPNVF329K4BAGW', 'image',
 'https://res.cloudinary.com/demo/image/upload/swatch_titan_violet_texture.jpg'),

-- Ví dụ 2: MacBook Pro M3 (ID: ...2M3)
-- Màu "Xám Không Gian" (ID Option: ...1L2)
-- Mặc định Global là #505050, nhưng riêng MacBook này muốn màu đậm hơn (#333333)
('01KCX7P5BVNBSEWH74NHWYN6PY', '01KCX7P5BV5PXSF2Q6A8FDD1FG', 'color', '#333333'),

-- Ví dụ 3: iPhone 15 Pro Max (ID: ...H8J)
-- Màu "Titan Tự Nhiên" (ID Option: ...4P5)
-- Hiển thị texture kim loại thay vì ảnh điện thoại
('01KCX7P5BVRQWX561MMZS4XXED', '01KCX7P5BW27V7T7RH424MM7GW', 'image',
 'https://res.cloudinary.com/demo/image/upload/swatch_titan_natural.jpg');


-- 4.1. Cập nhật số lượng cho Brands
UPDATE `brands` b
SET `products_count` = (SELECT COUNT(*)
                        FROM `products` p
                        WHERE p.brand_id = b.id
                          AND p.deleted_at IS NULL);

-- 4.2. Cập nhật số lượng cho Categories
UPDATE `categories` c
SET `products_count` = (SELECT COUNT(*)
                        FROM `products_categories` pc
                                 JOIN `products` p ON pc.product_id = p.id
                        WHERE pc.category_id = c.id
                          AND p.deleted_at IS NULL);

-- 4.3. Cập nhật Rating cho Products
UPDATE `products` p
SET `reviews_count` = (SELECT COUNT(*)
                       FROM `product_reviews` pr
                       WHERE pr.product_id = p.id
                         AND pr.status = 'approved'),
    `rating_avg`    = (SELECT COALESCE(AVG(rating), 0)
                       FROM `product_reviews` pr
                       WHERE pr.product_id = p.id
                         AND pr.status = 'approved');

-- 4.4. Cập nhật Specs Snapshot (Chỉ chạy nếu bảng product_specs có dữ liệu)
-- Lưu ý: Query này có thể chậm nếu có hàng triệu sản phẩm.
UPDATE `products` p
SET `specs_snapshot` = (SELECT JSON_OBJECTAGG(
                                       attr.name,
                                       TRIM(CONCAT_WS(' ', COALESCE(opt.value, ps.raw_value), NULLIF(ps.unit, '')))
                               )
                        FROM `product_specs` ps
                                 JOIN `attributes` attr ON ps.attribute_id = attr.id
                                 LEFT JOIN `attribute_values` opt ON ps.value_option_id = opt.id
                        WHERE ps.product_id = p.id
                          AND ps.is_highlight = 1);

INSERT INTO `product_images` (`id`, `product_id`, `url`, `alt_text`, `is_thumbnail`, `sort_order`)
VALUES ('01KCZN5BSQJVSC45QBEEW3EZ5Q', '01KCX7P5BVRQWX561MMZS4XXED',
        'https://cdn2.fptshop.com.vn/unsafe/750x0/filters:format(webp):quality(75)/2023_9_20_638307992305419305_iphone-15-promax-xanh-vang-1.jpg',
        'iPhone 15 Pro Max - Mặt trước chính diện', 1, 1),
       ('01KCZN5BSQ28QFDD8T0A79CWDW', '01KCX7P5BVRQWX561MMZS4XXED',
        'https://cdn2.fptshop.com.vn/unsafe/750x0/filters:format(webp):quality(75)/2023_9_20_638307992304365855_iphone-15-promax-xanh-vang-2.jpg',
        'iPhone 15 Pro Max - Góc nghiêng cạnh bên', 0, 2),
       ('01KCZN5BSQGN6G203BB3SQPH8N', '01KCX7P5BVRQWX561MMZS4XXED',
        'https://cdn2.fptshop.com.vn/unsafe/750x0/filters:format(webp):quality(75)/2023_9_20_638307992301539390_iphone-15-promax-vang-3.jpg',
        'iPhone 15 Pro Max - Cận cảnh cụm Camera', 0, 3),
       ('01KCZN5BSQFPFFJYM1FVWKFPYN', '01KCX7P5BVRQWX561MMZS4XXED',
        'https://cdn2.fptshop.com.vn/unsafe/750x0/filters:format(webp):quality(75)/2023_9_20_638307992303272057_iphone-15-promax-vang-4.jpg',
        'iPhone 15 Pro Max - Cạnh viền Titan', 0, 4),
       ('01KCZN5BSQFFMGHZT6QQKMFRZY', '01KCX7P5BVRQWX561MMZS4XXED',
        'https://cdn2.fptshop.com.vn/unsafe/750x0/filters:format(webp):quality(75)/2023_9_20_638307980087329808_iphone-15-promax-5.jpg',
        'iPhone 15 Pro Max - Chi tiết cổng USB-C', 0, 5),
       ('01KCZN5BSQB0DP8JKE9BW7MKCS', '01KCX7P5BVRQWX561MMZS4XXED',
        'https://cdn2.fptshop.com.vn/unsafe/750x0/filters:format(webp):quality(75)/2023_9_20_638307980086696914_iphone-15-promax-6.jpg',
        'iPhone 15 Pro Max - Màn hình Dynamic Island', 0, 6),
       ('01KCZN5BSQ10MDY3MDPKBNQJN7', '01KCX7P5BVRQWX561MMZS4XXED',
        'https://cdn2.fptshop.com.vn/unsafe/750x0/filters:format(webp):quality(75)/2023_9_20_638307980087009486_iphone-15-promax-7.jpg',
        'iPhone 15 Pro Max - Trải nghiệm thực tế 1', 0, 7),
       ('01KCZN5BSQFNEWFWZJZ3MEZPH7', '01KCX7P5BVRQWX561MMZS4XXED',
        'https://cdn2.fptshop.com.vn/unsafe/750x0/filters:format(webp):quality(75)/2023_9_20_638307980087009486_iphone-15-promax-8.jpg',
        'iPhone 15 Pro Max - Trải nghiệm thực tế 2', 0, 8),
       ('01KCZN5BSQJN96EMSC4YGC1YW0', '01KCX7P5BVRQWX561MMZS4XXED',
        'https://cdn2.fptshop.com.vn/unsafe/750x0/filters:format(webp):quality(75)/2023_9_20_638307992302484080_iphone-15-promax-vang-9.jpg',
        'iPhone 15 Pro Max - Mặt lưng màu Vàng', 0, 9),
       ('01KCZN5BSQNW7V85Y86724MMYP', '01KCX7P5BVRQWX561MMZS4XXED',
        'https://cdn2.fptshop.com.vn/unsafe/1920x0/filters:format(webp):quality(75)/2024_4_16_638488768365763037_15.jpg',
        'Tính năng - Hiệu năng chip A17 Pro', 0, 10),
       ('01KCZN5BSQJFFH0PYF759ZPCK4', '01KCX7P5BVRQWX561MMZS4XXED',
        'https://cdn2.fptshop.com.vn/unsafe/1920x0/filters:format(webp):quality(75)/2024_4_16_638488768362667033_10.jpg',
        'Tính năng - Khung vỏ Titan siêu bền', 0, 11),
       ('01KCZN5BSQP7S0HY29M9J1EW42', '01KCX7P5BVRQWX561MMZS4XXED',
        'https://cdn2.fptshop.com.vn/unsafe/1920x0/filters:format(webp):quality(75)/2024_4_16_638488768364505309_7.jpg',
        'Tính năng - Nút Action mới', 0, 12),
       ('01KCZN5BSQ4JTKMG4WHZDV5Q1N', '01KCX7P5BVRQWX561MMZS4XXED',
        'https://cdn2.fptshop.com.vn/unsafe/1920x0/filters:format(webp):quality(75)/2024_4_16_638488768365442895_6.jpg',
        'Tính năng - Camera 48MP sắc nét', 0, 13),
       ('01KCZN5BSQSS10BBDJ9BMVNKHR', '01KCX7P5BVRQWX561MMZS4XXED',
        'https://cdn2.fptshop.com.vn/unsafe/1920x0/filters:format(webp):quality(75)/2024_4_16_638488768366701340_3.jpg',
        'Tính năng - Zoom quang học 5x', 0, 14),
       ('01KCZN5BSQ6H1QYB6C4NSHHTRY', '01KCX7P5BVRQWX561MMZS4XXED',
        'https://cdn2.fptshop.com.vn/unsafe/1920x0/filters:format(webp):quality(75)/2024_4_16_638488768368578332_2.jpg',
        'Tính năng - Quay video chuyên nghiệp', 0, 15),
       ('01KCZN5BSQ65TRSMHA2H4ZV5GB', '01KCX7P5BVRQWX561MMZS4XXED',
        'https://cdn2.fptshop.com.vn/unsafe/1920x0/filters:format(webp):quality(75)/2024_4_16_638488768369246960_12.jpg',
        'Tính năng - Kết nối USB 3 tốc độ cao', 0, 16),
       ('01KCZN5BSQF0WGSH33HJ9277YB', '01KCX7P5BVRQWX561MMZS4XXED',
        'https://cdn2.fptshop.com.vn/unsafe/1920x0/filters:format(webp):quality(75)/2024_4_16_638488768369872028_4.jpg',
        'Tính năng - Thời lượng pin ấn tượng', 0, 17),
       ('01KCZN5BSQDYMKB6TA9BRZ4V40', '01KCX7P5BVRQWX561MMZS4XXED',
        'https://cdn2.fptshop.com.vn/unsafe/1920x0/filters:format(webp):quality(75)/2024_4_16_638488768370184446_5.jpg',
        'Tính năng - An toàn và bảo mật', 0, 18);

# ["01KCX7P5BFSG5RRSQC0QK1BDMY","01KCX7P5BVZV3N2VTMHNYRPWNG","01KCX7P5BV6K3N7CWCVWQZMH7F","01KCX7P5BVX9646SZWMSZWT51R","01KCX7P5BVM7FYB5MTAQBFD771","01KCX7P5BV5W7FAW3BY61BXJZ3","01KCX7P5BVFHA1EGY8GB0THTRJ","01KCX7P5BV51VHXGKF2YSS7Y0Y","01KCX7P5BVV62CW3V1XCGCQ7X3","01KCX7P5BV2XWYPADZZXAK88KD","01KCX7P5BVEPE3KG1SRKV5XFVP","01KCX7P5BV94FCF73XNC9BC1NA","01KCX7P5BV8SNJVHQRFAD16VE2","01KCX7P5BV8J0DQGPNCA5X0YQN","01KCX7P5BVBE3V90Z6PEKXDNJJ","01KCX7P5BV0AS5JP6YWXJXATR3","01KCX7P5BVN95WMYQZCAKC7QWX","01KCX7P5BVBYZCF5AMSYCJZ2Q0","01KCX7P5BVW2B4QZYSFMJ8RWJM","01KCX7P5BVBX2Z9X9C64GTY65C","01KCX7P5BVJXABATPD5HMATG8P","01KCX7P5BV2BV8AST177G3682X","01KCX7P5BV8PGKT5TEZS9FWNBP","01KCX7P5BV6DSSK9BZEJ4AYB76","01KCX7P5BVAX4GHVJNTHJNQHAS","01KCX7P5BV726X3E1JYS41QSWZ","01KCX7P5BVDNSHX5981D8ZXDXD","01KCX7P5BVNN4ZXG216MQ9G8DE","01KCX7P5BVFPFZFR5VN81S6H38","01KCX7P5BV1DMBHVD4GHXRS3WE","01KCX7P5BVMHRVNKEX1W9AQRH6","01KCX7P5BV2R02J8RNAJQHHX24","01KCX7P5BVNCW0SEX7DRPX11Y3","01KCX7P5BV9RZPNVF329K4BAGW","01KCX7P5BVBH3234YMB226VHVW","01KCX7P5BVCFY43EF886JXFR99","01KCX7P5BV5PXSF2Q6A8FDD1FG","01KCX7P5BVZFVJ615ER41WTYJ2","01KCX7P5BVD494NEAENTERFHJ9","01KCX7P5BV78FRNAFX1YJRE2RA","01KCX7P5BVRQWX561MMZS4XXED","01KCX7P5BVFV1VN8Q10WJWFS5G","01KCX7P5BVNBSEWH74NHWYN6PY","01KCX7P5BV7GC3ZV90GRDAGKPV","01KCX7P5BVXDTZ2TEFJ889SM80","01KCX7P5BV41VPJ6H8QKNM41NW","01KCX7P5BVS5DXAC105C0W1A1S","01KCX7P5BVVQQQV7WA10F4867D","01KCX7P5BVBRBPZCQFYGRY1G28","01KCX7P5BV4M4HVKK696P935J7","01KCX7P5BVXD6PVZMAS06W6QFP","01KCX7P5BVZJ8ZEW7035BT6TKG","01KCX7P5BV4WY9AJE1C02JP8N3","01KCX7P5BVPG43XBRTXGPYH7XE","01KCX7P5BVERJPQ30XPTAA0PEW","01KCX7P5BVTESXPNNFAZMDRQ8M","01KCX7P5BVD28YZH7G8T8MBVJ3","01KCX7P5BV86FWZNY0GS8NXGMS","01KCX7P5BVX8BZQZA7N7YBK4TS","01KCX7P5BVG9JY0H39G15HENTJ","01KCX7P5BVJJ3BSMZ6KPTJRGBZ","01KCX7P5BV5AVF1Z837X8PDZNJ","01KCX7P5BV4BGM86GTMSQMAQ30","01KCX7P5BVP6Z873ESRW861Q1Z","01KCX7P5BV6DDCB3X63YWHWHBK","01KCX7P5BVDZ3KX9MYPX5H0KN3","01KCX7P5BVQQYVBNSWGNJJD0QD","01KCX7P5BV954NH8CTVVW4PP0X","01KCX7P5BVGYX2PJPSNQQF91D6","01KCX7P5BVQ0BHES6CKF8GASCQ","01KCX7P5BWM2N668ET4EVZE9EP","01KCX7P5BW6PXGQ3F5ZSX3AJ4E","01KCX7P5BWF3C5VAC69C2AJT84","01KCX7P5BWV2QSMX8312PEN2RS","01KCX7P5BWQQWQ7VY0DHG0R19P","01KCX7P5BWAB7N1RJG4X9F8VX5","01KCX7P5BW8DW0KWSSSSA60MND","01KCX7P5BW3CA3RSVTPA3MXG2Q","01KCX7P5BW0N2PWPJFTP0HFJVT","01KCX7P5BWEJW7E7W6MAA5J1KS","01KCX7P5BWYZTWWVB6VXH82ENR","01KCX7P5BW01M4YHQH57V566Y7","01KCX7P5BWMHN0AE39PZCPX22M","01KCX7P5BWVGR905BR6N93FZ8F","01KCX7P5BW5NE4JGBX5J783KZV","01KCX7P5BWKA22CEWSPRB4MS0B","01KCX7P5BW3YXAG0C8FH3B7N4F","01KCX7P5BWDP7C53DXTCCFNVV9","01KCX7P5BWSHTC0A9TGTN7DV08","01KCX7P5BWS879MCR1GTAJ7X7A","01KCX7P5BWK8VA76E9KP5XZ31C","01KCX7P5BWP9T21FZZ6FWNVXG9","01KCX7P5BWVK9F3QF8B8ZAFSCS","01KCX7P5BWYB5VZ5Y0P4QKZF4S","01KCX7P5BW9JZ1B7RYZCR459EM","01KCX7P5BWH1B6AHE52JFZG0CW","01KCX7P5BW27V7T7RH424MM7GW","01KCX7P5BW9MD9ZJSBB54ADDX1","01KCX7P5BWZRZ154P84N4F9RCS","01KCX7P5BW90A27D7Y6F9Y56EH","01KCX7P5BW28PHS81NGCGKJ4XF","01KCX7P5BW7R735EBD7WAFT3NP","01KCX7P5BW08FHECJQNG4A80XV","01KCX7P5BWVW7ZAWZGCPMJ0JA7","01KCX7P5BW33W0P4C37V62ZH9T","01KCX7P5BW8EJXYTBSK3K95DRS","01KCX7P5BWJQQ05QHX28Y5KMMF","01KCX7P5BWEZYXERDH2NZ5XZA7","01KCX7P5BWHQQQPCMGF2JQHJWF","01KCX7P5BWT2Y6B8RBAM6HP5KT","01KCX7P5BWVHQJDYDW8YPWNHVW","01KCX7P5BWSCD0Q34Z9DA3Y3NF","01KCX7P5BWAX4X43C2CR0ZK21A","01KCX7P5BW30NWGTDYBK4CK81W","01KCX7P5BW7KFE7EA6EEH80BKR","01KCX7P5BWKKWQ3WTE25C8TJ9T","01KCX7P5BWQGAMSCR44K85F44S","01KCX7P5BW2JBP2824CSCXNWDK","01KCX7P5BWEGX52D2TCFFX352F","01KCX7P5BWJNZ28V7CCQW6Q56D","01KCX7P5BW22W7ZTGZ56E4DEEE","01KCX7P5BW47TGEP52P5VT8NR2","01KCX7P5BWRNK8NQ7P1Z1F96YN","01KCX7P5BWCV5M6MXMB0WM5SW6","01KCX7P5BWXASWD4EQTHDNRC7Z","01KCX7P5BW836FBRYNBC10HT8G","01KCX7P5BW7CGQA5SCJS35KQRD","01KCX7P5BWMP3N2QKYVW2SEPEX","01KCX7P5BW0AK7F3499FBQEQ5B","01KCX7P5BW0NTP4HJR4VR2GE3V","01KCX7P5BW1A1C9KC1NRG2P5BS","01KCX7P5BW1N9F0ZS1DJA8VT9J","01KCX7P5BWSYHYAMWZVTV0HJDY","01KCX7P5BWPNQ6N287YEGYD3GW","01KCX7P5BWN091HYEQFW33246F","01KCX7P5BWQ1SJ17JXPFPAZJZW","01KCX7P5BWPWFPKDBNZRP59Q9H","01KCX7P5BWGDYKVSY7JWTXPG6E","01KCX7P5BWS4GW5RMW8GY5DQ5X","01KCX7P5BW2MTJ1GXZYFCY3AS8","01KCX7P5BW9V0R2V70H38EPQ8H","01KCX7P5BWVMPM8GC7G1E73R0F","01KCX7P5BW7XJYVRZFV4Y7EA01","01KCX7P5BWGWN9MBZ95JY8R6Q5","01KCX7P5BWDYAB6337VCDGRPK9","01KCX7P5BWE91YG0CPR2G8B15Y","01KCX7P5BW6ABACH617PDAVHRH","01KCX7P5BW1ERVS8X8PH8EMWHA","01KCX7P5BW58CQ7DTFNC6BMBBR","01KCX7P5BWGXFJJ54PEA4A3SA6","01KCX7P5BWTVK55FBZS1ZKMBF6","01KCX7P5BWA3FVKP0FHJT9Z9QW","01KCX7P5BXPG37JZGRCTDHH6P9","01KCX7P5BXPA9APXCA7G0EY4Q2","01KCX7P5BX1SNWCQRGW807E7MT","01KCX7P5BXSAVQMDZRQCASCF4H","01KCX7P5BXH982GKV482BA94DM","01KCX7P5BX448RMYFH8S4TQSAG","01KCX7P5BX7DH28JJD2MX2NC4W","01KCX7P5BXJ96FBZVQXFRQD738","01KCX7P5BXA121QQFJ8ZF6CCJN","01KCX7P5BXAKYXK02Q2JTAR6ED","01KCX7P5BXC99QNA8YSG8QSS2A","01KCX7P5BX34SWKE9YA73G1CTH","01KCX7P5BX63H3RQHQ0NWVAGS6","01KCX7P5BXBSCV68CWF0DCP0T2","01KCX7P5BXRP21KF220DZ92H85","01KCX7P5BX3P7PEDJJ7M8A6M4H","01KCX7P5BXKN8B18ERCJSP2R0P","01KCX7P5BXDVJQHW4K6MNWYB3P","01KCX7P5BXADMFDZDPKHKVV1TK","01KCX7P5BX6F2M0TGPNAZX4C2C","01KCX7P5BXRDVVZQH1VY76A46Q","01KCX7P5BXPHVCCBFPTV5GE5GB","01KCX7P5BXJ7FR2C8GF31728RY","01KCX7P5BXY38A1ANAJ3HCF8KW","01KCX7P5BXYRZ29M2699FQ31FM","01KCX7P5BXA0X6DDEDDG0VRH9P","01KCX7P5BX483CBE6QYKHSZK2V","01KCX7P5BXATJP7JYKFD3WAG7X","01KCX7P5BXZ8CB806WNFS9J28J","01KCX7P5BX70Z1YR1J7K142BX7","01KCX7P5BX3XP8QYT1WT71XNMS","01KCX7P5BX7R2A78E5Y5GTZKZ4","01KCX7P5BX5NPTWSJT77SBKGBK","01KCX7P5BXA6NF8MVEMTRP4VJZ","01KCX7P5BX7GBPN41Q5YX1QTVZ","01KCX7P5BX2RX5NQSD4DZVZBCE","01KCX7P5BXS38E4T6J93YBJJ08","01KCX7P5BXQ51N1QBHZQWQCVBC","01KCX7P5BXDNFKMK8CWGSWTM9N","01KCX7P5BXXX7GTK2JX4KKK3V4","01KCX7P5BXWF0JK72K16KDP4BV","01KCX7P5BXB3YDHN4DM454HKP6","01KCX7P5BX6AB5KA23NT8QAVKG","01KCX7P5BXXE19HEWMVA5879FJ","01KCX7P5BXPRYJQJA9RCRMEDBH","01KCX7P5BXMVA598MJR43GH47Y","01KCX7P5BXBDXT6F1GX7A0RCAQ","01KCX7P5BXTYBNPPMYDYY66E8B"]