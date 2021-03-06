package com.itheima.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.chuanyin.SoulReport;
import com.itheima.mapper.SoulReportMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@DubboService
public class SoulReportServiceImpl implements SoulReportApi{
    @Autowired
    private SoulReportMapper reportMapper;
    @Override
    public List<SoulReport> findByUserId(Long userId) {
        LambdaQueryWrapper<SoulReport> qw = new LambdaQueryWrapper<>();
        qw.eq(SoulReport::getUserId,userId);
        return reportMapper.selectList(qw);
    }

    @Override
    public SoulReport findByIdAndUserId(String paperId, Long userId) {
        LambdaQueryWrapper<SoulReport> qw = new LambdaQueryWrapper<>();
        qw.eq(SoulReport::getId,paperId);
        qw.eq(SoulReport::getUserId,userId);
        return reportMapper.selectOne(qw);
    }

    @Override
    public void update(SoulReport soulReport) {
        reportMapper.updateById(soulReport);
    }

    @Override
    public String save(SoulReport soulReport) {
        reportMapper.insert(soulReport);
        return soulReport.getId().toString();
    }

    @Override
    public SoulReport findById(String reportId) {
        LambdaQueryWrapper<SoulReport> qw = new LambdaQueryWrapper<>();
        qw.eq(SoulReport::getId,reportId);
        return reportMapper.selectOne(qw);
    }

    @Override
    public List<SoulReport> findByScore(Long score,SoulReport soulReport) {
        LambdaQueryWrapper<SoulReport> qw = new LambdaQueryWrapper<>();
        qw.between(SoulReport::getScore,score -10,score + 10)
                .ne(SoulReport::getUserId,soulReport.getUserId())
                .eq(SoulReport::getPaperId,soulReport.getPaperId());
        return reportMapper.selectList(qw);
    }
}
