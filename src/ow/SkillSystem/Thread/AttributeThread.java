package ow.SkillSystem.Thread;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import ow.SkillSystem.data.OnlineData;
import ow.SkillSystem.data.SPlayer;


public class AttributeThread extends Thread{
	
	private String attribute;
	private int timer;
	private int max;
	
	public AttributeThread(String attribute, int timer, int max) {
		super();
		this.attribute = attribute;
		this.timer = timer;
		this.max = max;
	}
	
	public void run() {
		
		while(true) {
			Iterator<? extends Player> itn = Bukkit.getServer().getOnlinePlayers().iterator();
			
			while(itn.hasNext()) {
				SPlayer player = OnlineData.getSPlayer(itn.next());
				
				if(player.getAttribute(attribute) + 1 <= max) {
					player.setAttribute(attribute, player.getAttribute(attribute) + 1);
				}
			}
			
			try {
				Thread.sleep(timer);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

}
