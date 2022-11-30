package cn.tedu.mall.front.service.impl;

import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.front.service.IFrontProductService;
import cn.tedu.mall.pojo.product.vo.*;
import cn.tedu.mall.product.service.front.IForFrontAttributeService;
import cn.tedu.mall.product.service.front.IForFrontSkuService;
import cn.tedu.mall.product.service.front.IForFrontSpuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FrontProductServiceImpl implements IFrontProductService {

    @DubboReference
    private IForFrontSpuService dubboSpuService;
    // 根据spuId查询sku信息的dubbo调用对象
    @DubboReference
    private IForFrontSkuService dubboSkuService;
    // 根据spuId查询属性的dubbo调用对象
    @DubboReference
    private IForFrontAttributeService dubboAttributeService;


    // 根据分类id分页查询spu列表
    @Override
    public JsonPage<SpuListItemVO> listSpuByCategoryId(Long categoryId, Integer page, Integer pageSize) {
        // dubbo调用的方法是product模块编写的业务逻辑层方法
        // 这个方法中实际上完成了分页的操作,我们只需调用即可
        JsonPage<SpuListItemVO> jsonPage=
                dubboSpuService.listSpuByCategoryId(categoryId, page, pageSize);
        // 别忘了返回jsonPage !!!
        return jsonPage;
    }

    @Override
    public SpuStandardVO getFrontSpuById(Long id) {

        return null;
    }

    @Override
    public List<SkuStandardVO> getFrontSkusBySpuId(Long spuId) {
        return null;
    }

    @Override
    public SpuDetailStandardVO getSpuDetail(Long spuId) {
        return null;
    }

    @Override
    public List<AttributeStandardVO> getSpuAttributesBySpuId(Long spuId) {
        return null;
    }
}
