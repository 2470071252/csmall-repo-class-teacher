package cn.tedu.mall.seckill.service.impl;

import cn.tedu.mall.common.exception.CoolSharkServiceException;
import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.common.restful.ResponseCode;
import cn.tedu.mall.pojo.product.vo.SpuStandardVO;
import cn.tedu.mall.pojo.seckill.model.SeckillSpu;
import cn.tedu.mall.pojo.seckill.vo.SeckillSpuDetailSimpleVO;
import cn.tedu.mall.pojo.seckill.vo.SeckillSpuVO;
import cn.tedu.mall.product.service.seckill.IForSeckillSpuService;
import cn.tedu.mall.seckill.mapper.SeckillSpuMapper;
import cn.tedu.mall.seckill.service.ISeckillSpuService;
import cn.tedu.mall.seckill.utils.SeckillCacheUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.redisson.api.RSet;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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

    // 操作Redis的对象
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public SeckillSpuVO getSeckillSpu(Long spuId) {
        // 在后面完整版代码中,这里是要编写经过布隆过滤器判断的
        // 只有布隆过滤器中存在的id才能继续运行,否则发生异常

        // 当前方法的返回值SeckillSpuVO又是既包含秒杀信息又包含常规信息的对象
        // 目标是查询两方面的信息
        // 先判断Redis中是否已经有这个对象,先获取key
        // spuVOKey =  "mall:seckill:spu:vo:2"
        String spuVOKey= SeckillCacheUtils.getSeckillSpuVOKey(spuId);
        // 可以在判断前先声明返回值类型,赋值null即可
        SeckillSpuVO seckillSpuVO=null;
        // 判断 spuVOKey 是否已经在Redis中
        if(redisTemplate.hasKey(spuVOKey)){
            // 如果Redis已经存在这个Key,直接获取用于返回即可
            seckillSpuVO=(SeckillSpuVO) redisTemplate
                                .boundValueOps(spuVOKey).get();
        }else{
            // 如果Redis不存在这个Key,就需要从数据库查询了
            SeckillSpu seckillSpu=seckillSpuMapper.findSeckillSpuById(spuId);
            // 判断一下这个seckillSpu是否为null(因为布隆过滤器有误判)
            if(seckillSpu==null){
                throw new CoolSharkServiceException(
                        ResponseCode.NOT_FOUND,"您访问的商品不存在");
            }
        }



        return null;
    }

    @Override
    public SeckillSpuDetailSimpleVO getSeckillSpuDetail(Long spuId) {
        return null;
    }
}
