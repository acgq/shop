package com.github.shop.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.shop.ShopApplication;
import com.github.shop.entity.*;
import com.github.shop.generate.Order;
import com.github.shop.mock.MockRpcOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ShopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles("test")
public class MockOrderIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    MockRpcOrderService mockOrderRpcService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(mockOrderRpcService);
    }
    
    @Test
    public void createOrderSuccess() {
        OrderInfo orderInfo = new OrderInfo();
        GoodsInfo goods1 = new GoodsInfo(1L, 3000);
        GoodsInfo goods2 = new GoodsInfo(2L, 5000);
        orderInfo.setGoods(Arrays.asList(goods1, goods2));
        Order order = new Order();
        order.setId(1L);
        order.setShopId(1L);
        order.setUserId(2L);
        
        List<String> cookieWithUser2 = loginAndGetCookieWithUser2();
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        when(mockOrderRpcService.orderService.createOrder(any(), captor.capture())).thenReturn(order);
        
        HttpResponse response = postRequest("/api/v1/order", orderInfo, cookieWithUser2);
        assertEquals(OrderStatus.PENDING.getName(), captor.getValue().getStatus());
        assertEquals(HttpServletResponse.SC_CREATED, response.statusCode);
        Response<OrderResponse> orderResponse = response.asJson(new TypeReference<Response<OrderResponse>>() {
        });
        
        assertEquals(2, orderResponse.getData().getGoods().size());
        assertEquals(1, orderResponse.getData().getId());
    }
    
    @Test
    public void canRollBack() {
        OrderInfo orderInfo = new OrderInfo();
        GoodsInfo goods1 = new GoodsInfo(1L, 3000);
        GoodsInfo goods2 = new GoodsInfo(2L, 6000);
        orderInfo.setGoods(Arrays.asList(goods1, goods2));
        
        Order order = new Order();
        order.setId(1L);
        order.setShopId(1L);
        order.setUserId(2L);
        
        List<String> cookieWithUser2 = loginAndGetCookieWithUser2();
        HttpResponse response = postRequest("/api/v1/order", orderInfo, cookieWithUser2);
        
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.statusCode);
        
        createOrderSuccess();
        
    }
    
}
