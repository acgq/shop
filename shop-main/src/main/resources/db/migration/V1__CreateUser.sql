create table shop_user
(
    id          bigint primary key auto_increment,
    name        varchar(100) not null,
    tel         varchar(20) unique,
    avatar_url  varchar(1024),
    address     varchar(1024),
    create_time datetime     not null default current_timestamp,
    update_time datetime     not null default now() on update current_timestamp
) engine = InnoDB
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci;

insert into shop_user (id, name, tel, avatar_url, address)
values (1, 'user1', '13800000000', 'http://url', '火星')
