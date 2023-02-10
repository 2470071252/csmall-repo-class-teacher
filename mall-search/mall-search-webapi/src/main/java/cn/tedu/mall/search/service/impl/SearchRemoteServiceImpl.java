package cn.tedu.mall.search.service.impl;

import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.pojo.search.entity.SpuEntity;
import cn.tedu.mall.pojo.search.entity.SpuForElastic;
import cn.tedu.mall.search.repository.SpuEntityRepository;
import cn.tedu.mall.search.service.ISearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SearchRemoteServiceImpl implements ISearchService {

    // 装配包含查询方法的持久层
    @Autowired
    private SpuEntityRepository spuEntityRepository;

    @Override
    public JsonPage<SpuEntity> search(String keyword, Integer page, Integer pageSize) {
        Page<SpuEntity> spus=spuEntityRepository
                .querySearchByText(keyword, PageRequest.of(page-1,pageSize));
        // 分页查询调用结束,返回Page对象,我们要转换为JsonPage对象返回
        JsonPage<SpuEntity> jsonPage=new JsonPage<>();
        // 赋值相关数据
        jsonPage.setPage(page);
        jsonPage.setPageSize(pageSize);
        jsonPage.setTotalPage(spus.getTotalPages());
        jsonPage.setTotal(spus.getTotalElements());
        jsonPage.setList(spus.getContent());
        // 最后别忘了返回!
        return jsonPage;
    }

    @Override
    public void loadSpuByPage() {

    }
}
