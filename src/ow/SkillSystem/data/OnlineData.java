package ow.SkillSystem.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class OnlineData {
	
	//调整打出的伤害    格式：   生物/伤害设置/持续时间  正数是增加伤害，负数是减少
	public static Map<LivingEntity,String> damageset = Collections.synchronizedMap(new HashMap<>());
	
	//调整所受的伤害   格式：   生物/伤害设置/持续时间      
	//若为0则是无敌，正数是增加所受伤害，负数是减少所受伤害
	public static Map<LivingEntity, String> damagedset = Collections.synchronizedMap(new HashMap<>());
	
	//玩家列表
	public static Map<UUID,SPlayer> players = Collections.synchronizedMap(new HashMap<>());
	
	//玩家绑定技能的标记
	public static Map<UUID , String> playersetkey = Collections.synchronizedMap(new HashMap<>());
	
	/*==================================================================================*/
	
	//返回某个生物的伤害设定
	public static double getDamage(LivingEntity entity) {
		if(damageset.get(entity)==null) return 0;
		
		String[] parts = damageset.get(entity).split("/");
		return Double.parseDouble(parts[0]);
	}
	
	//增加生物打出伤害设定
	public static void addDamageSet(LivingEntity entity , double ds , int time) {
		damageset.put(entity, ds+"/"+time);
	}
	
	//修改生物打出伤害设定（在原基础上增加）
	public static void setDamageSet(LivingEntity entity , double dsplus) {
		String[] parts = damageset.get(entity).split("/");
		int time = Integer.parseInt(parts[1]);
		double ds = Double.parseDouble(parts[0]) + dsplus;
		damageset.put(entity, ds+"/"+time);
	}
	
	/*===============================================================*/
	
	//返回所受伤害的调整
	public static String getDamaged(LivingEntity entity) {
		if(damagedset.get(entity) == null) return null;
		
		return damagedset.get(entity).split("/")[0];
	}
	
	//增加生物所受伤害设定
	public static void addDamagedSet(LivingEntity entity , double ds , int time) {
		damagedset.put(entity, ds+"/"+time);
	}
	
	//修改生物所受伤害设定（在原基础上增加）
	public static void setDamagedSet(LivingEntity entity , double dsplus) {
		String[] parts = damagedset.get(entity).split("/");
		int time = Integer.parseInt(parts[1]);
		double ds = Double.parseDouble(parts[0]) + dsplus;
		damagedset.put(entity, ds+"/"+time);
	}
	
	/*===============================================================*/
	
	//时间流逝
	public static void setTime1() {
		
		Iterator<LivingEntity> it =  damageset.keySet().iterator();
		
		while(it.hasNext()) {
			LivingEntity entity = (LivingEntity) it.next();
			String[] parts = damageset.get(entity).split("/");
			double ds = Double.parseDouble(parts[0]);
			int time = Integer.parseInt(parts[1]) - 1;
			
			if(time == 0) {
				it.remove();
			}else {
				damageset.put(entity, ds+"/"+time);
			}
		}
		
	}
	
	public static void setTime2() {
		
		Iterator<LivingEntity> it =  damagedset.keySet().iterator();
		
		while(it.hasNext()) {
			LivingEntity entity = (LivingEntity) it.next();
			String[] parts = damagedset.get(entity).split("/");
			double ds = Double.parseDouble(parts[0]);
			int time = Integer.parseInt(parts[1]) - 1;
			
			if(time == 0) {
				it.remove();
			}else {
				damagedset.put(entity, ds+"/"+time);
			}
		}
		
	}
	
	//获取splayer
	public static SPlayer getSPlayer(Player p) {
		return players.get(p.getUniqueId());
	}
	
}
