package com.meng.bilibili;

import java.util.*;

public class BiliMaster {
	public boolean lastStatus = false;
	public boolean needTip = false;
	public int roomID = -1;
	public int uid= -1;
	public long lastVideo = 0;
	public long lastArtical = 0;
	public boolean needTipArtical = false;
	public boolean needTipVideo = false;
	public ArrayList<FansInGroup> fans = new ArrayList<>();

	public class FansInGroup {
		public long group;
		public long qq;
		public FansInGroup(long fromGroup, long fromQQ) {
			group = fromGroup;
			qq = fromQQ;
		}
	}

	public void addFans(long fromGroup, long fromQQ) {
		for (FansInGroup fig:fans) {
			if (fig.qq == fromQQ && fig.group == fromGroup) {
				return;
			}
		}
		fans.add(new FansInGroup(fromGroup, fromQQ));
	}

	public void removeFans(long fromGroup, long fromQQ) {
		for (int i=0;i < fans.size();++i) {
			if (fans.get(i).qq == fromQQ && fans.get(i).group == fromGroup) {
				fans.remove(i);
				return;
			}
		}
	}
}
