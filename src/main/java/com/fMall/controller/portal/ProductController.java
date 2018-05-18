package com.fMall.controller.portal;

import com.fMall.common.ServerResponse;
import com.fMall.service.IProductService;
import com.fMall.vo.ProductDetailVo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by 高琮 on 2018/5/16.
 */
@Controller
@RequestMapping("/product/")
public class ProductController {
    private final IProductService iProductService;

    @Autowired
    public ProductController(IProductService iProductService) {
        this.iProductService = iProductService;
    }


    /**
     * 用户获得某个产品的详细信息
     *
     * @param productId 商品ID
     * @return 统一返回对象
     */
    @RequestMapping(value = "get_detail.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
        return iProductService.getProductDetailForUser(productId);
    }

    /**
     * 用户分页获取产品列表
     *
     * @param keyWord    关键词
     * @param categoryId 品类Id
     * @param pageNum    页码
     * @param pageSize   每页含有的商品数量
     * @param orderBy    排序方式
     * @return 用户查询到的一页的商品
     */
    @RequestMapping(value = "get_productList.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<PageInfo> getProductList(@RequestParam(value = "keyWord", required = false) String keyWord,
                                                   @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                                   @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                   @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                   @RequestParam(value = "orderBy", defaultValue = "") String orderBy) {
        return iProductService.getProductByKeyWordCategory(keyWord, categoryId, pageNum, pageSize, orderBy);
    }
}
