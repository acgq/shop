package com.github.shop.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.shop.ShopApplication;
import com.github.shop.TestUtils;
import com.github.shop.entity.PageResponse;
import com.github.shop.entity.Response;
import com.github.shop.generate.Goods;
import com.github.shop.generate.Shop;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static javax.servlet.http.HttpServletResponse.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ShopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles("test")
public class GoodsIntegrationTest extends AbstractIntegrationTest {
    
    private Goods goods;
    
    @BeforeEach
    public void setUpGoods() {
        goods = TestUtils.createGoodsInstance(1L, 0);
    }
    
    @Test
    public void createGoods() throws JsonProcessingException {
        List<String> cookies = loginAndGetCookie();
        //assert create goods successful.
        String goodsJson = objectMapper.writeValueAsString(goods);
        HttpResponse httpResponse = postRequest("/api/v1/goods", goodsJson, cookies);
        assertEquals(SC_CREATED, httpResponse.statusCode);
        Response<Goods> goodsResponse = objectMapper.readValue(httpResponse.body, new TypeReference<Response<Goods>>() {
        });
        Assertions.assertNotNull(goodsResponse.getData().getId());
        
        //assert not authenticated to create goods in shop 3.
        goods.setShopId(3L);
        goodsJson = objectMapper.writeValueAsString(goods);
        httpResponse = postRequest("/api/v1/goods", goodsJson, cookies);
        assertEquals(SC_FORBIDDEN, httpResponse.statusCode);
    }
    
    @Test
    public void updateAndDeleteGoods() throws JsonProcessingException {
        List<String> cookie = loginAndGetCookie();
        //get goods by id.
        //id is 1.
        HttpResponse response = getRequest("/api/v1/goods/1", cookie);
        
        Response<Goods> goodsResponse = objectMapper.readValue(response.body, new TypeReference<Response<Goods>>() {
        });
        assertEquals(SC_OK, response.statusCode);
        assertEquals(1L, goodsResponse.getData().getId());
        //update goods.
        String goodsJson = objectMapper.writeValueAsString(goods);
        response = updateRequest("/api/v1/goods/1", goodsJson, cookie);
        goodsResponse = objectMapper.readValue(response.body, new TypeReference<Response<Goods>>() {
        });
        assertEquals(SC_OK, response.statusCode);
        assertEquals(goods.getName(), goodsResponse.getData().getName());
        
        //delete goods
        response = deleteRequest("/api/v1/goods/1", "", cookie);
        assertEquals(SC_NO_CONTENT, response.statusCode);
    }
    
    @Test
    public void getGoodsList() throws JsonProcessingException {
        List<String> cookies = loginAndGetCookie();
        //create shop
        Shop shop = TestUtils.createShopInstance(1L, 0);
        HttpResponse response = postRequest("/api/v1/shop", objectMapper.writeValueAsString(shop), cookies);
        
        Response<Shop> shopResponse = objectMapper.readValue(response.body, new TypeReference<Response<Shop>>() {
        });
        
        assertEquals(SC_CREATED, response.statusCode);
        assertEquals(shopResponse.getData().getOwnerUserId(), 1L);
        //create 20 goods
        Long shopId = shopResponse.getData().getId();
        for (int i = 0; i < 20; i++) {
            Goods goods = TestUtils.createGoodsInstance(shopId, i);
            int statusCode = postRequest("/api/v1/goods", objectMapper.writeValueAsString(goods), cookies).statusCode;
            assertEquals(statusCode, SC_CREATED);
        }
        
        //get goods list
        HttpResponse getResponse = getRequest("/api/v1/goods?pageSize=10&pageNum=2&shopId=" + shopId, cookies);
        
        assertEquals(SC_OK, getResponse.statusCode);
        PageResponse<Goods> pageResponse = objectMapper.readValue(getResponse.body, new TypeReference<PageResponse<Goods>>() {
        });
        assertEquals(2, pageResponse.getTotalPage());
        assertEquals(2, pageResponse.getPageNum());
        assertEquals(10, pageResponse.getData().size());
    }
}
