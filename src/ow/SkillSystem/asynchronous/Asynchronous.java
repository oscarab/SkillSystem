package ow.SkillSystem.asynchronous;

import org.bukkit.Bukkit;

import ow.SkillSystem.Main;
import ow.SkillSystem.data.ConfigHandle;

public class Asynchronous {
	public void handleParticle(ConfigHandle handle) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, new Runnable() {
			public void run() {
				handle.loadConfig();
				handle.loadItems();
				handle.loadParticle();
				handle.loadSkills();
				Bukkit.getConsoleSender().sendMessage("§f[§9SkillSystem§f]§2配置文件加载成功！");
			}
		});
	}
}
