create table shop_order
(
    id              bigint primary key auto_increment,
    user_id         bigint,
    shop_id         bigint,
    total_price     decimal,     -- 单位为分
    address         varchar(1024),
    express_company varchar(16),
    express_id      varchar(128),
    status          varchar(16), -- pending 未付款， paid 已付款  delivered 运送中 received 已收货
    create_time     datetime not null default now(),
    update_time     datetime not null default now() on update current_timestamp
) engine = InnoDB
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci;

create table shop_order_goods
(
    id       bigint primary key auto_increment,
    goods_id bigint,
    order_id bigint,
    number   int
) engine = InnoDB
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci;

insert into shop_order (id, user_id, shop_id, total_price, address, express_company, express_id, status)
values (1, 1, 1, 500, 'mars', 'sf', '1000000000', 'paid');

insert into shop_order_goods (goods_id, order_id, number)
values (1, 1, 5);
