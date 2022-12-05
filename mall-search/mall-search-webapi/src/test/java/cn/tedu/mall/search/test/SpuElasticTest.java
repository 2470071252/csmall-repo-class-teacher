package cn.tedu.mall.search.test;


import cn.tedu.mall.search.service.ISearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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


}
