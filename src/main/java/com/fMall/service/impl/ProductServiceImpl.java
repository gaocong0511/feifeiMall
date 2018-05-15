package com.fMall.service.impl;

import com.fMall.common.Const;
import com.fMall.common.ResponseCode;
import com.fMall.common.ServerResponse;
import com.fMall.dao.CategoryMapper;
import com.fMall.dao.ProductMapper;
import com.fMall.pojo.Category;
import com.fMall.pojo.Product;
import com.fMall.service.IProductService;
import com.fMall.util.DateTimeUtil;
import com.fMall.util.PropertiesUtil;
import com.fMall.vo.ProductDetailVo;
import com.fMall.vo.ProductListVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 高琮 on 2018/5/9.
 */
@Service
public class ProductServiceImpl implements IProductService {

    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;

    @Autowired
    public ProductServiceImpl(ProductMapper productMapper, CategoryMapper categoryMapper) {
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
    }

    /**
     * 新增产品或者更新产品的信息
     *
     * @param product 产品信息
     * @return 统一返回对象
     */
    @Override
    public ServerResponse saveOrUpdateProduct(Product product) {
        if (product == null) {
            return ServerResponse.createByErrorMessage("增加或者更新产品参数不正确");
        }
        //如果子图不是空的的话，就把第一个子图赋值给主图
        if (StringUtils.isNotBlank(product.getSubImages())) {
            String[] subImageArray = product.getSubImages().split(",");
            if (subImageArray.length > 0) {
                product.setMainImage(subImageArray[0]);
            }
        }
        //如果是更新产品的话，那么传递过来的产品的信息之中product的id是一定有值的
        if (product.getId() != null) {
            int rowCount = productMapper.updateByPrimaryKey(product);
            if (rowCount > 0) {
                return ServerResponse.createBySuccessMessage("更新产品信息成功");
            } else {
                return ServerResponse.createByErrorMessage("更新产品信息失败");
            }
        } else {
            int rowCount = productMapper.insert(product);
            if (rowCount > 0) {
                return ServerResponse.createBySuccessMessage("新增产品成功");
            } else {
                return ServerResponse.createByErrorMessage("新增产品失败");
            }
        }
    }

    /**
     * 设置指定产品的产品状态
     *
     * @param productId 商品ID
     * @param status    商品现在的状态
     * @return 统一返回对象 String型
     */
    @Override
    public ServerResponse<String> setProductStatus(Integer productId, Integer status) {
        if (productId == null || status == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEAGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if (rowCount > 0) {
            return ServerResponse.createBySuccessMessage("更新产品信息成功");
        } else {
            return ServerResponse.createByErrorMessage("更新产品信息失败");
        }
    }

    /**
     * 获取当前商品的详细信息
     *
     * @param productId 商品的id
     * @return 返回转换而成的product vo对象
     */
    @Override
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGAL_ARGUMENT.getCode(), ResponseCode.ILLEAGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("产品已经被删除");
        }
        if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) {
            return ServerResponse.createByErrorMessage("产品已经下架");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    /**
     * 分页获取第N页指定数量的产品列表
     *
     * @param pageNum  页码
     * @param pageSize 每页含有的数量
     * @return 统一返回对象  使用mybatis pageHelper
     */
    @Override
    public ServerResponse<PageInfo> getProductList(Integer pageNum, Integer pageSize) {
        /**
         * myBatis pageHelper使用
         * 1.首先startPage
         * 2.填充sql查询逻辑
         * 3.pageHelper收尾
         */
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.getProductList();
        List<ProductListVo> productListVoList = new ArrayList<>();
        for (Product productItem : productList) {
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    /**
     * 根据传入的查询条件获取指定数量的产品列表  并进行分页
     *
     * @param productName 商品名称
     * @param productId   商品的id
     * @param pageNum     页码
     * @param pageSize    每页商品的个数
     * @return 统一返回对象
     */
    @Override
    public ServerResponse<PageInfo> searchProductByNameAndProductId(String productName, Integer productId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        if (StringUtils.isNotBlank(productName)) {
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.getProductByNameAndProductId(productName, productId);
        List<ProductListVo> productListVoList = new ArrayList<>();
        for (Product productItem : productList) {
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    /**
     * 将product对象转换成product vo对象
     *
     * @param product product对象
     * @return product vo对象
     */
    private ProductDetailVo assembleProductDetailVo(Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.happymmall.com/"));

        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null) {
            productDetailVo.setParentCategoryId(0);//默认根节点
        } else {
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }

    /**
     * 将product对象转换为productListVo对象
     *
     * @param product Product对象
     * @return 转换完成之后的productListVo对象
     */
    private ProductListVo assembleProductListVo(Product product) {
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.happymmall.com/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }
}
