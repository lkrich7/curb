INSERT INTO curb_group(`group_id`, `name`, `url`, `create_time`, `update_time`)
VALUES(1, 'CurbSystemGroup', 'http://127.0.0.1:8080', NOW(), NOW());

INSERT INTO curb_group_secret(`group_id`, `secret`, `create_time`, `update_time`)
VALUES(1, UUID(), NOW(), NOW());

INSERT INTO curb_app(`app_id`, `group_id`, `name`, `url`, `state`, `create_time`, `update_time`)
VALUES(1, 1, 'CurbSystemApp', 'http://127.0.0.1:8080', 1, NOW(), NOW());

INSERT INTO curb_app_secret(`app_id`, `secret`, `create_time`, `update_time`)
VALUES(1, UUID(), NOW(), NOW());

INSERT INTO curb_user_role_system(`user_id`, `group_id`, `role_id`, `create_time`, `update_time`)
VALUES(1, 1, 1, NOW(), NOW());