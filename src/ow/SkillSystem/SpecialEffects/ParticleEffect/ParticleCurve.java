package ow.SkillSystem.SpecialEffects.ParticleEffect;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import com.destroystokyo.paper.ParticleBuilder;

import ow.SkillSystem.Main;
import ow.SkillSystem.Util;

public class ParticleCurve {
	
	//参数情况
	private class Parameter{
		char parameter;
		double from, to;
		double step;
		int delay;
		public Parameter(char parameter, double from, double to, double step, int delay) {
			this.parameter = parameter;
			this.from = from;
			this.to = to;
			this.step = step;
			this.delay = delay;
		}
	}
	
	double x,y,z;
	//参数方程
	String equationX, equationY, equationZ;
	//粒子类型
	Particle particle;
	//颜色
	Color color;
	//参数列表
	List<Parameter> parameters = new ArrayList<>();
	
	//初始化粒子曲线或曲面
	public ParticleCurve(String eq1, String eq2, String eq3, List<String> paras, List<Integer> colors, String particle) {
		equationX = eq1;
		equationY = eq2;
		equationZ = eq3;
		
		Util util = Main.util;
		for(String arg : paras) {
			String args[] = arg.split(":");
			Parameter p = new Parameter(args[0].charAt(0),
					util.getDoubleNumber(args[1], null),
					util.getDoubleNumber(args[2], null),
					util.getDoubleNumber(args[3], null),
					util.getIntNumber(args[4], null));
			parameters.add(p);
		}
		
		color = Color.fromRGB(colors.get(0), colors.get(1), colors.get(2));
		
		this.particle = Particle.valueOf(particle);
		
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
