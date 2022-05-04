package com.itheima.api;

public interface UserLikeApi {
    boolean saveOrUpdate(Long userId,Long likeUserId,boolean isLike);
}
