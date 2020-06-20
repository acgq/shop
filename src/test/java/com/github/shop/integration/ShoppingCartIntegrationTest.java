package com.github.shop.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.shop.ShopApplication;
import com.github.shop.entity.PageResponse;
import com.github.shop.entity.ShoppingCartData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ShopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles("test")
public class ShoppingCartIntegrationTest extends AbstractIntegrationTest {
    
    
    @Test
    public void testGetShoppingCartByUserId() {
        List<String> cookie = loginAndGetCookie();
        
        String url = "/api/v1/shoppingCart?pageSize=1&pageNum=2";
        
        HttpResponse response = getRequest(url, cookie);
        PageResponse<ShoppingCartData> pageResponse = response.asJson(new TypeReference<PageResponse<ShoppingCartData>>() {
        });
        
        assertEquals(HttpServletResponse.SC_OK, response.statusCode);
        assertEquals(2, pageResponse.getTotalPage());
        assertEquals(1, pageResponse.getData().size());
        assertEquals(2, pageResponse.getData().get(0).getGoods().size());
        
        
        url = "/api/v1/shoppingCart?pageSize=1&pageNum=1";
        response = getRequest(url, cookie);
        pageResponse = response.asJson(new TypeReference<PageResponse<ShoppingCartData>>() {
        });
        
        assertEquals(HttpServletResponse.SC_OK, response.statusCode);
        assertEquals(2, pageResponse.getTotalPage());
        assertEquals(1, pageResponse.getData().size());
        assertEquals(1, pageResponse.getData().get(0).getGoods().size());
        
    }
}
