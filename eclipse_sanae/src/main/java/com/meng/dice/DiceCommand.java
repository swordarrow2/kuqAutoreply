package com.meng.dice;
import java.util.*;
import com.meng.*;
import com.meng.config.*;

public class DiceCommand {
	public static DiceCommand ins;

	private String[] cmdMsg;
	private int pos=0;
	public boolean check(long fromGrouo, long fromQQ, String msg) {
		if (msg.charAt(0) != '.') {
			return false;
		}
		cmdMsg = msg.split(" ");
		pos = 0;
		if (pos < cmdMsg.length) {
			try {
				switch (next()) {
					case ".r":
						String rs = next();
						Autoreply.sendMessage(fromGrouo, 0, String.format("%s投掷%s:D100 = %d", ConfigManager.ins.getNickName(fromQQ), rs == null ?"": rs, Autoreply.ins.random.nextInt(100)));
						return true;
					case ".ra":
						String ras = next();
						Autoreply.sendMessage(fromGrouo, 0, String.format("%s进行检定:D100 = %d/%s", ConfigManager.ins.getNickName(fromQQ), Autoreply.ins.random.nextInt(Integer.parseInt(ras)), ras));
						return true;
					case ".li":
						Autoreply.sendMessage(fromGrouo, 0, String.format("%s的疯狂发作-总结症状:\n1D10=%d\n症状: 狂躁：调查员患上一个新的狂躁症，在1D10=%d小时后恢复理智。在这次疯狂发作中，调查员将完全沉浸于其新的狂躁症状。这是否会被其他人理解（apparent to other people）则取决于守秘人和此调查员。\n1D100=%d\n具体狂躁症: 臆想症（Nosomania）：妄想自己正在被某种臆想出的疾病折磨。(KP也可以自行从狂躁症状表中选择其他症状)", ConfigManager.ins.getNickName(fromQQ), Autoreply.ins.random.nextInt(11), Autoreply.ins.random.nextInt(11), Autoreply.ins.random.nextInt(101)));
						return true;
					case ".ti":
						Autoreply.sendMessage(fromGrouo, 0, String.format("%s的疯狂发作-临时症状:\n1D10=%d\n症状: 逃避行为：调查员会用任何的手段试图逃离现在所处的位置，状态持续1D10=%d轮。", ConfigManager.ins.getNickName(fromQQ), Autoreply.ins.random.nextInt(11), Autoreply.ins.random.nextInt(11)));
						return true;
					case ".rd":
						Autoreply.sendMessage(fromGrouo, 0, String.format("由于%s %s骰出了: D100=%d", next(), ConfigManager.ins.getNickName(fromGrouo, fromQQ), Autoreply.ins.random.nextInt()));
				}
			} catch (NumberFormatException ne) {
				Autoreply.sendMessage(fromGrouo, 0, "参数错误");
			}
		}
		return false;
	}
	
	private String next() {
		try {
			return cmdMsg[pos++];
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}
}
