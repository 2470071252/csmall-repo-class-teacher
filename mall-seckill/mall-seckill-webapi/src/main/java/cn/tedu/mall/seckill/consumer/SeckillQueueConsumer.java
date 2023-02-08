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
import org.springframework.stereotype.Service;

@Component
@Slf4j
@RabbitListener(queues = RabbitMqComponentConfiguration.SECKILL_QUEUE)
public class SeckillQueueConsumer {

    @Autowired
    private SeckillSkuMapper skuMapper;
    @Autowired
    private SuccessMapper successMapper;

    // 下面方法是队列接收到消息时,运行的方法
    @RabbitHandler
    public void process(Success success){
        // 先减少库存
        skuMapper.updateReduceStockBySkuId(
                success.getSkuId(),success.getQuantity());
        // 新增success对象到数据库
        successMapper.saveSuccess(success);
        // 如果上面两个数据库操作发送异常,引发了事务问题
        // 1.如果不要求精确统计,不处理也可以
        // 2.如果要求精确统计,首先可以编写try-catch块进行连库操作重试
        //      如果重试再失败,可以将失败的情况汇总后,提交到死信队列
        // 因为死信队列是人工处理,所以效率不能保证,实际开发中要慎重的使用死信队列

    }




}
