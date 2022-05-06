package com.itheima.mongo;

//常量定义
public class Constants {

    //手机APP短信验证码CHECK_CODE_
    public static final String SMS_CODE = "CHECK_CODE_";

	//推荐动态
	public static final String MOVEMENTS_RECOMMEND = "MOVEMENTS_RECOMMEND_";

    //推荐视频
    public static final String VIDEOS_RECOMMEND = "VIDEOS_RECOMMEND_";

	//圈子互动KEY
	public static final String MOVEMENTS_INTERACT_KEY = "MOVEMENTS_INTERACT_";

    //动态点赞用户HashKey
    public static final String MOVEMENT_LIKE_HASHKEY = "MOVEMENT_LIKE_";

    //圈子互动KEY
    public static final String COMMENT_INTERACT_KEY = "COMMENT_INTERACT_";

    //评论点赞用户HashKey
    public static final String COMMENT_LIKE_HASHKEY = "COMMENT_LIKE_";

    //动态喜欢用户HashKey
    public static final String MOVEMENT_LOVE_HASHKEY = "MOVEMENT_LOVE_";

    //视频点赞用户HashKey
    public static final String VIDEO_LIKE_HASHKEY = "VIDEO_LIKE";

    //访问用户
    public static final String VISITORS = "VISITORS";

    //关注用户
    public static final String FOCUS_USER = "FOCUS_USER_{}_{}";

	//初始化密码
    public static final String INIT_PASSWORD = "123456";

    //环信用户前缀
    public static final String HX_USER_PREFIX = "hx";

    //jwt加密盐
    public static final String JWT_SECRET = "itcast";

    //jwt超时时间
    public static final int JWT_TIME_OUT = 3_600;

    //用户喜欢Redis的key
    public static final String USER_LIKE_KEY="USER_LIKE_SET_";

    //用户不喜欢Redis的key
    public static final String USER_NOT_LIKE_KEY="USER_NOT_LIKE_SET_";

    //访问用户
    public static final String VISITORS_USER = "VISITOR_USER";
}
