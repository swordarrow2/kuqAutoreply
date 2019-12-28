
package com.meng;

import java.awt.*;  
import java.text.*;  

import org.jfree.chart.*; 
import org.jfree.chart.axis.*;  
import org.jfree.chart.plot.*;  
import org.jfree.data.time.*;  
import java.io.*;
import java.util.*;
import com.meng.messageProcess.*;
import com.meng.tools.*;  

public class MonthChart {
	public static MonthChart ins;
	public MonthChart() {  

	}
	public File check(GroupCounter.GroupSpeak gs) {
		TimeSeries timeseries = new TimeSeries("你群发言");
		Calendar c = Calendar.getInstance();
		for (int i=0;i <= c.getActualMaximum(Calendar.DAY_OF_MONTH);++i) {
			c.set(Calendar.DAY_OF_MONTH, i + 1);
			HashMap<Integer,Integer> everyHour=gs.hour.get(Tools.CQ.getDate(c.getTimeInMillis()));
			int oneDay=0;
			for (int oneHour:everyHour.values()) {
				oneDay += oneHour;
			}
			timeseries.add(new Day(i, c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR)), oneDay);
		}
		TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();  
		timeseriescollection.addSeries(timeseries);

		JFreeChart jfreechart = ChartFactory.createTimeSeriesChart("你群今日发言", "时间", "条", timeseriescollection, true, true, true);  
		XYPlot xyplot = (XYPlot) jfreechart.getPlot();  
		DateAxis dateaxis = (DateAxis) xyplot.getDomainAxis();  
		dateaxis.setDateFormatOverride(new SimpleDateFormat("MM:dd"));  
		ChartPanel frame1 = new ChartPanel(jfreechart, true);  
		dateaxis.setLabelFont(new Font("黑体", Font.BOLD, 14)); //水平底部标题  		
		dateaxis.setTickLabelFont(new Font("宋体", Font.BOLD, 12));//垂直标题  
		jfreechart.getLegend().setItemFont(new Font("黑体", Font.BOLD, 15));  
		jfreechart.getTitle().setFont(new Font("宋体", Font.BOLD, 20));//设置标题字体  
		File pic=null;
		try {
			pic = new File(Autoreply.appDirectory + "downloadImages/" + System.currentTimeMillis() + ".jpg");
			ChartUtils.saveChartAsJPEG( 
				pic, //文件保存物理路径包括路径和文件名 
				1.0f, //图片质量 ，0.0f~1.0f 
				frame1.getChart(), //图表对象 
				800, //图像宽度 ，这个将决定图表的横坐标值是否能完全显示还是显示省略号 
				480);
		} catch (IOException e) {}
		return pic;
	}    
}  
