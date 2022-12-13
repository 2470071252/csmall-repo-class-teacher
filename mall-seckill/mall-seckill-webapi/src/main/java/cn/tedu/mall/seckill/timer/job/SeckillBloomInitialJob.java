package cn.tedu.mall.seckill.timer.job;

import cn.tedu.mall.seckill.mapper.SeckillSpuMapper;
import cn.tedu.mall.seckill.utils.RedisBloomUtils;
import cn.tedu.mall.seckill.utils.SeckillCacheUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;


public class SeckillBloomInitialJob implements Job {

    // 装配操作布隆过滤器的类
    @Autowired
    private RedisBloomUtils redisBloomUtils;
    // 装配查询数据库中所有秒杀spu的id数组的mapper
    @Autowired
    private SeckillSpuMapper spuMapper;


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 这是缓存预热的方法,会在秒杀前运行,例如秒杀前5分钟
        // 实际业务中会查询即将开始的秒杀活动中所有商品的id数组
        // 如果有需求加载更多批次的秒杀商品数据,也可以查询更多批次,添加到布隆过滤器中
        // 我们设计秒杀是一天一个批次,预加载两个批次的数据
        // 所以我们可以使用日期做布隆过滤器的key
        // spu:bloom:filter:2022-12-13
        String bloomToDayKey=
                SeckillCacheUtils.getBloomFilterKey(LocalDate.now());
        // 再获取明天的key
        String bloomTomorrowKey=
                SeckillCacheUtils.getBloomFilterKey(LocalDate.now().plusDays(1));
        // 实际需求中,应该使用时间做条件,分别取查询今天和明天两个批次的商品spuId数组
        // 分别保存在上面不同的key中
        // 但是学习过程中,我们以使用布隆过滤器为目标,数据库中数据不支持多批次查询,所以只能做全查
        Long[] spuIds=spuMapper.findAllSeckillSpuIds();
        // 布隆过滤器保存信息必须是String类型数组
        // 我们现在是Long类型数组,所以需要进行转换
        String[] spuIdsStr=new String[spuIds.length];
        // 遍历spuIds数组,将其中元素转换为String后赋值给spuIdsStr数组
        for(int i=0;i<spuIds.length;i++){
            spuIdsStr[i]=spuIds[i]+"";
        }
        // 实际开发中,我们要对所有批次的商品都按上面转换的代码进行操作
        // 学习过程中,因为是全查所有数据,数据相同,所以操作一次即可
        // 模拟将两个批次的数据保存到布隆过滤器中
        redisBloomUtils.bfmadd(bloomToDayKey,spuIdsStr);
        redisBloomUtils.bfmadd(bloomTomorrowKey,spuIdsStr);
        System.out.println("两个批次的布隆过滤器加载完成");


    }
}
