import cn.qwsin.FPMining.Apriori;

import java.io.*;
import java.util.ArrayList;

public class TestApriori {
    public static void main(String[] args) throws IOException {
        String fn="C:\\Users\\QWsin\\Desktop\\文档\\课程相关\\数据挖掘与大数据分析\\实验2\\data.txt";
        File file = new File(fn);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        ArrayList<ArrayList<String>> data = new ArrayList<>();

        String s;
        while((s = br.readLine())!=null){
            String[] ss = s.split(",");//分割
            data.add(new ArrayList<>());
            int pos = data.size();
            for(int i=0;i<ss.length;++i) {
                data.get(pos - 1).add(ss[i]);
            }
        }

        Apriori apriori = new Apriori(data);
    }
}
