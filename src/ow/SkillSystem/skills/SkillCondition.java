package ow.SkillSystem.skills;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import ow.SkillSystem.Main;
import ow.SkillSystem.Util;

public class SkillCondition { 
	//触发技能的条件
    private String[] conditions = {"SelfHealth","EveryAttacking",
    "EveryKilling","TargetHealth","ItemConsuming","OnAir",
    "ItemHas","NextAtttacking","NextKilling","Run","None"};
    private String condition;
    //有关数值
    private int amount;
    private String examount = "0";
    //有关物品
    private ItemStack item;
    //等于0，大于2，大于等于1，小于等于-1，小于-2
    private int sign;
    //是否无条件
    private boolean isNone = false;
    
    public SkillCondition(String part) {
    	if(part.contains("Health")) {
    		setAboutHealth(part);
    	}else if(part.contains("Item")) {
    		setAboutItem(part);
    	}else {
    		setSingleCondition(part);
    	}
    }
    
    //处理关于生命值的条件
    private void setAboutHealth(String part){
    	Util util = new Util();
    	if(part.contains("SelfHealth")) {
    		condition = conditions[0];
    		examount = part.substring(11);
    	}else{
    		condition = conditions[3];
    		examount = part.substring(13);
    	}
		sign = util.getSign(part);
    }
    
    //处理关于物品的条件
    private void setAboutItem(String part) {
    	String[] parts = part.split(":");
    	condition = parts[0];
    	item = Main.items.get(parts[1]);
    	examount = parts[2];
    }
    
    //处理关于无附加属性的条件
    private void setSingleCondition(String part) {
    	if(part.equalsIgnoreCase("None")) {
    		isNone = true;
    		condition = "None";
    	}else {
    		condition = part;
    	}
    }
    
    public String getCondition() {
    	return condition;
    }
    
    //是否需要标记
    public boolean isNeedSign() {
    	return getCondition().contains("Attack")||
  			  getCondition().contains("Kill");
    }
    
    //检查是否满足条件
    public boolean check(Player self,LivingEntity target) {
    	//处理数值算式
    	amount = Main.util.getIntNumber(examount, self);
    	
    	//检测血量
    	if(condition.contains("Health")) {
        	double health = condition.contains("Self")?self.getHealth():target.getHealth();
        	switch(sign) {
        	case -2:{
        		return health < amount;
        	}
        	case -1:{
        		return health <= amount;
        	}
        	case 1:{
        		return health >= amount;
        	}
        	case 2:{
        		return health > amount;
        	}
        	default:{
        		return health == amount;
        	}
        	}
    	}//检测是否在空中
    	else if(condition.equalsIgnoreCase("OnAir")) {
    		return !self.isOnGround();
    	}//检测物品消耗
    	else if(condition.equalsIgnoreCase("ItemConsuming")) {

    		PlayerInventory inv = self.getInventory();
    		if(hasItem(item , inv , amount)) {
    			
    			removeItem(item , inv , amount);
    			
    			return true;
    		}
    		return false;
    	}//检测物品拥有
    	else if(condition.equalsIgnoreCase("ItemHas")) {

    		return hasItem(item , self.getInventory() , amount);
    	}//检测奔跑
    	else if(condition.equalsIgnoreCase("Run")) {
    		return self.isSprinting();
    	}
    	//无条件
    	else if(isNone) {
    		return true;
    	}
		return false;
    }
    
    //移除背包内指定数量的物品
    private void removeItem(ItemStack item , PlayerInventory inv , int arg) {
    	
    	for(int i  = 0 ; i < inv.getSize() ; i++) {
    		ItemStack it = inv.getItem(i);
    		//寻找背包中所需要的物品
    		if(it != null && item.isSimilar(it)) {
    			
    			if(it.getAmount() > arg) {
    				it.setAmount(it.getAmount() - arg);
    				return;
    			}else if(it.getAmount() == arg){
    				inv.setItem(i, null);
    				return;
    			}else {
    				arg -= it.getAmount();
    				inv.setItem(i, null);
    			}
    			
    		}
    	}
    	
    }
    
    //检测背包是否有指定数量的物品
    private boolean hasItem(ItemStack item , PlayerInventory inv , int arg) {
    	
    	for(int i  = 0 ; i < inv.getSize() ; i++) {

    		ItemStack it = inv.getItem(i);
    		if(it != null && item.isSimilar(it)) {
    			
    			if(it.getAmount() >= arg) {
    				return true;
    			}else {
    				arg -= it.getAmount();
    			}
    			
    		}
    		
    		if(arg <= 0) return true; 
    	}
    	
    	return false;
    }
    
}
