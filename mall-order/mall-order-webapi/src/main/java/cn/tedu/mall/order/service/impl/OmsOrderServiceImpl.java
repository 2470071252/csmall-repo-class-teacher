package cn.tedu.mall.order.service.impl;

import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.order.mapper.OmsOrderItemMapper;
import cn.tedu.mall.order.mapper.OmsOrderMapper;
import cn.tedu.mall.order.service.IOmsCartService;
import cn.tedu.mall.order.service.IOmsOrderService;
import cn.tedu.mall.pojo.order.dto.OrderAddDTO;
import cn.tedu.mall.pojo.order.dto.OrderListTimeDTO;
import cn.tedu.mall.pojo.order.dto.OrderStateUpdateDTO;
import cn.tedu.mall.pojo.order.vo.OrderAddVO;
import cn.tedu.mall.pojo.order.vo.OrderDetailVO;
import cn.tedu.mall.pojo.order.vo.OrderListVO;
import cn.tedu.mall.product.service.order.IForOrderSkuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public OrderAddVO addOrder(OrderAddDTO orderAddDTO) {
        return null;
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
}







