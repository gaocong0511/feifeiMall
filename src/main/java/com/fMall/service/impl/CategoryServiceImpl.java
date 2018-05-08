package com.fMall.service.impl;

import com.fMall.common.ServerResponse;
import com.fMall.dao.CategoryMapper;
import com.fMall.pojo.Category;
import com.fMall.service.ICategoryService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * Created by 高琮 on 2018/5/6.
 */
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryMapper categoryMapper;
    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    /**
     * 管理员添加用户品类
     *
     * @param categoryName 品类名称
     * @param parentId     父品类id
     * @return 统一返回对象
     */
    @Override
    public ServerResponse addCategory(String categoryName, Integer parentId) {
        if (parentId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);//分类可用

        int rowCount = categoryMapper.insert(category);
        if (rowCount > 0) {
            return ServerResponse.createBySuccessMessage("添加品类成功");
        }
        return ServerResponse.createByErrorMessage("添加品类失败");
    }

    /**
     * 更新品类的名称
     *
     * @param categoryId   品类ID
     * @param categoryName 新的品类的名称
     * @return 统一返回对象
     */
    @Override
    public ServerResponse updateCategoryName(Integer categoryId, String categoryName) {
        if (categoryId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (rowCount == 0) {
            return ServerResponse.createByErrorMessage("更新品类名称失败");
        }
        return ServerResponse.createBySuccess();
    }

    /**
     * 获得当前分类下面的所有分类(仅仅一层)
     *
     * @param categoryId 品类ID
     * @return 返回查询到的所有品类的集合
     */
    @Override
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId) {
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if (CollectionUtils.isEmpty(categoryList)) {
            logger.info("未找到当前节点的子节点");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    /**
     * 递归获得子品类
     *
     * @param categoryId 品类ID
     * @return 统一返回对象，返回查询到的所有品类的集合
     */
    @Override
    public ServerResponse<List<Integer>> getChildrenAndDeepChildrenById(Integer categoryId) {
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet, categoryId);

        List<Integer> categoryList = Lists.newArrayList();
        if (categoryId != null) {
            for (Category category : categorySet) {
                categoryList.add(category.getId());
            }

        }
        return ServerResponse.createBySuccess(categoryList);
    }

    /**
     * 递归时调用的方法
     *
     * @param categoryId 品类ID
     * @return 返回结果的集合
     */
    private Set<Category> findChildCategory(Set<Category> categorySet, Integer categoryId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null) {
            categorySet.add(category);
        }
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for (Category categoryItem : categoryList) {
            findChildCategory(categorySet, categoryItem.getId());
        }
        return categorySet;
    }
}
