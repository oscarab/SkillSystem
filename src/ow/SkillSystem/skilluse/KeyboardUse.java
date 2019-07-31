package ow.SkillSystem.skilluse;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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
				p.sendMessage("§f§l成功退出绑定！");
			}else {
				player.addKeyBoardSetting(key, Main.skillsdata.get( OnlineData.playersetkey.get(p) ) );
				p.sendMessage("§f§l成功绑定！");
			}

			OnlineData.playersetkey.remove(p);
		}
		
	}
	

	
}
