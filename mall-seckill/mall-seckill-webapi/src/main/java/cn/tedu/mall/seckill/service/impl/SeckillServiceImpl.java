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

    // 秒杀业务中,需要使用redis的是判断用户重复购买和判断库存数,都是操作字符串的
    // 所以使用stringRedisTemplate
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    // 秒杀生成订单直接调用普通订单生成的方法即可,dubbo调用order模块
    @DubboReference
    private IOmsOrderService dubboOrderService;
    // 将秒杀成功信息发送给rabbitMQ
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
        // 要从方法参数seckillOrderAddDTO中获取skuId
        Long skuId=seckillOrderAddDTO.getSeckillOrderItemAddDTO().getSkuId();
        // 从SpringSecurity上下文中获取用户Id
        Long userId=getUserId();
        // 我们明确了本次请求是哪个用户要购买哪个sku商品(userId和skuId的值)
        // 根据秒杀业务规则,一个用户每件sku只能购买1次
        // 所以我们可以结合userId和skuId生成一个检查重复购买的key
        // mall:seckill:reseckill:2:1
        String reSeckillCheckKey= SeckillCacheUtils.getReseckillCheckKey(skuId,userId);
        // 用上面的key向redis中发送命令,利用stringRedisTemplate的increment()方法
        // 可以实现下面效果
        // 1.如果上面的key在Redis中不存在,redis中会创建这个key,并生成一个值,值为1
        // 2.如果上面的key在Redis中存在,那么就会在当前数值的基础上再加1后保存
        //      例如已经是1了,就变为2保存起来
        // 3.无论这个key存在与否,都会将最后的值返回给程序
        // 综上,只有用户之前没有调用这个方法,返回值才为1,为1才表示用户是第一次购买这个sku
        Long seckillTimes=stringRedisTemplate
                .boundValueOps(reSeckillCheckKey).increment();
        // 如果seckillTimes值大于1,就是用户已经购买过了
        if(seckillTimes>1){
            // 抛出异常,提示不能重复购买,终止程序
            throw new CoolSharkServiceException(
                    ResponseCode.FORBIDDEN,"您已经购买过这个商品了,谢谢您的支持");
        }
        // 程序运行到此处,表示当前用户是第一次购买这个商品
        // 下面判断当前sku是否还有库存
        // 库存数是在缓存预热是加载到Redis中的,要获取对应sku的key
        // mall:seckill:sku:stock:2
        String skuStockKey=SeckillCacheUtils.getStockKey(skuId);
        // 判断这个key是否存在
        if(!stringRedisTemplate.hasKey(skuStockKey)){
            // 如果key不存在,抛出异常
            throw new CoolSharkServiceException(
                    ResponseCode.INTERNAL_SERVER_ERROR,"缓存中没有库存信息,购买失败");
        }

        return null;
    }


    public CsmallAuthenticationInfo getUserInfo(){
        // 编写获取SpringSecurity上下文的代码
        UsernamePasswordAuthenticationToken authenticationToken=
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        // 为了逻辑严谨,判断一下SpringSecurity获取的用户信息是否为null
        if(authenticationToken == null){
            throw new CoolSharkServiceException(
                    ResponseCode.UNAUTHORIZED,"您没有登录");
        }
        // 从SpringSecurity上下文中获取用户信息
        CsmallAuthenticationInfo csmallAuthenticationInfo=
                (CsmallAuthenticationInfo)
                        authenticationToken.getCredentials();
        // 最后别忘了将用户信息返回
        return csmallAuthenticationInfo;
    }
    //  业务逻辑层中实际需求都是获取用户的id
    // 我们再一个方法,直接返回用户id,方便业务调用
    public Long getUserId(){
        return getUserInfo().getId();
    }

}
