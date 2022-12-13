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

    // 装配包含查询方法的repository
    @Autowired
    private SpuEntityRepository spuEntityRepository;
    @Override
    public JsonPage<SpuEntity> search(String keyword, Integer page, Integer pageSize) {
        Page<SpuEntity> spus=spuEntityRepository.querySearchByText(
                                    keyword, PageRequest.of(page-1,pageSize));
        // 分页查询调用结束返回Page类型对象,我们要求返回JsonPage类型做统一分页查询的返回
        JsonPage<SpuEntity> jsonPage=new JsonPage<>();
        // 赋值分页信息
        jsonPage.setPage(page);
        jsonPage.setPageSize(pageSize);
        jsonPage.setTotalPage(spus.getTotalPages());
        jsonPage.setTotal(spus.getTotalElements());
        // 赋值分页数据
        jsonPage.setList(spus.getContent());
        // 最后返回!!!
        return jsonPage;
    }

    // 因为logstash自动同步数据库和ES中的数据,所以这个方法无需编写
    @Override
    public void loadSpuByPage() {

    }
}
