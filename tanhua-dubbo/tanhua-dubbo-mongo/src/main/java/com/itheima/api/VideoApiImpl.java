package com.itheima.api;

import com.itheima.mongo.Video;
import com.itheima.utils.IdWorker;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@DubboService
public class VideoApiImpl implements VideoApi{
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private IdWorker idWorker;

    @Override
    public String save(Video video) {
        //1.设置属性
        video.setVid(idWorker.getNextId("video"));
        video.setCreated(System.currentTimeMillis());
        //2.执行保存操作
        mongoTemplate.save(video);
        //3.返回对象id
        return video.getId().toHexString();
    }

    /**
     * 通过vids来查询对应的video数据
     * @param vids
     * @return
     */
    @Override
    public List<Video> findByVids(List<Long> vids) {
        Query query = Query.query(Criteria.where("vid").in(vids));
        return mongoTemplate.find(query,Video.class);
    }

    @Override
    public List<Video> queryVideoList(int page, Integer pageSize) {
        Query query = new Query().skip((page-1)*pageSize).limit(pageSize)
                .with(Sort.by(Sort.Order.desc("created")));
        return mongoTemplate.find(query,Video.class);
    }

    /**
     * 根据视频id来查询对应的视频对象
     * @param videoId
     * @return
     */
    @Override
    public Video findById(String videoId) {
        return mongoTemplate.findById(new ObjectId(videoId), Video.class);
    }
}
