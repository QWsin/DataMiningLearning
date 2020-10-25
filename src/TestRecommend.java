import cn.qwsin.Recommend.BiGraph;
import cn.qwsin.Recommend.UserItemCF;

public class TestRecommend {
    public static void main(String[] args){
        String filePath="data/ratings_disc.txt";
//        UserItemCF userItemCF = new UserItemCF(3,3);
//        userItemCF.loadData(filePath);
//        userItemCF.getSimMatrix_ItemCF();
//        userItemCF.recommendForAll_ItemCF();;
        BiGraph biGraph = new BiGraph();
        biGraph.loadData(filePath);
//        biGraph.heatConductionAll();
    }
}
