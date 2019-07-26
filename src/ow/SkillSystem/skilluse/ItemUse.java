package ow.SkillSystem.skilluse;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ow.SkillSystem.Main;
import ow.SkillSystem.data.OnlineData;
import ow.SkillSystem.data.SPlayer;
import ow.SkillSystem.skills.Skill;

public class ItemUse implements Listener{
	
	@EventHandler
	public void onIteract(PlayerInteractEvent event) {
		
		if(event.getAction().equals(Action.RIGHT_CLICK_AIR)
				||event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			
			ItemStack item = event.getItem();
			if(item == null) return;
			ItemMeta meta = item.getItemMeta();
			List<String> lores = new ArrayList<>();
			if(meta.getLore() == null) return;
			lores.addAll(meta.getLore());
			SPlayer p = OnlineData.getSPlayer(event.getPlayer());
			
			//如果物品lore里含有技能名字就触发技能
			for(String lore : lores) {
				for(Skill skill : Main.skills) {
					if(lore.contains(skill.getName())) {
						
						p.setSkill(skill);
						
						return;
					}
				}
			}

		}
		
	}

}
