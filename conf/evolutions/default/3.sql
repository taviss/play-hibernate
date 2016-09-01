# --- !Ups

ALTER TABLE `db`.`categories`
ADD COLUMN `deleted` TINYINT(2) NULL DEFAULT NULL AFTER `name`;

# --- !Downs

ALTER TABLE `db`.`categories`
DROP COLUMN `deleted` TINYINT(2) NULL DEFAULT NULL AFTER `name`;