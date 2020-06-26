package com.github.shop.generate.mapper;

import com.github.shop.generate.OrderGoodsMapping;
import com.github.shop.generate.OrderGoodsMappingExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderGoodsMappingMapper {
    long countByExample(OrderGoodsMappingExample example);

    int deleteByExample(OrderGoodsMappingExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OrderGoodsMapping record);

    int insertSelective(OrderGoodsMapping record);

    List<OrderGoodsMapping> selectByExample(OrderGoodsMappingExample example);

    OrderGoodsMapping selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") OrderGoodsMapping record, @Param("example") OrderGoodsMappingExample example);

    int updateByExample(@Param("record") OrderGoodsMapping record, @Param("example") OrderGoodsMappingExample example);

    int updateByPrimaryKeySelective(OrderGoodsMapping record);

    int updateByPrimaryKey(OrderGoodsMapping record);
}