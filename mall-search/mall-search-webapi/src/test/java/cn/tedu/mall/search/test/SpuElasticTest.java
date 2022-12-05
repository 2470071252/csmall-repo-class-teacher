package cn.tedu.mall.search.test;


import cn.tedu.mall.pojo.search.entity.SpuForElastic;
import cn.tedu.mall.search.repository.SpuForElasticRepository;
import cn.tedu.mall.search.service.ISearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

// 下面注解必须加!!!!
@SpringBootTest
public class SpuElasticTest {

    @Autowired
    private ISearchService searchService;
    @Test
    void loadData(){
        searchService.loadSpuByPage();
        System.out.println("ok");
    }

    @Autowired
    private SpuForElasticRepository spuRepository;
    @Test
    void showData(){
        Iterable<SpuForElastic> spus=spuRepository.findAll();
        spus.forEach(spu -> System.out.println(spu));
    }

    @Test
    void showTitle(){
        Iterable<SpuForElastic> spus=spuRepository
                .querySpuForElasticsByTitleMatches("手机苹果");
        spus.forEach(spu -> System.out.println(spu));
    }


    @Test
    void showQuery(){
        // 自定义查询4个字段包含指定关键字的方法
        //Iterable<SpuForElastic> spus=spuRepository.querySearch("手机");
        Page spus=spuRepository.querySearch("手机",
                                        PageRequest.of(0,2));
        spus.forEach(spu -> System.out.println(spu));
    }



}
