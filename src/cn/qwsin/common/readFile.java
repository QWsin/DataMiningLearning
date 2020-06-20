package cn.qwsin.common;

import cn.qwsin.Normalize.ListArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.function.DoublePredicate;

public class readFile {
    public static double[] readLine(String s){
        String[] tmp = s.trim().split("[, ]");
        double[] num = new double[tmp.length];
        for(int i=0;i<tmp.length;++i){
            num[i]=Double.parseDouble(tmp[i]);
        }
        return num;
    }

    //给出文件路径和归一到的范围，返回读取到的并且归一化之后的数据
    public static ArrayList<double[]> loadData(String filePath, int len) {
        ArrayList<double[]> data = new ArrayList<>();
        File file = new File(filePath);
        FileReader fileReader;
        try {
            fileReader = new FileReader(file);
            BufferedReader bf = new BufferedReader(fileReader);
            String s;
            while ((s = bf.readLine()) != null) {
                data.add(readFile.readLine(s));
            }
            if(data.size() > 0) {
                int dim = data.get(0).length;
                data = ListArray.Normalize(data, dim, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

}
