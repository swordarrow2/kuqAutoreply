package com.meng.tools;

import com.google.gson.*;
import com.google.gson.reflect.*;
import com.meng.*;
import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.*;
import java.util.*;

public class ZanManager {
    private HashSet<Long> hashSet = new HashSet<>();
    private String configPath = Autoreply.appDirectory + "configV3_zan.json";

    public ZanManager() {
        File jsonBaseConfigFile = new File(configPath);
        if (!jsonBaseConfigFile.exists()) {
            saveConfig();
        }
        Type type = new TypeToken<HashSet<Long>>() {
        }.getType();
        hashSet = new Gson().fromJson(Tools.FileTool.readString(configPath), type);
    }

    public void sendZan() {
        for (long l : Autoreply.instence.configManager.configJavaBean.masterList) {
            zanCore(l);
        }
        for (long l : Autoreply.instence.configManager.configJavaBean.adminList) {
            zanCore(l);
        }
        for (long l : hashSet) {
            zanCore(l);
        }
    }

    private void zanCore(long l) {
        int ii;
        for (int i = 0; i < 10; i++) {
            ii = Autoreply.CQ.sendLike(l);
            if (ii != 0) {
                System.out.println("赞" + l + "失败,返回值" + ii);
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean checkAdd(long fromGroup, long fromQQ, String msg) {
		if (msg.startsWith("z.add")) {
			hashSet.addAll(Autoreply.instence.CC.getAts(msg));
			saveConfig();
			Autoreply.sendMessage(fromGroup, fromQQ, "已添加至赞列表");
			return true;
        }
        return false;
    }

    private void saveConfig() {
        try {
            File file = new File(configPath);
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            writer.write(new Gson().toJson(hashSet));
            writer.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
