package cn.tedu.mall.seckill.timer.job;

import cn.tedu.mall.common.config.PrefixConfiguration;
import cn.tedu.mall.pojo.seckill.model.SeckillSku;
import cn.tedu.mall.pojo.seckill.model.SeckillSpu;
import cn.tedu.mall.seckill.mapper.SeckillSkuMapper;
import cn.tedu.mall.seckill.mapper.SeckillSpuMapper;
import cn.tedu.mall.seckill.utils.SeckillCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.RandomUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SeckillInitialJob implements Job {

    // 查询spu相关信息的mapper
    @Autowired
    private SeckillSpuMapper spuMapper;
    // 查询sku相关信息的mapper
    @Autowired
    private SeckillSkuMapper skuMapper;
    // 操作Redis的对象
    @Autowired
    private RedisTemplate redisTemplate;

    // 在秒杀开始前5分钟,进行秒杀信息的预热工作,将秒杀过程中的热点数据保存到Redis
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 先将5分钟后要开始进行秒杀的商品信息查询出来,所以要先获得一个5分钟后的时间对象
        LocalDateTime time=LocalDateTime.now().plusMinutes(5);
        // 查询这个时间进行秒杀的商品列表
        List<SeckillSpu> seckillSpus = spuMapper.findSeckillSpusByTime(time);
        // 遍历查询出的所有商品集合
        for (SeckillSpu spu : seckillSpus){
            // 要预热当前spu对应的所有秒杀sku中的库存数到Redis
            // 所以要根据spuId查询出它对应的sku列表
            List<SeckillSku> seckillSkus =
                            skuMapper.findSeckillSkusBySpuId(spu.getSpuId());
            // 再遍历seckillSkus集合,获取库存数
            for(SeckillSku sku: seckillSkus){
                log.info("开始将{}号sku商品的库存数预热到redis",sku.getSkuId());
                // 要想将库存数保存到Redis,先确定使用的key
                // SeckillCacheUtils.getStockKey是获取库存字符串常量的方法
                // 方法参数传入sku.getSkuId(),会追加在字符串常量之后
                // 最终skuStockKey的实际值可能是:   mall:seckill:sku:stock:1
                String skuStockKey=SeckillCacheUtils.getStockKey(sku.getSkuId());
                // 获取了key之后,检查Redis中是否已经包含这个key
                if(redisTemplate.hasKey(skuStockKey)){
                    // 如果这个Key已经存在了,证明之前已经完成了缓存,直接跳过即可
                    log.info("{}号sku的库存数已经缓存过了",sku.getSkuId());
                }else{
                    redisTemplate.boundValueOps(skuStockKey).set(
                            sku.getSeckillStock(),
                            // 秒杀时间+提前的5分钟+防雪崩随机数(30秒)
                            // 1000*60*60*2+1000*60*5+ RandomUtils.nextInt(30000),
                            1000*60*5+RandomUtils.nextInt(30000),
                            TimeUnit.MILLISECONDS);
                    log.info("{}号sku商品库存成功预热到Redis!",sku.getSkuId());
                }
            }
        }

    }
}
