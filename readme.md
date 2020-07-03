# 小微店铺

## 功能描述
### 基本功能

- 基于手机验证码的登录（使用模拟服务）
- 买家
  - 编辑个人信息/收货地址
  - 商品浏览
  - 加购物车
  - 下单
  - 付款（使用模拟服务）
  - 查看物流信息（使用模拟服务）
  - 确认收货
- 卖家
  - 新建/修改店铺
  - 添加/编辑/删除宝贝
  - 查看订单
  - 发货

### 高级功能

- Docker部署
- 分布式部署+负载均衡
- Redis分布式登录状态维持
- RPC调用（Dubbo）+微服务化

## 安装和部署
- 这是一个分布式应用，分为两个部分，二者直接使用Dubbo RPC通讯
    - 主模块(shop-main)：负责处理Http请求，更新商品，店铺，购物车信息
    - 订单模块(shop-order)：负责订单模块
    
- 应用依赖
    - Redis：进行分布式登录状态维持
    - MySQL：存储所有数据
    - Zookeeper：作为注册中心
    - Nginx：(可选)使用多实例部署和负载均衡时使用
    
- 部署步骤
  - 依赖环境搭建
  - `docker run -d -v /path/to/shop-data:/var/lib/mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=123456 -e MYSQL_DATABASE=shop --name=shop-mysql mysql`
  - `docker run -p 6379:6379 -d redis`
  - `docker run -p 2181:2181 -d zookeeper`
  - 等待半分钟，等容器启动完毕
  - 创建`order`数据库：
    ```
    docker exec -it shop-mysql mysql -uroot -proot -e 'create database if not exists `shop-order`'
    ```
  - 切换到项目目录
  - `mvn install -DskipTests`
  - `mvn flyway:migrate -pl shop-main` 
  - `mvn flyway:migrate -pl shop-order`  
- 启动应用本身
  - 在第一个窗口中运行 `java -jar shop-order/target/shop-order-0.0.1-SNAPSHOT.jar`
  - 在第二个窗口中运行 `java -jar shop-main/target/shop-main-0.0.1-SNAPSHOT.jar`
- 浏览器中打开http://localhost:8080
