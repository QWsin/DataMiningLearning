package cn.qwsin.Classify;

import cn.qwsin.common.Find;
import cn.qwsin.common.MyMath;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class KNN {

    private int k;//表示KNN中的k，即找出最近的多少个来进行判断
    private ArrayList<Data> trainSet;
    private Set<String> labels;

    private class Data{
        private double[] data;//存放数据向量
        private String label;//表示该数据的类型

        Data(double[] data, String label){
            this.data=data;
            this.label = label;
        }
    }

    //filePath表示训练集的路径
    public KNN(String trainFilePath, int k){
        this.labels=new HashSet<>();
        this.k=k;
        this.trainSet=loadDataSet(trainFilePath);
    }

    //从文件中读取数据，读取训练集和测试集的时候都可以用
    private ArrayList<Data> loadDataSet(String filePath){
        ArrayList<Data> datas = new ArrayList<>();
        try {
            File file = new File(filePath);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String s;
            while((s = br.readLine())!=null){
                if(s.length()==0) continue;
                String[] ss = s.trim().split("[, ]");
                double[] tmp = new double[ss.length-1];//用临时数组tmp实现转化，默认lable在最后
                for(int i=0;i<ss.length-1;++i){
                    tmp[i]=Double.parseDouble(ss[i]);
                }
                Data newData = new Data(tmp, ss[ss.length-1]);//构造Data
                labels.add(newData.label);
                datas.add(newData);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return datas;
    }

    //对某一个Data进行分类
    private String classify(Data test){
        if(k > trainSet.size()){
            System.out.println("k大于训练集数据总数，无法分类，请更改");
            return "";
        }
        Map<String,Integer> count = new HashMap<>();//计数每种lable出现次数

        double[] dis = new double[trainSet.size()];//预先把所有距离处理出来.减少计算次数，也方便改为一个极大值
        for(int i=0;i<trainSet.size();++i){
            dis[i]= MyMath.getDistance(test.data,trainSet.get(i).data,test.data.length);
        }

        //由于k一般都小于10，所以使用每次找出一个的算法，复杂度O(nk),这样的数据范围下，会比排序O(nlogn)快
        for(int t=1;t<=k;++t) {
            double mdis = Integer.MAX_VALUE;//记录这一趟的最小值
            int pos=-1;//记录最小值的位置
            for (int i = 0; i < trainSet.size(); ++i) {
                if(dis[i] < mdis){
                    mdis = dis[i];
                    pos = i;
                }
            }
            String key=trainSet.get(pos).label;
            //更新计数器
            if(count.containsKey(key)){
                int c=count.get(key);
                count.put(key,c+1);
            }else {
                count.put(key, 1);
            }
            //置为极大值，后面就不会再选择这个位置
            dis[pos]=Integer.MAX_VALUE;
        }

        return Find.findMost(count);
    }

    //预测测试集的lable并统计正确率
    public void predict(String testFilePath){
        ArrayList<Data> testSet = loadDataSet(testFilePath);
        Map<Pair<String,String>,Integer> count = new HashMap<>();
        int OK=0;//记录预测正确的数量
        for (Data data : testSet) {
            String predictLable = classify(data);
            String result = "错误*";
            if (predictLable.equals(data.label)) {
                ++OK;
                result="正确";
            }
            Pair<String,String> pair = new Pair<>(predictLable,data.label);
            if(count.containsKey(pair)){
                int k=count.get(pair);
                count.put(pair,k+1);
            }
            else{
                count.put(pair,1);
            }
        }
        for(String s1 : labels) System.out.print("\t"+s1);
        System.out.println();

        for(String s1 : labels){
            System.out.print(s1+" \t");
            for(String s2 : labels){
                int ans;
                if(count.get(new Pair<>(s1,s2))==null) ans=0;
                else ans=count.get(new Pair<>(s1,s2));
                System.out.print(ans+"\t");
            }
            System.out.println();
        }
        System.out.printf("总共%d个，正确%d个，错误%d个,正确率%.2f%%\n", testSet.size(),OK, testSet.size()-OK,(double)OK/ testSet.size()*100);
    }
}
