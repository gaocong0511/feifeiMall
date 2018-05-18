package com.fMall.controller.backend;

import com.fMall.common.Const;
import com.fMall.common.ResponseCode;
import com.fMall.common.ServerResponse;
import com.fMall.pojo.Product;
import com.fMall.pojo.User;
import com.fMall.service.IFileService;
import com.fMall.service.IProductService;
import com.fMall.service.IUserService;
import com.fMall.util.PropertiesUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by 高琮 on 2018/5/9.
 */
@Controller
@RequestMapping(value = "/manage/product")
public class ProductManageController {

    private final IUserService iUserService;
    private final IProductService iProductService;
    private final IFileService iFileService;

    @Autowired
    public ProductManageController(IUserService iUserService, IProductService iProductService, IFileService iFileService) {
        this.iUserService = iUserService;
        this.iProductService = iProductService;
        this.iFileService = iFileService;
    }

    /**
     * 更新或者新增产品
     *
     * @param session HTTPSession对象
     * @param product 产品
     * @return 统一返回对象
     */
    @RequestMapping(value = "product_save.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "当前用户没有登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.saveOrUpdateProduct(product);
        } else {
            return ServerResponse.createByErrorMessage("当前登录的用户不是管理员，请使用管理员账户登录");
        }
    }

    /**
     * 产品上架与下架的功能
     *
     * @param session   HttpSession对象
     * @param productId 产品
     * @param status    产品现在的状态
     * @return 统一返回对象
     */
    @RequestMapping(value = "product_save_status.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse productSaveStatus(HttpSession session, Integer productId, Integer status) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "当前没有登陆的用户");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.setProductStatus(productId, status);
        } else {
            return ServerResponse.createByErrorMessage("当前登录的用户不是管理员，请使用管理员账户登录");
        }
    }


    /**
     * 获得产品的详细信息
     *
     * @param session   HTTPSession对象
     * @param productId 商品的ID
     * @return 统一返回对象
     */
    @RequestMapping(value = "get_product_detail.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getProductDetail(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "当前没有登陆的用户");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.getProductDetail(productId);
        } else {
            return ServerResponse.createByErrorMessage("当前登录的用户不是管理员，请使用管理员账户登录");
        }
    }

    /**
     * 获取商品列表
     *
     * @param session  HTTPSession对象
     * @param pageNum  请求的当前页的页数
     * @param pageSize 请求的每页所能容纳的每页的商品数量
     * @return 统一返回对象
     */
    @RequestMapping(value = "get_product_list.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getProductList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "当前没有登陆的用户");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.getProductList(pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("当前登录的用户不是管理员，请使用管理员账户登录");
        }
    }

    /**
     * 分页搜索产品列表
     *
     * @param session     HTTPSession对象
     * @param productName 产品名称
     * @param productId   产品ID
     * @param pageNum     页码
     * @param pageSize    每页包含产品的数量
     * @return 统一返回对象
     */
    @RequestMapping(value = "search_product.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse searchProduct(HttpSession session, String productName, Integer productId, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "当前没有登陆的用户");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.searchProductByNameAndProductId(productName, productId, pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("当前登录的用户不是管理员，请使用管理员账户登录");
        }
    }

    /**
     * 上传文件到服务器
     *
     * @param session HTTPSession对象
     * @param file    文件
     * @param request request请求
     * @return 统一返回对象
     */
    @RequestMapping("upload_file.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session, @RequestParam(value = "uploadFile", required = false) MultipartFile file, HttpServletRequest request) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "当前没有登陆的用户");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload_file");
            String targetFileName = iFileService.upload(file, path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

            Map fileMap = Maps.newHashMap();
            fileMap.put("uri", targetFileName);
            fileMap.put("url", url);
            return ServerResponse.createBySuccess(fileMap);
        } else {
            return ServerResponse.createByErrorMessage("当前登录的用户不是管理员，请使用管理员账户登录");
        }
    }

    /**
     * 富文本内容的图片上传部分
     *
     * @param session  HTTPSession对象
     * @param file     要上传的图片
     * @param request  request请求
     * @param response response返回
     * @return 返回包含有信息的map对象
     */
    @RequestMapping("richText_img_upload.do")
    @ResponseBody
    public Map uploadRichTextImg(HttpSession session, @RequestParam(value = "uploadFile", required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        Map resultMap = Maps.newHashMap();
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            resultMap.put("success", false);
            resultMap.put("msg", "请登录管理员");
            return resultMap;
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload_file");
            String targetFileName = iFileService.upload(file, path);
            if (StringUtils.isBlank(targetFileName)) {
                resultMap.put("success", false);
                resultMap.put("msg", "上传失败");
                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            resultMap.put("success", true);
            resultMap.put("msg", "上传成功");
            resultMap.put("file_path", url);
            response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
            return resultMap;
        } else {
            resultMap.put("success", false);
            resultMap.put("msg", "无权限操作");
            return resultMap;
        }
    }
}
