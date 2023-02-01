package cn.tedu.mall.search.repository;

import cn.tedu.mall.pojo.search.entity.SpuForElastic;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

// SpuForElastic实体类操作ES的持久层接口
// 继承的父接口ElasticsearchRepository,对应的实体类基本的增删改查方法
@Repository
public interface SpuForElasticRepository extends
                            ElasticsearchRepository<SpuForElastic,Long> {

}
