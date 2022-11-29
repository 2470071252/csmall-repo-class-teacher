package cn.tedu.mall.front.service.impl;

import cn.tedu.mall.front.service.IFrontCategoryService;
import cn.tedu.mall.pojo.front.entity.FrontCategoryEntity;
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

    // front模块要dubbo调用product模块的方法,实现查询所有分类对象集合
    @DubboReference
    private IForFrontCategoryService dubboCategoryService;

    // 装配操作redis的对象
    @Autowired
    private RedisTemplate redisTemplate;

    // 开发过程中,使用Redis时规范要求需要定义一个常量来作为Redis的key,避免拼写错误
    public static final String CATEGORY_TREE_KEY="category_tree";

    // 返回三级分类树对象的方法
    @Override
    public FrontCategoryTreeVO categoryTree() {
        // 先检查Redis中是否已经保存了三级分类树对象
        if(redisTemplate.hasKey(CATEGORY_TREE_KEY)){
            // redis中如果已经有了这个key直接获取即可
            FrontCategoryTreeVO<FrontCategoryEntity> treeVO =
                    (FrontCategoryTreeVO<FrontCategoryEntity>)
                    redisTemplate.boundValueOps(CATEGORY_TREE_KEY).get();
            // 将查询出的数据返回
            return treeVO;
        }

        return null;
    }
}
