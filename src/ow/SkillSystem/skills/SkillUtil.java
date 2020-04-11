package ow.SkillSystem.skills;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SkillUtil {
	/**
	 *  拉扯目标生物到达玩家处
	 * @param user  目的玩家
	 * @param target  被拉扯的生物
	 */
	public void pull(Player user , LivingEntity target , double arg) { 
		Location locuser = user.getEyeLocation();
		Location loctarget = target.getEyeLocation();
		Vector vec = locuser.subtract(loctarget).toVector().normalize();
		target.setVelocity(vec.multiply(arg));
	}
	
	/**
	 *   击退目标
	 * @param user  目的玩家
	 * @param target 被击退的生物
	 */
	public void pushBack(Player user , LivingEntity target , double arg) {
		Location locuser = user.getEyeLocation();
		Location loctarget = target.getEyeLocation();
		Vector vec = loctarget.subtract(locuser).toVector().normalize();
		target.setVelocity(vec.multiply(arg));
	}
	
	/**
	 * 使目标飞起
	 * @param target 目标
	 * @param arg
	 */
	public void jump(LivingEntity target , double arg) {
		Location loca = target.getEyeLocation().clone();
		Location locb = loca.clone();
		loca.setY(loca.getY() + 1);
		Vector vec = loca.subtract(locb).toVector().normalize();
		target.setVelocity(vec.multiply(arg));
	}
	
	/**
	 * 朝某生物指向的方向飞跃一段距离
	 * @param entity 施行生物
	 * @param length 飞跃距离
	 */
	public void charge(LivingEntity entity , double length) {
		Vector sight = entity.getEyeLocation().getDirection().clone();
		entity.setVelocity(sight.multiply(length).setY(0.01D));
	}
	
	
	/**
	 * 获取玩家指向的生物
	 * @param p 玩家
	 * @return 若有则返回该生物，若无返回null
	 */
	public LivingEntity getTargetEntity(Player p , double distance) { 
		List<Entity> entities = p.getNearbyEntities(distance ,distance, distance);
		for(Entity en : entities) {
			if(en instanceof LivingEntity) {
				LivingEntity len = (LivingEntity) en ;
				Location end = len.getEyeLocation().clone();
				Location start = p.getEyeLocation().clone();
				Vector v = end.subtract(start).toVector();
				Vector sight = p.getEyeLocation().getDirection();
				
				double length = Math.cos(v.angle(sight));
				if(length >0.91) {
					return len;
				}
			}
		}
		return null;
	}
	/**
	 * 返回一条线上的生物
	 * @param p 玩家
	 * @param distance 距离
	 * @return
	 */
	public List<LivingEntity> getLineEntity(Player p , double distance){
		List<Entity> entities = p.getNearbyEntities(distance ,distance, distance);
		List<LivingEntity> args = new ArrayList<>();
		for(Entity en : entities) {
			if(en instanceof LivingEntity) {
				LivingEntity len = (LivingEntity) en ;
				Location end = len.getEyeLocation().clone();
				Location start = p.getEyeLocation().clone();
				Vector v = end.subtract(start).toVector();
				Vector sight = p.getEyeLocation().getDirection();
				
				double length = Math.cos(v.angle(sight));
				if(length >0.91) {
					args.add(len);
				}
			}
		}
		return args;
		
	}
	
}
