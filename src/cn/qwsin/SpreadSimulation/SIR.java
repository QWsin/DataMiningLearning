package cn.qwsin.SpreadSimulation;

import cn.qwsin.Graph.*;
import javafx.util.Pair;

import java.util.*;

//T表示结点类型，可以是int，string，或者结构体
public class SIR<T> {
    private double pu;//S->I的概率
    private double pb;//I->R的概率
    private int N=10;//每个节点重复模拟的次数
    private int len=10;//传播轮数
    private Map<T,State> state;//记录每个点的状态
    private Random r;

    public SIR(double pu,double pb){
        this.pu = pu;
        this.pb = pb;
        state = new HashMap<>();
        r = new Random();
    }

    public enum State{
        S, I, R
    }

    //对于单个结点模拟，返回固定回合后被感染的总节点数量
    private int run(T s,Graph<T> graph){
        int count=0;
        Queue<Pair<T,Integer>> q = new LinkedList<>();
        state.put(s,State.I);
        q.offer(new Pair<>(s,0));++count;//放入初始点，更新状态
        while(!q.isEmpty()){
            Pair<T,Integer> u = q.poll();
            if(u.getValue() > len) continue;
            for(T v : graph.to(u.getKey())){
                if(state.get(v)==State.S && r.nextDouble()<pu){//随机结果为感染下一个S点
                    ++count;//计数
                    state.put(v,State.I);//修改状态
                    Pair<T,Integer> nxt = new Pair<>(v,u.getValue()+1);
                    q.offer(nxt);//入队
                }
            }
            if(r.nextDouble()<pb){//如果变为R就不传播了
                state.put(u.getKey(),State.R);
            }else{//否则还会继续传播
                Pair<T,Integer> nxt = new Pair<>(u.getKey(),u.getValue()+1);
                q.offer(nxt);
            }
        }
        return count;
    }

    //进行仿真，返回一个按重要度排序的结点和重要度数组
    public ArrayList<Pair<T,Double>> simulate(Graph<T> graph){
        Set<T> points = graph.getVertex();
        ArrayList<Pair<T,Double>> result = new ArrayList<>();
        for(T v : points){
            double sum=0;
            for(int i=0;i<N;++i){
                for(T v2 : points) state.put(v2,State.S);//初始化状态全为S
                sum += run(v,graph);
            }
            sum/=N;
            result.add(new Pair<>(v,sum));
        }
        //从大到小排序
        result.sort((o1, o2) -> -o1.getValue().compareTo(o2.getValue()));
        return result;
    }
}
