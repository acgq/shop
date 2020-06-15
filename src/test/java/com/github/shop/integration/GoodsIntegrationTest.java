package com.github.shop.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.shop.ShopApplication;
import com.github.shop.entity.Response;
import com.github.shop.generate.Goods;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static javax.servlet.http.HttpServletResponse.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ShopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles("test")
public class GoodsIntegrationTest extends AbstractIntegrationTest {

    private Goods goods;

    @BeforeEach
    public void setUpGoods() {
        goods = new Goods();
        goods.setName("west world");
        goods.setDescription("west world s03");
        goods.setDetails("a science fiction TV series");
        goods.setPrice(10000L);
        goods.setStock(100);
        goods.setShopId(1L);
        goods.setImgUrl("img.uri");
    }

    @Test
    public void createGoods() throws JsonProcessingException {
        List<String> cookies = loginAndGetCookie();
        //assert create goods successful.
        String goodsJson = objectMapper.writeValueAsString(goods);
        HttpResponse httpResponse = postRequest("/api/v1/goods", goodsJson, cookies);
        Assertions.assertEquals(SC_CREATED, httpResponse.statusCode);
        Response<Goods> goodsResponse = objectMapper.readValue(httpResponse.body, new TypeReference<Response<Goods>>() {
        });
        Assertions.assertNotNull(goodsResponse.getData().getId());

        //assert not authenticated to create goods in shop 3.
        goods.setShopId(3L);
        goodsJson = objectMapper.writeValueAsString(goods);
        httpResponse = postRequest("/api/v1/goods", goodsJson, cookies);
        Assertions.assertEquals(SC_FORBIDDEN, httpResponse.statusCode);
    }

    @Test
    public void updateAndDeleteGoods() throws JsonProcessingException {
        List<String> cookie = loginAndGetCookie();
        //get goods by id.
        //id is 1.
        HttpResponse response = getRequest("/api/v1/goods/1", cookie);

        Response<Goods> goodsResponse = objectMapper.readValue(response.body, new TypeReference<Response<Goods>>() {
        });
        Assertions.assertEquals(SC_OK, response.statusCode);
        Assertions.assertEquals(1L, goodsResponse.getData().getId());
        //update goods.
        String goodsJson = objectMapper.writeValueAsString(goods);
        response = updateRequest("/api/v1/goods/1", goodsJson, cookie);
        goodsResponse = objectMapper.readValue(response.body, new TypeReference<Response<Goods>>() {
        });
        Assertions.assertEquals(SC_OK, response.statusCode);
        Assertions.assertEquals(goods.getName(), goodsResponse.getData().getName());

        //delete goods
        response = deleteRequest("/api/v1/goods/1", "", cookie);
        Assertions.assertEquals(SC_NO_CONTENT, response.statusCode);
    }
}
