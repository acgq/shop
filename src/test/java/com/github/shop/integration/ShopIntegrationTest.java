package com.github.shop.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.shop.ShopApplication;
import com.github.shop.TestUtils;
import com.github.shop.entity.PageResponse;
import com.github.shop.entity.Response;
import com.github.shop.generate.Shop;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static javax.servlet.http.HttpServletResponse.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ShopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles("test")
public class ShopIntegrationTest extends AbstractIntegrationTest {
    
    @Test
    public void createShopAndUpdate() throws JsonProcessingException {
        List<String> cookie = loginAndGetCookie();
        //成功创建
        Shop shopInstance = TestUtils.createShopInstance(1L, 0);
        HttpResponse response = postRequest("/api/v1/shop", shopInstance, cookie);
        assertEquals(SC_CREATED, response.statusCode);
        Response<Shop> shopResponse = objectMapper.readValue(response.body, new TypeReference<Response<Shop>>() {
        });
        assertNotNull(shopResponse.getData().getCreateTime());
        //获取刚才创建的商店信息
        Long shopId = shopResponse.getData().getId();
        
        response = getRequest("/api/v1/shop/" + shopId, cookie);
        shopResponse = objectMapper.readValue(response.body, new TypeReference<Response<Shop>>() {
        });
        assertEquals(SC_OK, response.statusCode);
        //修改店铺
        shopInstance = TestUtils.createShopInstance(1L, 100);
        response = updateRequest("/api/v1/shop/" + shopId, shopInstance, null);
        assertEquals(SC_UNAUTHORIZED, response.statusCode);
        response = updateRequest("/api/v1/shop/" + shopId, shopInstance, cookie);
        assertEquals(SC_OK, response.statusCode);
        
        //删除店铺 fail
        // id 为3 的店铺主人 id 为 2
        response = deleteRequest("/api/v1/shop/3", "", cookie);
        assertEquals(SC_FORBIDDEN, response.statusCode);
        //删除店铺 success
        response = deleteRequest("/api/v1/shop/" + shopId, "", cookie);
        assertEquals(SC_NO_CONTENT, response.statusCode);
    }
    
    @Test
    public void getShopList() throws JsonProcessingException {
        List<String> cookie = loginAndGetCookie();
        //create 21 shop
        HttpResponse response;
        for (int i = 0; i < 21; i++) {
            response = postRequest("/api/v1/shop",
                    TestUtils.createShopInstance(1L, i),
                    cookie);
            assertEquals(SC_CREATED, response.statusCode);
        }
        
        response = getRequest("api/v1/shop?pageSize=0&pageNum=2", cookie);
        assertEquals(SC_BAD_REQUEST, response.statusCode);
        
        response = getRequest("api/v1/shop?pageSize=10&pageNum=2", cookie);
        PageResponse<Shop> listPageResponse = objectMapper.readValue(response.body, new TypeReference<PageResponse<Shop>>() {
        });
        assertEquals(3, listPageResponse.getTotalPage());
        assertEquals(10, listPageResponse.getData().size());
    }
}
