import cn.qwsin.Classify.KNN;

public class TestKNN {
    public static void main(String[] args){
        String trainFilePath = "C:\\Users\\QWsin\\Desktop\\文档\\课程相关\\数据挖掘与大数据分析\\实验3\\数据\\forKNN\\iris.2D.test.txt";
        String testFilePath = "C:\\Users\\QWsin\\Desktop\\文档\\课程相关\\数据挖掘与大数据分析\\实验3\\数据\\forKNN\\iris.2D.train.txt";
        KNN knn = new KNN(trainFilePath,3);
        knn.predict(testFilePath);
    }
}
