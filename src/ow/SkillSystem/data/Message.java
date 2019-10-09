package ow.SkillSystem.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

public class Message {
	
    private File message;
	private YamlConfiguration messageyml;
	
	private List<String> lockKeyBoardMsg = new ArrayList<>();
	private List<String> lockItemSlotMsg = new ArrayList<>();
	private List<String> unlockMsg = new ArrayList<>();
	  
	public Message() throws IOException {
		message = new File("./plugins/SkillSystem/message.yml");
		messageyml = YamlConfiguration.loadConfiguration(message);
		if(!message.exists()) {
			lockKeyBoardMsg.add("§f§l请按下你需要绑定的按键");
			lockItemSlotMsg.add("§f§l请打开背包左击未绑定技能的技能槽");
			lockItemSlotMsg.add("§f§l打开背包右击1-9格空的物品栏可添加技能槽");
			unlockMsg.add("§f§l成功解除绑定");
			
			messageyml.set("lockKeyBoardMsg",lockKeyBoardMsg);
			messageyml.set("lockItemSlotMsg", lockItemSlotMsg);
			messageyml.set("unlockMsg", unlockMsg);
			messageyml.save(message);
		}else {
			lockKeyBoardMsg.addAll(messageyml.getStringList("lockKeyBoardMsg"));
			lockItemSlotMsg.addAll(messageyml.getStringList("lockItemSlotMsg"));
			unlockMsg.addAll(messageyml.getStringList("unlockMsg"));
		}
		
	}
	
	public List<String> getUnlockMsg(){
		return unlockMsg;
	}
	
	public List<String> getLockKeyBoardMsg(){
		return lockKeyBoardMsg;
	}
	
	public List<String> getLockItemSlotMsg(){
		return lockItemSlotMsg;
	}
	
	

}
