package com.fMall.service.impl;

import com.fMall.common.ServerResponse;
import com.fMall.dao.ShippingMapper;
import com.fMall.pojo.Shipping;
import com.fMall.service.IShippingService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by 高琮 on 2018/5/24.
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private final ShippingMapper shippingMapper;

    public ShippingServiceImpl(ShippingMapper shippingMapper) {
        this.shippingMapper = shippingMapper;
    }

    /**
     * 新增收货地址
     *
     * @param userId   用户id
     * @param shipping 收货地址
     * @return 返回是不是新建成功的信息
     */
    @Override
    public ServerResponse add(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
        if (rowCount > 0) {
            Map result = Maps.newHashMap();
            result.put("shippingId", shipping.getId());
            return ServerResponse.createBySuccessMessage("新建地址成功", result);
        }
        return ServerResponse.createByErrorMessage("新建地址失败");
    }

    /**
     * 删除某用户的某个收货地址
     *
     * @param userId     用户Id
     * @param shippingId 收货地址Id
     * @return 返回是不是已经删除成功了的信息
     */
    @Override
    public ServerResponse del(Integer userId, Integer shippingId) {
        int resultCount = shippingMapper.deleteByShippingIdUserId(userId, shippingId);
        if (resultCount > 0) {
            return ServerResponse.createBySuccess("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }

    /**
     * 更新某个用户的收货地址
     *
     * @param userId   用户ID
     * @param shipping 收货地址
     * @return 返回是不是更新地址成功的消息
     */
    @Override
    public ServerResponse update(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int rowCount = shippingMapper.updateByShipping(shipping);
        if (rowCount > 0) {
            return ServerResponse.createBySuccess("更新地址成功");
        }
        return ServerResponse.createByErrorMessage("更新地址失败");
    }



    @Override
    public ServerResponse<Shipping> select(Integer userId, Integer shippingId) {
        Shipping shipping=shippingMapper.selectByShippingIdAndUserId(userId, shippingId);
        if(shipping==null){
            return ServerResponse.createByErrorMessage("无法查询到该地址");
        }return ServerResponse.createBySuccessMessage("更新地址成功",shipping);
    }

    /**
     * 查询某用户的所有的收货地址
     * @param userId 用户的Id
     * @param pageNum 页码数
     * @param pageSize 每一页的数量
     * @return 收货地址的集合
     */
    @Override
    public ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
