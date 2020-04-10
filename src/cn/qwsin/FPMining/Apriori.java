package cn.qwsin.FPMining;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class Apriori {
    private ArrayList<ArrayList<String>> data;//事务数据集
    private float minSup=0.22f;
    private float minconf=0.7f;

    public Apriori(ArrayList<ArrayList<String>> data){
        this.data = data;

    }

    public ArrayList<String> FP_one(){
        HashMap<String,Integer> count = new HashMap<>();
        for (ArrayList<String> list : data) {
            for (String s : list) {
                int v = 0;
                if (count.containsKey(s)) {
                    v = count.get(s);
                }
                count.put(s, v + 1);
            }
        }
        /*
        TODO:考虑用什么数据结构存除FP
         */
    }
}
