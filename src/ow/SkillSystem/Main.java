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
	
	public static boolean PaPi = false;
	public static boolean VexView = false;
	public static boolean WorldGuard = false;
	
	public void onEnable() {
		plugin = this;
		util = new Util();
		
		//检测前置插件
		if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			getLogger().info("检测到PlaceholderAPI存在，可以使用相关变量！");
			PaPi = true;
		}
		if(Bukkit.getPluginManager().isPluginEnabled("VexView")) {
			getLogger().info("检测到VexView存在，可以使用键盘触发技能！");
			VexView = true;
		}
		if(Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
			//测试阶段
			WorldGuard = true;
		}
		
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
		initPlayers();
		getLogger().info("初始化完成现有玩家！");
		
		runThread();
		
		//加载监听器
		Bukkit.getPluginManager().registerEvents(new ItemUse() , this);
		if(VexView) {Bukkit.getPluginManager().registerEvents(new KeyboardUse(), this);}
		Bukkit.getPluginManager().registerEvents(new LivingEntityDamageListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
		Bukkit.getPluginManager().registerEvents(new NumberBoardUse(), this);
		
		getLogger().info("技能系统启动完成！当前版本:v1.4.8");
	}
	
	//初始化当前服务器中的玩家数据
	private void initPlayers() {
		Iterator<? extends Player> itn = Bukkit.getServer().getOnlinePlayers().iterator();
		
		while(itn.hasNext()) {
			Player p = itn.next();
			SPlayer player = new SPlayer(p);
			
			try {
				handle.loadPlayerYML(player);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			OnlineData.players.put(p.getUniqueId(), player);
		}
		
	}
	
	//保存当前服务器中的玩家数据
	private void savePlayers() {
		Iterator<? extends Player> itn = Bukkit.getServer().getOnlinePlayers().iterator();
		
		while(itn.hasNext()) {
			Player p = itn.next();
			SPlayer player = OnlineData.getSPlayer(p);
			
			player.saveKeyBoard();
		}
	}

	public void onDisable() {
		getLogger().info("技能系统正在保存数据...");
		
		savePlayers();
		
		getLogger().info("技能系统保存数据完成！");
		
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
				
			}else if(sender instanceof Player && args.length == 0) {
				
				Player player = (Player) sender;
				util.createInventory(player);
				
			}else if(sender.isOp() && args.length == 1 && args[0].equalsIgnoreCase("reload")) {
				sender.sendMessage("[SkillSystem]开始重载技能系统！");
				skillsdata.clear();
				skills.clear();
				items.clear();

				try {
					handle = new ConfigHandle();
				} catch (IOException e) {
					e.printStackTrace();
				}
				handle.loadItems();
				handle.loadSkills();
				
				savePlayers();
				OnlineData.players.clear();
				initPlayers();
				sender.sendMessage("[SkillSystem]重载技能系统完成！");
			}else if(args.length == 1 && sender instanceof Player) {
				
				Skill skill = skillsdata.get(args[0]);
				SPlayer player = OnlineData.getSPlayer((Player) sender);
				
				if(skill != null) {
					player.setSkill(skill);
				}

			}
			
		}
		return true;
	}

}
