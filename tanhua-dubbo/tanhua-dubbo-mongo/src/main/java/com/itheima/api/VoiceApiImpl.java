package com.itheima.api;

import cn.hutool.core.date.DateUtil;
import com.itheima.mongo.Voice;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@DubboService
public class VoiceApiImpl implements VoiceApi{
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public String save(Voice voice) {
        mongoTemplate.insert(voice);
        return voice.getId().toHexString();
    }

    @Override
    public List<Voice> findByGenderAndCreated(String gender) {
        String start = new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.beginOfYear(new Date()));
        Query query = Query.query(Criteria.where("gender").is(gender));
        return mongoTemplate.find(query,Voice.class);
    }
}
