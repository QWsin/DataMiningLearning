import cn.qwsin.FrequentPatternMining.Apriori;

import java.io.*;
import java.util.*;

public class TestApriori {
    public static void main(String[] args) throws IOException {
        String fn="C:\\Users\\QWsin\\Desktop\\文档\\课程相关\\数据挖掘与大数据分析\\实验2\\data.txt";
        File file = new File(fn);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        Map<Set<String>,Integer> data = new HashMap<>();

        String s;
        while((s = br.readLine())!=null){
            String[] ss = s.trim().split("[, ]");//正则表达式分割，因为空格和逗号都会出现
            Set<String> set = new HashSet<>();
            for(int i=0;i<ss.length;++i) {//第一列有序号的时候从1开始，否则从0开始
                set.add(ss[i]);
            }
            data.put(set,0);
        }

//        for(Set<String> set: data){
//            System.out.println(set);
//        }

        Apriori apriori = new Apriori(data,0.22f,0.7f);
        apriori.findAllFrequentItem();
        apriori.findAssociationRules();
    }
}
