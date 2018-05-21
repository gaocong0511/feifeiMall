package com.fMall.service.impl;

import com.fMall.common.Const;
import com.fMall.common.ResponseCode;
import com.fMall.common.ServerResponse;
import com.fMall.dao.CartMapper;
import com.fMall.dao.ProductMapper;
import com.fMall.pojo.Cart;
import com.fMall.pojo.Product;
import com.fMall.service.ICartService;
import com.fMall.util.BigDecimalUtil;
import com.fMall.util.PropertiesUtil;
import com.fMall.vo.CartProductVo;
import com.fMall.vo.CartVo;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by 高琮 on 2018/05/19
 */
public class CartServiceImpl implements ICartService {

    private final CartMapper cartMapper;
    private final ProductMapper productMapper;

    @Autowired
    public CartServiceImpl(CartMapper cartMapper, ProductMapper productMapper) {

        this.cartMapper = cartMapper;
        this.productMapper = productMapper;
    }

    /**
     * 向购物车之中增加商品
     *
     * @param userId    用户ID
     * @param productId 商品的ID
     * @param count     商品的数量
     * @return Cart的Vo对象
     */
    @Override
    public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEAGAL_ARGUMENT.getDesc());
        }

        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //当前这个产品不在这个用户的购物车之中
            Cart cartItem = new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartMapper.insert(cartItem);
        } else {
            //如果这个商品已经在这个用户的购物车里了，增加商品的数量
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return getCartList(userId);
    }


    /**
     * 获取当前用户的购物车之中的信息
     *
     * @param userId 用户的id
     * @return 购物车的vo对象
     */
    @Override
    public ServerResponse<CartVo> getCartList(Integer userId) {
        CartVo cartVo = getCartVoWithMaxLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    /**
     * 更新购物车之中商品的数量
     *
     * @param userId    用户的ID
     * @param productId 商品的ID
     * @param count     要更新商品的数量
     * @return
     */
    @Override
    public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGAL_ARGUMENT.getCode(), ResponseCode.ILLEAGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart != null) {
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKey(cart);
        return getCartList(userId);
    }

    /**
     * 批量删除购物车之中的商品
     *
     * @param userId     用户的id
     * @param productIds 用,分割的商品id的字符串
     * @return 删除完成之后刷新了的用户的购物车vo对象
     */
    @Override
    public ServerResponse<CartVo> deleteProduct(Integer userId, String productIds) {
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(productList)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGAL_ARGUMENT.getCode(), ResponseCode.ILLEAGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdProductIds(userId, productList);
        return getCartList(userId);
    }

    /**
     * 选中或者反选中购物车之中的某件商品
     *
     * @param userId    用户的ID
     * @param productId 商品的ID
     * @param checked   选中还是不选中该商品
     * @return
     */
    @Override
    public ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked) {
        cartMapper.checkOrUnCheckProduct(userId, productId, checked);
        return getCartList(userId);
    }

    /**
     * 获得当前用户已经加入购物车之中多少商品
     *
     * @param userId 用户的Id
     * @return 当前用户加入购物车之中商品的数量
     */
    @Override
    public ServerResponse<Integer> getCartProductCount(Integer userId) {
        if (userId == null) {
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }

    /**
     * 根据当前最大的余量来返回用户的购物车
     *
     * @param userId 用户的id
     * @return 购物车的vo对象
     */
    private CartVo getCartVoWithMaxLimit(Integer userId) {
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        BigDecimal cartTotalPrice = new BigDecimal("0");
        boolean allChecked = true;
        if (CollectionUtils.isNotEmpty(cartList)) {
            for (Cart cartItem : cartList) {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(cartItem.getProductId());

                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (product != null) {
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());

                    //判断当前的购物车之中的数量是不是够
                    int maxCountCanBuy = 0;
                    if (product.getStock() >= cartItem.getQuantity()) {
                        //目前的余量还是足够的
                        maxCountCanBuy = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    } else {
                        maxCountCanBuy = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);

                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(maxCountCanBuy);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(maxCountCanBuy);
                    //计算总的价格
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }
                if (cartItem.getChecked() == Const.Cart.CHECKED) {
                    //如果当前遍历到的商品时选中了的话，就增加到整个购物车的总价之中去
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                } else {
                    allChecked = false;
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(allChecked);
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }
}
