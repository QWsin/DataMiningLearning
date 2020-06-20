import cn.qwsin.Cluster.DBSCAN;

public class TestDBSCAN {
    public static void main(String[] args){
        String filePath="C:\\Users\\QWsin\\Desktop\\文档\\课程相关\\数据挖掘与大数据分析\\实验4\\data.txt";
        DBSCAN dbscan = new DBSCAN(2,4,2);
        dbscan.loadFile(filePath);
        dbscan.cluster();
    }
}
