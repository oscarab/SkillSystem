package ow.SkillSystem.data;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

public class DataManager {

	public SPlayer getSPlayer(Player p) {
		return getSPlayer(p.getUniqueId());
	}
	
	public SPlayer getSPlayer(UUID uuid) {
		return OnlineData.players.get(uuid);
	}
	
	public void removeProjectileDamage(Projectile pro) {
		OnlineData.projectiledamage.remove(pro);
	}
	
	public void addProjectileDamage(Projectile pro, double dam) {
		OnlineData.projectiledamage.put(pro, dam);
	}
	
	public double getProjectileDamage(Projectile pro) {
		return OnlineData.projectiledamage.get(pro);
	}
	
}
