package ow.SkillSystem.asynchronous;

import org.bukkit.Bukkit;

import ow.SkillSystem.Main;
import ow.SkillSystem.data.ConfigHandle;

public class Asynchronous {
	public void handleParticle(ConfigHandle handle) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, new Runnable() {
			public void run() {
				handle.loadParticle();
				Bukkit.getConsoleSender().sendMessage("§f[§9SkillSystem§f]§2粒子特效加载成功！");
				handle.loadSkills();
				Bukkit.getConsoleSender().sendMessage("§f[§9SkillSystem§f]§2技能加载成功！");
				Main.ready = true;
			}
		});
	}
}
