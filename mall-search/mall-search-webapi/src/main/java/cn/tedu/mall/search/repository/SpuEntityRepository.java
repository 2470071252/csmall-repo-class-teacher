package cn.tedu.mall.search.repository;

import cn.tedu.mall.pojo.search.entity.SpuEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpuEntityRepository extends ElasticsearchRepository<SpuEntity,Long> {
    // 要实现根据用户输入的关键字,查询ES中的商品列表
    // logstash将所有商品的spu信息(name,title,description,category_name)拼接成了一个字段
    // 这个字段叫search_text,我们搜索时只需要搜索这一个字段,就满足了之前设计的搜索需求
    // 因为search_text字段并没有在SpuEntity中声明,所以不能用方法名称查询,只能使用查询语句
    @Query("{\"match\":{\"search_text\":{\"query\":\"?0\"}}}")
    Page<SpuEntity> querySearchByText(String keyword, Pageable pageable);
}







