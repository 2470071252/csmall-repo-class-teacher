package cn.tedu.mall.seckill.service.impl;

import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.pojo.product.vo.SpuStandardVO;
import cn.tedu.mall.pojo.seckill.model.SeckillSpu;
import cn.tedu.mall.pojo.seckill.vo.SeckillSpuDetailSimpleVO;
import cn.tedu.mall.pojo.seckill.vo.SeckillSpuVO;
import cn.tedu.mall.product.service.seckill.IForSeckillSpuService;
import cn.tedu.mall.seckill.mapper.SeckillSpuMapper;
import cn.tedu.mall.seckill.service.ISeckillSpuService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SeckillSpuServiceImpl implements ISeckillSpuService {

    // 装配查询秒杀表信息的Mapper
    @Autowired
    private SeckillSpuMapper seckillSpuMapper;

    // 查询秒杀列表商品信息的方法中,返回值为SeckillSpuVO,其中包含常规spu信息和秒杀spu信息
    // 所以我们需要dubbo调用product模块,根据spuId查询出spu的常规信息
    @DubboReference
    private IForSeckillSpuService dubboSeckillSpuService;

    // 分页查询秒杀商品信息
    // 注意:返回值集合泛型SeckillSpuVO,既包含常规Spu信息又包含秒杀Spu信息
    @Override
    public JsonPage<SeckillSpuVO> listSeckillSpus(Integer page, Integer pageSize) {
        // 设置分页条件
        PageHelper.startPage(page,pageSize);
        // 执行查询秒杀表中所有商品信息的方法(会自动分页查询)
        List<SeckillSpu> seckillSpus=seckillSpuMapper.findSeckillSpus();
        // 实例化一个SeckillSpuVO泛型的集合,以备后续返回
        List<SeckillSpuVO> seckillSpuVOs=new ArrayList<>();
        // 遍历seckillSpus(没有常规信息的集合)
        for(SeckillSpu seckillSpu : seckillSpus){
            // 获得当前对象的spuId
            Long spuId=seckillSpu.getSpuId();
            // 获得了spuId,利用Dubbo查询这个spu的常规信息
            SpuStandardVO standardVO = dubboSeckillSpuService.getSpuById(spuId);
            // 秒杀信息在seckillSpu对象中,常规信息在standardVO对象中
            // 下面将两方信息都赋值到SeckillSpuVO中
            SeckillSpuVO seckillSpuVO=new SeckillSpuVO();
            // 将常规信息中同名属性赋值到seckillSpuVO
            BeanUtils.copyProperties(standardVO,seckillSpuVO);
            // 秒杀属性单独赋值
            seckillSpuVO.setSeckillListPrice(seckillSpu.getListPrice());
            seckillSpuVO.setStartTime(seckillSpu.getStartTime());
            seckillSpuVO.setEndTime(seckillSpu.getEndTime());
            // 此处seckillSpuVO就已经包含了常规信息和秒杀信息
            // 添加到集合中
            seckillSpuVOs.add(seckillSpuVO);
        }
        // 最后别忘了返回
        return JsonPage.restPage(new PageInfo<>(seckillSpuVOs));
    }

    @Override
    public SeckillSpuVO getSeckillSpu(Long spuId) {
        return null;
    }

    @Override
    public SeckillSpuDetailSimpleVO getSeckillSpuDetail(Long spuId) {
        return null;
    }
}
