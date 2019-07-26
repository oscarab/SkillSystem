package ow.SkillSystem.Thread;

import java.util.Iterator;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import ow.SkillSystem.data.OnlineData;
import ow.SkillSystem.data.SPlayer;

public class SkillThread extends Thread{
	
	//有关玩家的技能冷却或倒计时
	public void run() {
		
		while(true) {
			
			Iterator<Player> it = OnlineData.players.keySet().iterator();
			while(it.hasNext()) {
				SPlayer player =  OnlineData.players.get(it.next());
				player.handleSkillTime();
				player.handleExecutionTime();
			}
			
			//暂时
			Iterator<LivingEntity> entities = OnlineData.entityglowing.keySet().iterator();
			while(entities.hasNext()) {
				LivingEntity entity = entities.next();
				int time = OnlineData.entityglowing.get(entity);
				if(time == 0) {
					entity.setGlowing(false);
					entities.remove();
				}else {
					OnlineData.entityglowing.put(entity, time-1);
				}
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}

}
