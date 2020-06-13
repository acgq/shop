create table shop_shopping_cart
(
    id          bigint primary key auto_increment,
    user_id     bigint,
    goods_id    bigint,
    shop_id     bigint,
    number      int,
    status      varchar(16), --  "ok" 正常 , "deleted" 已删除
    create_time datetime not null default now(),
    update_time datetime not null default now() on update current_timestamp
) engine = InnoDB
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci;

insert into shop_shopping_cart (user_id, goods_id, shop_id, number, status)
values (1, 1, 1, 100, 'ok');
insert into shop_shopping_cart (user_id, goods_id, shop_id, number, status)
values (2, 2, 1, 300, 'ok');
insert into shop_shopping_cart (user_id, goods_id, shop_id, number, status)
values (3, 3, 2, 500, 'ok');




