package com.github.shop.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.shop.ShopApplication;
import com.github.shop.entity.GoodsWithNumber;
import com.github.shop.entity.OrderInfo;
import com.github.shop.entity.OrderResponse;
import com.github.shop.entity.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ShopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles("test")
public class OrderIntegrationTest extends AbstractIntegrationTest {
    
    @Test
    public void createOrderSuccess() throws JsonProcessingException {
        List<String> cookie = loginAndGetCookie();
        //arrange data
        OrderInfo orderInfo = new OrderInfo();
        GoodsWithNumber goods1 = new GoodsWithNumber();
        goods1.setId(1L);
        goods1.setNumber(10);
        GoodsWithNumber goods2 = new GoodsWithNumber();
        goods2.setId(2L);
        goods2.setNumber(100);
        orderInfo.setGoods(Arrays.asList(goods1, goods2));
        
        String json = objectMapper.writeValueAsString(orderInfo);
        
        HttpResponse response = postRequest("/api/v1/order", json, cookie);
        assertEquals(SC_CREATED, response.statusCode);
        Response<OrderResponse> orderResponse = response.asJson(new TypeReference<Response<OrderResponse>>() {
        });
        assertTrue(orderResponse.getData().getShopId() != null);
        assertTrue(orderResponse.getData().getShop() != null);
        assertEquals(2, orderResponse.getData().getGoods().size());
    }
}
