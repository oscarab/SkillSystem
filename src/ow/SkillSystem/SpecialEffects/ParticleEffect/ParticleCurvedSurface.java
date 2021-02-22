package ow.SkillSystem.SpecialEffects.ParticleEffect;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.destroystokyo.paper.ParticleBuilder;

import ow.SkillSystem.Main;

public class ParticleCurvedSurface extends ParticleCurve{
	
	private List<List<Double>> preXs;
	private List<List<Double>> preYs;
	private List<List<Double>> preZs;
	
	public ParticleCurvedSurface(String eq1, String eq2, String eq3, List<String> paras, String colors, String particle,
			int count, int adjustX) {
		super(eq1, eq2, eq3, paras, colors, particle, count, adjustX);
	}
	
	protected void preHandle() {
		Parameter para1 = parameters.get(0);
		Parameter para2 = parameters.get(1);
		preXs = new ArrayList<>();
		preYs = new ArrayList<>();
		preZs = new ArrayList<>();
		
		for(double i = para1.from; i <= para1.to; i += para1.step) {
			List<Double> tempx = new ArrayList<>();
			List<Double> tempy = new ArrayList<>();
			List<Double> tempz = new ArrayList<>();
			String eqx = equationX.replace("("+para1.parameter+")", "" + i);
			String eqy = equationY.replace("("+para1.parameter+")", "" + i);
			String eqz = equationZ.replace("("+para1.parameter+")", "" + i);
			for(double j = para2.from; j <= para2.to; j+= para2.step) {
				tempx.add(Main.util.getDoubleNumber(eqx.replace("("+para2.parameter+")", ""+j), null));
				tempy.add(Main.util.getDoubleNumber(eqy.replace("("+para2.parameter+")", ""+j), null));
				tempz.add(Main.util.getDoubleNumber(eqz.replace("("+para2.parameter+")", ""+j), null));
			}
			preXs.add(tempx);
			preYs.add(tempy);
			preZs.add(tempz);
		}
	}
	
	public void plays(Location loc) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, new Runnable() {

			public void run() {
				int size1 = preXs.size();
				int size2 = preXs.get(0).size();
				ParticleBuilder builder = new ParticleBuilder(particle);
				double nx,ny,nz,temp;
				double alpha = Math.toRadians(270 - loc.getYaw());
				double sina = Math.sin(alpha), cosa = Math.cos(alpha);
				double beta = Math.toRadians(-loc.getPitch());
				double sinb = Math.sin(beta), cosb = Math.cos(beta);
				double x = loc.getX(), y = loc.getY(), z = loc.getZ();
				Location location = loc.clone();
				
				for(int i = 0; i < size1; i++) {
					for(int j = 0; j < size2; j++) {
						nx = x + preXs.get(i).get(j);
						ny = y + preYs.get(i).get(j);
						nz = z + preZs.get(i).get(j);
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
			}
			
		});
	}

}
