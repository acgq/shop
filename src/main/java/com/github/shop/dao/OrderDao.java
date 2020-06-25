package com.github.shop.dao;

import com.github.shop.entity.GoodsWithNumber;
import com.github.shop.entity.OrderInfo;
import com.github.shop.exception.BadRequestException;
import com.github.shop.generate.Order;
import com.github.shop.generate.mapper.OrderMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OrderDao {
    private GoodsDao goodsDao;
    private ShopDao shopDao;
    private CustomOrderMapper customOrderMapper;
    private SqlSessionFactory sqlSessionFactory;
    private OrderMapper orderMapper;
    
    private static Logger logger = LoggerFactory.getLogger(OrderDao.class);
    
    public OrderDao(GoodsDao goodsDao,
                    ShopDao shopDao,
                    CustomOrderMapper customOrderMapper,
                    SqlSessionFactory sqlSessionFactory, OrderMapper orderMapper) {
        this.goodsDao = goodsDao;
        this.shopDao = shopDao;
        this.customOrderMapper = customOrderMapper;
        this.sqlSessionFactory = sqlSessionFactory;
        this.orderMapper = orderMapper;
    }
    
    public Order insertOrder(Order order) {
        orderMapper.insert(order);
        return order;
    }
    
    public void deductStock(OrderInfo orderInfo) {
        //扣减库存
        try (SqlSession sqlSession = sqlSessionFactory.openSession(false)) {
            CustomOrderMapper mapper = sqlSession.getMapper(CustomOrderMapper.class);
            for (GoodsWithNumber goodsInfo : orderInfo.getGoods()) {
                //assert goods number is positive
                if (goodsInfo.getNumber() <= 0) {
                    throw new BadRequestException("商品: " + goodsInfo.getId() + " 数量必须为正");
                }
                if (mapper.deductStock(goodsInfo) <= 0) {
                    throw new BadRequestException("扣减库存失败，商品id: " + goodsInfo.getId() + "商品数量" + goodsInfo.getNumber());
                }
            }
            sqlSession.commit();
        }
    }
    
    public void insertOrderInfo(OrderInfo orderInfo) {
        customOrderMapper.insertOrderInfo(orderInfo);
    }
}
