package com.mysocket;
import com.google.gson.*;
import java.io.*;
import java.util.*;

public class DataPack {

	private byte[] data;
	private final short headLength=18;
	private JsonObject jsonObject=new JsonObject();
	private String jsonString=""; 
	private Gson gson=new Gson();
	private int writePointer=0;
	private int longPointer=1;
	private int stringPointer=1;

	public static final short _0notification=0;				//小律影↔正邪    s1:通知文本
	public static final short _1verify=1;					//小律影→正邪    n1:qq号(setConnect中设置的qq号)
	public static final short _2getLiveList=2;				//小律影→正邪    不需要body
	public static final short _3returnLiveList=3;			//正邪→小律影    json数组  例:[{"name":"闲者","qq":"877247145","bid":12007285,"bliveRoom":1954885,"tipIn":[],"tip":[true,true,false]},{"name":"懒瘦","qq":"496276037","bid":15272850,"bliveRoom":3144622,"tipIn":[],"tip":[true,true,false]}]
	public static final short _4liveStart=4;				//正邪→小律影    n1:直播间号 s1:主播称呼
	public static final short _5liveStop=5;					//正邪→小律影    n1:直播间号 s1:主播称呼
	public static final short _6speakInLiveRoom=6;			//正邪→小律影    n1:直播间号 s1:主播称呼 n2:说话者BID s2:说话者称呼,如果配置文件中没有就是用户名 s3:说话内容
	public static final short _7newVideo=7;					//正邪→小律影    s1:用户名 s2:视频名 n1:AV号
	public static final short _8newArtical=8;				//正邪→小律影    s1:用户名 s2:专栏名 n1:CV号
	public static final short _9getPersonInfoByName=9;		//小律影→正邪    s1:称呼
	public static final short _10getPersonInfoByQQ=10;		//小律影→正邪    n1:qq号
	public static final short _11getPersonInfoByBid=11;		//小律影→正邪    n1:BID
	public static final short _12getPersonInfoByBiliLive=12;//小律影→正邪    n1:直播间号
	public static final short _13returnPersonInfo=13;		//正邪→小律影    json数组  例:[{"name":"闲者","qq":"877247145","bid":12007285,"bliveRoom":1954885,"tipIn":[],"tip":[true,true,false]},{"name":"懒瘦","qq":"496276037","bid":15272850,"bliveRoom":3144622,"tipIn":[],"tip":[true,true,false]}]
	public static final short _14coinsAdd=14;				//正邪→小律影    n1:幻币数量 n2:目标qq号
	public static final short _15groupBan=15; 				//小律影↔正邪    n1:群号 n2:QQ号 n3:时间(秒)
	public static final short _16groupKick=16;				//小律影↔正邪    n1:群号 n2:QQ号 n3:是否永久拒绝 0为否 1为是
	public static final short _17heartBeat=17;				//小律影→正邪    心跳，不需要body

	public static DataPack encode(short opCode, long timeStamp) {
		return new DataPack(opCode, timeStamp);
	}

	public static DataPack decode(byte[] bytes) {
		return new DataPack(bytes);
	}

	private DataPack(short opCode, long timeStamp) {
		data = new byte[headLength];
		write(getBytes(data.length));
		write(getBytes(headLength));
		write(getBytes((short)1));
		write(getBytes(timeStamp));
		write(getBytes(opCode));
	}   

	private DataPack(byte[] pack) {
		data = pack;
		jsonObject = gson.fromJson(new String(pack, headLength, getLength() - headLength), JsonObject.class);
	} 

	public byte[] getData() {
		byte[] retData=new byte[headLength + gson.toJson(jsonObject).length()];
		for (int i=0;i < headLength;++i) {
			retData[i] = data[i];
		}
		byte[] bs=null;
		try {
			bs = gson.toJson(jsonObject).getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		for (int i=headLength;i < bs.length;++i) {
			retData[i] = bs[i];
		}
		byte[] len=getBytes(retData.length);
		retData[0] = len[0];
		retData[1] = len[1];
		retData[2] = len[2];
		retData[3] = len[3];
		return retData;
	}

	public int getLength() {
		return readInt(data, 0);
	}  

	public short getHeadLength() {
		return readShort(data, 4);
	}

	public long getTimeStamp() {
		return readLong(data, 6);
	}

	public short getVersion() {
		return readShort(data, 14);
	}

	public short getOpCode() {
		return readShort(data, 16);
	}

	public void write(long l) {
		jsonObject.addProperty("n" + longPointer, l);
		++longPointer;
	}

	public void write(String s) {
		jsonObject.addProperty("s" + stringPointer, s);
		++stringPointer;
	}

	public long readNum() {
		return jsonObject.get("n" + longPointer).getAsLong();
	}

	public String readString() {
		return jsonObject.get("s" + stringPointer).getAsString();
	}

	private void write(byte[] bs) {
		for (int i=0;i < bs.length;++i) {
			data[writePointer++] = bs[i];
		}
	}

	private byte[] getBytes(int i) {
		byte[] bs=new byte[4];
		bs[0] = (byte) ((i >> 0) & 0xff);
		bs[1] = (byte) ((i >> 8) & 0xff);
		bs[2] = (byte) ((i >> 16) & 0xff);
		bs[3] = (byte) ((i >> 24) & 0xff);
		return bs;	
	}

	private byte[] getBytes(long l) {
		byte[] bs=new byte[8];
		bs[0] = (byte) ((l >> 0) & 0xff);
		bs[1] = (byte) ((l >> 8) & 0xff);
		bs[2] = (byte) ((l >> 16) & 0xff);
		bs[3] = (byte) ((l >> 24) & 0xff);
		bs[4] = (byte) ((l >> 32) & 0xff);
		bs[5] = (byte) ((l >> 40) & 0xff);
		bs[6] = (byte) ((l >> 48) & 0xff);
		bs[7] = (byte) ((l >> 56) & 0xff);
		return bs;	
	}

	private byte[] getBytes(short s) {
		byte[] bs=new byte[2];
		bs[0] = (byte) ((s >> 0) & 0xff);
		bs[1] = (byte) ((s >> 8) & 0xff) ;
		return bs;	
	}

	private short readShort(byte[] data, int pos) {
        return (short) ((data[pos] & 0xff) << 0 | (data[pos + 1] & 0xff) << 8);
	}

	private int readInt(byte[] data, int pos) {
        return (data[pos] & 0xff) << 0 | (data[pos + 1] & 0xff) << 8 | (data[pos + 2] & 0xff) << 16 | (data[pos + 3] & 0xff) << 24;
	}

	private long readLong(byte[] data, int pos) {
        return ((data[pos] & 0xffL) << 0) | (data[pos + 1] & 0xffL) << 8 | (data[pos + 2] & 0xffL) << 16 | (data[pos + 3] & 0xffL) << 24 | (data[pos + 4] & 0xffL) << 32 | (data[pos + 5] & 0xffL) << 40 | (data[pos + 6] & 0xffL) << 48 | (data[pos + 7] & 0xffL) << 56;
	}
}