# --- !Ups

ALTER TABLE `db`.`websites`
ADD COLUMN `deleted` TINYINT(2) NULL DEFAULT NULL AFTER `currency_element`;

# --- !Downs

ALTER TABLE `db`.`websites`
ADD COLUMN `deleted` TINYINT(2) NULL DEFAULT NULL AFTER `currency_element`;