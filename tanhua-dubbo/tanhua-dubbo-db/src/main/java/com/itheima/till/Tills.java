package com.itheima.till;

import java.util.List;

public class Tills {

    public static String getDeptIdSql(List<Long> deptIdList){
        String s = "";
        for(int i = 0; i < deptIdList.size();i++){
            if(i!=(deptIdList.size()-1)){
                s += deptIdList.get(i) + ",";
            }else{
                s += deptIdList.get(i);
            }
        }
        return s;
    }
}
