package com.meng.bilibili.live;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meng.Autoreply;
import com.meng.bilibili.main.SpaceToLiveJavaBean;
import com.meng.tools.Methods;
import com.meng.config.ConfigManager;
import com.meng.config.javabeans.PersonInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.concurrent.*;

public class LiveListener implements Runnable {

    public HashMap<Integer, LivePerson> livePersonMap = new HashMap<>();
    private boolean loadFinish = false;
    private ConcurrentHashMap<String, String> liveTimeMap = new ConcurrentHashMap<>();

    public LiveListener(final ConfigManager configManager) {
        System.out.println("直播检测启动中");
        Autoreply.instence.threadPool.execute(new Runnable() {
            @Override
            public void run() {
                for (PersonInfo cb : configManager.configJavaBean.personInfo) {
                    LiveListener.this.checkPerson(cb);
                }
                loadFinish = true;
                System.out.println("直播检测启动完成");
            }
        });
        File liveTimeFile = new File(Autoreply.appDirectory + "liveTime.json");
        if (!liveTimeFile.exists()) {
            saveLiveTime();
        }
        try {
            Type token = new TypeToken<HashMap<String, String>>() {
            }.getType();
            liveTimeMap = new Gson().fromJson(Methods.readFileToString(liveTimeFile.getAbsolutePath()), token);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkPerson(PersonInfo personInfo) {
        if (personInfo.bliveRoom == -1) {
            return;
        }
        if (personInfo.bliveRoom == 0) {
            if (personInfo.bid != 0) {
                SpaceToLiveJavaBean sjb = new Gson().fromJson(Methods.getSourceCode("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + personInfo.bid), SpaceToLiveJavaBean.class);
                if (sjb.data.roomid == 0) {
                    personInfo.bliveRoom = -1;
                    Autoreply.instence.configManager.saveConfig();
                    return;
                }
                personInfo.bliveRoom = sjb.data.roomid;
                Autoreply.instence.configManager.saveConfig();
                System.out.println("检测到用户" + personInfo.name + "(" + personInfo.bid + ")的直播间" + personInfo.bliveRoom);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!loadFinish) {
                    Thread.sleep(1000);
                    continue;
                }
                for (PersonInfo personInfo : Autoreply.instence.configManager.configJavaBean.personInfo) {
                    if (personInfo.bliveRoom == 0 || personInfo.bliveRoom == -1) {
                        continue;
                    }
                    SpaceToLiveJavaBean sjb = new Gson().fromJson(Methods.getSourceCode("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + personInfo.bid), SpaceToLiveJavaBean.class);
                    boolean living = sjb.data.liveStatus == 1;
                    LivePerson livePerson = livePersonMap.computeIfAbsent(personInfo.bid, new Function<Integer, LivePerson>() {
                        @Override
                        public LivePerson apply(Integer k) {
                            return new LivePerson();
                        }
                    });
                    livePerson.liveUrl = sjb.data.url;
					livePerson.roomID=sjb.data.roomid+"";
                    if (livePerson.needTip) {
                        if (!livePerson.lastStatus && living) {
                            onStart(personInfo, livePerson);
                        } else if (livePerson.lastStatus && !living) {
                            onStop(personInfo, livePerson);
                        }
                    }
                    livePerson.lastStatus = living;
                    livePerson.needTip = true;
                    Thread.sleep(2000);
                }
                Thread.sleep(60000);
            } catch (Exception e) {
                System.out.println("直播监视出了问题：");
                e.printStackTrace();
            }
        }
    }

    public void saveNow() {
        for (PersonInfo personInfo : Autoreply.instence.configManager.configJavaBean.personInfo) {
            if (personInfo.bliveRoom == 0 || personInfo.bliveRoom == -1) {
                continue;
            }
            LivePerson livePerson = livePersonMap.computeIfAbsent(personInfo.bid, new Function<Integer, LivePerson>() {
                @Override
                public LivePerson apply(Integer k) {
                    return new LivePerson();
                }
            });
            countLiveTime(personInfo, livePerson);
            livePerson.liveStartTimeStamp = System.currentTimeMillis() / 1000;
        }
    }

    private void onStart(PersonInfo personInfo, LivePerson livePerson) {
        livePerson.liveStartTimeStamp = System.currentTimeMillis() / 1000;
        tipStart(personInfo);
    }

    private void onStop(PersonInfo personInfo, LivePerson livePerson) {
        countLiveTime(personInfo, livePerson);
        tipFinish(personInfo);
    }

    private void countLiveTime(PersonInfo personInfo, LivePerson livePerson) {
        String timeStr = liveTimeMap.get(personInfo.name);
        if (timeStr == null) {
            liveTimeMap.put(personInfo.name, String.valueOf(System.currentTimeMillis() / 1000 - livePerson.liveStartTimeStamp));
        } else {
            long time = Long.parseLong(timeStr);
            time += System.currentTimeMillis() / 1000 - livePerson.liveStartTimeStamp;
            liveTimeMap.put(personInfo.name, String.valueOf(time));
        }
        saveLiveTime();
    }


    private void tipStart(PersonInfo p) {
        if (!p.isTipLive()) {
            return;
        }
        Autoreply.sendMessage(1023432971, 0, p.name + "开始直播" + p.bliveRoom, true);
        ArrayList<Long> groupList = Autoreply.instence.configManager.getPersonInfoFromName(p.name).tipIn;
        for (int i = 0, groupListSize = groupList.size(); i < groupListSize; i++) {
            long group = groupList.get(i);
            Autoreply.sendMessage(group, 0, p.name + "开始直播" + p.bliveRoom, true);
        }
    }

    private void tipFinish(PersonInfo p) {
        if (!p.isTipLive()) {
            return;
        }
        Autoreply.sendMessage(1023432971, 0, p.name + "直播结束" + p.bliveRoom, true);
        ArrayList<Long> groupList = Autoreply.instence.configManager.getPersonInfoFromName(p.name).tipIn;
        for (int i = 0, groupListSize = groupList.size(); i < groupListSize; i++) {
            long group = groupList.get(i);
            Autoreply.sendMessage(group, 0, p.name + "直播结束" + p.bliveRoom, true);
        }
    }

    public String getLiveTimeCount() {
        Iterator<Entry<String, String>> iterator = liveTimeMap.entrySet().iterator();
        StringBuilder stringBuilder = new StringBuilder();
        while (iterator.hasNext()) {
            Entry<String, String> entry = iterator.next();
            stringBuilder.append(entry.getKey()).append(cal(Integer.parseInt(entry.getValue()))).append("\n");
        }
        return stringBuilder.toString();
    }

    private String cal(int second) {
        int h = 0;
        int min = 0;
        int temp = second % 3600;
        if (second > 3600) {
            h = second / 3600;
            if (temp > 60) {
                min = temp / 60;
            }
        } else {
            min = second / 60;
        }
        if (h == 0) {
            return min + "分";
        } else {
            return h + "时" + min + "分";
        }
    }

    private void saveLiveTime() {
        try {
            File file = new File(Autoreply.appDirectory + "liveTime.json");
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            writer.write(new Gson().toJson(liveTimeMap));
            writer.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}