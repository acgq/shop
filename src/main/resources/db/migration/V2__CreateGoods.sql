create table shop_goods
(
    id          bigint primary key auto_increment,
    shop_id     bigint,
    name        varchar(100) not null,
    description varchar(1024),
    details     text,
    img_url     varchar(1024),
    price       bigint,      --  '单位为分'
    stock       int          not null default 0,
    status      varchar(16), --  "ok" 正常 , "deleted" 已删除
    create_time datetime     not null default now(),
    update_time datetime     not null default now() on update current_timestamp
) engine = InnoDB
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci;

insert into shop_goods (id, shop_id, name, description, details, img_url, price, stock, status)
values (1, 1, 'goods1', 'desc1', 'detail1', 'url1', 100, 5000, 'ok');
insert into shop_goods (id, shop_id, name, description, details, img_url, price, stock, status)
values (2, 1, 'goods2', 'desc2', 'detail2', 'url2', 200, 5000, 'ok');
insert into shop_goods (id, shop_id, name, description, details, img_url, price, stock, status)
values (3, 2, 'goods3', 'desc3', 'detail3', 'url3', 300, 5000, 'ok');
insert into shop_goods (id, shop_id, name, description, details, img_url, price, stock, status)
values (4, 2, 'goods4', 'desc4', 'detail4', 'url4', 400, 5000, 'ok');
insert into shop_goods (id, shop_id, name, description, details, img_url, price, stock, status)
values (5, 2, 'goods5', 'desc5', 'detail4', 'url4', 400, 5000, 'ok');
