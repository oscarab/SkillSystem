package ow.SkillSystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import ow.SkillSystem.Thread.*;
import ow.SkillSystem.data.ConfigHandle;
import ow.SkillSystem.data.OnlineData;
import ow.SkillSystem.data.SPlayer;
import ow.SkillSystem.listener.*;
import ow.SkillSystem.skills.Skill;
import ow.SkillSystem.skilluse.*;

public class Main extends JavaPlugin{
	
	public static Main plugin;
	public static Util util;
	public static ConfigHandle handle;
	
	public static HashMap<String,Skill> skillsdata = new HashMap<>();
	public static List<Skill> skills = new ArrayList<>();
	
	public static HashMap<String,ItemStack> items = new HashMap<>();
	
	//是否开放键盘数字键触发技能
	public static boolean isKeyBoard = true;
	
	public void onEnable() {
		plugin = this;
		util = new Util();
		
		try {
			handle = new ConfigHandle();
			handle.loadItems();
			handle.loadSkills();
			getLogger().info("配置文件加载成功！");
		} catch (IOException e) {
			e.printStackTrace();
			getLogger().info("创建相关配置文件时出现错误！请检查相关文件！");
		}
		
		//初始化现有玩家
		initPlayer();
		getLogger().info("初始化完成现有玩家！");
		
		runThread();
		
		//加载监听器
		Bukkit.getPluginManager().registerEvents(new ItemUse() , this);
		Bukkit.getPluginManager().registerEvents(new KeyboardUse(), this);
		Bukkit.getPluginManager().registerEvents(new LivingEntityDamageListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
		
		getLogger().info("技能系统启动完成！");
	}
	
	private void initPlayer() {
		Iterator<? extends Player> itn = Bukkit.getServer().getOnlinePlayers().iterator();
		
		while(itn.hasNext()) {
			Player p = itn.next();
			SPlayer player = new SPlayer(p);
			
			try {
				handle.loadPlayerYML(player);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			OnlineData.players.put(p, player);
		}
		
	}

	public void onDisable() {
		getLogger().info("技能系统正在保存数据...");
		
		Iterator<? extends Player> itn = Bukkit.getServer().getOnlinePlayers().iterator();
		
		while(itn.hasNext()) {
			Player p = itn.next();
			SPlayer player = OnlineData.players.get(p);
			
			player.saveKeyBoard();
		}
		
	}
	
	//启动线程
	public void runThread() {
		
		Thread pthread = new SkillThread();
		Thread dthread = new DamageThread();
		Thread ddthread = new DamagedThread();
		
		pthread.start();
		dthread.start();
		ddthread.start();
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String Label,String[] args){
		if(cmd.getName().equalsIgnoreCase("skillsystem")) {
			
			if(args.length == 3 && args[0].equalsIgnoreCase("give")) {
				Player p = getServer().getPlayer(args[1]);
				ItemStack item = items.get(args[2]);
				
				if(p != null && p.isOp()) {
					p.getInventory().addItem(item);
					sender.sendMessage("成功给予");
				}else {
					sender.sendMessage("该玩家不在线");
				}
				
			}else if(sender instanceof Player && args.length == 0 && Bukkit.getPluginManager().getPlugin("VexView") != null) {
				Player player = (Player) sender;
				util.createInventory(player);
			}
			
		}
		return true;
	}

}
