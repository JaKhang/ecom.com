# read and add only -- don't update, delete public_key
CREATE TABLE `user_keys`
(
    `id`         BIGINT AUTO_INCREMENT,
    `user_id`    CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    `public_key` TEXT                                                  NOT NULL,
    `created_at` TIMESTAMP                                             NULL DEFAULT CURRENT_TIMESTAMP,
    `revoked_at` TIMESTAMP                                             NULL,
    CONSTRAINT `fk_ur_key_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    PRIMARY KEY (`id`)

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

DELIMITER $$

CREATE TRIGGER `before_user_key_update`
    BEFORE UPDATE
    ON `user_keys`
    FOR EACH ROW
BEGIN

    -- user_id is immutable
    IF NOT (old.`user_id` <=> new.`user_id`) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'user_id cannot be modified';
    END IF;

    -- public_key is immutable
    IF NOT (old.`public_key` <=> new.`public_key`) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'public_key cannot be modified';
    END IF;

    -- revoked_at can only transition from NULL -> TIMESTAMP once
    IF old.`revoked_at` IS NOT NULL
        AND NOT (old.`revoked_at` <=> new.`revoked_at`)
    THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT =
                    'revoked_at cannot be modified after revocation';
    END IF;

END$$

DELIMITER ;

CREATE TABLE order_signatures
(
    order_id    CHAR(26) CHARACTER SET ascii COLLATE ascii_general_ci PRIMARY KEY,
    user_key_id BIGINT   NOT NULL,
    hash        CHAR(64) NOT NULL,
    signature   TEXT     NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (order_id)
        REFERENCES orders (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;;


DELIMITER $$

CREATE TRIGGER `before_order_signature_update`
    BEFORE UPDATE
    ON `order_signatures`
    FOR EACH ROW
BEGIN
    -- user_key_id is immutable
    IF NOT (old.`user_key_id` <=> new.`user_key_id`) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'user_key_id cannot be modified';
    END IF;
END$$

DELIMITER ;