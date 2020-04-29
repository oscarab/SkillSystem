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
	
	//参数方程
	String equationX, equationY, equationZ;
	//粒子类型
	Particle particle;
	//颜色
	Color color;
	//参数列表
	List<Parameter> parameters = new ArrayList<>();
	//数量
	int count;
	
	//初始化粒子曲线或曲面
	public ParticleCurve(String eq1, String eq2, String eq3, List<String> paras, String colors, String particle, int count) {
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
		
		String args[] = colors.split(",");
		color = Color.fromRGB(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		
		this.particle = Particle.valueOf(particle);
		this.count = count;
	}
	
	public void plays(World world, double x, double y, double z) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, new Runnable() {

			@Override
			public void run() {
				
				for(Parameter para : parameters) {

					for(double i = para.from; i <= para.to; i+=para.step) {
						double nx = x + Main.util.getDoubleNumber(equationX.replace("("+para.parameter+")", "" + i), null);
						double ny = y + Main.util.getDoubleNumber(equationY.replace("("+para.parameter+")", ""+ i), null);
						double nz = z + Main.util.getDoubleNumber(equationZ.replace("("+para.parameter+")", "" + i), null);
						Location location = new Location(world, nx, ny, nz);
						ParticleBuilder builder = new ParticleBuilder(particle);
						
						builder.location(location);
						builder.color(color);
						builder.count(count);
						builder.spawn();
						
						try {
							Thread.sleep(para.delay);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
					}
				}
				
			}
			
		});
	}

}
