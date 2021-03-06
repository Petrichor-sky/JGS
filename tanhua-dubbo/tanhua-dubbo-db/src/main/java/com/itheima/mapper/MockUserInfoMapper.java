package com.itheima.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.entity.MockUserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MockUserInfoMapper extends BaseMapper<MockUserInfo> {

    @Select("<script>" +
            "select count(*) from tb_mock_user_info " +
            "where sex = #{gender} " +
            "And id in" +
            "<foreach item = 'item' index = 'index' collection = 'ids' open = '(' separator = ',' close = ')'>" +
            "#{item}" +
            "</foreach>" +
            "</script>")
    Object countByGender(@Param("gender") String gender,@Param("ids") List<Long> ids);

   @Select("<script>" +
           "select count(*) from tb_mock_user_info " +
           "where age between #{startAge} And #{endAge} " +
           "And id in" +
           "<foreach item = 'item' index = 'index' collection = 'ids' open = '(' separator = ',' close = ')'>" +
           "#{item}" +
           "</foreach>" +
           "</script>")
    Object countByAge(@Param("startAge") int startAge,@Param("endAge") int endAge,@Param("ids") List<Long> ids);

    @Select("<script>" +
            "select count(*) from tb_mock_user_info " +
            "where occupation = #{industry} " +
            "And id in" +
            "<foreach item = 'item' index = 'index' collection = 'ids' open = '(' separator = ',' close = ')'>" +
            "#{item}" +
            "</foreach>" +
            "</script>")
    Object countByIndustry(@Param("industry") String industry,@Param("ids") List<Long> ids);

    @Select("<script>" +
            "select count(*) from tb_mock_user_info " +
            "where city = #{local} " +
            "And id in" +
            "<foreach item = 'item' index = 'index' collection = 'ids' open = '(' separator = ',' close = ')'>" +
            "#{item}" +
            "</foreach>" +
            "</script>")
    Object countByLocal(@Param("local") String local,@Param("ids") List<Long> ids);

    @Select("<script>" +
            "select count(*) from tb_mock_user_info " +
            "where city IN ('??????','??????','??????','??????','??????') " +
            "And id in" +
            "<foreach item = 'item' index = 'index' collection = 'ids' open = '(' separator = ',' close = ')'>" +
            "#{item}" +
            "</foreach>" +
            "</script>")
    Object countByLocalTotal1(@Param("localTotal")String localTotal,@Param("ids") List<Long> ids);

    @Select("<script>" +
            "select count(*) from tb_mock_user_info " +
            "where city IN ('??????','??????','??????','??????','?????????') " +
            "And id in" +
            "<foreach item = 'item' index = 'index' collection = 'ids' open = '(' separator = ',' close = ')'>" +
            "#{item}" +
            "</foreach>" +
            "</script>")
    Object countByLocalTotal2(@Param("localTotal")String localTotal,@Param("ids") List<Long> ids);

    @Select("<script>" +
            "select count(*) from tb_mock_user_info " +
            "where city IN ('??????','??????','??????','??????','??????','??????','??????','??????') " +
            "And id in" +
            "<foreach item = 'item' index = 'index' collection = 'ids' open = '(' separator = ',' close = ')'>" +
            "#{item}" +
            "</foreach>" +
            "</script>")
    Object countByLocalTotal3(@Param("localTotal")String localTotal,@Param("ids") List<Long> ids);

    @Select("<script>" +
            "select count(*) from tb_mock_user_info " +
            "where city IN ('??????','??????','??????','??????','??????') " +
            "And id in" +
            "<foreach item = 'item' index = 'index' collection = 'ids' open = '(' separator = ',' close = ')'>" +
            "#{item}" +
            "</foreach>" +
            "</script>")
    Object countByLocalTotal4(@Param("localTotal")String localTotal,@Param("ids") List<Long> ids);

    @Select("<script>" +
            "select count(*) from tb_mock_user_info " +
            "where city IN ('??????','??????','?????????') " +
            "And id in" +
            "<foreach item = 'item' index = 'index' collection = 'ids' open = '(' separator = ',' close = ')'>" +
            "#{item}" +
            "</foreach>" +
            "</script>")
    Object countByLocalTotal5(@Param("localTotal")String localTotal,@Param("ids") List<Long> ids);
}
