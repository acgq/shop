package com.github.shop.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.shop.ShopApplication;
import com.github.shop.entity.*;
import com.github.shop.generate.Order;
import com.github.shop.mock.MockRpcOrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
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
        assertEquals(SC_CREATED, response.statusCode);
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
        
        assertEquals(SC_BAD_REQUEST, response.statusCode);
        
        createOrderSuccess();
        
    }
    
    @Test
    public void throw404NotFound() {
        List<String> cookieWithUser2 = loginAndGetCookieWithUser2();
        RpcOrderGoods orderGoods = new RpcOrderGoods();
        orderGoods.setOrder(null);
        when(mockOrderRpcService.orderService.getOrderById(1L)).thenReturn(orderGoods);
        
        HttpResponse response = getRequest("/api/v1/order/1", cookieWithUser2);
        assertEquals(SC_NOT_FOUND, response.statusCode);
    }
    
    @Test
    public void testUpdateStatus() throws JsonProcessingException {
        Order order = new Order();
        order.setStatus(OrderStatus.RECEIVED.getName());
        when(mockOrderRpcService.updateOrderStatus(any(), anyLong()))
                .thenReturn(mockRpcOrderGoods(1L, 1L, 1, 3, 99, OrderStatus.DELIVERED));
        
        List<String> cookie = loginAndGetCookie();
        HttpResponse response = updateRequest("/api/v1/order/-1", order, cookie);
        assertEquals(SC_BAD_REQUEST, response.statusCode);
        
        response = updateRequest("/api/v1/order/1", order, cookie);
        assertEquals(SC_OK, response.statusCode);
    }
    
    @Test
    public void updateExpressInfo() throws JsonProcessingException {
        OrderInfo orderInfo = new OrderInfo();
        GoodsInfo goods1 = new GoodsInfo(1L, 3000);
        GoodsInfo goods2 = new GoodsInfo(2L, 5000);
        orderInfo.setGoods(Arrays.asList(goods1, goods2));
        Order order = new Order();
        order.setExpressCompany("sf");
        order.setExpressId("123456");
        RpcOrderGoods orderGoods = mockRpcOrderGoods(1L, 1, 1, 3, 99, OrderStatus.PENDING);
        when(mockOrderRpcService.getOrderById(1L))
                .thenReturn(orderGoods);
        //update express company failed
        List<String> cookieWithUser2 = loginAndGetCookieWithUser2();
        HttpResponse response = updateRequest("/api/v1/order/1", order, cookieWithUser2);
        Assertions.assertEquals(SC_FORBIDDEN, response.statusCode);
        //update success
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        when(mockOrderRpcService.updateExpressInformation(captor.capture()))
                .thenReturn(mockRpcOrderGoods(1L, 1L, 1, 3, 99, OrderStatus.DELIVERED));
        
        List<String> cookie = loginAndGetCookie();
        response = updateRequest("/api/v1/order/1", order, cookie);
        Response<OrderResponse> orderResponse = response.asJson(new TypeReference<Response<OrderResponse>>() {
        });
        assertEquals(OrderStatus.DELIVERED.getName(), orderResponse.getData().getStatus());
        assertEquals(OrderStatus.DELIVERED.getName(), captor.getValue().getStatus());
        
    }
    
    
    @Test
    public void deleteOrderSuccess() {
        when(mockOrderRpcService.orderService.deleteOrder(anyLong(), anyLong()))
                .thenReturn(mockRpcOrderGoods(1L, 1L, 1L, 2, 100, OrderStatus.DELETED));
        
        when(mockOrderRpcService.orderService.deleteOrder(anyLong(), anyLong()))
                .thenReturn(mockRpcOrderGoods(1L, 1L, 1L, 2, 99, OrderStatus.DELETED));
        List<String> cookie = loginAndGetCookie();
        
        HttpResponse response = deleteRequest("/api/v1/order/1", null, cookie);
        assertEquals(SC_NO_CONTENT, response.statusCode);
    }
    
    @Test
    public void GetPageResponse() {
        when(mockOrderRpcService.orderService.getOrders(anyLong(), anyInt(), anyInt(), any()))
                .thenReturn(mockPageRpcOrderGoods());
        List<String> cookie = loginAndGetCookie();
        
        HttpResponse response = getRequest("/api/v1/order?pageSize=2&pageNum=1", cookie);
        PageResponse<OrderResponse> orderResponse = response.asJson(new TypeReference<PageResponse<OrderResponse>>() {
        });
        assertEquals(SC_OK, response.statusCode);
        assertEquals(3, orderResponse.getTotalPage());
        assertEquals(2, orderResponse.getPageSize());
        assertEquals(2, orderResponse.getData().size());
    }
    
    public RpcOrderGoods mockRpcOrderGoods(long orderId,
                                           long shopId,
                                           long userId,
                                           long goodsId,
                                           int number,
                                           OrderStatus orderStatus) {
        RpcOrderGoods orderGoods = new RpcOrderGoods();
        Order order = new Order();
        GoodsInfo goodsInfo = new GoodsInfo();
        
        goodsInfo.setId(goodsId);
        goodsInfo.setNumber(number);
        
        order.setId(orderId);
        order.setUserId(userId);
        order.setShopId(shopId);
        order.setStatus(orderStatus.getName());
        
        orderGoods.setGoods(Arrays.asList(goodsInfo));
        orderGoods.setOrder(order);
        return orderGoods;
    }
    
    public PageResponse<RpcOrderGoods> mockPageRpcOrderGoods() {
        RpcOrderGoods orderGoods1 = mockRpcOrderGoods(1L, 1L, 1L, 2L, 99, OrderStatus.PENDING);
        RpcOrderGoods orderGoods2 = mockRpcOrderGoods(1L, 2L, 1L, 4L, 99, OrderStatus.PENDING);
        
        PageResponse<RpcOrderGoods> pageResponse = PageResponse.of(2, 1, 3, Arrays.asList(orderGoods1, orderGoods2));
        return pageResponse;
    }
    
}
