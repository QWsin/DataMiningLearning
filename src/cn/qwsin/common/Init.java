package cn.qwsin.common;

import java.util.ArrayList;

public class Init {
    public static ArrayList<ArrayList<Double>> initArray2(int n,int m,Double v){
        ArrayList<ArrayList<Double>> res = new ArrayList<>();
        for(int i=0;i<n;++i){
            ArrayList<Double> tmp = new ArrayList<>();
            for(int j=0;j<m;++j) tmp.add(v);
            res.add(tmp);
        }
        return res;
    }
}
