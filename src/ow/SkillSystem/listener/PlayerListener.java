package ow.SkillSystem.listener;

import java.io.IOException;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
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
	
	//监控玩家的攻击行为
	@EventHandler(priority = EventPriority.MONITOR)
	public void onDamage(EntityDamageByEntityEvent event) {
		//让玩家执行在时间内的技能条
		if(event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
			Player p = (Player) event.getDamager();
			SPlayer player = OnlineData.getSPlayer(p);
			
			player.runExecution("ATTACK");
		}
		
	}
	
	//监控玩家的击杀行为
	@EventHandler(priority = EventPriority.MONITOR)
	public void onKill(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();
		Player p = entity.getKiller();
		
		if(p == null) return;
		SPlayer player = OnlineData.getSPlayer(p);
		
		player.runExecution("KILL");
	}

}
