package cn.qwsin.common;

import java.util.Map;

public class Find {
    //找出众数,即integer最大的string，返回string名称
    public static String findMost(Map<String, Integer> count) {
        int maxCount=0;
        String maxName="";

        for(Map.Entry<String,Integer> entry : count.entrySet()){
            if(entry.getValue() > maxCount){
                maxCount = entry.getValue();
                maxName = entry.getKey();
            }
        }
        return maxName;
    }
}
