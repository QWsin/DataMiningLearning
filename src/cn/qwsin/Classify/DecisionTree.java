package cn.qwsin.Classify;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class DecisionTree {

    private Set<Record> records;//保存记录
    private String[] attributes;//保存属性
    private String LabelName;//保存类别名称
    private TreeNode root;//保存树根

    public DecisionTree(){
        records = new HashSet<>();
    }

    //Record类表示数据集中一条记录
    public class Record{
        int ID;//数据编号
        public String label;//类别
        public Map<String,String> attrDisc;//离散的属性值
        Record(){
            attrDisc = new HashMap<>();
        }
    }

    //AttributeField类表示某种属性
    public class AttributeField{
        String name;
        String attrValueDisc;//存放离散属性值
        public double attrValueContLow;//存放连续属性的上下限
        public double attrValueContHigh;
    }


    //treeNode类表示Decision tree上的一个结点
    public class TreeNode {
        Map<AttributeField, TreeNode> children;//存储儿子节点
        String attrName;//该节点往下分割时使用的属性名称
        Set<Record> records;//该节点包含的记录数
        Set<String> attrNames;//该节点包含的属性名称
        String label;//叶子节点的属性标识
        TreeNode(){
            children = new HashMap<>();
        }
        boolean isLeaf(){return children==null || children.size()==0;}
    }

    //flag 0:train 1:test
    //train需要额外处理属性值
    private Record getRecord(String s,int flag){
        Record record = new Record();
        //TODO:对连续属性的处理
        String[] value=s.trim().split("[, ]");
        for(int i=0;i<value.length-(1-flag);++i){
            record.attrDisc.put(attributes[i],value[i]);//放入特征值
        }
        if(flag==0)record.label=value[value.length-1];
        return record;
    }

    //统计各个类别在记录中出现的次数
    private Map<String, Integer> countLabel(Set<Record> records){
        Map<String, Integer> count = new HashMap<>();
        for(Record record: records){
            if(count.containsKey(record.label)){//该类别出现过，计数在原有基础上+1
                int t=count.get(record.label);
                count.put(record.label,t+1);
            }else{//该类别未出现过，记为出现一次
                count.put(record.label,1);
            }
        }
        return count;
    }

    //信息熵计算
    private double entropy(Set<Record> records){
        Map<String, Integer> count=countLabel(records);
        //对于每种类别，累加值
        double sum=0;
        for(Map.Entry<String,Integer> entry: count.entrySet()){
            double pk=(double)entry.getValue()/records.size();//计算出现概率
            sum+=pk*(Math.log(pk)/ Math.log(2));
        }
        return sum*(-1);
    }

    //根据某种属性值将记录划分（由于多处会用到所以构建函数）
    private Map<String,Set<Record>> divByAttr(Set<Record> records, String attributeName){
        Map<String,Set<Record>> divSets = new HashMap<>();//每个属性值对应一个记录集合
        for(Record record: records){
            String value=record.attrDisc.get(attributeName);//该记录对应属性的值
            if(divSets.containsKey(value)){//之前出现过，在原来的基础上添加一个再放入
                Set<Record> t=divSets.get(value);
                t.add(record);
                divSets.put(value,t);
            }else{//之前没有出现，新建一个放入
                Set<Record> t = new HashSet<>();
                t.add(record);
                divSets.put(value,t);
            }
        }
        return divSets;
    }

    //按照某个属性划分之后，信息增益的大小
    private double gainPartition(Set<Record> records, String attributeName){
        double gain=entropy(records);

        //TODO:此处应该有连续->离散的处理

        Map<String,Set<Record>> count = divByAttr(records,attributeName);

        //累加每种值的贡献
        for(Map.Entry<String,Set<Record>> entry: count.entrySet()){
            gain-=(double)entry.getValue().size()/records.size()*entropy(entry.getValue());
        }
        return gain;
    }

    //判断是否所有记录都属于同一类别
    private boolean haveSameLabel(Set<Record> records){
        int first=1;
        Record tmp = null;
        for(Record record : records){
            if(first == 1){
                tmp=record;
                first=0;
            }else{
                if(record!=tmp) return false;
            }
        }
        return true;
    }

    //判断是否某些记录中对于某些属性，值是一样的
    private boolean haveSameValue(Set<Record> records, Set<String> attrNames){
        int first=1;
        Record tmp=null;
        for(Record record: records){
            if(first==1){
                //取第一个作为参照
                first=0;
                tmp=record;
            }
            else{
                //遍历属性，判断是否都一样，只要有一个不同就不满足条件（只有一条记录时一定满足条件）
                for(String attr : attrNames) {
                    if (!record.attrDisc.get(attr).equals(tmp.attrDisc.get(attr))){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //找出records中出现次数最多的label，有多个label出现次数一样多时，随机返回一个
    private String findTheMostLabel(Set<Record> records){
        Map<String,Integer> vis = countLabel(records);
        int maxNum=0;
        String maxNumLabel=null;
        for(Map.Entry<String, Integer> entry : vis.entrySet()){
            if(entry.getValue() > maxNum){
                maxNum=entry.getValue();
                maxNumLabel=entry.getKey();
            }
        }
        return maxNumLabel;
    }

    //使用ID3方法建树
    private TreeNode rootTree_ID3(Set<Record> records,Set<String> attrNames){
        TreeNode root=new TreeNode();
        root.records=records;
        root.attrNames=attrNames;
        //同属一个类别，标记后返回
        if(haveSameLabel(records)){
            for(Record record : records) {
                root.label = record.label;
                return root;
            }
        }

        //没有可用来划分的属性或者所有记录在可用属性都有相同的值
        if(attrNames.size() == 0 || haveSameValue(records,attrNames)){
            root.label=findTheMostLabel(records);
            return root;
        }

        double maxGain=0;//记录最大信息增益和对应属性名
        String maxGainName="";
        //枚举可用属性名称，寻找信息增益最大的那一个
        for(String attrName : attrNames){
            double gain= gainPartition(records,attrName);
            if(gain > maxGain){
                maxGain=gain;
                maxGainName=attrName;
            }
        }

        root.attrName=maxGainName;

        //构建新的可用属性集合（去除本节点使用的）
        Set<String> newAttrNames = new HashSet<>(attrNames);
        newAttrNames.remove(maxGainName);

        //按照信息增益最大的属性划分集合，并且每一个集合产生一个儿子结点
        Map<String,Set<Record>> divSets=divByAttr(records,maxGainName);
        for(Map.Entry<String,Set<Record>> entry: divSets.entrySet()){
            AttributeField attributeField = new AttributeField();
            attributeField.name=maxGainName;
            attributeField.attrValueDisc=entry.getKey();
            root.children.put(attributeField,rootTree_ID3(entry.getValue(),newAttrNames));
        }

        return root;
    }

    //对一条新记录预测其分类
    private String classify(TreeNode root,Record record){
        //TODO:对于连续属性做处理
        if(root.isLeaf()){
            return root.label;
        }

        String value = record.attrDisc.get(root.attrName);//记录中该节点分支属性的值
        for(Map.Entry<AttributeField, TreeNode> entry : root.children.entrySet()){
            if(entry.getKey().attrValueDisc.equals(value)){//判断该分支属性值是否和该记录相同
                return classify(entry.getValue(), record);
            }
        }
        System.out.printf("分类出现错误，出现了训练时没有出现的属性值，属性名称:%s,属性值:%s\n",root.attrName,value);
        return "";
    }

    //TODO:测试读入函数
    //加载测试数据
    public Set<Record> loadTestRecord(String filePath){
        Set<Record> records = new HashSet<>();
        try{
            File file = new File(filePath);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String s;

            int cnt=0;
            while((s = br.readLine())!=null){//以下为特征
                Record record = getRecord(s,1);
                record.ID=++cnt;
                //TODO:对连续属性的处理
                records.add(record);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return records;
    }

    //加载训练数据（因为训练数据有类别而测试数据没有，所以需要分别写读入函数）
    public void loadTrainRecord(String filePath){
        try{
            File file = new File(filePath);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String s;
            s = br.readLine();
            String[] attrs = s.trim().split("[, ]");//第一行 为特征名称
            LabelName = attrs[attrs.length-1];

            attributes = new String[attrs.length-1];
            System.arraycopy(attrs, 0, attributes, 0, attrs.length - 1);

            int cnt=0;
            while((s = br.readLine())!=null){//以下为特征
                Record record=getRecord(s,0);
                record.ID=++cnt;
                records.add(record);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void buildTreeID3(){
        Set<String> attrs=new HashSet<>();
        Collections.addAll(attrs, attributes);
        root=rootTree_ID3(records,attrs);
    }

    //外部调用的函数，将test数据全部分类
    public Set<Record> classifyTest(Set<Record> testRecords){
        Set<Record> result = new HashSet<>(testRecords);
        for(Record record : result){
            record.label=classify(root, record);
        }
        return result;
    }
}
