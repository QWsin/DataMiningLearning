package cn.qwsin.FeatureSelection;

import cn.qwsin.InstanceOutput.Output;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

public class Weka {
    //instances待处理数据集，k是筛选的特征数量
    public static Instances featureSelection(Instances instances, int k) throws Exception {
        AttributeSelection as = new AttributeSelection();
        Ranker ranker = new Ranker();
        ranker.setThreshold(0.0);//筛选大于某个值的特征
        ranker.setNumToSelect(k-1);//选几个特征,最终选出来的等于参数+1个

        ASEvaluation ae= new InfoGainAttributeEval();
        as.setEvaluator(ae);
        as.setSearch(ranker);
        as.setInputFormat(instances);
        return Filter.useFilter(instances, as);
    }

    public static void main(String[] args) throws Exception {
        String filePath="C:\\Users\\QWsin\\Documents\\weka-data\\iris.arff";
        Output output = new Output();

        output.printStep("读取数据...");
        ConverterUtils.DataSource dataSource= new ConverterUtils.DataSource(filePath);
        Instances instances = dataSource.getDataSet();

        output.printStep("筛选特征...");
        int k=2;
        Instances reductData= featureSelection(instances,k);
        output.printAttribute(reductData);
        output.printStep("保存文件...");
        ConverterUtils.DataSink.write(filePath.substring(0,filePath.length()-5)+"FeatureSelected_"+(k)+".arff", reductData);
        output.printStep("Finished");
    }
}
