package com.meng.groupChat;

import com.google.gson.*;
import com.google.gson.reflect.*;
import com.meng.*;
import com.meng.picEdit.*;
import com.meng.tools.*;
import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.*;
import java.util.*;   

public class GroupCounter2 {
	public static GroupCounter2 ins;
	public HashMap<Long,GroupSpeak> groupsMap = new HashMap<>(32);
	private File file;
	public DayChart dchart;
	public MonthChart mchart;
	public ChartDrawer chartDrawer=new ChartDrawer();
	public GroupCounter2() {
		file = new File(Autoreply.appDirectory + "properties\\GroupCount2.json");
        if (!file.exists()) {
            try {
                FileOutputStream fos = new FileOutputStream(file);
                OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                writer.write(new Gson().toJson(groupsMap));
                writer.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Type type = new TypeToken<HashMap<Long, GroupSpeak>>() {
        }.getType();
        groupsMap = Autoreply.gson.fromJson(Tools.FileTool.readString(file), type);
		Autoreply.instence.threadPool.execute(new Runnable() {
				@Override
				public void run() {
					saveData();
				}
			});
		dchart = new DayChart();
		mchart = new MonthChart();
	}
	public class GroupSpeak {
		public int all=0;
		public HashMap<String,HashMap<Integer,Integer>> hour=new HashMap<>(16);		
	}

	public void addSpeak(long group, int times) {
		GroupSpeak gs=groupsMap.get(group);
		if (gs == null) {
			gs = new GroupSpeak();
			groupsMap.put(group, gs);
		}
		gs.all += times;
		HashMap<Integer,Integer> everyHourHashMap = gs.hour.get(Tools.CQ.getDate());
		if (everyHourHashMap == null) {
			everyHourHashMap = new HashMap<>();
			gs.hour.put(Tools.CQ.getDate(), everyHourHashMap);
		}
		Date da=new Date();
		int nowHour=da.getHours();
		if (everyHourHashMap.get(nowHour) == null) {
			everyHourHashMap.put(nowHour, times);
		} else {
			int stored=everyHourHashMap.get(nowHour);
			stored += times;
			everyHourHashMap.put(nowHour, stored);
		}
	}

	public HashMap<Integer,Integer> getSpeak(long group, String date) {
		GroupSpeak gs = groupsMap.get(group);
		if (gs == null) {
			return null;
		}
		HashMap<Integer,Integer> hr = gs.hour.get(date);
		if (hr == null) {
			return null;
		}
		return hr;
	}

	private void saveData() {
        while (true) {
            try {
                Thread.sleep(60000);
                FileOutputStream fos = new FileOutputStream(file);
                OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                writer.write(new Gson().toJson(groupsMap));
                writer.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

	public class DayChart {
		public DayChart() {  

		}
		public File check(GroupCounter2.GroupSpeak gs) {
			return chartDrawer.draw24hChart(gs);
		}    
	}

	public class MonthChart {
		public MonthChart() {  

		}
		public File check(GroupCounter2.GroupSpeak gs) {
			return chartDrawer.draw30dChart(gs);
		}
	}
}
