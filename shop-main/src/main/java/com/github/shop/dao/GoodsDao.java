package com.github.shop.dao;

import com.github.shop.entity.ExistStatus;
import com.github.shop.entity.GoodsInfo;
import com.github.shop.entity.OrderInfo;
import com.github.shop.exception.BadRequestException;
import com.github.shop.exception.ResourceNotFoundException;
import com.github.shop.generate.Goods;
import com.github.shop.generate.GoodsExample;
import com.github.shop.generate.Shop;
import com.github.shop.generate.mapper.GoodsMapper;
import com.github.shop.generate.mapper.ShopMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GoodsDao {
    private final GoodsMapper goodsMapper;
    private final ShopMapper shopMapper;
    private final GoodsStockMapper goodsStockMapper;
    
    @Autowired
    public GoodsDao(GoodsMapper goodsMapper, ShopMapper shopMapper, GoodsStockMapper goodsStockMapper) {
        this.goodsMapper = goodsMapper;
        this.shopMapper = shopMapper;
        this.goodsStockMapper = goodsStockMapper;
    }
    
    public Goods createGoods(Goods goods) {
        goods.setCreateTime(Instant.now());
        goods.setUpdateTime(Instant.now());
        goodsMapper.insert(goods);
        return goods;
    }
    
    public Goods getGoodsById(Long goodsId) {
        return goodsMapper.selectByPrimaryKey(goodsId);
    }
    
    public Goods updateGoods(Goods goods) {
        goods.setUpdateTime(Instant.now());
        goodsMapper.updateByPrimaryKey(goods);
        return goods;
    }
    
    public List<Goods> getGoods(int pageSize, int pageNum, Long shopId) {
        GoodsExample goodsExample = new GoodsExample();
        goodsExample.createCriteria().andStatusEqualTo(ExistStatus.OK.getName());
        goodsExample.setLimit(pageSize);
        goodsExample.setOffset((pageNum - 1) * pageSize);
        if (shopId != null) {
            goodsExample.createCriteria().andShopIdEqualTo(shopId);
        }
        return goodsMapper.selectByExample(goodsExample);
    }
    
    public int getGoodsCount(Long shopId) {
        GoodsExample goodsExample = new GoodsExample();
        
        if (shopId != null) {
            Shop shop = shopMapper.selectByPrimaryKey(shopId);
            goodsExample.createCriteria().andStatusEqualTo(ExistStatus.OK.getName())
                    .andShopIdEqualTo(shopId);
            if (shop == null) {
                throw new ResourceNotFoundException("没有找到指定商店");
            }
        } else {
            goodsExample.createCriteria().andStatusEqualTo(ExistStatus.OK.getName());
        }
        return (int) goodsMapper.countByExample(goodsExample);
    }
    
    public List<Goods> getGoodsInList(List<Long> goodsIdList) {
        GoodsExample goodsExample = new GoodsExample();
        goodsExample.createCriteria().andIdIn(goodsIdList);
        
        return goodsMapper.selectByExample(goodsExample);
    }
    
    public Map<Long, Goods> getIdToGoodsMap(List<Long> goodsIdList) {
        GoodsExample example = new GoodsExample();
        example.createCriteria().andIdIn(goodsIdList);
        
        List<Goods> goodsList = goodsMapper.selectByExample(example);
        Map<Long, Goods> idToGoodsMap = goodsList.stream()
                .collect(Collectors.toMap(Goods::getId, goods -> goods));
        return idToGoodsMap;
    }
    
    @Transactional()
    public void deductStock(OrderInfo orderInfo) {
        List<GoodsInfo> goods = orderInfo.getGoods();
        for (GoodsInfo goodsInfo : goods) {
            if (goodsInfo.getNumber() <= 0) {
                throw new BadRequestException("商品: " + goodsInfo.getId() + " 数量必须为正");
            }
            if (goodsStockMapper.deductStock(goodsInfo) <= 0) {
                throw new BadRequestException("扣减库存失败，商品id: " + goodsInfo.getId() + "商品数量" + goodsInfo.getNumber());
            }
        }
    }
}
