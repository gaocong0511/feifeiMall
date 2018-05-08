package com.fMall.controller.backend;

import com.fMall.common.Const;
import com.fMall.common.ResponseCode;
import com.fMall.common.ServerResponse;
import com.fMall.pojo.User;
import com.fMall.service.ICategoryService;
import com.fMall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by 高琮 on 2018/5/6.
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    private final IUserService iUserService;
    private final ICategoryService iCategoryService;

    @Autowired
    public CategoryManageController(IUserService iUserService, ICategoryService iCategoryService) {
        this.iUserService = iUserService;
        this.iCategoryService = iCategoryService;
    }


    /**
     * 管理员向后台之中添加分类
     *
     * @param session      Session对象
     * @param categoryName 品类名称
     * @return 统一返回对象
     */
    @RequestMapping(value = "add_category.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId", defaultValue = "0") int parentId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "当前用户没有登录");
        }
        //校验管理员权限
        ServerResponse responseResult = iUserService.checkAdminRole(user);
        if (responseResult.isSuccess()) {
            return iCategoryService.addCategory(categoryName, parentId);
        } else {
            return ServerResponse.createByErrorMessage("需要管理员权限，当前用户无操作权限");
        }
    }

    /**
     * 更新某个品类的名称
     *
     * @param session      session对象
     * @param categoryId   品类Id
     * @param categoryName 新的品类名称
     * @return 统一返回对象
     */
    @RequestMapping(value = "update_categoryName.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse setCategoryName(HttpSession session, Integer categoryId, String categoryName) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "当前用户没有登录");
        }
        //校验管理员权限
        ServerResponse responseResult = iUserService.checkAdminRole(user);
        if (responseResult.isSuccess()) {
            //更新categoryName
            return iCategoryService.updateCategoryName(categoryId, categoryName);
        } else {
            return ServerResponse.createByErrorMessage("需要管理员权限，当前用户无操作权限");
        }
    }


    /**
     * 获取当前品类下的单层的子品类
     *
     * @param session    session 对象
     * @param categoryId 品类ID
     * @return 统一返回对象
     */
    @RequestMapping(value = "get_children_parallel_category.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "当前用户没有登录");
        }
        //校验管理员权限
        ServerResponse responseResult = iUserService.checkAdminRole(user);
        if (responseResult.isSuccess()) {
            return iCategoryService.getChildrenParallelCategory(categoryId);
        } else {
            return ServerResponse.createByErrorMessage("需要管理员权限，当前用户无操作权限");
        }
    }

    /**
     * 递归的获得当前节点下面的子节点
     * @param session session对象
     * @param categoryId 品类的id
     * @return 统一返回对象
     */
    @RequestMapping(value = "get_category_and_deep_children_category.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0")Integer categoryId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "当前用户没有登录");
        }
        //校验管理员权限
        ServerResponse responseResult = iUserService.checkAdminRole(user);
        if (responseResult.isSuccess()) {
            return iCategoryService.getChildrenAndDeepChildrenById(categoryId);
        } else {
            return ServerResponse.createByErrorMessage("需要管理员权限，当前用户无操作权限");
        }
    }
}
