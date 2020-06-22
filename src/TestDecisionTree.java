import cn.qwsin.Classify.DecisionTree;

import java.util.Set;

public class TestDecisionTree {
    public static void main(String[] args){
        DecisionTree tree=new DecisionTree();
        String keShePath="C:\\Users\\QWsin\\Desktop\\文档\\课程相关\\数据挖掘课设\\data";
        String keChengPath="C:\\Users\\QWsin\\Desktop\\文档\\课程相关\\数据挖掘与大数据分析\\实验3\\数据\\forDecisionTree";
        tree.loadTrainRecord(keShePath+"\\adult.data");
        Set<DecisionTree.Record> test = tree.loadTestRecord(keShePath+"\\adult.test");
        tree.buildTree_ID3();
        Set<DecisionTree.Record> result = tree.classifyTest(test);

//        tree.buildRandomForest(100,3);
//        Set<DecisionTree.Record> result = tree.voteTest(test);
        tree.analyse(result);
    }
}
