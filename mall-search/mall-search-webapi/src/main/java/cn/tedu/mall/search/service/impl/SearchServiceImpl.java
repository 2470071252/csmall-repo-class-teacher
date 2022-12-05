package cn.tedu.mall.search.service.impl;

import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.pojo.product.model.Spu;
import cn.tedu.mall.pojo.search.entity.SpuForElastic;
import cn.tedu.mall.product.service.front.IForFrontSpuService;
import cn.tedu.mall.search.mapper.SpuForElasticRepository;
import cn.tedu.mall.search.service.ISearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SearchServiceImpl implements ISearchService {

    // dubbo调用product模块分页查询所有spu的方法
    @DubboReference
    private IForFrontSpuService dubboSpuService;
    @Autowired
    private SpuForElasticRepository spuRepository;

    @Override
    public void loadSpuByPage() {
        // 这个方法需要循环调用分页查询所有spu数据的方法,直到将所有数据都查出
        // 每次循环的操作就是将当前从数据库中查询的数据新增到ES
        // 循环条件应该是总页数,但是总页数需要查询一次之后才能得知,所以我们使用do-while循环
        int i=1;     // 循环变量,从1开始,因为可以直接当页码使用
        int page=5;  // 总页数,也是循环条件,是循环操作运行一次之后会被赋值,这里赋默认值或不赋值皆可

        do{
            // dubbo调用查询当前页的spu数据
            JsonPage<Spu> spus=dubboSpuService.getSpuByPage(i,2);
            // 查询出的List是Spu类型,不能直接新增到ES中,需要转换为SpuForElastic
            List<SpuForElastic> esSpus=new ArrayList<>();
            // 遍历分页查询出的数据库的集合
            for(Spu spu : spus.getList()){

            }

        }while (i<=page);
    }

    @Override
    public JsonPage<SpuForElastic> search(String keyword, Integer page, Integer pageSize) {
        return null;
    }


}
