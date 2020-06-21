package com.github.shop.service.impl;

import com.github.shop.controller.ShoppingCartController;
import com.github.shop.dao.GoodsDao;
import com.github.shop.dao.ShoppingCartDao;
import com.github.shop.entity.ExistStatus;
import com.github.shop.entity.GoodsWithNumber;
import com.github.shop.entity.PageResponse;
import com.github.shop.entity.ShoppingCartData;
import com.github.shop.exception.BadRequestException;
import com.github.shop.exception.ResourceNotFoundException;
import com.github.shop.generate.Goods;
import com.github.shop.generate.ShoppingCart;
import com.github.shop.generate.ShoppingCartExample;
import com.github.shop.generate.mapper.ShoppingCartMapper;
import com.github.shop.service.ShoppingCartService;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private Logger logger = LoggerFactory.getLogger(ShoppingCartServiceImpl.class);
    private ShoppingCartDao shoppingCartDao;
    private GoodsDao goodsDao;
    private SqlSessionFactory sqlSessionFactory;
    
    @Autowired
    public ShoppingCartServiceImpl(ShoppingCartDao shoppingCartDao, GoodsDao goodsDao, SqlSessionFactory sqlSessionFactory) {
        this.shoppingCartDao = shoppingCartDao;
        this.goodsDao = goodsDao;
        this.sqlSessionFactory = sqlSessionFactory;
    }
    
    //合并购物车中信息
    private static ShoppingCartData merge(List<ShoppingCartData> goodsInSameShop) {
        if (goodsInSameShop.size() == 0) {
            return null;
        }
        ShoppingCartData mergedResult = new ShoppingCartData();
        mergedResult.setGoods(new ArrayList<>());
        mergedResult.setShop(goodsInSameShop.get(0).getShop());
        for (ShoppingCartData shoppingCartData : goodsInSameShop) {
            for (GoodsWithNumber good : shoppingCartData.getGoods()) {
                mergedResult.getGoods().add(good);
            }
        }
        return mergedResult;
    }
    
    @Override
    public PageResponse<ShoppingCartData> getShoppingCartOfUser(Long id, int pageNum, int pageSize) {
        int totalShopNumber = shoppingCartDao.countShopNumberByUserId(id);
        int totalPage = totalShopNumber % pageSize == 0 ? totalShopNumber / pageSize : totalShopNumber / pageSize + 1;
        //将查找出的商品按店铺归类。
        List<ShoppingCartData> result = shoppingCartDao.getShoppingCartDataByUserId(id, pageNum, pageSize);
//                        .stream()
//                        .collect(Collectors.groupingBy(shoppingCartData -> shoppingCartData.getShop().getId()))
//                        .values()
//                        .stream()
//                        .map(ShoppingCartServiceImpl::merge)
//                        .collect(Collectors.toList());
        return PageResponse.of(pageSize, pageNum, totalPage, result);
    }
    
    @Override
    public ShoppingCartData deleteShoppingCart(long userId, Long goodsId) {
        List<ShoppingCart> shoppingCart = shoppingCartDao.getShoppingCartInfo(goodsId);
        if (shoppingCart == null || shoppingCart.size() == 0) {
            logger.debug("找不到商品 userId:{} goodsId:{}", userId, goodsId);
            throw new ResourceNotFoundException("在购物车中找不到对应商品");
        }
        //把状态设置为 deleted
        shoppingCart.forEach(item -> item.setStatus(ExistStatus.DELETED.getName()));
        
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            ShoppingCartMapper mapper = sqlSession.getMapper(ShoppingCartMapper.class);
            shoppingCart.forEach(item -> {
                mapper.updateByPrimaryKey(item);
            });
            sqlSession.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        long shopId = shoppingCart.get(0).getShopId();
        return shoppingCartDao.getShoppingCartDataByUserIdAndShopId(userId, shopId);
    }
    
    @Override
    public ShoppingCartData addGoodsToShoppingCart(Long userId, ShoppingCartController.ShoppingCartInfo goodsInfo) {
        List<Long> goodsIdList = goodsInfo.getGoods().stream()
                .map(ShoppingCartController.ShoppingCartItem::getGoodsId)
                .collect(Collectors.toList());
        //查到所有要添加的商品
        List<Goods> goodsList = goodsDao.getGoodsInList(goodsIdList);
        Map<Long, Goods> idToGoodsInstance = getIdToGoodsMap(goodsIdList);
        //校验输入参数合法
        if (idToGoodsInstance.values().stream()
                .map(Goods::getShopId)
                .collect(Collectors.toSet())
                .size() != 1) {
            logger.debug("输入不合法{} {} {}", userId, goodsIdList, goodsList);
            throw new BadRequestException("非法输入");
        }
        
        //生成需要插入的数据.
        List<ShoppingCart> goodsToAdd = rawDataToShoppingCartRow(userId, idToGoodsInstance, goodsInfo);
        Long shopId = goodsToAdd.get(0).getShopId();
        //批量插入购物车
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            ShoppingCartMapper mapper = sqlSession.getMapper(ShoppingCartMapper.class);
            goodsToAdd.forEach(shoppingCart -> {
                insertShoppingCartInfo(mapper, shoppingCart, userId);
            });
            sqlSession.commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //获取更新后购物车中当前商店中的商品
        return shoppingCartDao.getShoppingCartDataByUserIdAndShopId(userId, shopId);
    }
    
    private void insertShoppingCartInfo(ShoppingCartMapper mapper, ShoppingCart shoppingCart, Long userId) {
        ShoppingCartExample example = new ShoppingCartExample();
        example.createCriteria().andUserIdEqualTo(userId).andGoodsIdEqualTo(shoppingCart.getGoodsId());
        //删除购物车中已有的要添加的商品
        mapper.deleteByExample(example);
        mapper.insert(shoppingCart);
    }
    
    private List<ShoppingCart> rawDataToShoppingCartRow(long userId, Map<Long, Goods> goodsIdMap, ShoppingCartController.ShoppingCartInfo goodsInfo) {
        long shopId = goodsIdMap.values().stream().findFirst().get().getShopId();
        return goodsInfo.getGoods().stream()
                .filter(shoppingCartItem -> goodsIdMap.containsKey(shoppingCartItem.getGoodsId()))
                .map(inputShoppingCartItem -> {
                    ShoppingCart shoppingCartToInsert = new ShoppingCart();
                    shoppingCartToInsert.setShopId(shopId);
                    shoppingCartToInsert.setGoodsId(inputShoppingCartItem.getGoodsId());
                    shoppingCartToInsert.setNumber(inputShoppingCartItem.getNumber());
                    shoppingCartToInsert.setUserId(userId);
                    shoppingCartToInsert.setCreateTime(Instant.now());
                    shoppingCartToInsert.setUpdateTime(Instant.now());
                    shoppingCartToInsert.setStatus(ExistStatus.OK.getName());
                    return shoppingCartToInsert;
                })
                .collect(Collectors.toList());
    }
    
    private Map<Long, Goods> getIdToGoodsMap(List<Long> goodsIdList) {
        List<Goods> goodsList = goodsDao.getGoodsInList(goodsIdList);
        return goodsList.stream()
                .collect(Collectors.toMap(Goods::getId, goods -> goods));
    }
}
