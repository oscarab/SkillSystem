package ow.SkillSystem.SpecialEffects;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public class ParticleEffect {
	private Particle particle;
	private int count;
	
	public ParticleEffect(String name , int count) {
		particle = Particle.valueOf(name);
		this.count = count;
	}
	
	//生成简单的单个粒子
	public void playNormal(World world , Location location) {
		world.spawnParticle(particle, location, count);
	}

}
