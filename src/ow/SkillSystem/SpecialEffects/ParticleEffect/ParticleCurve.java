package ow.SkillSystem.SpecialEffects.ParticleEffect;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;

import com.destroystokyo.paper.ParticleBuilder;

import ow.SkillSystem.Main;
import ow.SkillSystem.Util;

public class ParticleCurve {
	
	//参数情况
	protected class Parameter{
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
	protected String equationX, equationY, equationZ;
	//粒子类型
	protected Particle particle;
	//颜色
	protected Color color;
	//参数列表
	protected List<Parameter> parameters = new ArrayList<>();
	//数量
	protected int count;
	//X轴调整
	protected int adjustX;
	
	protected List<Double> preX = new ArrayList<>();
	protected List<Double> preY = new ArrayList<>();
	protected List<Double> preZ = new ArrayList<>();
	
	//初始化粒子曲线或曲面
	public ParticleCurve(String eq1, String eq2, String eq3, List<String> paras, String colors, String particle, int count, int adjustX) {
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
		this.adjustX = adjustX;
		
		preHandle();
	}
	
	protected void preHandle() {
		Parameter para = parameters.get(0);
		for(double i = para.from; i <= para.to; i+=para.step) {
			preX.add(Main.util.getDoubleNumber(equationX.replace("("+para.parameter+")", "" + i), null));
			preY.add(Main.util.getDoubleNumber(equationY.replace("("+para.parameter+")", ""+ i), null));
			preZ.add(Main.util.getDoubleNumber(equationZ.replace("("+para.parameter+")", "" + i), null));
		}
	}
	
	public void plays(Location loc) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, new Runnable() {

			@Override
			public void run() {
				ParticleBuilder builder = new ParticleBuilder(particle);
				double nx,ny,nz,temp;
				double alpha = Math.toRadians(270 - loc.getYaw());
				double sina = Math.sin(alpha), cosa = Math.cos(alpha);
				double beta = Math.toRadians(-loc.getPitch());
				double sinb = Math.sin(beta), cosb = Math.cos(beta);
				double x = loc.getX(), y = loc.getY(), z = loc.getZ();
				Location location = loc.clone();
				int size = preX.size();
				
				for(int j = 0; j < size; j++) {
					nx = x + preX.get(j);
					ny = y + preY.get(j);
					nz = z + preZ.get(j);
					temp = nx;
					nx = (nx - x)*cosb - (ny - y)*sinb + x;
					ny = (temp - x)*sinb + (ny - y)*cosb + y;
					temp = nx;
					nx = (nz - z)*sina + (nx - x)*cosa + x;
					nz = (nz - z)*cosa - (temp - x)*sina + z;   //坐标根据朝向旋转变换
					location.set(nx, ny, nz);
					
					builder.location(location);
					builder.color(color);
					builder.count(count);
					builder.spawn();				
				}
				
			}
			
		});
	}

}
