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
import org.redisson.api.RSet;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SeckillSpuServiceImpl implements ISeckillSpuService {

    // 装配查询秒杀表信息的Mapper
    @Autowired
    private SeckillSpuMapper seckillSpuMapper;
    // 秒杀列表信息中,返回值为SeckillSpuVO,这个类型包含了商品的常规信息和秒杀信息
    // 我们需要通过product模块才能查询pms数据库的商品常规信息,所有要Dubbo调用
    @DubboReference
    private IForSeckillSpuService dubboSeckillSpuService;

    // 分页查询秒杀商品列表
    // 返回值泛型:SeckillSpuVO,这个类型包含了商品的常规信息和秒杀信息
    @Override
    public JsonPage<SeckillSpuVO> listSeckillSpus(Integer page, Integer pageSize) {
        // 设置分页条件
        PageHelper.startPage(page,pageSize);
        // 执行查询
        List<SeckillSpu> seckillSpus=seckillSpuMapper.findSeckillSpus();
        // 先声明匹配返回值类型的泛型集合,以用于最后的返回
        List<SeckillSpuVO> seckillSpuVOs=new ArrayList<>();
        // 遍历seckillSpus(没有常规信息的集合)
        for(SeckillSpu seckillSpu : seckillSpus){
            // 取出当前商品的spuId
            Long spuId=seckillSpu.getSpuId();
            // 利用dubbo根据spuId查询商品常规信息
            SpuStandardVO standardVO = dubboSeckillSpuService.getSpuById(spuId);
            // 秒杀信息在seckillSpu对象中,常规信息在standardVO对象里
            // 先实例化SeckillSpuVO,然后向它赋值常规信息和秒杀信息
            SeckillSpuVO seckillSpuVO=new SeckillSpuVO();
            // 将常规信息中同名属性赋值到seckillSpuVO
            BeanUtils.copyProperties(standardVO,seckillSpuVO);
            // 秒杀信息单独手动赋值即可
            seckillSpuVO.setSeckillListPrice(seckillSpu.getListPrice());
            seckillSpuVO.setStartTime(seckillSpu.getStartTime());
            seckillSpuVO.setEndTime(seckillSpu.getEndTime());
            // 到此为止seckillSpuVO就赋值完成了
            // 将它添加到返回值的集合中
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
