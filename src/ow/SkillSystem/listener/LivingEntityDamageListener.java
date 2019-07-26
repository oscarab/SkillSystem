package ow.SkillSystem.listener;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
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
			
		}else if(event.getDamager() instanceof Arrow){
			
			Arrow arrow = (Arrow) event.getDamager();
			
			if(arrow.getShooter() instanceof LivingEntity) {
				LivingEntity entity = (LivingEntity) arrow.getShooter();
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
		
		event.getDamager().sendMessage("你造成了"+event.getDamage()+"点伤害");
		
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
