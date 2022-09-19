#                        [MapReduce](https://hadoop.apache.org/docs/stable/hadoop-mapreduce-client/hadoop-mapreduce-client-core/MapReduceTutorial.html)简介

<span style=color:red></span>

## 1.1定义

​	MapReduce是一个<span style=color:red>分布式运算程序</span>的编程框架，是用户开发“基于Hadoop的数据分析应用”的核心框架。它的核心功能是<span style=color:red>用户编写的业务逻辑代码</span>将和<span style=color:red>自带默认组件</span>整合成一个完整的<span style=color:red>分布式运算程序</span>，并发运行在一个Hadoop集群上。

## 1.2优缺点

### 	1.2.1优点

1.  MapReduce 易于编程：

   <span style=color:red>它简单的实现一些接口，就可以完成一个分布式程序</span>， 这个分布式程序可以分布到大量廉价的 PC 机器上运行。也就是说你写一个分布式程序，跟写一个简单的串行程序是一模一样的。就是因为这个特点使得 MapReduce 编程变得非常流行。 

2.  良好的扩展性：
   当你的计算资源不能得到满足的时候，你可以通过<span style=color:red>简单的增加机器</span>来扩展它的计算能力。

3.  高容错性：
   假如其中一台机器挂了，它可以把上面的<span style=color:red>计算任务自动转移</span>到另外一个节点上运行，不至于这个任务运行失败，而且这个过程不需要人工参与，而完全是由 Hadoop 内部完成的。  

4.  适合 TB/PB 级以上海量数据的离线处理：
   可以实现上千台服务器集群并发工作，提供数据处理能力。 

### 1.2.2缺点

1. 不擅长实时计算

   MapReduce 无法像 mysql 一样，在毫秒或者秒级内返回结果。

2.  不擅长流式计算
   流式计算的输入数据是动态的，而 MapReduce 的<span style=color:red>输入数据集是静态的</span>，不能动态变化。这是因为 MapReduce自身的设计特点决定了数据源<span style=color:red>必须</span>是静态的。

3.  不擅长 DAG（有向无环图）计算
   多个应用程序存在依赖关系，后一个应用程序的输入为前一个的输出。在这种情况下，MapReduce 并不是不能做，而是使用后， <span style=color:red>每个 MapReduce 作业的输出结果都会写入到磁盘，会造成大量的磁盘 IO，导致性能非常的低下。 </span>

## 1.3核心思想

1. 分布式的运算程序往往需要分成至少2个阶段。
2. 第一个阶段的MapTask并发实例，完全并行运行，互不相干。
3. 第二个阶段的ReduceTask并发实例互不相干，但是他们的数据依赖于上一个阶段的所有MapTask并发实例的输出。
4. MapReduce编程模型只能包含一个Map阶段和一个Reduce阶段，如果用户的业务逻辑非常复杂，那就只能多个MapReduce程序，串行运行。

## 1.4进程

1. MrAppMaster：负责整个程序 (一个Job或Task或Mr）的过程调度及状态协调。
2. MapTask：负责 Map 阶段的整个数据处理流程。
3. ReduceTask：负责 Reduce 阶段的整个数据处理流程。 

## 1.5[常见数据序列化类型](http://www.atguigu.com/jsjj/22823.html)

## 1.6编程规范

用户编写的程序分为3个部分：<span style=color:red>Mapper,Reducer,Driver</span>

1. Mapper阶段：
   1. 用户自定义的Mapper要继承自己的父类
   2. Mapper的输入数据是KV键值对形式
   3. Mapper中业务逻辑写在map（）方法中
   4. Mapper的输出数据也是KV键值对形式
   5. <span style=color:red>MapMask进程对每一对kv调用一次map()方法</span>
2. Reducer阶段：
   1. 用户自定义的Reduce要继承自己的父类
   2. Reducer的输入数据类型对应Mapper的输出数据类型，也是KV
   3. Reducer的业务逻辑写在reduce()方法中
   4. <span style=color:red>ReduceTask进程对每一组相同k的键值对调用一次reduce()方法</span>

3. Driver阶段

   1. 相当于Yarn集群的客户端，用于提交我们整个程序到YARN集群，提交的是封装了MapReduce程序相关运行参数的job对象

   

## 1.7 Hadoop序列化

1. 什么是序列化
   1.   **序列化**就是<span style=color:red>把内存中的对象，转换成字节序列</span>（或其他数据传输协议）以便于存储到磁盘（持久化）和网络传输。  
   2. **反序列化**就是将收到字节序列（或其他数据传输协议）或者是磁盘的持久化数据，<span style=color:red>转换成内存中的对象。</span>

2. 如何实现

   1. 必须实现Writable接口

   2. 反序列化时，需要反射调用空参构造函数，所以必须有空参构造

      ```java
      public FlowBean() {
      	super();
      }
      ```

   3.   重写序列化方法  

      ```java
      @Override
      public void write(DataOutput out) throws IOException {
      	out.writeLong(upFlow);
      	out.writeLong(downFlow);
      	out.writeLong(sumFlow);
      }
      ```

   4.  重写反序列化方法  

      ```java
      @Override
      public void readFields(DataInput in) throws IOException {
      	upFlow = in.readLong();
      	downFlow = in.readLong();
      	sumFlow = in.readLong();
      }
      ```

      

   5.   <span style=color:red>注意反序列化的顺序和序列化的顺序完全一致  </span>

   6. 要想把结果显示在文件中，需要重写toString()，可用"\t"分开，方便后续用  

   7. 如果需要将自定义的bean放在key中传输，则还需要实现Comparable接口  

## 1.8 MapReduce框架原理

### **1.InputForma**t数据输入

1. FileInputFormat切片机制
   1. 简单的按照文件的内容长度进行切分
   2. 切片大小默认等于Block大小
   3. <span style=color:red>切片是不需要考虑数据集整体，而是逐个针对每一个文件单独切片</span>

2. **TextInputFormat**
   1. TextInputFormat是默认的FileInputFormat实现类。按行读取每条记录。键是存储该行在整个文件中的起始字节偏移量， LongWritable类型。值是这行的内容，不包括任何行终止符（换行符和回车符），Text类型。
3. **CombineTextInputFormat**切片机制  
   1. CombineTextInputFormat用于小文件过多的场景，它可以将多个小文件从逻辑上规划到一个切片中，这样，多个小文件就可以交给一个MapTask处理。

### 2.Shuffle**机制  

Map方法之后，Reduce方法之前的数据处理过程称之为Shuffle。

1. **Partition**分区

   1. 如果ReduceTask数量>getPartiton的结果数，则会多产生几个空的输出文件
   2. 如果1<ReduceTask数量<getPartiton的结果数，则有一部分分区数据无处安放
   3. 如果ReduceTask数量=1，则不管MapTask端输出多少个分区文件，最终结果都交给一个ReduceTask，也就只产生一个输出文件
   4. <span style=color:red>分区号必须从0开始，逐一累加</span>

2. **WritableComparable**排序  

   1. 排序是MapReduce框架中最重要的操作之一
   2. MapTask和ReduceTask均会对数据按照key进行排序。该操作属于hadoop的默认行为。<span style=color:red>任何应用程序中的数据均会被排序，而不管逻辑上是否需要</span>

3. ### **Combiner**合并

   1. Combiner是MapReduce程序中Mapper和Reducer之外的一种组件
   2. Combiner组件的父类就是Reduce
   3. Combiner是在每一个MapTask所在节点运行，Reducer是接收全局所有Mapper的输出结果
   4. Combiner的意义就是对每一个MapTask的输出进行局部汇总，以减小网络传输量
   5. Combiner能够应用的前提是不能影响最终的业务逻辑，而且Combiner的输出kv应该与Reduce的输入kv类型要对应起来

### 3 .OutputFormat数据输出  

1. OutputFormat是MapReduce输出的基类，所有实现MapReduce输出都实现了OutputFormat接口
2. 自定义OutputFormat步骤：
   1. 自定义一个类继承FileOutputFormat
   2. 改写RecordWriter，具体改写输出数据的方法write()