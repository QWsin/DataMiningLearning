package cn.qwsin.Classify;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KNN {

    private int k;//表示KNN中的k，即找出最近的多少个来进行判断
    private ArrayList<Data> trainSet;

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
        trainSet=loadDataSet(trainFilePath);
        this.k=k;
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
                String[] ss = s.trim().split("[, ]");
                double[] tmp = new double[ss.length-1];//用临时数组tmp实现转化，默认lable在最后
                for(int i=0;i<ss.length-1;++i){
                    tmp[i]=Double.parseDouble(ss[i]);
                }
                Data newData = new Data(tmp, ss[ss.length-1]);//构造Data
                datas.add(newData);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return datas;
    }

    //计算欧几里得距离
    private double getDistance(Data a,Data b){
        if(a.data.length!=b.data.length){
            System.out.println("维数不同，无法计算欧几里得距离");
            return -1;
        }
        double dis=0;
        for(int i=0;i<a.data.length;++i){
            dis += Math.pow(a.data[i]-b.data[i],2);
        }
        return Math.sqrt(dis);
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
            dis[i]=getDistance(test,trainSet.get(i));
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

        int maxCount=0;
        String result="";
        for(Map.Entry<String,Integer> entry : count.entrySet()){
            if(entry.getValue() > maxCount){
                maxCount = entry.getValue();
                result = entry.getKey();
            }
        }
        return result;
    }

    //预测测试集的lable并统计正确率
    public void predict(String testFilePath){
        ArrayList<Data> testSet = loadDataSet(testFilePath);
        int OK=0;//记录预测正确的数量
        for (Data data : testSet) {
            String predictLable = classify(data);
            String result = "错误*";
            if (predictLable.equals(data.label)) {
                ++OK;
                result="正确";
            }
            System.out.printf("预测label:%s 真实label:%s %s\n",predictLable,data.label,result);
        }
        System.out.printf("总共%d个，正确%d个，错误%d个,正确率%.2f%%\n", testSet.size(),OK, testSet.size()-OK,(double)OK/ testSet.size()*100);
    }
}
