import cn.qwsin.FrequentPatternMining.Apriori;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TestApriori {
    public static void main(String[] args) throws IOException {
        String fn="C:\\Users\\QWsin\\Desktop\\文档\\课程相关\\数据挖掘与大数据分析\\实验2\\data.txt";
        File file = new File(fn);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        ArrayList<Set<String>> data = new ArrayList<>();

        String s;
        while((s = br.readLine())!=null){
            String[] ss = s.split("[, ]");//正则表达式分割，因为空格和逗号都会出现
            data.add(new HashSet<>());
            int pos = data.size();
            for(int i=1;i<ss.length;++i) {
                data.get(pos - 1).add(ss[i]);
            }
        }

//        for(Set<String> set: data){
//            System.out.println(set);
//        }

        Apriori apriori = new Apriori(data,0.22f,0.7f);
        apriori.findAllFrequentItem();
        apriori.findAssociationRules();
    }
}
