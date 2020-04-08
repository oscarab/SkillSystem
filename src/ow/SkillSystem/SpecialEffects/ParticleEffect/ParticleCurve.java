package ow.SkillSystem.SpecialEffects.ParticleEffect;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import com.destroystokyo.paper.ParticleBuilder;

import ow.SkillSystem.Main;

public class ParticleCurve {
	
	double x,y,z;
	
	String equationX, equationY, equationZ;
	
	public ParticleCurve(Location origin, String eq1, String eq2, String eq3) {
		x = origin.getX();
		y = origin.getY();
		z = origin.getZ();
		equationX = eq1;
		equationY = eq2;
		equationZ = eq3;
	}
	
	public void plays(World world) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, new Runnable() {

			@Override
			public void run() {
				for(int i = 0; i < 100; i++) {
					double j = i * ((3.1415926)/50);
					double nx = x + Main.util.getDoubleNumber(equationX.replace("cost", ""+Math.cos(j)), null);
					double ny = y + Main.util.getDoubleNumber(equationY.replace("t", ""+ j), null);
					double nz = z + Main.util.getDoubleNumber(equationZ.replace("sint", ""+Math.sin(j)), null);
					Location location = new Location(world, nx, ny, nz);
					ParticleBuilder builder = new ParticleBuilder(Particle.REDSTONE);
					builder.location(location);
					builder.color(Color.ORANGE);
					builder.count(0);
					builder.spawn();
				}
			}
			
		});
	}

}
