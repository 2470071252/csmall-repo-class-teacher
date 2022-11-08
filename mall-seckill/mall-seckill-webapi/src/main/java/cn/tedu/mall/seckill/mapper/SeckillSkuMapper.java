package cn.tedu.mall.seckill.mapper;

import cn.tedu.mall.pojo.seckill.model.SeckillSku;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeckillSkuMapper {

    // 根据spuId查询sku列表
    List<SeckillSku> findSeckillSkusBySpuId(Long spuId);


}
