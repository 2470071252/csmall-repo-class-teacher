package cn.tedu.mall.search.repository;

import cn.tedu.mall.pojo.search.entity.SpuEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpuEntityRepository extends
                            ElasticsearchRepository<SpuEntity,Long> {
    // 根据用户输入的关键字,匹配ES中的商品
    // logstash将所有商品的spu信息需要分词的字段,拼接成了一个字段:search_text
    // 因为SpuEntity实体类中并没有创建search_text对应的字段,所以这个查询需要通过编写查询语句完成
    @Query("{\"match\":{\"search_text\":{\"query\":\"?0\"}}}")
    Page<SpuEntity> querySearchByText(String keyword, Pageable pageable);

}
