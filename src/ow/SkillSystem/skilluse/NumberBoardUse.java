package ow.SkillSystem.skilluse;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import ow.SkillSystem.Main;
import ow.SkillSystem.data.OnlineData;

//传统背包栏技能触发
public class NumberBoardUse implements Listener{
	
	//监听技能绑定方面的按下
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		
		Inventory inv = event.getClickedInventory();
		InventoryView view = event.getView();
		if(inv == null || view == null) { return; }

		if(view.getTitle().contains("[技能系统]按键绑定") && event.getWhoClicked() instanceof Player) {
			
			event.setCancelled(true);
			
			Player player = (Player) event.getWhoClicked();
			ItemStack item = inv.getItem(event.getSlot());
			if(item == null || !item.getItemMeta().hasLore() || item.getItemMeta().getLore().size() <2) return;
			
			String name = item.getItemMeta().getDisplayName();
			String lore = item.getItemMeta().getLore().get(1);
			
			if((lore.equalsIgnoreCase(" ") || lore.equalsIgnoreCase("§4§l未绑定")) && event.getClick().equals(ClickType.LEFT)) {
				//左键开始绑定
				OnlineData.playersetkey.put(player.getUniqueId(), name);
				if(Main.VexView) {
					
					sendMoreMessage(player , Main.message.getLockKeyBoardMsg());
					
				}else {
					
					sendMoreMessage(player , Main.message.getLockItemSlotMsg());
					
				}
				
			}else if(Main.VexView && lore.contains("§f绑定按键： §4§l") && event.getClick().equals(ClickType.RIGHT)) {
				//右键可以解除绑定
				OnlineData.getSPlayer(player).removeKeyBoardSkill( Main.skillsdata.get(name) );
				sendMoreMessage(player , Main.message.getUnlockMsg());
				
			}
			
			player.closeInventory();
		}
		
		if(!Main.VexView) {
			int slot = event.getSlot();
			ItemStack item = inv.getItem(slot);
			//不允许技能物品脱离背包
			if(!(inv instanceof PlayerInventory && slot <= 8) && item != null && (item.equals(getNoSkillItem()) || isSkillItem(item))) {
				inv.setItem(slot, null);
				event.setCancelled(true);
			}
		}
		
		if(inv instanceof PlayerInventory && event.getSlot() <= 8 && !Main.VexView && event.getWhoClicked() instanceof Player) {
			
			//打开玩家背包进行绑定技能
			handleSkillBindToItem(inv , event);
			
		}

	}
	
	@EventHandler
	public void onChangeItem(PlayerItemHeldEvent event) {
		int slot = event.getNewSlot();
		Player player = event.getPlayer();
		
		//按下数字键触发技能
		if(slot <= 8 && !Main.VexView) {
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
		if((item.equals(getNoSkillItem()) || isSkillItem(item)) && !Main.VexView) {
			
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDie(PlayerDeathEvent event) {
		//防止死亡时掉落技能方面的物品
		List<ItemStack> items = event.getDrops();
		List<ItemStack> itemsremove = new ArrayList<>();
		
		for(ItemStack item : items) {
			if(item.equals(getNoSkillItem()) || isSkillItem(item)) {
				itemsremove.add(item);
			}
		}
		items.removeAll(itemsremove);
		
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		//防止技能物品被放置出来
		ItemStack item = event.getItem();
		
		if(!Main.VexView && item != null) {
			
			if(item.equals(getNoSkillItem())) {
				PlayerInventory inv = event.getPlayer().getInventory();
				inv.setItemInMainHand(null);
				event.setCancelled(true);
			}
			
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		//在VexView开启时清除数字键触发技能的物品
		if(Main.VexView) {
			Player player = event.getPlayer();
			PlayerInventory inv = player.getInventory();
			
			for(int i = 0 ; i < 9 ; i++) {
				if(inv.getItem(i) != null && (inv.getItem(i).equals(getNoSkillItem()) || isSkillItem(inv.getItem(i)))) {
					inv.setItem(i, null);
				}
			}
			
		}
	}
	
	private void handleSkillBindToItem(Inventory inv , InventoryClickEvent event) {
			
			Player player = (Player) event.getWhoClicked();
			ItemStack noskill = getNoSkillItem();
			int slot = event.getSlot();
			ItemStack clickitem = inv.getItem(slot);
			
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
			else if(clickitem.equals(noskill) && event.getClick().equals(ClickType.LEFT) && OnlineData.playersetkey.get(player.getUniqueId()) != null){
				
				String skillname = OnlineData.playersetkey.get(player.getUniqueId());
				inv.setItem(slot, getSkillItem(skillname));
				OnlineData.playersetkey.remove(player.getUniqueId());
				event.setCancelled(true);
				
			}
			//解除绑定
			else if(isSkillItem(clickitem) && event.getClick().equals(ClickType.RIGHT)) {
				
				inv.setItem(slot, noskill);
				event.setCancelled(true);
				
			}
			
			if(clickitem != null && (clickitem.equals(noskill) || isSkillItem(clickitem))) event.setCancelled(true);

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
	
	private void sendMoreMessage(Player player , List<String> args) {
		for(String arg : args) {
			player.sendMessage(arg);
		}
	}

}
