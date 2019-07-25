package ow.SkillSystem.skills;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import ow.SkillSystem.Util;
import ow.SkillSystem.SpecialEffects.ParticleEffect;
import ow.SkillSystem.SpecialEffects.SoundEffect;
import ow.SkillSystem.data.OnlineData;

public class SkillEffect {   //技能实际效果
    @SuppressWarnings("unused")
	private String[] effects = {"Charge","PotionEffect",
    		"DamageSet","Damage","HealthSet",
    		"ShowEntity","ShootArrows","Fire","Lightning",
    "Pull","PushBack","Message","ParticleEffect","SoundEffect",
    "Jump","Explosion","DamagedSet","Stop"};
    private String effect;
    private double amount;
    private String msg;
    private PotionEffect potioneffect;
    private ParticleEffect particleeffect;
    private SoundEffect soundeffect;
    
    //开始处理技能条中的效果部分
    public SkillEffect(String part) {
    	if(part.startsWith("PotionEffect")) {
    		setPotionEffect(part.split(":"));
    	}else if(part.startsWith("Message")) {
    		setMessage(part.split(":"));
    	}else if(part.contains("Effect")) {
    		setEffect(part.split(":"));
    	}else {
    		setAboutNumber(part.split(":"));
    	}
    }
    
    /*处理效果后仅单个数字
     * Charge DamageSet Damage HealthIncrease
     * Fire Pull PushBack ShootArrows Jump
     */
    private void setAboutNumber(String[] parts) {
    	Util util = new Util();
    	amount = util.getDoubleNumber(parts[1]);
    	effect = parts[0];
    }
    
    //处理药水效果
    private void setPotionEffect(String[] parts) {
    	Util util = new Util();
    	potioneffect = new PotionEffect(PotionEffectType.getByName(parts[1]),util.getIntNumber(parts[2]),util.getIntNumber(parts[3]));
    	effect = "PotionEffect";
    }
    
    //处理信息效果
    private void setMessage(String[] parts) {
    	msg = parts[1];
    	effect = "Message";
    }
    
    //处理粒子与声音效果
    private void setEffect(String[] parts) {
    	
    	if(parts[0].equalsIgnoreCase("ParticleEffect")) {
    		
        	effect = "ParticleEffect";
        	particleeffect = new ParticleEffect(parts[1] , Integer.parseInt(parts[2]));
        	
    	}else {
    		
    		effect = "SoundEffect";
    		soundeffect = new SoundEffect(parts[1],Float.parseFloat(parts[2]),Float.parseFloat(parts[2]));
    	}
    	
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
    	
    	//药水效果
    	if(effect.equalsIgnoreCase("PotionEffect")) {
    		for(LivingEntity entity : entities) {
    			entity.addPotionEffect(potioneffect);
    		}
    	}//伤害效果
    	else if(effect.equalsIgnoreCase("Damage")) {
    		for(LivingEntity entity : entities) {
    			entity.damage(amount);
    		}
    	}//显形
    	else if(effect.equalsIgnoreCase("showEntity")) {
    		for(LivingEntity entity : entities) {
    			entity.setGlowing(true);
    			//do
    		}
    	}//着火
    	else if(effect.equalsIgnoreCase("Fire")) {
    		for(LivingEntity entity : entities) {
    			entity.setFireTicks((int) amount);
    		}
    	}//闪电
    	else if(effect.equalsIgnoreCase("Lightning")) {
    		for(LivingEntity entity : entities) {
    			
    			for(int i = 0 ; i< amount ; i++) {
        			entity.getWorld().strikeLightning(entity.getLocation());
    			}

    		}
    	}//射箭
    	else if(effect.equalsIgnoreCase("ShootArrows")) {
    		for(LivingEntity entity : entities) {
    			Location loc = entity.getEyeLocation();
    			
        		entity.getWorld().spawnArrow(loc, loc.getDirection(), (float) amount, 12);

    		}
    	}//血量强行调整
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
    		
    	}//强行调整攻击力
    	else if(effect.equalsIgnoreCase("DamageSet")) {
    		for(LivingEntity entity : entities) {
    			
    			if(OnlineData.damageset.get(entity) == null) {
        			OnlineData.addDamageSet(entity, amount, duration);
    			}else {
        			OnlineData.setDamageSet(entity, amount);
    			}
    			
    		}
    	}//击退
    	else if(effect.equalsIgnoreCase("PushBack")) {
    		for(LivingEntity entity : entities) {
    			skillutil.pushBack(user, entity, amount);
    		}
    	}//拉近
    	else if(effect.equalsIgnoreCase("Pull")) {
    		for(LivingEntity entity : entities) {
    			skillutil.pull(user, entity, amount);
    		}
    	}//冲锋
    	else if(effect.equalsIgnoreCase("Charge")) {
    		for(LivingEntity entity : entities) {
    			skillutil.charge(entity, amount);
    		}
    	}//发送信息
    	else if(effect.equalsIgnoreCase("Message")) {
    		for(LivingEntity entity : entities) {
    			entity.sendMessage(msg);
    		}
    	}//粒子效果
    	else if(effect.equalsIgnoreCase("ParticleEffect")) {
    		for(LivingEntity entity : entities) {
    			particleeffect.playNormal(entity.getWorld(), entity.getLocation());
    		}
    	}//声音效果
    	else if(effect.equalsIgnoreCase("SoundEffect")) {
    		for(LivingEntity entity : entities) {
    			soundeffect.play(entity.getWorld(), entity.getLocation());
    		}
    	}//击飞
    	else if(effect.equalsIgnoreCase("Jump")) {
    		for(LivingEntity entity : entities) {
    			skillutil.jump(entity, amount);
    		}
    	}//爆炸
    	else if(effect.equalsIgnoreCase("Explosion")) {
    		for(LivingEntity entity : entities) {
    			entity.getWorld().createExplosion(entity.getLocation(), (float) amount);
    		}
    	}//强行调整[所受]的伤害
    	else if(effect.equalsIgnoreCase("DamagedSet")) {
    		for(LivingEntity entity : entities) {
    			
    			if(OnlineData.damagedset.get(entity) == null) {
        			OnlineData.addDamagedSet(entity, amount, duration);
    			}else {
        			OnlineData.setDamagedSet(entity, amount);
    			}
    			
    		}
    	}
    	
    }
}
