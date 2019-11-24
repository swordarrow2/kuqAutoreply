package com.meng.messageProcess;

import com.meng.Autoreply;
import com.meng.MessageSender;
import com.meng.bilibili.live.LivePerson;
import com.meng.tools.Methods;
import com.sobte.cqp.jcq.entity.CQImage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static com.meng.Autoreply.sendMessage;

public class PrivateMsgRunnable implements Runnable {
    private int subType = 0;
    private int msgId = 0;
    private long fromGroup = 0;
    private long fromQQ = 0;
    private String fromAnonymous = "";
    private String msg = "";
    private int font = 0;
    private long timeStamp = 0;

    public PrivateMsgRunnable(MessageSender ms) {
        font = ms.font;
        fromGroup = ms.fromGroup;
        fromQQ = ms.fromQQ;
        msg = ms.msg;
        msgId = ms.msgId;
        subType = ms.subType;
        timeStamp = ms.timeStamp;
    }

    @Override
    public synchronized void run() {
        check();
    }

    private boolean check() {
        File[] imageFiles = null;
        List<CQImage> images = Autoreply.instence.CC.getCQImages(msg);
        if (images.size() != 0) {
            imageFiles = new File[images.size()];
            for (int i = 0, imagesSize = images.size(); i < imagesSize; i++) {
                CQImage image = images.get(i);
                try {
                    imageFiles[i] = Autoreply.instence.fileTypeUtil.checkFormat(image.download(Autoreply.appDirectory + "downloadImages/", image.getMd5()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (msg.equals(".live")) {
            String msgSend;
            final StringBuilder stringBuilder = new StringBuilder();
            Autoreply.instence.liveListener.livePersonMap.forEach(new BiConsumer<Integer, LivePerson>() {
                @Override
                public void accept(Integer key, LivePerson livePerson) {
                    if (livePerson.lastStatus) {
                        stringBuilder.append(Autoreply.instence.configManager.getPersonInfoFromBid(key).name).append("正在直播").append(livePerson.liveUrl).append("\n");
                    }
                }
            });
            msgSend = stringBuilder.toString();
            Autoreply.sendMessage(fromGroup, fromQQ, msgSend.equals("") ? "居然没有飞机佬直播" : msgSend);
            return true;
        }
        if (Methods.isPohaitu(fromGroup, fromQQ, msg)) {
            return true;
        }
        if (Methods.isSetu(fromGroup, fromQQ, msg)) {
            return true;
        }
        if (Autoreply.instence.barcodeManager.check(fromGroup, fromQQ, msg, imageFiles)) {// 二维码
            return true;
        }
        if (Autoreply.instence.picSearchManager.check(fromGroup, fromQQ, msg, imageFiles)) {// 搜索图片
            return true;
        }
      //  if (Autoreply.instence.rollPlane.check(fromGroup, msg)) {// roll
      //      return true;
      //  }
        if (msg.equals("查看统计")) {
            sendMessage(fromGroup, fromQQ, Autoreply.instence.useCount.getMyCount(fromQQ));
            return true;
        }
        if (msg.equals("查看排行")) {
            sendMessage(fromGroup, fromQQ, Autoreply.instence.useCount.getTheFirst());
            return true;
        }
        return false;
    }
}