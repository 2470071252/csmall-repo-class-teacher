package cn.tedu.mall.order.service.impl;

import cn.tedu.mall.common.exception.CoolSharkServiceException;
import cn.tedu.mall.common.pojo.domain.CsmallAuthenticationInfo;
import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.common.restful.ResponseCode;
import cn.tedu.mall.order.mapper.OmsOrderItemMapper;
import cn.tedu.mall.order.mapper.OmsOrderMapper;
import cn.tedu.mall.order.service.IOmsCartService;
import cn.tedu.mall.order.service.IOmsOrderService;
import cn.tedu.mall.order.utils.IdGeneratorUtils;
import cn.tedu.mall.pojo.order.dto.OrderAddDTO;
import cn.tedu.mall.pojo.order.dto.OrderListTimeDTO;
import cn.tedu.mall.pojo.order.dto.OrderStateUpdateDTO;
import cn.tedu.mall.pojo.order.model.OmsOrder;
import cn.tedu.mall.pojo.order.vo.OrderAddVO;
import cn.tedu.mall.pojo.order.vo.OrderDetailVO;
import cn.tedu.mall.pojo.order.vo.OrderListVO;
import cn.tedu.mall.product.service.order.IForOrderSkuService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

// 订单管理模块的业务逻辑层实现类,因为后期秒杀模块也是要生成订单的,需要dubbo调用这个方法
@DubboService
@Service
@Slf4j
public class OmsOrderServiceImpl implements IOmsOrderService {

    // dubbo调用减少库存的方法
    @DubboReference
    private IForOrderSkuService dubboSkuService;
    @Autowired
    private IOmsCartService omsCartService;
    @Autowired
    private OmsOrderMapper omsOrderMapper;
    @Autowired
    private OmsOrderItemMapper omsOrderItemMapper;

    // 新增订单的方法
    // 这个方法中利用Dubbo远程调用了product模块的数据库操作,有分布式事务需求
    // 所以使用注解激活Seata分布式事务的功能
    @GlobalTransactional
    @Override
    public OrderAddVO addOrder(OrderAddDTO orderAddDTO) {
        // 第一部分:收集信息,准备数据
        // 先实例化OmsOrder对象,最终实现新增订单到数据库的对象就是它
        OmsOrder order=new OmsOrder();
        // 将参数orderAddDTO中的同名属性赋值到order对象中
        BeanUtils.copyProperties(orderAddDTO,order);
        // orderAddDTO中包含的属性并不齐全,还有一些是可空内容,
        // order对象现在还缺失属性,我们可以编写一个方法来专门收集或生成
        loadOrder(order);
        // 第二部分:执行数据库操作指令
        // 第三部分:准备返回值,返回给前端
        return null;
    }

    // 为order对象补齐属性的方法
    private void loadOrder(OmsOrder order) {
        // order对象中的id和sn是没有被赋值的可能的,因为参数中根本就没有同名属性
        // 先给id赋值,这个id是从Leaf分布式序列号生成系统中获取的
        Long id= IdGeneratorUtils.getDistributeId("order");
        order.setId(id);
        // 再给sn赋值,sn赋值的原则是生成一个UUID,是给用户看的订单号
        order.setSn(UUID.randomUUID().toString());
        // 给userId赋值
        // 后期的秒杀业务也会调用当前类的新增订单方法,userId属性会在秒杀业务中被赋值
        // 因为dubbo远程调用时不能同时发送当前登录用户信息,
        // 所以我们要判断一下order中的userId是否为null
        if(order.getUserId() ==null){
            // 如果userId为null 就需要从SpringSecurity上下文获取用户id
            order.setUserId(getUserId());
        }
    }


    @Override
    public void updateOrderState(OrderStateUpdateDTO orderStateUpdateDTO) {

    }

    @Override
    public JsonPage<OrderListVO> listOrdersBetweenTimes(OrderListTimeDTO orderListTimeDTO) {
        return null;
    }

    @Override
    public OrderDetailVO getOrderDetail(Long id) {
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
