package ow.SkillSystem;

import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.clip.placeholderapi.PlaceholderAPI;
import ow.SkillSystem.data.OnlineData;
import ow.SkillSystem.data.SPlayer;
import ow.SkillSystem.skills.Skill;
import ow.SkillSystem.skills.SkillCondition;
import ow.SkillSystem.skills.SkillEffect;
import ow.SkillSystem.skills.SkillTarget;

public class Util {
	
  public double getDoubleNumber(String part ,Player p){
	  ScriptEngine se = new ScriptEngineManager().getEngineByName("js");
	  
	  part = replaceAPI(part , p);

	  try {
		return Double.parseDouble(se.eval(part).toString());
	} catch (ScriptException e) {
		e.printStackTrace();
		Bukkit.getConsoleSender().sendMessage("§4在处理包含算式的技能条中遭遇了错误，请及时检查配置文件！");
		Bukkit.getConsoleSender().sendMessage("§4具体错误处：§f"+part);
	}
	  return 0;
	 
  }
  
  public int getIntNumber(String part , Player p) {
	  ScriptEngine se = new ScriptEngineManager().getEngineByName("js");
	  part = replaceAPI(part , p);
	  
	  try {
		return (int) Double.parseDouble(se.eval(part).toString());
	} catch (ScriptException e) {
		e.printStackTrace();
		Bukkit.getConsoleSender().sendMessage("§4在处理包含算式的技能条中遭遇了错误，请及时检查配置文件！");
		Bukkit.getConsoleSender().sendMessage("§4具体错误处：§f"+part);
	}
	  return 0;

  }
  
  public int getSign(String part) {
	  if(part.contains(">=")) {
		  return 1;
	  }else if(part.contains("<=")) {
		  return -1;
	  }else if(part.contains(">")) {
		  return 2;
	  }else if(part.contains("<")){
		 return -2; 
	  }else {
		  return 0;
	  }
  }
  
  /**
   * 应用PlaceholderAPI作为变量前置
   * @param part 需要进行替换的字符串
   * @param p 对应的玩家
   * @return 返回替换好的字符串
   */
  public String replaceAPI(String part ,Player p) {
	  if(Main.PaPi) {
		return PlaceholderAPI.setPlaceholders(p, part);
	  }
	  return part;
  }
  
  /** 为一个玩家创建一个技能按钮绑定页面
   */
  public void createInventory(Player p) {
	  Inventory inv = Bukkit.createInventory(p, 54 , "[技能系统]按键绑定");
	  
	  for(ItemStack item : getItems(p)) {
		  inv.addItem(item);
	  }
	  
	  p.openInventory(inv);
  }
  
  /**
   * 获取技能绑定界面的物品按钮
   */
  private List<ItemStack> getItems(Player player){
	  List<ItemStack> items = new ArrayList<>();
	  
	  for(Skill skill : Main.skills) {
		  
		  if( (!player.hasPermission("SkillSystem."+skill.getName()) && skill.getIsNeedPermission()) 
				  || !skill.canUseKeyBoard()) continue;
		  
		  ItemStack item = new ItemStack(Material.SLIME_BALL);
		  ItemMeta meta = item.getItemMeta();
		  List<String> lores = new ArrayList<>();
		  SPlayer p = OnlineData.getSPlayer(player);
		  
		  meta.setDisplayName(skill.getName());
		  lores.add("§f左键单击开始绑定"+(Main.VexView ? "，右键单击取消绑定":""));
		  
		  int key = p.getKeyBoardSkill(skill);
		  if(key != -1 && Main.VexView) {
			  lores.add("§f绑定按键： §4§l"+getKeyChar(key));
		  }else {
			  
			  if(Main.VexView) {
				  lores.add("§4§l未绑定");
			  }else {
				  lores.add(" ");
			  }
			  
		  }
		  
		  meta.setLore(lores);
		  item.setItemMeta(meta);
		  
		  items.add(item);
	  }
	  
	  return items;
  }
  
  /**是否是可持续的技能执行
   * 如果是攻击或被攻击的条件下的，可以持续
   * 如果是伤害调整的效果，可以持续
   */
  public boolean canHasDuration(SkillCondition condition , SkillTarget target , SkillEffect effect) {
	  if(condition.getCondition().contains("Every")) {
		  return true;
	  }
      if(effect.getEffect().equalsIgnoreCase("DamageSet") || effect.getEffect().equalsIgnoreCase("DamagedSet")) {
		  return true;
	  }
	  return false;
  }
  
  /**
   * 使玩家执行技能
   */
  public void runSkill(SPlayer player , Skill skill) {
	  Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, new Thread() {

		@Override
		public void run() {
			
			skill.run(player.getPlayer());
		
		}
		  
	  });
  }
  
  //获取数字所对应的键盘字符
  private String getKeyChar(int key) {
	  switch(key) {
	  case 1: return "ESC";
	  
	  case 2: return "1";
	  case 3: return "2";
	  case 4: return "3";
	  case 5: return "4";
	  case 6: return "5";
	  case 7: return "6";
	  case 8: return "7";
	  case 9: return "8";
	  case 10: return "9";
	  case 11: return "0";
	  
	  case 12: return "-";
	  case 13: return "=";
	  case 14: return "Backspace";
	  case 15: return "TAB";
	  
	  case 16: return "Q";
	  case 17: return "W";
	  case 18: return "E";
	  case 19: return "R";
	  case 20: return "T";
	  case 21: return "Y";
	  case 22: return "U";
	  case 23: return "I";
	  case 24: return "O";
	  case 25: return "P";
	  
	  case 26: return "[";
	  case 27: return "]";
	  case 28: return "Enter";
	  case 29: return "Control";
	  
	  case 30: return "A";
	  case 31: return "S";
	  case 32: return "D";
	  case 33: return "F";
	  case 34: return "G";
	  case 35: return "H";
	  case 36: return "J";
	  case 37: return "K";
	  case 38: return "L";
	  
	  case 39: return ";";
	  case 40: return "'";
	  case 41: return "`";
	  case 42: return "Shift(左)";
	  case 43: return "\\";
	  
	  case 44: return "Z";
	  case 45: return "X";
	  case 46: return "C";
	  case 47: return "V";
	  case 48: return "B";
	  case 49: return "N";
	  case 50: return "M";
	  
	  case 51: return ",";
	  case 52: return ".";
	  case 53: return "/";
	  case 54: return "Shift(右)";
	  case 56: return "Alt";
	  case 57: return "空格";
	  
	  default : return "未知";
	  }
  }
}
