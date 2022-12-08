package cn.tedu.mall.seckill.service.impl;

import cn.tedu.mall.pojo.product.vo.SkuStandardVO;
import cn.tedu.mall.pojo.seckill.model.SeckillSku;
import cn.tedu.mall.pojo.seckill.vo.SeckillSkuVO;
import cn.tedu.mall.product.service.seckill.IForSeckillSkuService;
import cn.tedu.mall.seckill.mapper.SeckillSkuMapper;
import cn.tedu.mall.seckill.service.ISeckillSkuService;
import cn.tedu.mall.seckill.utils.SeckillCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SeckillSkuServiceImpl implements ISeckillSkuService {
    @Autowired
    private SeckillSkuMapper skuMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    // sku常规信息要用dubbo调用product模块获取
    @DubboReference
    private IForSeckillSkuService dubboSkuServivce;

    @Override
    public List<SeckillSkuVO> listSeckillSkus(Long spuId) {
        // 执行查询,根据spuId获取sku列表
        List<SeckillSku> seckillSkus=skuMapper.findSeckillSkusBySpuId(spuId);
        // 当前方法的返回值集合泛型为SeckillSkuVO
        // 是既包含秒杀信息,又包含常规信息的对象
        // 我们先实例化这个集合,以备后面循环遍历时向其添加元素
        List<SeckillSkuVO> seckillSkuVOs=new ArrayList<>();
        // 遍历根据spuId查询出的sku列表集合
        for(SeckillSku sku : seckillSkus){
            // 获取skuId,后面会多次使用
            Long skuId=sku.getSkuId();
            // 获取当前sku对应的key
            String skuVOKey= SeckillCacheUtils.getSeckillSkuVOKey(skuId);
            // 声明SeckillSkuVO对象,并赋值null
            SeckillSkuVO seckillSkuVO=null;
            // 判断redis中是否包含这个key
            if(redisTemplate.hasKey(skuVOKey)){
                seckillSkuVO=(SeckillSkuVO)redisTemplate
                        .boundValueOps(skuVOKey).get();
            }else{
                // 如果redis中不存在这个key,就要查询数据库
                // 利用dubbo查询当前sku的常规信息
                SkuStandardVO skuStandardVO=dubboSkuServivce.getById(skuId);
                // 实例化seckillSkuVO对象
                seckillSkuVO=new SeckillSkuVO();
                // 常规属性同名赋值操作
                BeanUtils.copyProperties(skuStandardVO,seckillSkuVO);
                // 秒杀信息手动赋值
                seckillSkuVO.setSeckillPrice(sku.getSeckillPrice());
                seckillSkuVO.setStock(sku.getSeckillStock());
                seckillSkuVO.setSeckillLimit(sku.getSeckillLimit());
                // seckillSkuVO完成了秒杀信息和常规信息的赋值,保存在redis中
                redisTemplate.boundValueOps(skuVOKey)
                        .set(seckillSkuVO,5*60*1000+ RandomUtils.nextInt(10000),
                                            TimeUnit.MILLISECONDS);
            }
            // 在if-else结构结束后,确定获取了seckillSkuVO对象后
            // 将seckillSkuVO对象添加到集合中以便返回
            seckillSkuVOs.add(seckillSkuVO);
        }
        // 返回这个集合!!!!!
        return seckillSkuVOs;
    }
}
