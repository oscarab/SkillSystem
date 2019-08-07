package ow.SkillSystem.Thread;

import java.util.Iterator;
import java.util.UUID;

import ow.SkillSystem.data.OnlineData;
import ow.SkillSystem.data.SPlayer;

public class SkillThread extends Thread{
	
	//有关玩家的技能冷却或倒计时
	public void run() {
		
		while(true) {
			
			Iterator<UUID> it = OnlineData.players.keySet().iterator();
			while(it.hasNext()) {
				SPlayer player =  OnlineData.players.get(it.next());
				player.handleSkillTime();
				player.handleExecutionTime();
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}

}
