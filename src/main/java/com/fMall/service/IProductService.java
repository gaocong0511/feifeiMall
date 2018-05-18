package com.fMall.service;

import com.fMall.common.ServerResponse;
import com.fMall.pojo.Product;
import com.fMall.vo.ProductDetailVo;
import com.github.pagehelper.PageInfo;

/**
 * Created by 高琮 on 2018/5/9.
 */
public interface IProductService {
    ServerResponse saveOrUpdateProduct(Product product);

    ServerResponse<String> setProductStatus(Integer productId,Integer status);

    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductList(Integer pageNum,Integer pageSize);

    ServerResponse<PageInfo> searchProductByNameAndProductId(String productName,Integer productId,int pageNum,int pageSize);

    ServerResponse<ProductDetailVo> getProductDetailForUser(Integer productId);

    ServerResponse<PageInfo> getProductByKeyWordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy);
}
