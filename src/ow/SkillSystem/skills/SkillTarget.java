package ow.SkillSystem.skills;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import ow.SkillSystem.Main;

public class SkillTarget {      //技能释放的对象
	
	//PointEntity,RaduisEntity,Self,LineEntity
	private String target;
	private String distance;
	
	public SkillTarget(String target) {
		if(target.contains(":")) {
			String[] args = target.split(":");
			this.target = args[0];
			this.distance = args[1];
		}else {
			this.target = target;
		}
	}
	
	public String getTargetType() {
		return target;
	}
	
	/**
	 * 获取技能释放的目标生物
	 * @param self 玩家
	 * @return 范围目标生物全体
	 */
	public List<LivingEntity> getTarget(Player self) {
	    List<LivingEntity> entities = new ArrayList<>();
		  
		if(target.equalsIgnoreCase("Self")) {
			
			entities.add(self);
			
		}else if(target.equalsIgnoreCase("RaduisEntity")) {
			  
			double radius = Main.util.getDoubleNumber(distance, self);
			
			for(Entity entity : self.getNearbyEntities(radius, radius, radius)) {
				if(entity instanceof LivingEntity) {
					  entities.add((LivingEntity) entity);
				}
			}
			  
		}else if(target.equalsIgnoreCase("PointEntity")){
			
			SkillUtil sutil = new SkillUtil();
			double distance = Main.util.getDoubleNumber(this.distance, self);
			LivingEntity entity = sutil.getTargetEntity(self, distance);
			
			if(entity != null) entities.add(entity);
			
		}else if(target.equalsIgnoreCase("LineEntity")) {
			
			SkillUtil sutil = new SkillUtil();
			double distance = Main.util.getDoubleNumber(this.distance, self);
			
			entities.addAll(sutil.getLineEntity(self, distance));
			
		}
		  
		  return entities;
	  }

	
    
}
