package ow.SkillSystem.listener;

import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import ow.SkillSystem.Main;
import ow.SkillSystem.data.OnlineData;
import ow.SkillSystem.data.SPlayer;

public class PlayerListener implements Listener{
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		SPlayer player = new SPlayer(p);
		
		try {
			//加载玩家按键信息
			Main.handle.loadPlayerYML(player);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		OnlineData.players.put(p, player);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		
		//玩家按键信息保存
		OnlineData.players.get(p).saveKeyBoard();
		
		OnlineData.players.remove(p);
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		//让玩家执行在时间内的技能条
		if(event.getDamager() instanceof Player) {
			Player p = (Player) event.getDamager();
			SPlayer player = OnlineData.getSPlayer(p);
			
			player.runExecution(getAttackType(event));
		}
		
	}
	
	//返回伤害类型，击杀或攻击
	String getAttackType(EntityDamageByEntityEvent event) {
		if(event.getEntity().isDead()) {
			return "KILL";
		}else {
			return "ATTACK";
		}
	}

}
