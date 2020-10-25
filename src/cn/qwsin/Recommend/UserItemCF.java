package cn.qwsin.Recommend;

import cn.qwsin.common.*;

import java.io.PrintStream;
import java.util.*;

public class UserItemCF {

    private int mostSimNum;//最相似的用户数量
    private int mostRecNum;//最值得推荐的产品数量a
    private int userNum;//用户数量
    private int movieNum;//电影数量
    private ArrayList<ArrayList<Double>> rating;//用户评分矩阵
    private ArrayList<ArrayList<Double>> sim;//用户/项相似矩阵
    private ArrayList<ArrayList<Double>> predict;//记录预测分数

    public UserItemCF(int mostRecNum, int mostSimNum){
        this.mostRecNum = mostRecNum;
        this.mostSimNum = mostSimNum;
    }

    public void loadData(String filePath){
        int[] tmp = new int[2];
        rating = LoadData.load(filePath);
        if(rating == null) rating = new ArrayList<>();
        userNum = rating.size();
        movieNum = rating.get(0).size();
    }

    //计算用户之间的相似矩阵
    public void getSimMatrix_UserCF(){
        sim = Init.initArray2(userNum,userNum,-1.0);
        for(int i=0;i<userNum;++i){
            sim.get(i).set(i,1.0);//自己和自己的一定是1
            for(int j=i+1;j<userNum;++j){
                //使用余弦方式计算相关系数
                double xy=0,y2=0,x2=0;
                for(int k=0;k<movieNum;++k){
                    xy += rating.get(i).get(k)*rating.get(j).get(k);
                    y2 += MyMath.sqr(rating.get(j).get(k));
                    x2 += MyMath.sqr(rating.get(i).get(k));
                }
                double ans = xy/Math.sqrt(y2*x2);
                sim.get(i).set(j,ans);
                sim.get(j).set(i,ans);
            }
        }
    }

    //给某一个用户推荐商品
    public Integer[] recommend_UserCF(int userID){
        Integer[] v = new Integer[userNum];
        for(int i=0;i<userNum;++i) v[i]=i;
        //按照相似度排序，记得处理时要去除自己
        Arrays.sort(v, (o1, o2) -> -sim.get(userID).get(o1).compareTo(sim.get(userID).get(o2)));

        predict = Init.initArray2(userNum,movieNum,-1.0);
        for(int i=0;i<movieNum;++i)
            if(true || rating.get(userID).get(i)==-1.0) {//枚举没买的
                double fz = 0, fm = 0;//定义分子分母
                for (int j = 1; j < Math.min(userNum, 1 + mostSimNum); ++j) {//枚举用户
                    int user2 = v[j];
                    if (rating.get(user2).get(i) == -1.0) continue;
                    fz += sim.get(userID).get(user2) * rating.get(user2).get(i);
                    fm += sim.get(userID).get(user2);
                }
                if (fm == 0) continue;
                predict.get(userID).set(i,fz/fm);
            }
        Integer[] movie = new Integer[movieNum];
        for(int i=0;i<movieNum;++i) movie[i]=i;
        Arrays.sort(movie, (o1, o2) -> -predict.get(userID).get(o1).compareTo(predict.get(userID).get(o2)));
        return movie;
    }

    //给所有用户推荐商品
    public void recommendForAll_UserCF(){
        for(int i=0;i<userNum;++i){
            System.out.printf("正在为第%d位用户推荐:",i+1);
            Integer[] rec = recommend_UserCF(i);
            for(int j=0;j<mostRecNum;++j){
                System.out.printf("%d-%.1f, ",rec[j]+1,predict.get(i).get(rec[j]));
            }
            System.out.println();
        }

        //评估部分
        double sumMSE=0;
        for(int i=0;i<userNum;++i){
            System.out.printf("第%d位用户的均方差:",i);
            double MSE=0;
            for(int j=0;j<movieNum;++j){
                MSE += MyMath.sqr(rating.get(i).get(j)-predict.get(i).get(j));
            }
            sumMSE += MSE/movieNum;
            System.out.println(MSE/movieNum);
        }
        System.out.println("总均方差:"+sumMSE/userNum);
    }

    //计算商品之间的相似性
    public void getSimMatrix_ItemCF(){
        sim = Init.initArray2(movieNum,movieNum,-1.0);
        for(int i=0;i<movieNum;++i){//枚举两个item计算相关度
            System.out.printf("给第%d个项计算相似度\n",i);
            sim.get(i).set(i,1.0);
            for(int j=0;j<movieNum;++j){
                double xy=0,x2=0,y2=0;
                for(int k=0;k<userNum;++k){
                    xy += rating.get(k).get(i)*rating.get(k).get(j);
                    x2 += MyMath.sqr(rating.get(k).get(i));
                    y2 += MyMath.sqr(rating.get(k).get(j));
                }
                double ans=xy/Math.sqrt(x2*y2);
                sim.get(i).set(j,ans);
                sim.get(j).set(i,ans);
            }
        }
    }

    //将某个商品推荐给用户
    public Integer[] recommend_ItemCF(int movieID,Double[] predict){
        Integer[] movie = new Integer[movieNum];
        for(int i=0;i<movieNum;++i) movie[i]=i;
        Arrays.sort(movie, (o1, o2) -> -sim.get(o1).get(movieID).compareTo(sim.get(o2).get(movieID)));

        for(int i=0;i<userNum;++i) if(true || rating.get(i).get(movieID)==-1.0){
            double fm=0,fz=0;
            for(int j=1;j<Math.min(movieNum,1+mostSimNum);++j){
                int movie2=movie[j];
                if(rating.get(i).get(movie2)==-1.0) continue;
                fz+=sim.get(movie2).get(movieID)*rating.get(i).get(movie2);
                fm+=sim.get(movie2).get(movieID);
            }
            if(fm==0) continue;
            predict[i] = fz/fm;
        }

        Integer[] user = new Integer[userNum];
        for(int i=0;i<userNum;++i) user[i]=i;
        Arrays.sort(user, (o1, o2) -> -predict[o1].compareTo(predict[o2]));
        return user;
    }

    //将所有商品推荐给用户
    public void recommendForAll_ItemCF(){
        try {
            PrintStream ps = new PrintStream("D:/log.txt");
            PrintStream out = System.out;
            double sumMSE=0;
            for (int i = 0; i < movieNum; ++i) {
//                System.setOut(out);
//                System.out.printf("%d电影处理中\n",i);
//                System.setOut(ps);
                System.out.printf("%d电影被推荐给: ", i);
                Double[] predict = new Double[userNum];
                Arrays.fill(predict,-1.0);
                Integer[] rec = recommend_ItemCF(i, predict);
                for (int j = 0; j < mostRecNum; ++j) {
                    System.out.printf("%d-%.1f, ", rec[j], predict[rec[j]]);
                }

                double MSE=0;
                for(int j=0;j<userNum;++j){
                    MSE += MyMath.sqr(rating.get(j).get(i)-predict[j]);
                }
                MSE/=movieNum;
                sumMSE+=MSE;
                System.out.printf("第%d电影均方差%.6f\n",i,MSE);
            }
            System.out.println("总均方差:"+sumMSE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
