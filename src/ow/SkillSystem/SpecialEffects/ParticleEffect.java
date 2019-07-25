package ow.SkillSystem.SpecialEffects;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public class ParticleEffect {
	private Particle particle;
	private int time;
	
	public ParticleEffect(String name , int time) {
		particle = Particle.valueOf(name);
		this.time = time;
	}
	
	//生成简单的单个粒子
	public void playNormal(World world , Location location) {
		world.spawnParticle(particle , location.getX() , location.getY() , location.getZ() , time*20);
	}

}
