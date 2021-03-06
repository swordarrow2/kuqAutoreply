package com.meng.messageProcess;

import com.meng.*;
import com.meng.bilibili.live.*;
import com.meng.config.javabeans.*;
import com.meng.tools.*;
import java.io.*;
import java.util.*;

import static com.meng.Autoreply.sendMessage;

public class GroupMsgPart2Runnable implements Runnable {
    private int subType = 0;
    private int msgId = 0;
    private long fromGroup = 0;
    private long fromQQ = 0;
    private String fromAnonymous = "";
    private String msg = "";
    private int font = 0;
    private long timeStamp = 0;
    private File[] imageFiles = null;

    public GroupMsgPart2Runnable(MessageSender ms) {
        font = ms.font;
        fromGroup = ms.fromGroup;
        fromQQ = ms.fromQQ;
        msg = ms.msg;
        msgId = ms.msgId;
        subType = ms.subType;
        timeStamp = ms.timeStamp;
        imageFiles = ms.imageFiles;
	}

    @Override
    public synchronized void run() {
        check();
	}

    private boolean check() {
        if (msg.equalsIgnoreCase("loaddic")) {
            Autoreply.instence.addGroupDic();
            sendMessage(fromGroup, fromQQ, "loaded");
            return true;
		}
		if (msg.startsWith(".nn ")) {
			if (msg.contains("~") || msg.contains("～")) {
				return true;
			}
			String name=msg.substring(4);
			if (name.length() > 30) {
				Autoreply.sendMessage(fromGroup, 0, "太长了,记不住");
				return true;
			}
			Autoreply.instence.configManager.setNickName(fromQQ, name);
			Autoreply.sendMessage(fromGroup, 0, "我以后会称呼你为" + name);
			return true;
		}
		if (msg.equals("原曲认知")) {
			File musicFragment=Autoreply.instence.musicManager.createMusicCut(new Random().nextInt(16), 10, fromQQ);
			Autoreply.sendMessage(fromGroup, 0, Autoreply.instence.CC.record(musicFragment.getName()));	
			return true;
		}
		if (msg.startsWith("原曲认知回答 ")) {
			Autoreply.instence.musicManager.judgeAnswer(fromGroup, fromQQ, msg.substring(7));
			return true;
		}
		if (msg.startsWith("原曲认知 ")) {
			switch (msg) {
				case "原曲认知 E":
				case "原曲认知 e":
				case "原曲认知 easy":
				case "原曲认知 Easy":
					Autoreply.sendMessage(fromGroup, 0, Autoreply.instence.CC.record(Autoreply.instence.musicManager.createMusicCut(new Random().nextInt(16), 10, fromQQ).getName()));	
					break;
				case "原曲认知 N":
				case "原曲认知 n":
				case "原曲认知 normal":
				case "原曲认知 Normal":
					Autoreply.sendMessage(fromGroup, 0, Autoreply.instence.CC.record(Autoreply.instence.musicManager.createMusicCut(new Random().nextInt(16), 6, fromQQ).getName()));
					break;
				case "原曲认知 H":
				case "原曲认知 h":
				case "原曲认知 hard":
				case "原曲认知 Hard":
					Autoreply.sendMessage(fromGroup, 0, Autoreply.instence.CC.record(Autoreply.instence.musicManager.createMusicCut(new Random().nextInt(16), 3, fromQQ).getName()));
					break;
				case "原曲认知 L":
				case "原曲认知 l":
				case "原曲认知 lunatic":
				case "原曲认知 Lunatic":
					Autoreply.sendMessage(fromGroup, 0, Autoreply.instence.CC.record(Autoreply.instence.musicManager.createMusicCut(new Random().nextInt(16), 1, fromQQ).getName()));
					break;		
			}
		}
		if (msg.equals(".nn")) {
			Autoreply.instence.configManager.setNickName(fromQQ, null);
			Autoreply.sendMessage(fromGroup, 0, "我以后会用你的QQ昵称称呼你");
			return true;
		}
        if (msg.equals("椰叶查询")) {
            sendMessage(fromGroup, fromQQ, "查询结果：" + Autoreply.instence.CC.at(fromQQ));
            return true;
		}
        if (Tools.CQ.checkAt(fromGroup, fromQQ, msg)) {//@
            return true;
		}
        GroupConfig groupConfig = Autoreply.instence.configManager.getGroupConfig(fromGroup);
        if (groupConfig.isRepeat() && Autoreply.instence.repeatManager.check(fromGroup, fromQQ, msg, imageFiles)) {
            return true;
		}
        if (msg.equals(".live")) {
            String msgSend;
            StringBuilder stringBuilder = new StringBuilder();
            for (Map.Entry<Integer,LivePerson> entry:Autoreply.instence.liveListener.livePersonMap.entrySet()) {	
				if (entry.getValue().lastStatus) {
					stringBuilder.append(Autoreply.instence.configManager.getPersonInfoFromBid(entry.getKey()).name).append("正在直播").append(entry.getValue().liveUrl).append("\n");
				}
			}
            msgSend = stringBuilder.toString();
            Autoreply.sendMessage(fromGroup, fromQQ, msgSend.equals("") ? "居然没有飞机佬直播" : msgSend);
            return true;
		}
		if (Autoreply.instence.spellCollect.check(fromGroup, fromQQ, msg)) {
			return true;
		}
        if (msg.contains("大膜法")) {
            if (!groupConfig.isMoshenfusong()) {
                return true;
			}
            switch (msg) {
                case "大膜法 膜神复诵":
					Autoreply.instence.threadPool.execute(new MoShenFuSong(fromGroup, fromQQ, new Random().nextInt(4)));
					break;
                case "大膜法 膜神复诵 Easy":
					Autoreply.instence.threadPool.execute(new MoShenFuSong(fromGroup, fromQQ, 0));
					break;
                case "大膜法 膜神复诵 Normal":
					Autoreply.instence.threadPool.execute(new MoShenFuSong(fromGroup, fromQQ, 1));
					break;
                case "大膜法 膜神复诵 Hard":
					Autoreply.instence.threadPool.execute(new MoShenFuSong(fromGroup, fromQQ, 2));
					break;
                case "大膜法 膜神复诵 Lunatic":
					Autoreply.instence.threadPool.execute(new MoShenFuSong(fromGroup, fromQQ, 3));
					break;
                case "大膜法 膜神复诵 Overdrive":
					Autoreply.instence.threadPool.execute(new MoShenFuSong(fromGroup, fromQQ, 4));
					break;
                case "大膜法 c568连":
					Autoreply.instence.threadPool.execute(new MoShenFuSong(fromGroup, fromQQ, 5));
					break;
                default:
					break;
			}
            return true;
		}
        if (groupConfig.isPohai() && Tools.CQ.isPohaitu(fromGroup, fromQQ, msg)) {
            return true;
		}
        if (groupConfig.isSetu() && Tools.CQ.isSetu(fromGroup, fromQQ, msg)) {
            return true;
		}
        if (groupConfig.isNvZhuang() && Tools.CQ.isNvZhuang(fromGroup, fromQQ, msg)) {
            return true;
		}
        if (groupConfig.isBarcode() && Autoreply.instence.barcodeManager.check(fromGroup, fromQQ, msg, imageFiles)) {// 二维码
            return true;
		}
        if (groupConfig.isSearchPic() && Autoreply.instence.picSearchManager.check(fromGroup, fromQQ, msg, imageFiles)) {// 搜索图片
            return true;
		}
        //   if (groupConfig.isKuiping() && Tools.CQ.checkLook(fromGroup, msg)) {// 窥屏检测
        //       return true;
        //    }
        if (groupConfig.isBilibiliCheck() && Autoreply.instence.biliLinkInfo.check(fromGroup, fromQQ, msg)) {// 比利比利链接详情
            return true;
		}
        if (groupConfig.isCqCode() && Autoreply.instence.CQcodeManager.check(fromGroup, msg)) {// 特殊信息(签到分享等)
            return true;
		}
        if (Autoreply.instence.banner.checkBan(fromQQ, fromGroup, msg)) {// 禁言
            return true;
		}
        if (groupConfig.isCuigeng() && Autoreply.instence.updateManager.check(fromGroup, msg)) {
            return true;
		}
        if (Autoreply.instence.timeTip.check(fromGroup, fromQQ)) {// 根据时间提醒
            return true;
		}
		//   if(groupConfig.isRoll() && Autoreply.instence.rollPlane.check(fromGroup,msg)) {// roll
		//       return true;
		//   }
        if (msg.equals("提醒戒膜")) {
            sendMessage(fromGroup, 0, Autoreply.instence.CC.image(new File(Autoreply.appDirectory + "pic\\jiemo.jpg")));
            return true;
		}
        if (msg.equals("查看统计")) {
            sendMessage(fromGroup, fromQQ, Autoreply.instence.useCount.getMyCount(fromQQ));
            return true;
		}
        if (msg.equals("查看排行")) {
            sendMessage(fromGroup, fromQQ, Autoreply.instence.useCount.getTheFirst());
            return true;
		}
        if (msg.equals("查看群统计")) {
            sendMessage(fromGroup, fromQQ, Autoreply.instence.groupCount.getMyCount(fromGroup));
            return true;
		}
        if (msg.equals("查看群排行")) {
            sendMessage(fromGroup, fromQQ, Autoreply.instence.groupCount.getTheFirst());
            return true;
		}
        if (msg.equals("查看活跃数据")) {
            sendMessage(fromGroup, fromQQ, "https://qqweb.qq.com/m/qun/activedata/active.html?gc=" + fromGroup);
            return true;
		}
        if (Autoreply.instence.picEditManager.check(fromGroup, fromQQ, msg)) {
            return true;
		}
		if (Autoreply.instence.diceImitate.check(fromGroup, fromQQ, msg)) {
			return true;
		}
		if (Autoreply.instence.seqManager.check(fromGroup, fromQQ, msg)) {
			return true;
		}
		if (msg.contains("@") && Autoreply.instence.CC.getAt(msg) == -1000) {
            sendMessage(fromGroup, fromQQ, "野蛮假at");
            return true;
		}
        return groupConfig.isDic() && Autoreply.instence.dicReplyManager.check(fromGroup, fromQQ, msg);
	}
}
