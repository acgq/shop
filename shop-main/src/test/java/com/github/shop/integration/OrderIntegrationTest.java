package com.github.shop.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.shop.ShopApplication;
import com.github.shop.entity.*;
import com.github.shop.generate.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ShopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles("test")
public class OrderIntegrationTest extends AbstractIntegrationTest {
    
    @Test
    public void createOrderSuccess() {
        List<String> cookie = loginAndGetCookieWithUser2();
        //arrange data
        HttpResponse response = createOrderWithTwoGoodsInShop1(cookie);
        
        assertEquals(SC_CREATED, response.statusCode);
        Response<OrderResponse> orderResponse = response.asJson(new TypeReference<Response<OrderResponse>>() {
        });
        assertTrue(orderResponse.getData().getShopId() != null);
        assertTrue(orderResponse.getData().getShop() != null);
        assertEquals(2, orderResponse.getData().getGoods().size());
        
        Long orderId = orderResponse.getData().getId();
        
        //get order by order id;
        response = getRequest("/api/v1/order/" + orderId, cookie);
        assertEquals(SC_OK, response.statusCode);
        orderResponse = response.asJson(new TypeReference<Response<OrderResponse>>() {
        });
        assertTrue(orderResponse.getData().getShopId() != null);
        assertTrue(orderResponse.getData().getShop() != null);
        assertEquals(2, orderResponse.getData().getGoods().size());
    }
    
    private HttpResponse createOrderWithTwoGoodsInShop1(List<String> cookie) {
        OrderInfo orderInfo = new OrderInfo();
        GoodsInfo goods1 = new GoodsInfo(1L, 10);
        GoodsInfo goods2 = new GoodsInfo(2L, 100);
        orderInfo.setGoods(Arrays.asList(goods1, goods2));
        
        return postRequest("/api/v1/order", orderInfo, cookie);
    }
    
    @Test
    public void deductStock() {
        OrderInfo orderInfo = new OrderInfo();
        GoodsInfo goods1 = new GoodsInfo(1L, 1000);
        GoodsInfo goods2 = new GoodsInfo(2L, 2000);
        orderInfo.setGoods(Arrays.asList(goods1, goods2));
        
        List<String> cookieWithUser2 = loginAndGetCookieWithUser2();
        //deduct stock success
        HttpResponse response = postRequest("/api/v1/order", orderInfo, cookieWithUser2);
        assertEquals(SC_CREATED, response.statusCode);
        
        //deduct stock fail;
        orderInfo = new OrderInfo();
        goods1 = new GoodsInfo(1L, 4000);
        goods2 = new GoodsInfo(2L, 4000);
        orderInfo.setGoods(Arrays.asList(goods1, goods2));
        
        response = postRequest("/api/v1/order", orderInfo, cookieWithUser2);
        assertEquals(SC_BAD_REQUEST, response.statusCode);
        assertTrue(response.body.contains("扣减库存失败"));
        
    }
    
    @Test
    public void updateOrderInfo() throws JsonProcessingException {
        List<String> cookieWithUser2 = loginAndGetCookieWithUser2();
        //user2 13900000000 create a order with two goods in shop 1.
        HttpResponse response = createOrderWithTwoGoodsInShop1(cookieWithUser2);
        Response<OrderResponse> orderResponse = response.asJson(new TypeReference<Response<OrderResponse>>() {
        });
        //shop owner update express company
        List<String> cookieWithUser1 = loginAndGetCookie();
        Long orderId = orderResponse.getData().getId();
        Order order = new Order();
        order.setExpressCompany("sf");
        order.setExpressId("100000000000000");
        response = updateRequest("/api/v1/order/" + orderId, order, cookieWithUser2);
        // assert 403
        assertEquals(SC_FORBIDDEN, response.statusCode);
        response = updateRequest("/api/v1/order/" + orderId, order, cookieWithUser1);
        orderResponse = response.asJson(new TypeReference<Response<OrderResponse>>() {
        });
        assertEquals(OrderStatus.DELIVERED.getName(), orderResponse.getData().getStatus());
        
        //order owner update order status. receive goods.
        order = new Order();
        order.setStatus(OrderStatus.RECEIVED.getName());
        response = updateRequest("/api/v1/order/" + orderId, order, cookieWithUser2);
        orderResponse = response.asJson(new TypeReference<Response<OrderResponse>>() {
        });
        assertEquals(SC_OK, response.statusCode);
        assertEquals(OrderStatus.RECEIVED.getName(), orderResponse.getData().getStatus());
    }
    
    @Test
    public void deleteOrder() {
        List<String> cookieWithUser2 = loginAndGetCookieWithUser2();
        HttpResponse response = createOrderWithTwoGoodsInShop1(cookieWithUser2);
        Response<OrderResponse> orderResponse = response.asJson(new TypeReference<Response<OrderResponse>>() {
        });
        Long orderId = orderResponse.getData().getId();
        
        response = deleteRequest("/api/v1/order/" + orderId, "", cookieWithUser2);
        
        assertEquals(SC_NO_CONTENT, response.statusCode);
    }
    
    @Test
    public void getOrdersSuccessful() {
        List<String> cookieWithUser2 = loginAndGetCookieWithUser2();
        //create 10 order
        for (int i = 0; i < 10; i++) {
            createOrderWithTwoGoodsInShop1(cookieWithUser2);
        }
        //get order
        
        HttpResponse response = getRequest("/api/v1/order?pageSize=2&pageNum=3", cookieWithUser2);
        PageResponse<OrderResponse> orderResponse = response.asJson(new TypeReference<PageResponse<OrderResponse>>() {
        });
        assertEquals(SC_OK, response.statusCode);
        assertEquals(5, orderResponse.getTotalPage());
        assertEquals(2, orderResponse.getData().size());
        assertEquals(2, orderResponse.getData().get(0).getGoods().size());
    }
}
