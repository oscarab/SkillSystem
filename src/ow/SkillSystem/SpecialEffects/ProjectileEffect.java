package ow.SkillSystem.SpecialEffects;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.entity.WitherSkull;

import ow.SkillSystem.Main;
import ow.SkillSystem.data.OnlineData;

//抛射物 效果
public class ProjectileEffect {
	
	private String type;
	private String damage;
	private String speed;
	
	public ProjectileEffect(String type , String damage , String speed) {
		this.type = type;
		this.damage = damage;
		this.speed = speed;
	}
	
	public void runOnEntity(LivingEntity entity , Player user) {
    	double damage = Main.util.getDoubleNumber(this.damage, user);
    	double speed = Main.util.getDoubleNumber(this.speed, user);
		Location loc = entity.getEyeLocation();
		Location shootloc = loc.add(loc.getDirection().normalize());
		Projectile projectile = null;
    	
    	switch(type) {
    	case "Arrow": projectile = entity.getWorld().spawn(shootloc , Arrow.class);break;
    	case "Snowball": projectile = entity.getWorld().spawn(shootloc , Snowball.class);break;
    	case "Fireball": projectile = entity.getWorld().spawn(shootloc , Fireball.class);break;
    	case "LargeFireball": projectile = entity.getWorld().spawn(shootloc , LargeFireball.class);break;
    	case "SmallFireball": projectile = entity.getWorld().spawn(shootloc , SmallFireball.class);break;
    	case "WitherSkull": projectile = entity.getWorld().spawn(shootloc , WitherSkull.class);break;
    	case "DragonFireball": projectile = entity.getWorld().spawn(shootloc , DragonFireball.class);break;
    	case "Egg": projectile = entity.getWorld().spawn(shootloc , Egg.class);break;
    	case "ThrownExpBottle": projectile = entity.getWorld().spawn(shootloc, ThrownExpBottle.class);break;
    	}
    	
    	if(projectile != null) {
    		projectile.setVelocity(loc.getDirection().multiply(speed));
    		projectile.setShooter(entity);
    		OnlineData.projectiledamage.put(projectile, damage);
    	}


	}

}
