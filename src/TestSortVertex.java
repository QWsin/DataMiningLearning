import cn.qwsin.Graph.Graph;
import cn.qwsin.SortVertex.*;;
import cn.qwsin.SpreadSimulation.SIR;
import cn.qwsin.common.ReadFile;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class TestSortVertex {
    public static void testSort() throws IOException {

    }
    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        Degree<Integer> degree = new Degree<>();
        Probability<Integer> prob = new Probability<>();
        Comentropy<Integer> comen = new Comentropy<>();
        SIR<Integer> sir = new SIR<>(0.3,0.3);

        Graph<Integer> graph = new Graph<>();

        String filePath="data/可用网络/Yeast.txt";
        BufferedReader br = ReadFile.getBR(filePath);
        String s;
        if(br==null) return ;
        s=br.readLine();
        double[] tmp = ReadFile.readLine(s);
        int n = (int)tmp[0];
        int m = (int)tmp[1];
        for(int i=0;i<n;++i) graph.addVertex(i);
        for(int i=0;i<m;++i){
            s=br.readLine();
            tmp = ReadFile.readLine(s);
            int u = (int)tmp[0];
            int v = (int)tmp[1];
            graph.addEdge(u-1,v-1);
        }

        Pair<Map<Integer,Double>,ArrayList<Integer>> probRes = prob.sortByProb(graph,0.3);
        Pair<Map<Integer,Integer>,ArrayList<Integer>> degreeRes = degree.sortByDegree(graph);
        Pair<Map<Integer,Integer>,ArrayList<Integer>> hindexRes = degree.sortByHindex(graph);
        Pair<Map<Integer,Integer>,ArrayList<Integer>> KshellRes = degree.sortByKshell(graph);
        Pair<Map<Integer,Double>,ArrayList<Integer>> comentRes = comen.sortByComent(graph);
        Pair<Map<Integer,Double>,ArrayList<Integer>> std = sir.simulate(graph);
        double[] probArr = new double[probRes.getValue().size()];
        double[] degrArr = new double[degreeRes.getValue().size()];
        double[] hindexArr = new double[hindexRes.getValue().size()];
        double[] KshellArr = new double[KshellRes.getValue().size()];
        double[] comentArr = new double[comentRes.getValue().size()];
        double[] stdArr = new double[std.getValue().size()];
        for(int i=0;i<KshellRes.getValue().size();++i) KshellArr[i]=KshellRes.getValue().get(i);
        for(int i=0;i<probRes.getValue().size();++i) probArr[i]=probRes.getValue().get(i);
        for(int i=0;i<degreeRes.getValue().size();++i) degrArr[i]=degreeRes.getValue().get(i);
        for(int i=0;i<hindexRes.getValue().size();++i) hindexArr[i]=hindexRes.getValue().get(i);
        for(int i=0;i<comentRes.getValue().size();++i) comentArr[i]=comentRes.getValue().get(i);
        for(int i=0;i<std.getValue().size();++i) stdArr[i]=std.getValue().get(i);

//        System.out.println("基于概率:"+MyMath.CaculateCorrelation(probArr,stdArr));
//        System.out.println("基于度数:"+MyMath.CaculateCorrelation(degrArr,stdArr));
//        System.out.println("基于Hindex:"+MyMath.CaculateCorrelation(hindexArr,stdArr));
//        System.out.println("基于Kshell:"+MyMath.CaculateCorrelation(KshellArr,stdArr));
//        System.out.println("基于信息熵:"+MyMath.CaculateCorrelation(comentArr,stdArr));
//        System.out.println("基于Kshell-基于index:"+MyMath.CaculateCorrelation(KshellArr,hindexArr));
        System.out.println("top10的点+Kshell值");
        for(int i=0;i<10;++i) System.out.println((KshellArr[i]+1)+":"+KshellRes.getKey().get((int)KshellArr[i]));
        System.out.println("概率的值");
        for(int i=0;i<10;++i){
            System.out.println((probArr[i])+1+":"+probRes.getKey().get((int)probArr[i]));
        }
        System.out.println("度数的值");
        for(int i=0;i<10;++i){
            System.out.println((degrArr[i]+1)+":"+degreeRes.getKey().get((int)degrArr[i]));
        }
        System.out.println("Hindex的值");
        for(int i=0;i<10;++i){
            System.out.println((hindexArr[i]+1)+":"+hindexRes.getKey().get((int)hindexArr[i]));
        }
        System.out.println("信息熵的值");
        for(int i=0;i<10;++i){
            System.out.println((comentArr[i]+1)+":"+comentRes.getKey().get((int)comentArr[i]));
        }
        System.out.println("SIR的值");
        for(int i=0;i<10;++i){
            System.out.println((stdArr[i]+1)+":"+std.getKey().get((int)stdArr[i]));
        }
//        for(int i=0;i<10;++i) System.out.print((stdArr[i]+1)+" ");
        long endTime = System.currentTimeMillis();
        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
    }
}
