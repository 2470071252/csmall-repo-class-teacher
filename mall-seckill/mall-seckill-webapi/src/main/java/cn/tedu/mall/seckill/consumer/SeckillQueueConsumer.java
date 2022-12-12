package cn.tedu.mall.seckill.consumer;

import cn.tedu.mall.pojo.seckill.model.Success;
import cn.tedu.mall.seckill.config.RabbitMqComponentConfiguration;
import cn.tedu.mall.seckill.mapper.SeckillSkuMapper;
import cn.tedu.mall.seckill.mapper.SuccessMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = RabbitMqComponentConfiguration.SECKILL_QUEUE)
@Slf4j
public class SeckillQueueConsumer {

    @Autowired
    private SeckillSkuMapper seckillSkuMapper;
    @Autowired
    private SuccessMapper successMapper;

    // 下面开始编写接收消息队列中消息的方法
    @RabbitHandler
    public void process(Success success){
        // 先减少库存
        seckillSkuMapper.updateReduceStockBySkuId(
                success.getSkuId(),success.getQuantity());
        // 新增success对象到数据库
        successMapper.saveSuccess(success);
        // 如果上面两个数据库操作发生异常
        // 可能会引发事务问题,如果统计不需要非常精确,不处理也可以
        // 如果统计需要精确,发生异常后,可以编写重试代码,如果重试不行,可以考虑使用死信队列
        // 但是因为死信队列需要人工处理,所以实际开发慎用

    }



}






