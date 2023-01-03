package cn.tedu.mall.front.service.impl;

import cn.tedu.mall.front.service.IFrontCategoryService;
import cn.tedu.mall.pojo.front.entity.FrontCategoryEntity;
import cn.tedu.mall.pojo.front.vo.FrontCategoryTreeVO;
import cn.tedu.mall.pojo.product.vo.CategoryStandardVO;
import cn.tedu.mall.product.service.front.IForFrontCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // 先检查Redis中是否已经保存了三级分类树对象
        if(redisTemplate.hasKey(CATEGORY_TREE_KEY)){
            // redis中如果已经有了这个key直接获取即可
            FrontCategoryTreeVO<FrontCategoryEntity> treeVO=
                    (FrontCategoryTreeVO<FrontCategoryEntity>)
                    redisTemplate.boundValueOps(CATEGORY_TREE_KEY).get();
            // 将从Redis中查询出的对象返回
            return treeVO;
        }
        // Redis中如果没有三级分类树信息,表示本次情况可能是首次访问
        // 就需要从数据库中查询分类对象结合后,构建三级分类树,再保存到Redis中了
        // Dubbo调用查询所有分类对象的方法
        List<CategoryStandardVO> categoryStandardVOs =
                        dubboCategoryService.getCategoryList();
        // CategoryStandardVO是没有children属性的,FrontCategoryEntity是有children属性的
        // 下面编写一个专门的方法,用于构建三级分类树对象
        // 大概思路就是先将CategoryStandardVO类型对象转换为FrontCategoryEntity
        // 然后在进行正确的父子关联
        // 整个转换的过程比较复杂,所以我们单独编写一个方法
        FrontCategoryTreeVO<FrontCategoryEntity> treeVO=
                                        initTree(categoryStandardVOs);

        return null;
    }

    private FrontCategoryTreeVO<FrontCategoryEntity> initTree(List<CategoryStandardVO> categoryStandardVOs) {
        // 第一步:
        // 确定所有分类id包含的子分类对象
        // 我们可以以分类id作为Key,这个分类对象包含的所有子分类作为Value,保存到Map中
        // 因为一个分类对象可以包含多个子分类,所以这个Map的value是List类型
        Map<Long,List<FrontCategoryEntity>> map=new HashMap<>();
        log.info("准备构建三级分类树,节点数量为:{}",categoryStandardVOs.size());

        return null;



    }
}
