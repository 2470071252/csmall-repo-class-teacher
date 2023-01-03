package cn.tedu.mall.front.service.impl;

import cn.tedu.mall.front.service.IFrontCategoryService;
import cn.tedu.mall.pojo.front.vo.FrontCategoryTreeVO;
import cn.tedu.mall.product.service.front.IForFrontCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FrontCategoryServiceImpl implements IFrontCategoryService {

    // front模块要dubbo调用product模块的方法,获取数据库中所有的分类信息
    @DubboReference
    private IForFrontCategoryService dubboCategoryService;

    // 装配操作Redis的对象
    @Autowired
    private RedisTemplate redisTemplate;

    // 在开发时,使用Redis规范:使用Key时这个Key必须是一个常量,避免拼写错误
    public static final String CATEGORY_TREE_KEY="category_tree";

    @Override
    public FrontCategoryTreeVO categoryTree() {

        return null;
    }
}
