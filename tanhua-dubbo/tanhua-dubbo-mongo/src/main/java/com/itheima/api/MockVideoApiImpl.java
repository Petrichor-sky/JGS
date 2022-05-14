package com.itheima.api;

import com.itheima.mongo.MockVideo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@DubboService
public class MockVideoApiImpl implements MockVideoApi{
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public void save(MockVideo vo) {
        mongoTemplate.save(vo);
    }

    /**
     * 根据条件查询对应的视频
     * @param uid
     * @param page
     * @param pageSize
     * @param sortOrder     ascending 升序 descending 降序
     * @param sortProp  排序字段
     * @return
     */
    @Override
    public List<MockVideo> findByIdAndPage(Long uid, Integer page, Integer pageSize, String sortOrder, String sortProp) {
        Pageable pageable = PageRequest.of(page-1,pageSize);
        sortOrder = sortOrder.trim();
        sortProp = sortProp.trim();
        Criteria criteria  = new Criteria();
        Query query = new Query();
        if (!ObjectUtils.isEmpty(uid)){
            criteria.and("userId").is(uid);
        }

        if (!StringUtils.isEmpty(sortProp) && "ascending".equals(sortOrder)){
            //采用排序字段和定义的排序顺序进行排序---->升序
            query = Query.query(criteria).with(Sort.by(Sort.Order.asc(sortProp))).with(pageable);

        }
        if (!StringUtils.isEmpty(sortProp) && "descending".equals(sortOrder)){
            //采用排序字段和定义的排序顺序进行排序---->降序
            query = Query.query(criteria).with(Sort.by(Sort.Order.desc(sortProp))).with(pageable);
        }
        return mongoTemplate.find(query,MockVideo.class);
    }
}
