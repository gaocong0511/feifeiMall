package com.fMall.service;

import com.fMall.common.ServerResponse;
import com.fMall.pojo.Category;

import java.util.List;

/**
 * Created by 高琮 on 2018/5/6.
 */
public interface ICategoryService {
    ServerResponse addCategory(String categoryName, Integer parentId);

    ServerResponse updateCategoryName(Integer categoryId,String categoryName);

    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    ServerResponse<List<Integer>> getChildrenAndDeepChildrenById(Integer categoryId);
}
