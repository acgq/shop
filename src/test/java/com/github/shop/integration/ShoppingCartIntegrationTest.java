package com.github.shop.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.shop.ShopApplication;
import com.github.shop.controller.ShoppingCartController;
import com.github.shop.entity.GoodsWithNumber;
import com.github.shop.entity.PageResponse;
import com.github.shop.entity.Response;
import com.github.shop.entity.ShoppingCartData;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ShopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ShoppingCartIntegrationTest extends AbstractIntegrationTest {
    
    
    @Test
    public void canGetShoppingCartDataByUserId() {
        List<String> cookie = loginAndGetCookie();
        
        String url = "/api/v1/shoppingCart?pageSize=1&pageNum=2";
        
        HttpResponse response = getRequest(url, cookie);
        PageResponse<ShoppingCartData> pageResponse = response.asJson(new TypeReference<PageResponse<ShoppingCartData>>() {
        });
        
        assertEquals(SC_OK, response.statusCode);
        assertEquals(2, pageResponse.getTotalPage());
        assertEquals(1, pageResponse.getData().size());
        assertEquals(2, pageResponse.getData().get(0).getGoods().size());
        
        
        url = "/api/v1/shoppingCart?pageSize=1&pageNum=1";
        response = getRequest(url, cookie);
        pageResponse = response.asJson(new TypeReference<PageResponse<ShoppingCartData>>() {
        });
        
        assertEquals(SC_OK, response.statusCode);
        assertEquals(2, pageResponse.getTotalPage());
        assertEquals(1, pageResponse.getData().size());
        assertEquals(1, pageResponse.getData().get(0).getGoods().size());
        
    }
    
    @Test
    public void canAddShoppingCartInfo() throws JsonProcessingException {
        List<String> cookie = loginAndGetCookie();
        
        ShoppingCartController.ShoppingCartInfo shoppingCartInfo = new ShoppingCartController.ShoppingCartInfo();
        ShoppingCartController.ShoppingCartItem goodsToInsert = new ShoppingCartController.ShoppingCartItem();
        goodsToInsert.setGoodsId(5L);
        goodsToInsert.setNumber(99);
        shoppingCartInfo.setGoods(Collections.singletonList(goodsToInsert));
        
        String json = objectMapper.writeValueAsString(shoppingCartInfo);
        
        HttpResponse response = postRequest("/api/v1/shoppingCart", json, cookie);
        Response<ShoppingCartData> shoppingCartData = response.asJson(new TypeReference<Response<ShoppingCartData>>() {
        });
        
        assertEquals(SC_OK, response.statusCode);
        assertEquals(2, shoppingCartData.getData().getShop().getId());
        assertEquals(3, shoppingCartData.getData().getGoods().size());
        assertEquals(Sets.newHashSet(500, 100, 99), shoppingCartData.getData().getGoods().stream()
                .map(GoodsWithNumber::getNumber)
                .collect(Collectors.toSet()));
        // add same goods again with number 1000
        goodsToInsert.setNumber(1000);
        json = objectMapper.writeValueAsString(shoppingCartInfo);
        response = postRequest("/api/v1/shoppingCart", json, cookie);
        shoppingCartData = response.asJson(new TypeReference<Response<ShoppingCartData>>() {
        });
        
        assertEquals(SC_OK, response.statusCode);
        assertEquals(3, shoppingCartData.getData().getGoods().size());
        assertEquals(Sets.newHashSet(500, 100, 1000), shoppingCartData.getData().getGoods().stream()
                .map(GoodsWithNumber::getNumber)
                .collect(Collectors.toSet()));
    }
    
    @Test
    public void canDeleteShoppingCartData() {
        List<String> cookie = loginAndGetCookie();
        
        HttpResponse response = deleteRequest("/api/v1/shoppingCart/4", "", cookie);
        Response<ShoppingCartData> shoppingCartData = response.asJson(new TypeReference<Response<ShoppingCartData>>() {
        });
        
        assertEquals(SC_OK, response.statusCode);
        assertEquals(2, shoppingCartData.getData().getShop().getId());
        assertEquals(1, shoppingCartData.getData().getGoods().size());
        assertEquals(500, shoppingCartData.getData().getGoods().get(0).getNumber());
        
        //delete goods with number 1
        response = deleteRequest("/api/v1/shoppingCart/1", "", cookie);
        shoppingCartData = response.asJson(new TypeReference<Response<ShoppingCartData>>() {
        });
        
        assertEquals(SC_OK, response.statusCode);
        assertEquals(null, shoppingCartData.getData());
    }
    
}
