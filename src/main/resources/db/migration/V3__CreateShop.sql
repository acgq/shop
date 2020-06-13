create table shop_shop
(
    id            bigint primary key auto_increment,
    name          varchar(100) not null,
    description   varchar(1024),
    img_url       varchar(1024),
    owner_user_id bigint,
    status        varchar(16), --  "ok" 正常 , "deleted" 已删除
    create_time   datetime     not null default now(),
    update_time   datetime     not null default now() on update current_timestamp
) engine = InnoDB
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci;

insert into shop_shop (id, name, description, img_url, owner_user_id, status)
VALUES (1, 'shop1', 'desc1', 'url1', 1, 'ok');
insert into shop_shop (id, name, description, img_url, owner_user_id, status)
VALUES (2, 'shop2', 'desc2', 'url2', 1, 'ok');
