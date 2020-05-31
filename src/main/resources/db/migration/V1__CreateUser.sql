create table shop_user
(
    id          bigint primary key auto_increment,
    name        varchar(100) not null,
    tel         varchar(20) unique,
    avatar_url  varchar (1024),
    create_time datetime default current_timestamp,
    update_time datetime default null on update current_timestamp
) engine = InnoDB
  default charset = utf8mb4;
