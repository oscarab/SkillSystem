package ow.SkillSystem.data;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ow.SkillSystem.Main;
import ow.SkillSystem.skills.Skill;

//配置文件处理
public class ConfigHandle {
  private File skills;
  private YamlConfiguration skillsyml;
  private File items;
  private YamlConfiguration itemsyml;
  
  public ConfigHandle() throws IOException {
	  //技能
		skills = new File("./plugins/SkillSystem/skills.yml");
		skillsyml = YamlConfiguration.loadConfiguration(skills);
		if(!skills.exists()) {
			skillsyml.save(skills);
		}
		
		//物品
		items = new File("./plugins/SkillSystem/items.yml");
		itemsyml = YamlConfiguration.loadConfiguration(items);
		if(!items.exists()) {
			itemsyml.save(items);
		}
		
  }
  
  //载入技能文件
  public void loadSkills() {
		Iterator<String> itn = skillsyml.getKeys(false).iterator();
		while(itn.hasNext()) {
			String key = itn.next();
			int cooldown = skillsyml.getInt(key+".cooldown");
			boolean np = skillsyml.getBoolean(key+".needPermission");
			boolean ck = skillsyml.getBoolean(key+".cankeyBoard");
			String msg = skillsyml.getString(key+".message");
			List<String> worlds = skillsyml.getStringList(key+".banWorlds");
			List<String> description = skillsyml.getStringList(key+".description");
			int packet = skillsyml.getInt(key+".packet");
			
			Skill skill = new Skill(key , cooldown ,np , msg , ck , worlds , description);
			for(int i = 1 ; i <= packet ; i++) {
				skill.setExecution(skillsyml.getStringList(key+".executionPacket"+i));
			}
			
			Main.skillsdata.put(key,skill);
			Main.skills.add(skill);
		}
  }
  
  //载入物品
  public void loadItems() {
		Iterator<String> itn = itemsyml.getKeys(false).iterator();
		while(itn.hasNext()) {
			String key = itn.next();
			String name = itemsyml.getString(key+".name");
			String type = itemsyml.getString(key+".type");
			List<String> lores = itemsyml.getStringList(key+".lore");
			
			ItemStack item = new ItemStack(Material.valueOf(type));
			ItemMeta meta = item.getItemMeta();
			
			if(lores != null) {meta.setLore(lores);}
			if(name != null) {meta.setDisplayName(name);}
			item.setItemMeta(meta);
			
			Main.items.put(key,item);
		}
  }
  
  //初始化加载玩家的按键情况
  public void loadPlayerYML(SPlayer player) throws IOException {
	  
		File playerf = new File("./plugins/SkillSystem/Players/"+player.getPlayer().getName()+".yml");
		YamlConfiguration playeryml = YamlConfiguration.loadConfiguration(playerf);
		
		if(!playerf.exists()) {
			playeryml.save(playerf);
		}
		
		Iterator<String> itn = playeryml.getKeys(false).iterator();
		while(itn.hasNext()) {
			
			String key = itn.next();
			player.addKeyBoardSetting(Integer.parseInt(key), Main.skillsdata.get(playeryml.getString(key)));
			
		}
		
  }
  
  //保存玩家按键数据情况
  public void savePlayerYML(HashMap<Integer,Skill> kb , Player player) throws IOException {
	  
		File playerf = new File("./plugins/SkillSystem/Players/"+player.getPlayer().getName()+".yml");
		YamlConfiguration playeryml = YamlConfiguration.loadConfiguration(playerf);
		Iterator<Integer> itn = kb.keySet().iterator();
		
		while(itn.hasNext()) {
			
			int key = itn.next();
			
			if(kb.get(key) == null) {
				playeryml.set(key+"", null);
			}else {
				playeryml.set(key+"", kb.get(key).getName());
			}

		}
		
		playeryml.save(playerf);
  }
  

}
