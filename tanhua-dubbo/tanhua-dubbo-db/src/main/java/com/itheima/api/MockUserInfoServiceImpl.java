package com.itheima.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.entity.MockUserInfo;
import com.itheima.mapper.MockUserInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DubboService
public class MockUserInfoServiceImpl implements MockUserInfoApi{
    @Autowired
    private MockUserInfoMapper userInfoMapper;

    @Override
    public MockUserInfo findById(Long userID) {
        return userInfoMapper.selectById(userID);
    }

    /**
     * 用户数据
     * @param page
     * @param pageSize
     * @param id
     * @param nickname
     * @param city
     * @return
     */
    @Override
    public IPage<MockUserInfo> findByPage(Integer page, Integer pageSize, Long id, String nickname, String city) {
        IPage<MockUserInfo> iPage = new Page<>(page,pageSize);
        QueryWrapper<MockUserInfo> qw = new QueryWrapper<>();
        qw.eq(!ObjectUtils.isEmpty(id),"id",id);
        qw.like(!StringUtils.isEmpty(nickname),"nickname",nickname);
        qw.like(!StringUtils.isEmpty(city),"city",city);
        qw.orderByDesc("created");
        return userInfoMapper.selectPage(iPage,qw);
    }

    @Override
    public Map<String, List<Map<String, Object>>> countByTime(List<Long> userIds) {
        /*
            industryDistribution	行业分布TOP10
            ageDistribution         年龄分布
            genderDistribution      性别分布
            localDistribution       地区分布
            localTotal              地区合计
         */
        Map<String, List<Map<String, Object>>> map = new HashMap<>();
        //年龄分布
        List<Map<String, Object>> ageList = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("title","0-17岁");
        params.put("amount",userInfoMapper.countByAge(0,17,userIds));
        Map<String, Object> params2 = new HashMap<>();
        params2.put("title","18-23岁");
        params2.put("amount",userInfoMapper.countByAge(18,23,userIds));
        Map<String, Object> params3 = new HashMap<>();
        params3.put("title","24-30岁");
        params3.put("amount",userInfoMapper.countByAge(24,30,userIds));
        Map<String, Object> params4 = new HashMap<>();
        params4.put("title","31-40岁");
        params4.put("amount",userInfoMapper.countByAge(31,40,userIds));
        Map<String, Object> params5 = new HashMap<>();
        params5.put("title","41-50岁");
        params5.put("amount",userInfoMapper.countByAge(41,50,userIds));
        Map<String, Object> params6 = new HashMap<>();
        params6.put("title","50岁+");
        params6.put("amount",userInfoMapper.countByAge(51,120,userIds));
        ageList.add(params);
        ageList.add(params2);
        ageList.add(params3);
        ageList.add(params4);
        ageList.add(params5);
        ageList.add(params6);
        map.put("ageDistribution",ageList);

        //性别分布
        List<Map<String, Object>> genderList = new ArrayList<>();
        Map<String, Object> params7 = new HashMap<>();
        params7.put("title","男性用户");
        params7.put("amount",userInfoMapper.countByGender("男",userIds));
        genderList.add(params7);
        Map<String, Object> params8 = new HashMap<>();
        params8.put("title","女性用户");
        params8.put("amount",userInfoMapper.countByGender("女",userIds));
        genderList.add(params8);
        map.put("genderDistribution",genderList);

        //行业分布
        List<Map<String, Object>> industryList= new ArrayList<>();
        Map<String, Object> industryMap1 = new HashMap<>();
        industryMap1.put("title","制造");
        industryMap1.put("amount",userInfoMapper.countByIndustry("制造",userIds));

        Map<String, Object> industryMap2 = new HashMap<>();
        industryMap2.put("title","服务");
        industryMap2.put("amount",userInfoMapper.countByIndustry("服务",userIds));
        Map<String, Object> industryMap3 = new HashMap<>();
        industryMap3.put("title","地产");
        industryMap3.put("amount",userInfoMapper.countByIndustry("地产",userIds));
        Map<String, Object> industryMap4 = new HashMap<>();
        industryMap4.put("title","住宿");
        industryMap4.put("amount",userInfoMapper.countByIndustry("住宿",userIds));
        Map<String, Object> industryMap5 = new HashMap<>();
        industryMap5.put("title","教育");
        industryMap5.put("amount",userInfoMapper.countByIndustry("教育",userIds));
        Map<String, Object> industryMap6 = new HashMap<>();
        industryMap6.put("title","餐饮");
        industryMap6.put("amount",userInfoMapper.countByIndustry("餐饮",userIds));
        Map<String, Object> industryMap7 = new HashMap<>();
        industryMap7.put("title","金融");
        industryMap7.put("amount",userInfoMapper.countByIndustry("金融",userIds));
        Map<String, Object> industryMap8 = new HashMap<>();
        industryMap8.put("title","医疗");
        industryMap8.put("amount",userInfoMapper.countByIndustry("医疗",userIds));
        Map<String, Object> industryMap9 = new HashMap<>();
        industryMap9.put("title","文娱");
        industryMap9.put("amount",userInfoMapper.countByIndustry("文娱",userIds));
        Map<String, Object> industryMap10 = new HashMap<>();
        industryMap10.put("title","物流");
        industryMap10.put("amount",userInfoMapper.countByIndustry("物流",userIds));
        industryList.add(industryMap1);
        industryList.add(industryMap2);
        industryList.add(industryMap3);
        industryList.add(industryMap4);
        industryList.add(industryMap5);
        industryList.add(industryMap6);
        industryList.add(industryMap7);
        industryList.add(industryMap8);
        industryList.add(industryMap9);
        industryList.add(industryMap10);
        map.put("industryDistribution",industryList);


        //地区分布  localDistribution
        List<Map<String, Object>> localList = new ArrayList<>();
        Map<String, Object> localMap1 = new HashMap<>();
        localMap1.put("title","黑龙江");
        localMap1.put("amount",userInfoMapper.countByLocal("黑龙江",userIds));
        Map<String, Object> localMap2 = new HashMap<>();
        localMap2.put("title","湖南");
        localMap2.put("amount",userInfoMapper.countByLocal("湖南",userIds));
        Map<String, Object> localMap3 = new HashMap<>();
        localMap3.put("title","贵州");
        localMap3.put("amount",userInfoMapper.countByLocal("贵州",userIds));
        Map<String, Object> localMap4 = new HashMap<>();
        localMap4.put("title","云南");
        localMap4.put("amount",userInfoMapper.countByLocal("云南",userIds));
        Map<String, Object> localMap5 = new HashMap<>();
        localMap5.put("title","重庆");
        localMap5.put("amount",userInfoMapper.countByLocal("重庆",userIds));
        Map<String, Object> localMap6 = new HashMap<>();
        localMap6.put("title","新疆");
        localMap6.put("amount",userInfoMapper.countByLocal("新疆",userIds));
        Map<String, Object> localMap7 = new HashMap<>();
        localMap7.put("title","安徽");
        localMap7.put("amount",userInfoMapper.countByLocal("安徽",userIds));
        Map<String, Object> localMap8 = new HashMap<>();
        localMap8.put("title","湖北");
        localMap8.put("amount",userInfoMapper.countByLocal("湖北",userIds));
        Map<String, Object> localMap10 = new HashMap<>();
        localMap10.put("title","四川");
        localMap10.put("amount",userInfoMapper.countByLocal("四川",userIds));
        Map<String, Object> localMap11 = new HashMap<>();
        localMap11.put("title","青海");
        localMap11.put("amount",userInfoMapper.countByLocal("青海",userIds));
        Map<String, Object> localMap12 = new HashMap<>();
        localMap12.put("title","内蒙古");
        localMap12.put("amount",userInfoMapper.countByLocal("内蒙古",userIds));
        Map<String, Object> localMap13 = new HashMap<>();
        localMap13.put("title","陕西");
        localMap13.put("amount",userInfoMapper.countByLocal("陕西",userIds));
        Map<String, Object> localMap14 = new HashMap<>();
        localMap14.put("title","辽宁");
        localMap14.put("amount",userInfoMapper.countByLocal("辽宁",userIds));
        Map<String, Object> localMap15 = new HashMap<>();
        localMap15.put("title","福建");
        localMap15.put("amount",userInfoMapper.countByLocal("福建",userIds));
        Map<String, Object> localMap16 = new HashMap<>();
        localMap16.put("title","山东");
        localMap16.put("amount",userInfoMapper.countByLocal("山东",userIds));
        Map<String, Object> localMap17 = new HashMap<>();
        localMap17.put("title","澳门");
        localMap17.put("amount",userInfoMapper.countByLocal("澳门",userIds));
        Map<String, Object> localMap18 = new HashMap<>();
        localMap18.put("title","广东");
        localMap18.put("amount",userInfoMapper.countByLocal("广东",userIds));
        Map<String, Object> localMap20 = new HashMap<>();
        localMap20.put("title","吉林");
        localMap20.put("amount",userInfoMapper.countByLocal("吉林",userIds));
        Map<String, Object> localMap21 = new HashMap<>();
        localMap21.put("title","甘肃");
        localMap21.put("amount",userInfoMapper.countByLocal("甘肃",userIds));
        Map<String, Object> localMap22 = new HashMap<>();
        localMap22.put("title","青海");
        localMap22.put("amount",userInfoMapper.countByLocal("青海",userIds));
        Map<String, Object> localMap23 = new HashMap<>();
        localMap23.put("title","江苏");
        localMap23.put("amount",userInfoMapper.countByLocal("江苏",userIds));
        Map<String, Object> localMap24 = new HashMap<>();
        localMap24.put("title","广西");
        localMap24.put("amount",userInfoMapper.countByLocal("广西",userIds));
        Map<String, Object> localMap25 = new HashMap<>();
        localMap25.put("title","北京");
        localMap25.put("amount",userInfoMapper.countByLocal("北京",userIds));
        Map<String, Object> localMap26 = new HashMap<>();
        localMap26.put("title","上海");
        localMap26.put("amount",userInfoMapper.countByLocal("上海",userIds));
        Map<String, Object> localMap27 = new HashMap<>();
        localMap27.put("title","杭州");
        localMap27.put("amount",userInfoMapper.countByLocal("杭州",userIds));
        Map<String, Object> localMap28 = new HashMap<>();
        localMap28.put("title","深圳");
        localMap28.put("amount",userInfoMapper.countByLocal("深圳",userIds));
        localList.add(localMap1);
        localList.add(localMap2);
        localList.add(localMap3);
        localList.add(localMap4);
        localList.add(localMap5);
        localList.add(localMap6);
        localList.add(localMap7);
        localList.add(localMap8);
        localList.add(localMap10);
        localList.add(localMap11);
        localList.add(localMap12);
        localList.add(localMap13);
        localList.add(localMap14);
        localList.add(localMap15);
        localList.add(localMap16);
        localList.add(localMap17);
        localList.add(localMap18);
        localList.add(localMap20);
        localList.add(localMap21);
        localList.add(localMap22);
        localList.add(localMap23);
        localList.add(localMap24);
        localList.add(localMap25);
        localList.add(localMap26);
        localList.add(localMap27);
        localList.add(localMap28);
        map.put("localDistribution",localList);

        //地区分布  localTotal
        List<Map<String, Object>> localTotalList = new ArrayList<>();
        Map<String, Object> localTotalMap1 = new HashMap<>();
        localTotalMap1.put("title","华南地区");
        localTotalMap1.put("amount",userInfoMapper.countByLocalTotal1("华南地区",userIds));
        Map<String, Object> localTotalMap2 = new HashMap<>();
        localTotalMap2.put("title","华北地区");
        localTotalMap2.put("amount",userInfoMapper.countByLocalTotal2("华北地区",userIds));
        Map<String, Object> localTotalMap3 = new HashMap<>();
        localTotalMap3.put("title","华东地区");
        localTotalMap3.put("amount",userInfoMapper.countByLocalTotal3("华东地区",userIds));
        Map<String, Object> localTotalMap4 = new HashMap<>();
        localTotalMap4.put("title","西北地区");
        localTotalMap4.put("amount",userInfoMapper.countByLocalTotal4("西北地区",userIds));
        Map<String, Object> localTotalMap5 = new HashMap<>();
        localTotalMap5.put("title","东北地区");
        localTotalMap5.put("amount",userInfoMapper.countByLocalTotal5("东北地区",userIds));
        localTotalList.add(localTotalMap1);
        localTotalList.add(localTotalMap2);
        localTotalList.add(localTotalMap3);
        localTotalList.add(localTotalMap4);
        localTotalList.add(localTotalMap5);
        map.put("localTotal",localTotalList);

        return map;
    }
}
