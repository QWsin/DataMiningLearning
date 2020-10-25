import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.qwsin.Cluster.DBSCAN;
import cn.qwsin.Cluster.Kmeans;
import cn.qwsin.Normalize.ListArray;
import cn.qwsin.common.ReadFile;

public class stockAnalyze {
    static ArrayList<String> names = new ArrayList<>();
    private static ArrayList<double[]> loadData(){
        String dirPath="data/96-SP500";
        String listPath="data/96-SP500/股票标记.txt";
        try{
            BufferedReader br = ReadFile.getBR(listPath);
            if(br==null) {
                System.out.println("读取股票标记文件出错");
                return null;
            }
            ArrayList<double[]> data = new ArrayList<>();
            int cnt=0;
            String name;
            while((name=br.readLine())!=null){
                name=name.trim();
                if(name.length()==0) continue;
                //读取每一支股票的名字，再按照名字去找SQ文件
                names.add(name);
                BufferedReader brFile = ReadFile.getBR(dirPath+"/"+name+".SQ");
                if(brFile==null){
                    System.out.println("加载文件失败");
                    continue;
                }
                ArrayList<Double> price = new ArrayList<>();
                String s;
                while((s=brFile.readLine())!=null){
                    //读取每一行，其实有用的只有最后一个，表示当前价格
                    s=s.trim();
                    if(s.length()==0) continue;
                    double[] tmp = ReadFile.readLine(s);
                    price.add(tmp[tmp.length-1]);
                }
                //转为double数组
                double[] R = new double[price.size()-1];
                for(int i=0;i<price.size()-1;++i){
                    //根据公式计算R
                    R[i]=Math.log(price.get(i+1)/price.get(i));
                }
                data.add(R);
            }
            return data;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void run(){
        ArrayList<double[]> data = loadData();
        if(data!=null && data.size()>0) {
            data = ListArray.Normalize(data, data.get(0).length, 1);//归一化
            //Kmeans处理
//            Kmeans kmeans = new Kmeans(9,data.get(0).length,data);
//            kmeans.clusterStock(names);

            //DBSCAN处理
            DBSCAN dbscan = new DBSCAN(0.8,3,data.get(0).length,data);
            dbscan.clusterStock(names);
        }
    }

    public static void analyse() throws IOException {
        BufferedReader br = ReadFile.getBR("data/股票结果/kinds.txt");
        Map<String,String> kind = new HashMap<>();//名字到类别的映射
        if(br!=null){
            String s;
            while((s=br.readLine())!=null){
                String[] tmp=s.trim().split("\\s+");
                kind.put(tmp[0],tmp[1]);
            }
        }

        br = ReadFile.getBR("data/股票结果/Kmeansresult.txt");
        double sum=0,quan=0;
        if(br!=null){
            String s;
            while((s=br.readLine())!=null){
                Map<String,Integer> count = new HashMap<>();//统计每种类别有多少
                int sumlen=0;//当前聚类的总数量
                s=s.substring(1,s.length()-1);
                String[] names = s.split(",");
                sumlen = names.length;
                for(String name : names){
                    name=name.trim();
                    if(!kind.containsKey(name)){
                        int stop=1;
                    }
                    if(count.containsKey(kind.get(name))){
                        int k=count.get(kind.get(name));
                        count.put(kind.get(name),k+1);
                    }else{
                        count.put(kind.get(name),1);
                    }
                }
                double pi=0;
                String name="";
                for(Map.Entry<String,Integer> entry : count.entrySet()){
                    double cur = (double)entry.getValue()/sumlen;
                    if(cur > pi){
                        pi=cur;
                        name=entry.getKey();
                    }
                }
                sum+=sumlen*pi;
                quan+=sumlen;
                System.out.println(name);
            }
        }
        sum/=quan;
        System.out.println(sum);
    }

    public static void main(String[] args) throws IOException {
        run();
//        analyse();
    }
}
