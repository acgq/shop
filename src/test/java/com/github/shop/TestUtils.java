package com.github.shop;

import com.github.shop.generate.Goods;
import com.github.shop.generate.Shop;

public class TestUtils {
    
    public static Shop createShopInstance(Long ownerId, int customNum) {
        Shop shop = new Shop();
        shop.setName("我的小店 " + customNum);
        shop.setOwnerUserId(ownerId);
        shop.setDescription("我新开的小店");
        shop.setImgUrl("Http://img.im");
        
        return shop;
    }
    public static Goods createGoodsInstance(Long shopId, int customNum) {
        Goods goods = new Goods();
        goods.setShopId(shopId);
        goods.setName("我的商品" + customNum);
        goods.setPrice(1000L);
        goods.setStock(1000);
        goods.setImgUrl("http://img.img");
        goods.setDescription("我的商品很棒");
        goods.setDetails("商品详情");
        return goods;
    }
}
