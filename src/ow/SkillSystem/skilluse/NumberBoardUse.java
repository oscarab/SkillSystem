package ow.SkillSystem.skilluse;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import ow.SkillSystem.Main;
import ow.SkillSystem.data.OnlineData;

public class NumberBoardUse implements Listener{
	
	//监听技能绑定方面的按下
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		
		Inventory inv = event.getClickedInventory();
		InventoryView view = event.getView();
		if(inv == null || view == null) { return; }
		
		if(view.getTitle().contains("[技能系统]按键绑定") && event.getWhoClicked() instanceof Player) {
			
			ItemStack item = inv.getItem(event.getSlot());
			if(item == null || !item.getItemMeta().hasLore()) return;
			
			String name = item.getItemMeta().getDisplayName();
			Player player = (Player) event.getWhoClicked();
			String lore = item.getItemMeta().getLore().get(1);
			
			if((lore.contains(" ") || lore.contains("未绑定")) && event.getClick().equals(ClickType.LEFT)) {
				//左键开始绑定
				OnlineData.playersetkey.put(player, name);
				if(Main.VexView) {
					player.sendMessage("§f§l请按下你需要绑定的按键");
				}else {
					player.sendMessage("§f§l请打开背包左击未绑定技能的技能槽");
					player.sendMessage("§f§l打开背包右击1-9格空的物品栏可添加技能槽");
				}
				
			}else if(Main.VexView && lore.contains("绑定按键") && event.getClick().equals(ClickType.RIGHT)) {
				//右键可以解除绑定
				OnlineData.getSPlayer(player).removeKeyBoardSkill( Main.skillsdata.get(name) );
				player.sendMessage("§f§l成功解除绑定");
				
			}
			
			player.closeInventory();
			event.setCancelled(true);
		}
		
		//放置技能绑定物品到物品栏
		if(inv instanceof PlayerInventory && event.getSlot() <= 8 && !Main.VexView && event.getWhoClicked() instanceof Player) {
			
			Player player = (Player) event.getWhoClicked();
			ItemStack noskill = getNoSkillItem();
			ItemStack clickitem = inv.getItem(event.getSlot());
			int slot = event.getSlot();

			
			//添加一个未绑定技能按钮
			if(clickitem == null && event.getClick().equals(ClickType.RIGHT)) {
				
				inv.setItem(slot, noskill);
				if(inv.firstEmpty() > 8 || ((PlayerInventory) inv).getItemInMainHand().equals(noskill)) inv.setItem(slot, null);
				event.setCancelled(true);
				
			}
			else if(clickitem == null) {
				return;
			}
			//再次右击就移除
			else if(clickitem.equals(noskill) && event.getClick().equals(ClickType.RIGHT)) {
				
				inv.setItem(slot, null);
				event.setCancelled(true);
				
			}
			//绑定技能上去
			else if(clickitem.equals(noskill) && event.getClick().equals(ClickType.LEFT) && OnlineData.playersetkey.get(player) != null){
				
				String skillname = OnlineData.playersetkey.get(player);
				inv.setItem(slot, getSkillItem(skillname));
				OnlineData.playersetkey.remove(player);
				event.setCancelled(true);
				
			}
			//解除绑定
			else if(isSkillItem(clickitem) && event.getClick().equals(ClickType.RIGHT)) {
				
				inv.setItem(slot, noskill);
				event.setCancelled(true);
				
			}
			
			if(clickitem != null && (clickitem.equals(noskill) || isSkillItem(clickitem))) event.setCancelled(true);

		}
	}
	
	@EventHandler
	public void onChangeItem(PlayerItemHeldEvent event) {
		int slot = event.getNewSlot();
		
		//按下数字键触发技能
		if(slot <= 8) {
			Player player = event.getPlayer();
			ItemStack item = player.getInventory().getItem(slot);
			 
			 if(item !=null && isSkillItem(item)) {
				 
				 OnlineData.getSPlayer(player).setSkill(Main.skillsdata.get(item.getItemMeta().getDisplayName()));
				 event.setCancelled(true);
				 
			 }else if(item != null && item.equals(getNoSkillItem())) {
				 event.setCancelled(true);
			 }
		}
		
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		ItemStack item = event.getItemDrop().getItemStack();
		//防止掉落技能方面的物品
		if(item.equals(getNoSkillItem()) || isSkillItem(item)) {
			event.setCancelled(true);
		}
	}
	
	private ItemStack getNoSkillItem() {
		ItemStack item = new ItemStack(Material.BARRIER);
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName("§f未绑定技能");
		item.setItemMeta(meta);
		
		return item;
	}
	
	private ItemStack getSkillItem(String name) {
		ItemStack item = new ItemStack(Material.SLIME_BALL);
		ItemMeta meta = item.getItemMeta();
		List<String> lores = new ArrayList<>();
		
		lores.add("§f已绑定");
		meta.setDisplayName(name);
		meta.setLore(lores);
		item.setItemMeta(meta);
		return item;
	}
	
	private boolean isSkillItem(ItemStack item) {
		if(item.getType().equals(Material.SLIME_BALL) && item.hasItemMeta() && item.getItemMeta().hasLore()) {
			return item.getItemMeta().getLore().get(0).equalsIgnoreCase("§f已绑定");
		}
		return false;
	}

}
