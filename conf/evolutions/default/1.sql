# Products schema

# --- !Ups

ALTER TABLE `db`.`products`
CHANGE COLUMN `link_address` `link_address` VARCHAR(512) NULL DEFAULT NULL ;

ALTER TABLE `db`.`websites`
ADD COLUMN `price_element` VARCHAR(45) NULL AFTER `keyword`,
ADD COLUMN `currency_element` VARCHAR(45) NULL AFTER `price_element`;

ALTER TABLE `db`.`prices`
CHANGE COLUMN `id` `id` BIGINT(20) NOT NULL AUTO_INCREMENT ;

# --- !Downs

ALTER TABLE `db`.`products`
CHANGE COLUMN `link_address` `link_address` VARCHAR(128) NULL DEFAULT NULL ;

ALTER TABLE `db`.`websites`
DROP COLUMN `price_element`,
DROP COLUMN `currency_element`;

ALTER TABLE `db`.`prices`
CHANGE COLUMN `id` `id` BIGINT(20) NOT NULL ;