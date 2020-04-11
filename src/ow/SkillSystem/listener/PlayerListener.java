package ow.SkillSystem.listener;

import java.io.IOException;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
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
import ow.SkillSystem.net.UpdateCheck;

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
		
		OnlineData.players.put(p.getUniqueId(), player);
		
		//如果进入玩家为op则检查插件版本
		if(p.isOp()) {
			new UpdateCheck().check(p);
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		
		//玩家按键信息保存
		OnlineData.players.get(p.getUniqueId()).saveData();
		
		OnlineData.players.remove(p.getUniqueId());
	}
	
	//监控玩家的攻击行为
	@EventHandler(priority = EventPriority.MONITOR)
	public void onDamage(EntityDamageByEntityEvent event) {
		//让玩家执行在时间内的技能条
		
		if(event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
			Player p = (Player) event.getDamager();
			SPlayer player = OnlineData.getSPlayer(p);
			
			player.runExecution("ATTACK");
		}else if(event.getDamager() instanceof Projectile && event.getEntity() instanceof LivingEntity) {
			
			Projectile projectile = (Projectile) event.getDamager();
			
			if(projectile.getShooter() instanceof Player) {
				Player p = (Player) projectile.getShooter();
				SPlayer player = OnlineData.getSPlayer(p);
				
				player.runExecution("ATTACK");
			}
			
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
