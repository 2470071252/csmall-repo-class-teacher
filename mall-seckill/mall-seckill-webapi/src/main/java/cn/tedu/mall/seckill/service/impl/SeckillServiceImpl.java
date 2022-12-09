package cn.tedu.mall.seckill.service.impl;

import cn.tedu.mall.common.exception.CoolSharkServiceException;
import cn.tedu.mall.common.pojo.domain.CsmallAuthenticationInfo;
import cn.tedu.mall.common.restful.ResponseCode;
import cn.tedu.mall.order.service.IOmsOrderService;
import cn.tedu.mall.pojo.seckill.dto.SeckillOrderAddDTO;
import cn.tedu.mall.pojo.seckill.vo.SeckillCommitVO;
import cn.tedu.mall.seckill.service.ISeckillService;
import cn.tedu.mall.seckill.utils.SeckillCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SeckillServiceImpl implements ISeckillService {

    // 秒杀业务中,使用Redis的代码都是在判断数值,直接使用字符串类型的Redis对象
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    // 需要dubbo调用mall_order模块的普通订单生成业务
    @DubboReference
    private IOmsOrderService dubboOrderService;
    //需要将秒杀成功信息发送给消息队列
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /*
    1.判断用户是否为重复购买和Redis中该Sku是否有库存
    2.秒杀订单转换成普通订单,需要使用dubbo调用order模块的生成订单方法
    3.使用消息队列(RabbitMQ)将秒杀成功记录信息保存到success表中
    4.秒杀订单信息返回
    */
    @Override
    public SeckillCommitVO commitSeckill(SeckillOrderAddDTO seckillOrderAddDTO) {
        // 第一阶段:判断用户是否为重复购买和Redis中该Sku是否有库存
        // 从方法的参数中,获得用户想要购买的skuId
        Long skuId=seckillOrderAddDTO.getSeckillOrderItemAddDTO().getSkuId();
        // 从SpringSecurity上下文中获得用户Id
        Long userId=getUserId();
        // 我们明确了本次请求是哪个用户要购买哪个商品(userId和skuId的值)
        // 根据秒杀业务限制,每个用户只能购买skuId一次
        // 所以可以根据userId和skuId生成检查重复购买的key
        // mall:seckill:reseckill:2:1
        String reSeckillCheckKey= SeckillCacheUtils.getReseckillCheckKey(skuId,userId);
        // 用上面字符串做key,向redis中发送命令使用stringRedisTemplate的increment方法
        // increment是增长的意思,这个方法效果如下
        // 1.如果上面的key在redis中不存在,redis中会使用这个key,创建一个值,值为1
        // 2.如果上面的key已经在redis中,那么就会在当前的值基础上加1再保存,例如当前已经是1,会变为2
        // 3.无论当前key存在不存在,都会将处理之后的值返回
        // 综上,只要用户调用这个方法返回值为1,就表示这个用户没有买过这个商品
        Long seckillTimes=stringRedisTemplate.
                boundValueOps(reSeckillCheckKey).increment();


        // 第二阶段:秒杀订单转换成普通订单,需要使用dubbo调用order模块的生成订单方法
        // 第三阶段:使用消息队列(RabbitMQ)将秒杀成功记录信息保存到success表中
        // 第四阶段:秒杀订单信息返回
        return null;
    }

    public CsmallAuthenticationInfo getUserInfo(){
        // 编写获取SpringSecurity上下文的代码
        UsernamePasswordAuthenticationToken authenticationToken=
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        // 为了逻辑严谨,判断一下SpringSecurity上下文中信息是不是null
        if(authenticationToken == null){
            throw new CoolSharkServiceException(
                    ResponseCode.UNAUTHORIZED,"您没有登录!");
        }
        // 从SpringSecurity上下文中获得用户信息
        CsmallAuthenticationInfo csmallAuthenticationInfo=
                (CsmallAuthenticationInfo) authenticationToken.getCredentials();
        // 最终别忘了返回
        return csmallAuthenticationInfo;
    }
    // 业务逻辑层需求中,实际上只需要用户的id
    // 我们可以再编写一个方法,从用户对象中获取id
    public Long getUserId(){
        return getUserInfo().getId();
    }
}
