package cn.tedu.mall.front.service.impl;

import cn.tedu.mall.front.service.IFrontCategoryService;
import cn.tedu.mall.pojo.front.entity.FrontCategoryEntity;
import cn.tedu.mall.pojo.front.vo.FrontCategoryTreeVO;
import cn.tedu.mall.pojo.product.vo.CategoryStandardVO;
import cn.tedu.mall.product.service.front.IForFrontCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // Redis中没有三级分类树信息,表示本次请求可能是首次访问
        // 就需要从数据库中查询分类对象集合,再构建三级分类树,再保存到Redis的业务流程
        // dubbo调用查询所有分类对象的方法
        List<CategoryStandardVO> categoryStandardVOs =
                                dubboCategoryService.getCategoryList();
        // 记住CategoryStandardVO是没有children属性的,FrontCategoryEntity是有的!
        // 下面需要编写一个方法,将子分类对象保存到对应的父分类对象的children属性中
        // 大概思路就是先将CategoryStandardVO转换为FrontCategoryEntity类型,然后再将父子分类关联
        // 整个转换和关联的过程比较复杂,我们编写一个方法来完成
        FrontCategoryTreeVO<FrontCategoryEntity> treeVO=
                                            initTree(categoryStandardVOs);

        return null;
    }

    private FrontCategoryTreeVO<FrontCategoryEntity> initTree(
                            List<CategoryStandardVO> categoryStandardVOs) {
        // 第一步:
        // 确定所有分类的父分类id
        // 以父分类的id为Key,以子分类对象为value保存在一个Map中
        // 一个父分类可以包含多个子分类对象,所以这个map的value是个list
        Map<Long,List<FrontCategoryEntity>> map= new HashMap<>();
        log.info("准备构建的三级分类树对象数量为:{}",categoryStandardVOs.size());
        // 遍历数据库查询出来的所有分类集合对象
        for(CategoryStandardVO categoryStandardVO: categoryStandardVOs){
            // 因为CategoryStandardVO对象没有children属性,不能保存关联的子分类对象
            // 所以要将categoryStandardVO中的值赋值给能保存children属性的FrontCategoryEntity对象
            FrontCategoryEntity frontCategoryEntity=new FrontCategoryEntity();
            // 同名属性赋值
            BeanUtils.copyProperties(categoryStandardVO,frontCategoryEntity);
        }

        return null;
    }
}
