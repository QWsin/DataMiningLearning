package cn.qwsin.Normalize;

import cn.qwsin.InstanceOutput.Output;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;

public class Weka {
    public static Instances normalize(Instances instances) throws Exception {
        weka.filters.unsupervised.attribute.Normalize norm = new weka.filters.unsupervised.attribute.Normalize();//建立一个归一化filter
        norm.setInputFormat(instances);//为filter导入数据
        return Filter.useFilter(instances, norm);
    }
    public static void main(String[] args) throws Exception {
        Output output = new Output();
        output.printStep("读取数据...");
        String filePath="C:\\Users\\QWsin\\Documents\\weka-data\\iris.arff";
        DataSource source = new DataSource(filePath); //获取数据源
        Instances instances = source.getDataSet();//导入数据

        output.printStep("归一化处理..");
        Instances newInstances= normalize(instances);

        output.printStep("打印结果");
        output.printAttribute(newInstances);
        output.printStep("输出到文件..");
//        ConverterUtils.DataSink.write(filePath.substring(0,filePath.length()-5)+"Normalized.arff", newInstances);
    }
}
