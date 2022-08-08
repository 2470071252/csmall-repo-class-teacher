package cn.tedu.mall.seckill.mapper;

import cn.tedu.mall.pojo.seckill.model.SeckillSpu;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SeckillSpuMapper {

    // 查询秒杀商品列表
    List<SeckillSpu> findSeckillSpus();

    // 根据给定的时间,查询秒杀商品信息
    List<SeckillSpu> findSeckillSpusByTime(LocalDateTime time);

    // 查询所有秒杀商品spu的id数组
    Long[] findAllSeckillSpuIds();





}
