package cn.tedu.mall.seckill.mapper;

import cn.tedu.mall.pojo.seckill.model.SeckillSpu;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SeckillSpuMapper {

    // 查询秒杀商品列表的方法
    List<SeckillSpu> findSeckillSpus();

    // 根据指定时间,查询出正在进行秒杀的商品列表
    List<SeckillSpu> findSeckillSpusByTime(LocalDateTime time);

    // 根据spuId查询秒杀spu信息
    SeckillSpu findSeckillSpuById(Long spuId);

    // 布隆过滤器用:查询获得所有秒杀spu商品的spuId数组
    Long[] findAllSeckillSpuIds();


}
