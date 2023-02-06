package cn.tedu.mall.seckill.service.impl;

import cn.tedu.mall.pojo.seckill.model.SeckillSku;
import cn.tedu.mall.pojo.seckill.vo.SeckillSkuVO;
import cn.tedu.mall.product.service.seckill.IForSeckillSkuService;
import cn.tedu.mall.seckill.mapper.SeckillSkuMapper;
import cn.tedu.mall.seckill.service.ISeckillSkuService;
import cn.tedu.mall.seckill.utils.SeckillCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SeckillSkuServiceImpl implements ISeckillSkuService {
    @Autowired
    private SeckillSkuMapper skuMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    // sku常规信息的查询还是dubbo调用product模块获取
    @DubboReference
    private IForSeckillSkuService dubboSkuService;

    @Override
    public List<SeckillSkuVO> listSeckillSkus(Long spuId) {
        // 执行查询, 根据spuId查询sku列表
        List<SeckillSku> seckillSkus=skuMapper.findSeckillSkusBySpuId(spuId);
        // 上面查询返回值泛型为SeckillSku,是秒杀信息集合
        // 当前方法的返回值是SeckillSkuVO是包含秒杀信息和常规信息的对象
        // 我们先实例化返回值类型泛型的集合,以备后续返回时使用
        List<SeckillSkuVO> seckillSkuVOs=new ArrayList<>();
        // 遍历秒杀信息集合对象
        for(SeckillSku sku : seckillSkus){
            // 获取skuId后面会经常使用
            Long skuId=sku.getSkuId();
            // 获取sku对的key
            String skuVOKey= SeckillCacheUtils.getSeckillSkuVOKey(skuId);
            SeckillSkuVO seckillSkuVO=null;
            // 判断当前Redis中是否已经有这个key
            if(redisTemplate.hasKey(skuVOKey)){
                seckillSkuVO=(SeckillSkuVO)redisTemplate
                                 .boundValueOps(skuVOKey).get() ;
            }
        }
        return null;
    }
}
