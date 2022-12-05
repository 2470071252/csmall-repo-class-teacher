package cn.tedu.mall.search.repository;


import cn.tedu.mall.pojo.search.entity.SpuForElastic;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

//SpuForElastic实体类操作ES的持久层接口
//需要基础SpringData提供的一个父接口,父接口中提供了对应当前实体类基本的增删改查方法
@Repository
public interface SpuForElasticRepository extends
                                ElasticsearchRepository<SpuForElastic,Long> {

    // 查询title字段包含指定关键字(分词)的spu数据
    Iterable<SpuForElastic> querySpuForElasticsByTitleMatches(String title);
}





