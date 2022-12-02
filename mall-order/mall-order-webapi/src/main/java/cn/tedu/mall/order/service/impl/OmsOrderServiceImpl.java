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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

//订单管理模块的业务逻辑层实现类,因为后面秒杀模块需要生成订单的功能,所以注册到dubbo
@DubboService
@Service
@Slf4j
public class OmsOrderServiceImpl implements IOmsOrderService {

    // dubbo调用减少库存数的方法
    @DubboReference
    private IForOrderSkuService dubboSkuService;
    @Autowired
    private IOmsCartService omsCartService;
    @Autowired
    private OmsOrderMapper omsOrderMapper;
    @Autowired
    private OmsOrderItemMapper omsOrderItemMapper;

    // 新增订单的方法
    // 这个方法dubbo调用了product模块的方法,操作了数据库,有分布式事务的需求
    // 需要使用注解激活Seata分布式事务的功能
    @GlobalTransactional
    @Override
    public OrderAddVO addOrder(OrderAddDTO orderAddDTO) {
        // 第一部分:收集信息,准备数据
        // 先实例化OmsOrder对象
        OmsOrder order=new OmsOrder();
        // 将参数orderAddDTO同名属性赋值到order对象中
        BeanUtils.copyProperties(orderAddDTO,order);
        // orderAddDTO中实际上只有一部分内容,order对象属性并不完全
        // 所以我们要编写一个方法,完成order未赋值属性的收集或生成
        loadOrder(order);


        // 第二部分:执行数据库操作指令
        // 第三部分:准备返回值,返回给前端
        return null;
    }
    // 为order对象补全属性值的方法
    private void loadOrder(OmsOrder order) {
        // 我们设计新增订单的功能使用Leaf分布式序列生成系统
        Long id= IdGeneratorUtils.getDistributeId("order");
        order.setId(id);
        // 生成用户看到的订单编号,即生成UUID
        order.setSn(UUID.randomUUID().toString());
        // 赋值userId
        // 以后秒杀业务会调用这个方法,userId属性会被赋值
        // 被远程调用时,当前方法无法获取登录用户信息,所以要判断一下order的userId是否为null
        if(order.getUserId() == null) {
            // 从SpringSecurity上下文中获取当前登录用户id
            order.setUserId(getUserId());
        }

        // 判断订单状态,如果为null,设置默认值为0
        if(order.getState() == null){
            order.setState(0);
        }

        // 为了保证下单时间gmt_order和数据创建gmt_create时间一致
        // 我们在代码中为它们赋相同的值
        LocalDateTime now=LocalDateTime.now();
        order.setGmtOrder(now);
        order.setGmtCreate(now);
        order.setGmtModified(now);

        // 后端代码对实际应付金额进行验算,以求和前端数据一致
        // 实际应付金额=原价-优惠+运费
        // 金钱相关数据使用BigDecimal类型,防止浮点偏移的误差,取消取值范围限制

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







