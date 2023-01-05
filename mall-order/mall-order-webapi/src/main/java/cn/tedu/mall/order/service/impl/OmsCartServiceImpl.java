package cn.tedu.mall.order.service.impl;

import cn.tedu.mall.common.exception.CoolSharkServiceException;
import cn.tedu.mall.common.pojo.domain.CsmallAuthenticationInfo;
import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.common.restful.ResponseCode;
import cn.tedu.mall.order.mapper.OmsCartMapper;
import cn.tedu.mall.order.service.IOmsCartService;
import cn.tedu.mall.pojo.order.dto.CartAddDTO;
import cn.tedu.mall.pojo.order.dto.CartUpdateDTO;
import cn.tedu.mall.pojo.order.model.OmsCart;
import cn.tedu.mall.pojo.order.vo.CartStandardVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OmsCartServiceImpl implements IOmsCartService {

    @Autowired
    private OmsCartMapper omsCartMapper;

    @Override
    public void addCart(CartAddDTO cartDTO) {
        // 要查询当前登录用户的购物车中是否已经包含指定商品,要先获取当前登录用id
        // 单独编写一个方法,从SpringSecurity上下文中获取用户id
        Long userId=getUserId();
        // 先按照参数中用户id和skuId进行查询
        OmsCart omsCart=omsCartMapper
                .selectExistsCart(userId,cartDTO.getSkuId());
        // 判断当前用户购物车中是否已经有这个商品
        if(omsCart == null){
            // 如果不存在这个商品进行新增操作
            // 新增购物车需要的参数类型是OmsCart,所以要先实例化一个OmsCart对象
            OmsCart newCart=new OmsCart();
            // 将参数cartDTO中的同名属性赋值到newCart对象中
            BeanUtils.copyProperties(cartDTO,newCart);
            // cartDTO对象中没有userId属性,需要单独为newCart赋值
            newCart.setUserId(userId);
            // 执行新增操作,新增sku信息到购物车表
            omsCartMapper.saveCart(newCart);
        }else{
            // 如果已经存在执行数量的修改
            // 运行到这里omsCart一定不是null
            // 我们需要做的就是将购物车中原有的数量和本次新增的商品数量相加
            // 再赋值到当前的omsCart属性中去执行修改
            // 我们需要将omsCart对象的getQuantity()和cartDTO对象的getQuantity()相加
            omsCart.setQuantity(omsCart.getQuantity()+cartDTO.getQuantity());
            // 确定了数量之后,执行持久层方法进行修改
            omsCartMapper.updateQuantityById(omsCart);
        }
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

    // 业务逻辑层方法中需要获得用户id
    // 我们的项目会在控制器方法运行前运行的过滤器代码中,对前端传入的表明用户身份的JWT解析
    // 解析后保存到SpringSecurity上下文中,我们可以从SpringSecurity上下文中获取用户信息
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








