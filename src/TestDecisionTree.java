import cn.qwsin.Classify.DecisionTree;

import java.util.Set;

public class TestDecisionTree {
    public static void main(String[] args){
        DecisionTree tree=new DecisionTree();
        tree.loadTrainRecord("C:\\Users\\QWsin\\Desktop\\文档\\课程相关\\数据挖掘与大数据分析\\实验3\\数据\\forDecisionTree\\train.txt");
        Set<DecisionTree.Record> test = tree.loadTestRecord("C:\\Users\\QWsin\\Desktop\\文档\\课程相关\\数据挖掘与大数据分析\\实验3\\数据\\forDecisionTree\\test.txt");
        tree.buildTreeID3();
        Set<DecisionTree.Record> result = tree.classifyTest(test);
        for(DecisionTree.Record record : result){
            System.out.print(record.attrDisc);
            System.out.println(" Label:"+record.label);
        }
    }
}
