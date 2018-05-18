package com.fMall.dao;

import com.fMall.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> getProductList();

    List<Product> getProductByNameAndProductId(@Param("productName")String productName,@Param("productId")Integer productId);

    List<Product> getProductByNameAndCategoryIds(@Param("productName")String productName,
                                                 @Param("categoryIdList")List<Integer> categoryIdList);
}