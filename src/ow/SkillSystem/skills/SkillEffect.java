package ow.SkillSystem.skills;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import ow.SkillSystem.Main;
import ow.SkillSystem.SpecialEffects.*;
import ow.SkillSystem.data.OnlineData;

public class SkillEffect {   //技能实际效果
    @SuppressWarnings("unused")
	private String[] effects = {"Charge","PotionEffect",
    		"DamageSet","Damage","HealthSet",
    		"Shoot","Fire","Lightning",
    "Pull","PushBack","Message","ParticleEffect","SoundEffect",
    "Jump","Explosion","DamagedSet","Command","Stop","CooldownSet"};
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
    private SoundEffect soundeffect;
    
    //抛射物效果
    private ProjectileEffect projectile;
    
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
    	}else if(part.contains(":")){
    		setAboutNumber(part.split(":"));
    	}else if(part.equalsIgnoreCase("Stop")){
    		effect = "Stop";
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
        	
    	}else {
    		
    		effect = "SoundEffect";
    		soundeffect = new SoundEffect(parts[1],Float.parseFloat(parts[2]),Float.parseFloat(parts[3]));
    	}
    	
    }
    
    //处理抛射物效果
    private void setAboutProjectile(String[] parts) {
    	
    	projectile = new ProjectileEffect(parts[1] , parts[2] , parts[3]);
    	effect = "Shoot";
    	
    }
    
    public String getEffect() {
    	return effect;
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
    			
    			for(int i = 0 ; i< amount ; i++) {
        			entity.getWorld().strikeLightning(entity.getLocation());
    			}

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
        			Double afterh = health + amount > entity.getMaxHealth()? entity.getMaxHealth(): health + amount;
        			entity.setHealth(afterh);
    			}
    			
    		}
    		
    	}
    	//强行调整攻击力
    	else if(effect.equalsIgnoreCase("DamageSet")) {
    		for(LivingEntity entity : entities) {
    			
    			if(OnlineData.damageset.get(entity) == null) {
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
    			entity.getWorld().createExplosion(loc.getX() , loc.getY() , loc.getZ() , (float) amount , false , false);
    		}
    	}
    	//强行调整[所受]的伤害
    	else if(effect.equalsIgnoreCase("DamagedSet")) {
    		for(LivingEntity entity : entities) {
    			
    			if(OnlineData.damagedset.get(entity) == null) {
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
    					target.setOp(true);
    					Bukkit.dispatchCommand(target, Main.util.replaceAPI(command, target));
    					target.setOp(false);
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
    	
    }
}
