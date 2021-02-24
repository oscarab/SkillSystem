package ow.SkillSystem.placeholderExpansion;

import org.bukkit.OfflinePlayer;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import ow.SkillSystem.data.OnlineData;
import ow.SkillSystem.data.SPlayer;

public class PlaceholderExtend extends PlaceholderExpansion{

	@Override
	public String getAuthor() {
		return "OscarWen";
	}

	@Override
	public String getIdentifier() {
		return "SkillSystem";
	}

	@Override
	public String getVersion() {
		return "1.6.1";
	}
	
	@Override
    public boolean persist(){
        return true;
    }
	
	@Override
    public boolean canRegister(){
        return true;
    }
	
	public String onRequest(OfflinePlayer player, String identifier){
		if(player == null) {
			return "";
		}
		
		SPlayer p = OnlineData.getSPlayer(player.getUniqueId());
		if(p.isAttribute("Attribute." + identifier)) {
			return ""+p.getAttribute("Attribute." + identifier);
		}
		return "None";
	}

}
