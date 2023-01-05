package cn.tedu.mall.order.service.impl;

import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.order.mapper.OmsCartMapper;
import cn.tedu.mall.order.service.IOmsCartService;
import cn.tedu.mall.pojo.order.dto.CartAddDTO;
import cn.tedu.mall.pojo.order.dto.CartUpdateDTO;
import cn.tedu.mall.pojo.order.model.OmsCart;
import cn.tedu.mall.pojo.order.vo.CartStandardVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OmsCartServiceImpl implements IOmsCartService {

    @Autowired
    private OmsCartMapper omsCartMapper;

    @Override
    public void addCart(CartAddDTO cartDTO) {
        // 先按照参数中用户id和skuId进行查询
        // 判断当前用户购物车中是否已经有这个商品
        // 如果不存在这个商品进行新增操作
        // 如果已经存在执行数量的修改
    }

    @Override
    public JsonPage<CartStandardVO> listCarts(Integer page, Integer pageSize) {
        return null;
    }

    @Override
    public void removeCart(Long[] ids) {

    }

    @Override
    public void removeAllCarts() {

    }

    @Override
    public void removeUserCarts(OmsCart omsCart) {

    }

    @Override
    public void updateQuantity(CartUpdateDTO cartUpdateDTO) {

    }
}
