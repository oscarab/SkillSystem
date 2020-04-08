package ow.SkillSystem.SpecialEffects.ParticleEffect;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public class ParticleEffect {
	private Particle particle;
	private double offsetx = 0, offsety = 0, offsetz = 0;
	private int count;
	
	public ParticleEffect(String name , int count) {
		particle = Particle.valueOf(name);
		this.count = count;
	}
	
	public ParticleEffect(String name, Color color) {
		particle = Particle.valueOf(name);
		count = 0;
		offsetx = color.getRed() / 256;
		offsety = color.getGreen() / 256;
		offsetz = color.getBlue() / 256;
	}
	
	//生成简单的单个粒子
	public void playNormal(World world , Location location) {
		world.spawnParticle(particle, location, count, offsetx, offsety, offsetz);
	}

}
