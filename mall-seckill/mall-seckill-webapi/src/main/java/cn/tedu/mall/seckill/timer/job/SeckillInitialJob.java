package cn.tedu.mall.seckill.timer.job;

import cn.tedu.mall.seckill.mapper.SeckillSkuMapper;
import cn.tedu.mall.seckill.mapper.SeckillSpuMapper;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@Slf4j
public class SeckillInitialJob implements Job {

    // 查询sku信息的mapper
    @Autowired
    private SeckillSkuMapper skuMapper;
    // 查询spu相关信息的mapper
    @Autowired
    private SeckillSpuMapper spuMapper;
    // 操作Redis的对象
    @Autowired
    private RedisTemplate redisTemplate;

    /*
    RedisTemplate对象在保存数据到Redis时,会将数据先序列化之后再保存
    这样做,对java对象或类似的数据在Redis中的读写效率高,缺点是不能在redis中修改这个数据
    现在我们要保存的是秒杀sku的库存数,如果这个数也用RedisTemplate保存,也会有上面的问题
    容易在高并发的情况下,由于线程安全问题导致"超卖"
    解决办法就是我们需要创建一个能够直接在Redis中修改数据的对象,避免线程安全问题防止"超卖"
    SpringDataRedis提供了StringRedisTemplate类型,它是可以直接操作redis中字符串的
    使用StringRedisTemplate向Redis保存数据,直接存字符串值,没有序列化过程
    而且它支持java中直接发送指令修改数值类型的内容,所以适合保存库存数
    这样就避免了java代码中对库存数修改带来的线程安全问题
     */
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

    }
}
