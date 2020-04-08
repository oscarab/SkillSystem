package ow.SkillSystem.listener;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import ow.SkillSystem.data.OnlineData;

public class LivingEntityDamageListener implements Listener{
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		
		//根据攻击者进行伤害调整
		if(event.getDamager() instanceof LivingEntity) {
			
			LivingEntity entity = (LivingEntity) event.getDamager();
			double damageset = event.getDamage()+OnlineData.getDamage(entity);
			
			event.setDamage(damageset < 0 ? 0 : damageset);
			
		}
		//抛射物伤害设定
		else if(event.getDamager() instanceof Projectile){
			
			Projectile projectile = (Projectile) event.getDamager();
			
			if(OnlineData.projectiledamage.get(projectile) != null) {
				double projectiledamage = OnlineData.projectiledamage.get(projectile);
				
				//设置弹射物初始伤害
				event.setDamage(projectiledamage);
			}
			
			if(projectile.getShooter() instanceof LivingEntity) {
				LivingEntity entity = (LivingEntity) projectile.getShooter();
				double damageset = event.getDamage()+OnlineData.getDamage(entity);
				
				event.setDamage(damageset < 0 ? 0 : damageset);
		    }  
			
		}
		
		//调整所受伤害
		if(event.getEntity() instanceof LivingEntity) {
			
			LivingEntity entity = (LivingEntity) event.getEntity();
			String damage = OnlineData.getDamaged(entity);
			
			if(damage != null) {
				double d = Double.parseDouble(damage);
				
				event.setDamage(event.getDamage()+d < 0 ? 0 : event.getDamage()+d);
			}
			
		}
		
	}
	
	@EventHandler
	public void onAllDamagae(EntityDamageEvent event) {
		//无敌设定
		if(event.getEntity() instanceof LivingEntity) {
			
			LivingEntity entity = (LivingEntity) event.getEntity();
			String damage = OnlineData.getDamaged(entity);
			
			if(damage != null) {
				double d = Double.parseDouble(damage);
				if(d == 0) {
					event.setCancelled(true);
				}
			}
			
		}
	}

}
