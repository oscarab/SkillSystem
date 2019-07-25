package ow.SkillSystem.skilluse;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import lk.vexview.event.KeyBoardPressEvent;
import ow.SkillSystem.Main;
import ow.SkillSystem.data.OnlineData;
import ow.SkillSystem.data.SPlayer;
import ow.SkillSystem.skills.Skill;

public class KeyboardUse implements Listener{

	@EventHandler
	public void onKeyBoardPress(KeyBoardPressEvent event) {
		
		if(event.getEventKeyState()) return;
		
		Player p = event.getPlayer();
		SPlayer player = OnlineData.getSPlayer(p);
		int key = event.getKey();
		Skill skill = player.getKeyBoardSkill(key);
		
		//释放技能
		if(skill!=null) {
			player.setSkill(skill);
		}
		
		//技能绑定事宜
		if(OnlineData.playersetkey.get(p) != null) {
			
			if(key == 57) {  //若为空格退出绑定
				p.sendMessage("§0§l成功退出绑定！");
			}else {
				player.addKeyBoardSetting(key, Main.skillsdata.get( OnlineData.playersetkey.get(p) ) );
				p.sendMessage("§0§l成功绑定！");
			}

			OnlineData.playersetkey.remove(p);
		}
		
	}
	
	//监听技能绑定方面的按下
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		Inventory inv = event.getClickedInventory();
		InventoryView view = event.getView();
		
		if(view.getTitle().contains("[技能系统]按键绑定") && event.getWhoClicked() instanceof Player) {
			
			ItemStack item = inv.getItem(event.getSlot());
			if(item == null || !item.getItemMeta().hasLore()) return;
			
			String name = item.getItemMeta().getDisplayName();
			Player player = (Player) event.getWhoClicked();
			
			if(item.getItemMeta().getLore().get(1).contains("未绑定") && event.getClick().equals(ClickType.LEFT)) {
				//左键开始绑定
				OnlineData.playersetkey.put(player, name);
				player.sendMessage("§0§l请按下你需要绑定的按键");
				
			}else if(item.getItemMeta().getLore().get(1).contains("绑定按键") && event.getClick().equals(ClickType.RIGHT)) {
				//右键可以解除绑定
				OnlineData.getSPlayer(player).removeKeyBoardSkill( Main.skillsdata.get(name) );
				player.sendMessage("§0§l成功解除绑定");
				
			}
			
			player.closeInventory();
			event.setCancelled(true);
		}
	}
	
}
