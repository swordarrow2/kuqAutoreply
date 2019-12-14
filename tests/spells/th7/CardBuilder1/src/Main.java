import java.util.*;

public class Main
{
	public static void main(String[] args)
	{
		StringBuilder sb=new StringBuilder();
		for(String s:spells){
			sb.append(String.format(".addSpell(\"%s\",  )\n",s));
		}
		System.out.println(sb.toString());
	}
	public static String[] spells=new String[]{
		"冰符「冰袭方阵」",

		"寒符「延长的冬日」",
		"冬符「花之凋零」",
		"白符「波状光」",
		"怪符「桌灵转」",

		"仙符「凤凰卵」",	
		"仙符「凤凰展翅」",	
		"式符「飞翔晴明」",
		"阴阳「道满晴明」",	
		"阴阳「晴明大纹」",
		"天符「天仙鸣动」",
		"翔符「飞翔韦驮天」",
		"童符「护法天童乱舞」",
		"仙符「尸解永远」",
		"鬼符「鬼门金神」",
		"方符「奇门遁甲」",	

		"操符「少女文乐」",
		"苍符「博爱的法兰西人偶」",
		"苍符「博爱的奥尔良人偶」",
		"红符「红发的荷兰人偶」",
		"白符「白垩的俄罗斯人偶」",
		"暗符「雾之伦敦人偶」",
		"回符「轮回的西藏人偶」",
		"雅符「春之京人偶」",
		"诅咒「魔彩光的上海人偶」",
		"诅咒「上吊的蓬莱人偶」",

		"弦奏「Guarneri del Gesù」",
		"神弦「Stradivarius」",
		"伪弦「Pseudo Stradivarius」",

		"管灵「日野幻想」",
		"冥管「灵之克里福德」",
		"管灵「灵之克里福德」",

		"冥键「法吉奥里冥奏」",
		"键灵「蓓森朵芙神奏」",

		"骚符「幽灵絮语」",
		"骚符「活着的骚灵」",
		"合葬「棱镜协奏曲」",
		"骚葬「冥河边缘」",
		"大合葬「灵车大协奏曲」",
		"大合葬「灵车大协奏曲改」",
		"大合葬「灵车大协奏曲怪」",

		"幽鬼剑「妖童饿鬼之断食」",
		"饿鬼剑「饿鬼道草纸」",
		"饿王剑「饿鬼十王的报应」",
		"狱界剑「二百由旬之一闪」",
		"狱炎剑「业风闪影阵」",
		"狱神剑「业风神闪斩」",
		"畜趣剑「无为无策之冥罚」",
		"修罗剑「现世妄执」",
		"人界剑「悟入幻想」",
		"人世剑「大悟显晦」",
		"人神剑「俗谛常住」",
		"天上剑「天人之五衰」",
		"天界剑「七魄忌讳」",
		"天神剑「三魂七魄」",

		"六道剑「一念无量劫」",
		"亡乡「亡我乡 -彷徨的灵魂-」",
		"亡乡「亡我乡 -宿罪-」",
		"亡乡「亡我乡 -无道之路-」",
		"亡乡「亡我乡 -自尽-」",
		"亡舞「生者必灭之理 -眩惑-」",
		"亡舞「生者必灭之理 -死蝶-」",
		"亡舞「生者必灭之理 -毒蛾-」",
		"亡舞「生者必灭之理 -魔境-」",
		"华灵「死蝶」",
		"华灵「燕尾蝶」",
		"华灵「深固难徙之蝶」",
		"华灵「蝶幻」",
		"幽曲「埋骨于弘川 -伪灵-」",
		"幽曲「埋骨于弘川 -亡灵-」",
		"幽曲「埋骨于弘川 -幻灵-」",
		"幽曲「埋骨于弘川 -神灵-」",
		"樱符「完全墨染的樱花 -封印-」",
		"樱符「完全墨染的樱花 -亡我-」",
		"樱符「完全墨染的樱花 -春眠-」",
		"樱符「完全墨染的樱花 -开花-」",
		"「反魂蝶 -一分咲-」",
		"「反魂蝶 -三分咲-」",
		"「反魂蝶 -五分咲-」",
		"「反魂蝶 -八分咲-」",

		"鬼符「青鬼赤鬼」",
		"鬼神「飞翔毘沙门天」",		
		"式神「仙狐思念」",
		"式神「十二神将之宴」",
		"式辉「狐狸妖怪激光」",
		"式辉「迷人的四面楚歌」",
		"式辉「天狐公主 -Illusion-」",
		"式弹「往生极乐的佛教徒」",
		"式弹「片面义务契约」",
		"式神「橙」",
		"「狐狗狸的契约」",
		"幻神「饭纲权现降临」",

		"式神「前鬼后鬼的守护」",	
		"式神「凭依荼吉尼天」",
		"结界「梦境与现实的诅咒」",
		"结界「动与静的均衡」",
		"结界「光与暗的网孔」",
		"罔两「直与曲的梦乡」",
		"罔两「八云紫的神隐」",
		"罔两「栖息于禅寺的妖蝶」",
		"魍魉「二重黑死蝶」",
		"式神「八云蓝」",
		"「人与妖的境界」",
		"结界「生与死的境界」",
		"紫奥义「弹幕结界」"
	};
}
