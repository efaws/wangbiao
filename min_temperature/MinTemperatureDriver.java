package shangke;

import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.conf.Configuration;
public class MinTemperatureDriver {
    public static class MinTemperatureMapper extends Mapper<LongWritable,Text,Text, NullWritable>{
        private Text outKey = new Text();
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            if(!(line.length()==0)){
                String date = line.substring(6, 14);
                float min_tm = Float.parseFloat(line.substring(47, 53).trim());
                if(min_tm>50) {
                    outKey.set(String.valueOf(min_tm));
                    context.write(new Text(date + "的最低气温为： " + outKey+"(此数据无效为垃圾数据)"), NullWritable.get());
                }else {
                    outKey.set(String.valueOf(min_tm));
                    context.write(new Text(date + "的最低气温为： " + outKey), NullWritable.get());
                }
            }
        }
    }


    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);
        job.setJarByClass(MinTemperatureDriver.class);
        job.setMapperClass(MinTemperatureMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(NullWritable.class);
        job.setNumReduceTasks(0);
        FileInputFormat.setInputPaths(job, new Path("D:\\hadoop\\tm_input\\"));
        FileOutputFormat.setOutputPath(job, new Path("D:\\hadoop\\tm_output\\"));
        System.exit(job.waitForCompletion(true) ? 0 : 1);

    }
}
