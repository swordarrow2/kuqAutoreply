package com.meng.gameData.TouHou;

import com.meng.config.*;
import java.net.*;
import java.nio.*;
import java.util.*;
import org.java_websocket.*;
import org.java_websocket.handshake.*;
import org.java_websocket.server.*;

public class QuestionServer extends WebSocketServer {

	public QuestionServer(int port) throws UnknownHostException {
		super(new InetSocketAddress(port));
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		System.out.println("websocket连接");
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		System.out.println("websocket断开");
	}

	@Override
	public void onMessage(WebSocket conn, String message) {

	}
	@Override
	public void onMessage(WebSocket conn, ByteBuffer message) {
		SanaeDataPack dataRec=SanaeDataPack.decode(message.array());
		SanaeDataPack sdp=null;
		switch (dataRec.getOpCode()) {
			case SanaeDataPack._40addQuestion:
				TouHouKnowledge.QABuilder qab= new TouHouKnowledge.QABuilder();
				qab.setFlag(dataRec.readInt());
				qab.setQuestion(dataRec.readString());
				int anss=dataRec.readInt();
				qab.setTrueAnswer(dataRec.readInt());
				for (int i=0;i < anss;++i) {
					qab.setAnswer(dataRec.readString());
				}
				qab.setReason(dataRec.readString());
				TouHouKnowledge.ins.addQA(qab.build());
				sdp = SanaeDataPack.encode(SanaeDataPack._0notification, dataRec);
				sdp.write("添加成功");
				break;
			case SanaeDataPack._41getAllQuestion:
				sdp = writeQA(TouHouKnowledge.ins.qaList);
				break;
		}
		if (sdp != null) {
			conn.send(sdp.getData());
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		ex.printStackTrace();
		if (conn != null) {
			// some errors like port binding failed may not be assignable to a specific websocket
		}
	}

	@Override
	public void onStart() {
		System.out.println("quesServer started!");
		setConnectionLostTimeout(100);
	}
	private SanaeDataPack writeQA(ArrayList<TouHouKnowledge.QA> qas) {
		SanaeDataPack sdp=SanaeDataPack.encode(SanaeDataPack._42retAllQuestion);
		for (TouHouKnowledge.QA qa:qas) {
			sdp.write(qa.flag);//flag
			sdp.write(qa.q);//ques
			sdp.write(qa.a.size());//ansCount
			sdp.write(qa.t);
			for (String s:qa.a) {
				sdp.write(s);
			}
			sdp.write(qa.r);
		}
		return sdp;
	}
}
	
