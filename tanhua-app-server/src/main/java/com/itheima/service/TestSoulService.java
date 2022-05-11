package com.itheima.service;

import com.itheima.api.*;
import com.itheima.chuanyin.*;
import com.itheima.utils.ThreadLocalUtils;
import com.itheima.vo.OptionsVo;
import com.itheima.vo.PaperListVo;
import com.itheima.vo.QuestionsVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Service
public class TestSoulService {
    @DubboReference
    private SoulReportApi soulReportApi;
    @DubboReference
    private SoulPaperAPi soulPaperAPi;
    @DubboReference
    private SoulPaperQuestionApi soulPaperQuestionApi;
    @DubboReference
    private SoulQuestionApi soulQuestionApi;
    @DubboReference
    private SoulOptionsApi soulOptionsApi;
    @DubboReference
    private UserInfoApi userInfoApi;


    /**
     * 问卷列表
     * @return
     */
    public List<PaperListVo> getTestSoul() {
        //获取当前登录者的ID
        Long userId = ThreadLocalUtils.get();
        //分三个等级，初级，中级，高级，等级进行逐一解锁
        //初级问卷
        PaperListVo paperListVo1 = new PaperListVo();
        //中级问卷
        PaperListVo paperListVo2 = new PaperListVo();
        //高级问卷
        PaperListVo paperListVo3 = new PaperListVo();
        //调用方法分别填充问题
        fillTestSoul(paperListVo1, 1);
        fillTestSoul(paperListVo2, 2);
        fillTestSoul(paperListVo3, 3);
        //根据用户查询report表中有没有对应的报告
        List<SoulReport> reportList = soulReportApi.findByUserId(userId);
        int size = reportList.size();
        if (size == 0){
            //表示没有测试过，开启初级测试，其他的为关闭状态
            paperListVo1.setIsLock(0);
            paperListVo2.setIsLock(1);
            paperListVo3.setIsLock(1);
        }else if (size == 1){
            //如果有一个，说明完成初级，需要开启中级
            paperListVo1.setIsLock(0);
            paperListVo2.setIsLock(0);
            paperListVo3.setIsLock(1);
            //获取最新的报告ID
            SoulReport soulReport = reportList.get(0);
            if (soulReport.getPaperId() == 1)
                paperListVo1.setReportId(soulReport.getId().toString());
            paperListVo2.setReportId(null);
            paperListVo3.setReportId(null);
        }else if (size == 2) {
            //如果有一个，说明完成初级，需要开启中级
            paperListVo1.setIsLock(0);
            paperListVo2.setIsLock(0);
            paperListVo3.setIsLock(0);
            //获取最新的报告ID
            for (SoulReport soulReport : reportList) {
                if (soulReport.getPaperId() == 1) {
                    paperListVo1.setReportId(soulReport.getId().toString());
                }
                if (soulReport.getPaperId() == 2) {
                    paperListVo2.setReportId(soulReport.getId().toString());
                }
            }
            paperListVo3.setReportId(null);
        }else if (size == 3) {
            for (SoulReport soulReport : reportList) {
                if (soulReport.getPaperId() == 1) {
                    paperListVo1.setReportId(soulReport.getId().toString());
                }
                if (soulReport.getPaperId() == 2) {
                    paperListVo2.setReportId(soulReport.getId().toString());
                }
                if (soulReport.getPaperId() == 3) {
                    paperListVo3.setReportId(soulReport.getId().toString());
                }
            }
        }
        //将三个问卷集合进行返回
        List<PaperListVo> list = new ArrayList<>();
        list.add(paperListVo1);
        list.add(paperListVo2);
        list.add(paperListVo3);
        return list;
    }

    //此方法为对问卷进行填充
    private void fillTestSoul(PaperListVo paperListVo, int id) {
        //根据soulPaper表中的属性进行填充
        //添加问卷的编号
        paperListVo.setId(String.valueOf(id));
        SoulPaper soulPaper = soulPaperAPi.findById(id);
        //设置等级
        paperListVo.setLevel(soulPaper.getLevel());
        //设置名称
        paperListVo.setName(soulPaper.getName());
        //设置问卷封面
        paperListVo.setCover(soulPaper.getCover());
        //设置星级
        paperListVo.setStar(soulPaper.getStar());
        //根据SoulPaperQuestion中的问题进行填充
        List<QuestionsVo> questionList = soulQuestionApi.findByPaperId(String.valueOf(id));
        for (QuestionsVo questionsVo : questionList) {
            List<OptionsVo> optionsVoList = soulOptionsApi.findByQuestionId(questionsVo.getId());
            questionsVo.setOptions(optionsVoList);
        }
        //将问题集合添加到问卷属性中
        paperListVo.setQuestions(questionList);
    }

    /**
     * 提交问卷
     * @param map
     * @return
     */
    public String submitTestSoul(Map<String, List<Answers>> map) {
        //获取当前用户对象
        Long userId = ThreadLocalUtils.get();
        //设置初始化分数
        Long score = 0L;
        Long questionId = 0L;
        Collection<List<Answers>> answerList = map.values();
        for (List<Answers> answers : answerList) {
            for (Answers answer : answers) {
                //拿到问题的ID
                questionId = Long.valueOf(answer.getQuestionId());
                //拿到选项的ID
                String optionId = answer.getOptionId();
                //根据条件查询对应的信息
                SoulOptions soulOptions = soulOptionsApi.findByOptionId(optionId);
                //计算总分
                score += soulOptions.getScore();

            }
        }
        //查询对应的试卷ID
        SoulQuestion question = soulQuestionApi.findById(questionId);
        //查询该用户是否存在该问卷报告
        SoulReport soulReport = soulReportApi.findByIdAndUserId(question.getPaperId(),userId);
        //判断问卷报告是否为空
        if (!ObjectUtils.isEmpty(soulReport)){
            //如果不为空的话，执行修改操作
            soulReport.setPaperId(Long.valueOf(question.getPaperId()));
            soulReport.setScore(score);
            soulReport.setUpdated(new Date(System.currentTimeMillis()));
            //执行修改操作
            soulReportApi.update(soulReport);
            return soulReport.getId().toString();
        }else {
            //如果为空的话，执行添加操作
            soulReport = new SoulReport();
            soulReport.setUserId(userId);
            soulReport.setPaperId(Long.valueOf(question.getPaperId()));
            soulReport.setScore(score);
            soulReport.setUpdated(new Date(System.currentTimeMillis()));
            soulReport.setCreated(new Date(System.currentTimeMillis()));
            //执行添加操作
            return soulReportApi.save(soulReport);
        }

    }
}
