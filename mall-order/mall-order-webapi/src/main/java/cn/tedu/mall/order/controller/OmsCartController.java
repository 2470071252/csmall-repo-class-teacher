package cn.tedu.mall.order.controller;

import cn.tedu.mall.common.restful.JsonResult;
import cn.tedu.mall.order.service.IOmsCartService;
import cn.tedu.mall.pojo.order.dto.CartAddDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oms/cart")
@Api(tags = "购物车管理模块")
public class OmsCartController {
    @Autowired
    private IOmsCartService omsCartService;

    @PostMapping("/add")
    @ApiOperation("新增sku信息到购物车")
    // 在运行本控制器方法前,已经经过了过滤器的代码, 过滤器中解析的前端传入的JWT
    // 解析正确后,会将用户信息保存在SpringSecurity上下文中
    // 酷鲨商城前台用户登录时,登录代码中会给用户赋予一个固定的权限名称ROLE_user
    // 下面的注解就是在判断登录的用户是否具备这个权限,其实主要作用还是判断用户是否登录
    // 这个注解的效果是从SpringSecurity中判断当前登录用户权限,
    // 如果没登录返回401,权限不匹配返回403
    @PreAuthorize("hasAuthority('ROLE_user')")
    // @Validated注解是激活SpringValidation框架用的
    // 参数CartAddDTO中,有多个属性设置了非空的验证规则,如果有设置了规则的属性为null
    // 会抛出BindException异常,终止方法调用,运行全局异常处理类中对应的方法
    public JsonResult addCart(@Validated CartAddDTO cartAddDTO){
        omsCartService.addCart(cartAddDTO);
        return JsonResult.ok("新增sku到购物车完成");
    }

}








