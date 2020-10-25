package cn.qwsin.Recommend;

import cn.qwsin.common.*;

import java.io.BufferedReader;
import java.util.ArrayList;

public class LoadData {
    //作用是将数据从评分列表转为矩阵
    public static ArrayList<ArrayList<Double>> load(String filePath){
        BufferedReader br = ReadFile.getBR(filePath);
        if(br==null) return null;
        ArrayList<ArrayList<Double>> dataset = new ArrayList<>();
        String s;
        try {
            s=br.readLine();
            double[] t = ReadFile.readLine(s);
            int userNum=(int)t[0];
            int movieNum=(int)t[1];
            dataset = Init.initArray2(userNum,movieNum,-1.0);
            while ((s = br.readLine()) != null) {
                s = s.trim();
                if (s.length() == 0) continue;
                double[] tmp = ReadFile.readLine(s);//每行分别是 用户 电影 评分
                //因为数据都是从1开始，所以需要减1
//                if((int)tmp[1]>1000) continue;//**暂时忽略1k以上的
                dataset.get((int)tmp[0]-1).set((int)tmp[1]-1,tmp[2]);//修改分数
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return dataset;
    }
}

