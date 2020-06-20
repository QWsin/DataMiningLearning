import cn.qwsin.Cluster.Kmeans;

public class TestKmeans {
    public static void main(String[] args){
        Kmeans test = new Kmeans(3);
        String filePath = "C:\\Users\\QWsin\\Desktop\\文档\\课程相关\\数据挖掘与大数据分析\\实验4\\data.txt";
        test.load(filePath);
        test.cluster();
    }
}
