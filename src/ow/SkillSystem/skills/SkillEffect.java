package ow.SkillSystem.skills;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import ow.SkillSystem.Main;
import ow.SkillSystem.SpecialEffects.*;
import ow.SkillSystem.SpecialEffects.ParticleEffect.ParticleCurve;
import ow.SkillSystem.SpecialEffects.ParticleEffect.ParticleCurvedSurface;
import ow.SkillSystem.SpecialEffects.ParticleEffect.ParticleEffect;
import ow.SkillSystem.data.OnlineData;
import ow.SkillSystem.data.SPlayer;

public class SkillEffect {   //技能实际效果
    @SuppressWarnings("unused")
	private String[] effects = {"Charge","PotionEffect",
    		"DamageSet","Damage","HealthSet",
    		"Shoot","Fire","Lightning",
    "Pull","PushBack","Message","ParticleEffect","SoundEffect",
    "Jump","Explosion","DamagedSet","Command", "Goto",
    "AttributeSet", "AttributeRemove","CoolDownSet"};
    private String effect;
    
    //半成品的数字，尚未进行处理，仍为算式形式
    private String examount = "0";
    
    //信息发送效果
    private String msg;
    
    //要执行的命令
    private String command;
    private String cmdsender;
    
    //预处理药水效果
    private String peduration = "0";
    private String peamplifier = "0";
    private PotionEffectType petype;
    private PotionEffect potioneffect;
    
    //粒子效果与声音效果
    private ParticleEffect particleeffect;
    private ParticleCurve particlecurve;
    private ParticleCurvedSurface particlesurface;
    private SoundEffect soundeffect;
    
    //抛射物效果
    private ProjectileEffect projectile;
    
    //属性点
    private String attribute;
    
    //跳转效果
    private int packet = 0;
    private int line = 1;
    
    //开始处理技能条中的效果部分
    public SkillEffect(String part) {
    	if(part.startsWith("PotionEffect")) {
    		setPotionEffect(part.split(":"));
    	}else if(part.startsWith("Message")) {
    		setAboutMessage(part.split(":"));
    	}else if(part.contains("Effect")) {
    		setEffect(part.split(":"));
    	}else if(part.startsWith("Command")){
    		 setAboutCommand(part.split(":"));
    	}else if(part.startsWith("Shoot")){
    		setAboutProjectile(part.split(":"));
    	}else if(part.startsWith("Goto")) {
    		setGoto(part.split(":"));
    	}else if(part.equalsIgnoreCase("AttributeRemove")) {
    		effect = "AttributeRemove";
    		attribute = "Attribute."+part.split(":")[1];
    	}else if(part.startsWith("AttributeSet") || part.startsWith("CoolDownSet")) {
    		setAboutAttribute(part.split(":"));
    	}else if(part.contains(":")){
    		setAboutNumber(part.split(":"));
    	}
    }
    
    /*处理效果后仅单个数字
     * Charge DamageSet Damage HealthIncrease
     * Fire Pull PushBack Jump
     */
    private void setAboutNumber(String[] parts) {
    	examount = parts[1];
    	effect = parts[0];
    }
    
    //处理药水效果
    private void setPotionEffect(String[] parts) {

    	petype = PotionEffectType.getByName(parts[1]);
    	peduration = parts[2];
    	peamplifier = parts[3];

    	effect = "PotionEffect";
    }
    
    //处理发送信息的效果
    private void setAboutMessage(String[] parts) {
    	msg = parts[1];
    	effect = parts[0];
    }
    
    //处理指令执行的效果
    private void setAboutCommand(String[] parts) {
    	effect = "Command";
    	cmdsender = parts[1];
    	command = parts[2];
    }
    
    //处理粒子与声音效果
    private void setEffect(String[] parts) {
    	
    	if(parts[0].equalsIgnoreCase("ParticleEffect")) {
    		
        	effect = "ParticleEffect";
        	particleeffect = new ParticleEffect(parts[1] , Integer.parseInt(parts[2]));
        	
    	}else if(parts[0].equalsIgnoreCase("ParticleCurveEffect")) {
    		
        	effect = "ParticleCurveEffect";
            particlecurve = Main.particleEffect.get(parts[1]);
            
    	}else if(parts[0].equalsIgnoreCase("ParticleCurvedSurfaceEffect")) {
    		
        	effect = "ParticleCurvedSurfaceEffect";
    		particlesurface = (ParticleCurvedSurface) Main.particleEffect.get(parts[1]);
    		
    	}
    	else {
    		
    		effect = "SoundEffect";
    		soundeffect = new SoundEffect(parts[1],Float.parseFloat(parts[2]),Float.parseFloat(parts[3]));
    	}
    	
    }
    
    //处理抛射物效果
    private void setAboutProjectile(String[] parts) {
    	
    	projectile = new ProjectileEffect(parts[1] , parts[2] , parts[3]);
    	effect = "Shoot";
    	
    }
    
    //处理属性点与技能冷却设置
    private void setAboutAttribute(String[] parts) {
    	effect = parts[0];
    	if(parts[0].contains("Attribute")) {
        	attribute = "Attribute."+parts[1];
    	}else {
        	attribute = parts[1];
    	}
    	examount = parts[2];
    }
    
    //处理跳转效果
    private void setGoto(String[] parts) {
    	
    	effect = "Goto";
    	packet = Integer.parseInt(parts[1]);
    	line = Integer.parseInt(parts[2]);
    	
    }
    
    public String getEffect() {
    	return effect;
    }
    
    public int getGotoPacket() {
    	return packet;
    }
    public int getGotoLine() {
    	return line;
    }
    
    /**
     * 执行技能效果
     * @param entities 执行的目标
     * @param user 使用者
     * @param duration 持续时间（非必要）
     */
    public void run(List<LivingEntity> entities , Player user , int duration) {
  	
    	SkillUtil skillutil = new SkillUtil();
    	//将未处理的算式进行处理
    	double amount = Main.util.getDoubleNumber(examount, user);
    	
    	//药水效果
    	if(effect.equalsIgnoreCase("PotionEffect")) {
    		potioneffect = new PotionEffect(petype,Main.util.getIntNumber(peduration, user),Main.util.getIntNumber(peamplifier, user));
    		for(LivingEntity entity : entities) {
    			entity.addPotionEffect(potioneffect);
    		}
    	}
    	//伤害效果
    	else if(effect.equalsIgnoreCase("Damage")) {
    		for(LivingEntity entity : entities) {
    			entity.damage(amount);
    		}
    	}
    	//着火
    	else if(effect.equalsIgnoreCase("Fire")) {
    		for(LivingEntity entity : entities) {
    			entity.setFireTicks((int) amount);
    		}
    	}
    	//闪电
    	else if(effect.equalsIgnoreCase("Lightning")) {
    		for(LivingEntity entity : entities) {
    			
        		entity.getWorld().strikeLightningEffect(entity.getLocation());
        		entity.damage(amount);

    		}
    	}
    	//抛射物
    	else if(effect.equalsIgnoreCase("Shoot")) {
    		for(LivingEntity entity : entities) {
    			
    			projectile.runOnEntity(entity, user);
    			
    		}
    	}
    	//血量强行调整
    	else if(effect.equalsIgnoreCase("HealthSet")) {
    		
    		for(LivingEntity entity : entities) {
    			Double health = entity.getHealth();
    			
    			if(health + amount < 0) {
        			entity.setHealth(0);
    			}else {
    				Double maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        			Double afterh = health + amount > maxHealth? maxHealth: health + amount;
        			entity.setHealth(afterh);
    			}
    			
    		}
    		
    	}
    	//强行调整攻击力
    	else if(effect.equalsIgnoreCase("DamageSet")) {
    		for(LivingEntity entity : entities) {
    			
    			if(OnlineData.getDamaged(entity) == null) {
        			OnlineData.addDamageSet(entity, amount, duration);
    			}else {
        			OnlineData.setDamageSet(entity, amount);
    			}
    			
    		}
    	}
    	//击退
    	else if(effect.equalsIgnoreCase("PushBack")) {
    		for(LivingEntity entity : entities) {
    			skillutil.pushBack(user, entity, amount);
    		}
    	}
    	//拉近
    	else if(effect.equalsIgnoreCase("Pull")) {
    		for(LivingEntity entity : entities) {
    			skillutil.pull(user, entity, amount);
    		}
    	}
    	//冲锋
    	else if(effect.equalsIgnoreCase("Charge")) {
    		for(LivingEntity entity : entities) {
    			skillutil.charge(entity, amount);
    		}
    	}
    	//发送信息
    	else if(effect.equalsIgnoreCase("Message")) {
    		for(LivingEntity entity : entities) {
    			
    			if(entity instanceof Player) {
    				msg = Main.util.replaceAPI(msg, (Player) entity);
    			}
    			
    			entity.sendMessage(msg);
    		}
    	}
    	//粒子效果
    	else if(effect.equalsIgnoreCase("ParticleEffect")) {
    		for(LivingEntity entity : entities) {
    			particleeffect.playNormal(entity.getWorld(), entity.getEyeLocation());
    		}
    	}
    	//粒子曲线
    	else if(effect.equalsIgnoreCase("ParticleCurveEffect")) {
    		for(LivingEntity entity : entities) {
    			Location loc = entity.getLocation();
    			particlecurve.plays(loc);
    		}
    	}
    	//粒子曲面
    	else if(effect.equalsIgnoreCase("ParticleCurvedSurfaceEffect")) {
    		for(LivingEntity entity : entities) {
    			Location loc = entity.getLocation();
    			particlesurface.plays(loc);
    		}
    	}
    	//声音效果
    	else if(effect.equalsIgnoreCase("SoundEffect")) {
    		for(LivingEntity entity : entities) {
    			soundeffect.play(entity.getWorld(), entity.getLocation());
    		}
    	}
    	//击飞
    	else if(effect.equalsIgnoreCase("Jump")) {
    		for(LivingEntity entity : entities) {
    			skillutil.jump(entity, amount);
    		}
    	}
    	//爆炸
    	else if(effect.equalsIgnoreCase("Explosion")) {
    		for(LivingEntity entity : entities) {
    			Location loc = entity.getLocation();
    			entity.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, loc.getX(), loc.getY(), loc.getZ(), 1);
    			entity.damage(amount);
    		}
    	}
    	//强行调整[所受]的伤害
    	else if(effect.equalsIgnoreCase("DamagedSet")) {
    		for(LivingEntity entity : entities) {
    			
    			if(OnlineData.getDamaged(entity) == null) {
        			OnlineData.addDamagedSet(entity, amount, duration);
    			}else {
        			OnlineData.setDamagedSet(entity, amount);
    			}
    			
    		}
    	}
    	//执行指令
    	else if(effect.equalsIgnoreCase("Command")) {
    		for(LivingEntity entity : entities) {
    			
    			if(entity instanceof Player) {
    				Player target = (Player) entity;
    				
    				switch(cmdsender) {
    				case "op":{
    					boolean isop = target.isOp();
    					if(isop) {
        					Bukkit.dispatchCommand(target, Main.util.replaceAPI(command, target));
    					}else {
        					target.setOp(true);
        					Bukkit.dispatchCommand(target, Main.util.replaceAPI(command, target));
        					target.setOp(false);
    					}
    					break;
    				}
    				case "console":{
    					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Main.util.replaceAPI(command, target));
    					break;
    				}
    				case "player":{
    					Bukkit.dispatchCommand(target, Main.util.replaceAPI(command, target));
    					break;
    				}
    				
    				}
    				
    			}
    			
    		}
    	}
    	//属性点设置
    	else if(effect.equalsIgnoreCase("AttributeSet")) {
    		
    		for(LivingEntity entity : entities) {
    			if(entity instanceof Player) {
    				
    				SPlayer splayer = OnlineData.getSPlayer((Player) entity);
    				splayer.setAttribute(attribute, splayer.getAttribute(attribute) + Main.util.getIntNumber(examount, user));
    				
    			}
    		}
    		
    	}
    	//属性点删除
    	else if(effect.equalsIgnoreCase("AttributeRemove")) {
    		
    		for(LivingEntity entity : entities) {
    			if(entity instanceof Player) {
    				
    				SPlayer splayer = OnlineData.getSPlayer((Player) entity);
    				splayer.removeAttribute(attribute);
    				
    			}
    		}
    		
    	}
    	//技能冷却调整
    	else if(effect.equalsIgnoreCase("CoolDownSet")) {
    		
    		for(LivingEntity entity : entities) {
    			if(entity instanceof Player) {
    			
    				SPlayer splayer = OnlineData.getSPlayer((Player) entity);
    				Skill skill = Main.skillsdata.get(attribute);
    				splayer.setCoolDown(skill, splayer.getCoolDown(skill) + Main.util.getIntNumber(examount, user));
    				
    			}
    		}
    	}
    	
    }
}
