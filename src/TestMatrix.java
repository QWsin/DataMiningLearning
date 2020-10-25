
import cn.qwsin.Recommend.MatrixBased;
public class TestMatrix {
    public static void main(String[] args){
        MatrixBased matrixBased = new MatrixBased(4,0.00004,0.01);
        matrixBased.loadData("data/ratings_disc.txt");
        matrixBased.train(5000);
        for(int i=0;i<20;++i){
            System.out.printf("%.2f ",matrixBased.query(0,i));
        }
    }
}
