package myProject;
import java.io.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class dataprofile {

  public static class datmapper
       extends Mapper<IntWritable, Text, Text, Text>{
	  
	  public void map(IntWritable key, Text value, Context context
              ) throws IOException, InterruptedException {
			
	
		String[] tokenString = value.toString().split(";;");
		
			/*context.write(new Text("eventid"), new Text(tokenString[0]));
			context.write(new Text("venuename"), new Text(tokenString[1]));
			context.write(new Text("description"), new Text(tokenString[2]));
*/			context.write(new Text("distance"), new Text(tokenString[3]));
			//context.write(new Text("startTime"), new Text(tokenString[4]));
			context.write(new Text("timeFromNow"), new Text(tokenString[5]));
			/*context.write(new Text("attending"), new Text(tokenString[6]));
			context.write(new Text("declined"), new Text(tokenString[7]));
			context.write(new Text("maybe"), new Text(tokenString[8]));
			context.write(new Text("noreply"), new Text(tokenString[9]));
			context.write(new Text("venueid"), new Text(tokenString[10]));
			context.write(new Text("venuename"), new Text(tokenString[11]));
			context.write(new Text("city"), new Text(tokenString[12));
			context.write(new Text("country"), new Text(tokenString[13]));
			context.write(new Text("latitude"), new Text(tokenString[14]));
			context.write(new Text("longitude"), new Text(tokenString[15]));
			context.write(new Text("state"), new Text(tokenString[16]));
			context.write(new Text("street"), new Text(tokenString[17]));
			context.write(new Text("zip"), new Text(tokenString[18]));
			*/
		
		} 
	}



	public static class datreducer
	 extends Reducer<Text,Text,Text,Text> {
	
	
	public void reduce(Text key, Iterable<Text> values,
	                 Context context
	                 ) throws IOException, InterruptedException {
	
	
	if(key.toString().equals("distance")){
		long max = Long.MIN_VALUE;
		long min = Long.MAX_VALUE;
		long sum = 0;
		long count =0;
	for (Text val : values) {
	  //if(val.toString().matches("-?\\d+(\\.\\d+)?")){
		Long x = Long.valueOf(val.toString());
		if(x>max){max=x;}
		else if(x<min){ min=x;}
		sum += x;
		count++;
		 
	  }
	float avg_dist = sum/(float)count;
	StringBuilder sb = new StringBuilder();
	sb.append(Long.toString(max));
	sb.append(",");
	sb.append(Long.toString(min));
	sb.append(",");
	sb.append(Float.toString(avg_dist));
	
	context.write(key,new Text(sb.toString()));
	}
	
	
	if(key.toString().equals("timeFromNow")){
		long max = Long.MIN_VALUE;
		long min = Long.MAX_VALUE;
		long sum = 0;
		long count =0;
	for (Text val : values) {
	  //if(val.toString().matches("-?\\d+(\\.\\d+)?")){
		Long x = Long.valueOf(val.toString());
		if(x>max){max=x;}
		else if(x<min){ min=x;}
		sum += x;
		count++;
		 
	  }
	float avg_time = sum/(float)count;
	StringBuilder sb = new StringBuilder();
	sb.append(Long.toString(max));
	sb.append(",");
	sb.append(Long.toString(min));
	sb.append(",");
	sb.append(Float.toString(avg_time));
	context.write(key,new Text(sb.toString()));
	}
	}
	
	
	}
	
	
	public static void main(String[] args) throws Exception {
	Configuration conf = new Configuration();
	conf.set("textinputformat.record.delimiter","&*&");
	Job job = new Job(conf);
	job.setNumReduceTasks(1);
	job.setJarByClass(dataprofile.class);
	job.setMapperClass(datmapper.class);
	job.setReducerClass(datreducer.class);
	job.setOutputKeyClass(Text.class);
	job.setOutputValueClass(Text.class);
	FileInputFormat.addInputPath(job, new Path(args[0]));
	FileOutputFormat.setOutputPath(job, new Path(args[1]));
	System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}