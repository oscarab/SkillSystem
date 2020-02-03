package ow.SkillSystem.skills;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import ow.SkillSystem.Main;
import ow.SkillSystem.Util;
import ow.SkillSystem.data.OnlineData;

public class SkillCondition { 
	//触发技能的条件
    @SuppressWarnings("unused")
	private String[] conditions = {"SelfHealth","EveryAttacking",
    "EveryKilling","TargetHealth","ItemConsuming","OnAir",
    "ItemHas","NextAtttacking","NextKilling","Run","Storm","Time","Biome",
    "Probability","HasPointEntity","HasRaduisEntity","Equation","Attribute","None"};
    
    private String condition;
    
    //有关数值
    private int amount;
    private String examount = "0";
    
    //有关字符串
    private String tag;
    
    //有关物品
    private ItemStack item;
    
    //等于0，大于2，大于等于1，小于等于-1，小于-2
    private int sign;
    
    //概率
    private String probability = "0";
    
    //距离
    private String distance = "0";
    
    //方程式
    private String[] equation = new String[2];
    
    //是否无条件
    private boolean isNone = false;
    
    public SkillCondition(String part) {
    	if(part.contains("Health") || part.contains("Time")) {
    		setAboutSignCondition(part);
    	}else if(part.contains("Item")) {
    		setAboutItem(part);
    	}else if(part.contains("Biome")){
    		setAboutBiome(part);
    	}else if(part.contains("Probability")) {
    		setAboutProbability(part);
    	}else if(part.contains("Entity")) {
    		setAboutEntity(part);
    	}else if(part.startsWith("Equation") || part.startsWith("Attribute")){
    		setAboutEquation(part);
    	}
    	else {
    		setSingleCondition(part);
    	}
    }
    
    //处理包含比较符号的条件
    private void setAboutSignCondition(String part){
    	Util util = new Util();
		sign = util.getSign(part);
    	if(part.contains("SelfHealth")) {
    		condition = "SelfHealth";
    		examount = part.substring(11);
    	}else if(part.contains("TargetHealth")){
    		condition = "TargetHealth";
    		examount = part.substring(13);
    	}else {
    		condition = "Time";
    		examount = part.substring(5);
    	}
    }
    
    //处理关于物品的条件
    private void setAboutItem(String part) {
    	String[] parts = part.split(":");
    	condition = parts[0];
    	item = Main.items.get(parts[1]);
    	examount = parts[2];
    }
    
    //设置关于生物群系的条件
    private void setAboutBiome(String part) {
    	String[] parts = part.split(":");
    	condition = parts[0];
    	tag = parts[1];
    }
    
    //设置关于概率的条件
    private void setAboutProbability(String part) {
    	condition = "Probability";
    	probability = part.split(":")[1];
    }
    
    //处理关于目标生物存在的条件
    private void setAboutEntity(String part) {
    	String[] parts = part.split(":");
    	condition = parts[0];
    	distance = parts[1];
    }
    
    //处理方程式的条件
    private void setAboutEquation(String part) {
    	String[] parts = part.split(":");
    	condition = parts[0];
    	
    	Util util = new Util();
		sign = util.getSign(parts[1]);
		equation = parts[1].split(util.getSign(sign));
    	
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
        	return compare(health , amount);
    	}
    	//检测时间
    	else if(condition.equalsIgnoreCase("Time")) {
    		long time = self.getWorld().getTime();
    		return compare(time , amount);
    		
    	}
    	//检测是否在空中
    	else if(condition.equalsIgnoreCase("OnAir")) {
    		return !self.isOnGround();
    	}
    	//检测物品消耗
    	else if(condition.equalsIgnoreCase("ItemConsuming")) {

    		PlayerInventory inv = self.getInventory();
    		if(hasItem(item , inv , amount)) {
    			
    			removeItem(item , inv , amount);
    			
    			return true;
    		}
    		return false;
    	}
    	//检测物品拥有
    	else if(condition.equalsIgnoreCase("ItemHas")) {

    		return hasItem(item , self.getInventory() , amount);
    	}
    	//检测奔跑
    	else if(condition.equalsIgnoreCase("Run")) {
    		return self.isSprinting();
    	}
    	//检测雷暴
    	else if(condition.equalsIgnoreCase("Storm")) {
    		return self.getWorld().hasStorm();
    	}
    	//检测生物群系
    	else if(condition.equalsIgnoreCase("Biome")) {
    		Location loc = self.getLocation();
    		return tag.equalsIgnoreCase(self.getWorld().getBiome(loc.getBlockX(), loc.getBlockZ()).name());
    	}
    	//概率触发
    	else if(condition.equalsIgnoreCase("Probability")) {
    		double pro = Main.util.getDoubleNumber(probability, self);
    		
    		return Math.random() <= pro;
    	}
    	//指向生物
    	else if(condition.equalsIgnoreCase("HasPointEntity")) {
    		
    		double distance = Main.util.getDoubleNumber(this.distance, self);
    		return new SkillUtil().getTargetEntity(self, distance) != null;
    	
    	}
    	//范围生物
    	else if(condition.equalsIgnoreCase("HasRaduisEntity")) {
    		
    		double distance = Main.util.getDoubleNumber(this.distance, self);
    		return !self.getNearbyEntities(distance, distance, distance).isEmpty();
    		
    	}
    	//算式比较
    	else if(condition.equalsIgnoreCase("Equation")) {
    		
    		double left = Main.util.getDoubleNumber(equation[0], self);
    		double right = Main.util.getDoubleNumber(equation[1], self);
    		
    		return compare(left , right);
    		
    	}
    	//属性点要求
    	else if(condition.equalsIgnoreCase("Attribute")) {
    		
    		if(target instanceof Player) {
    			int attr = OnlineData.getSPlayer((Player) target).getAttribute("Attribute."+equation[0]);
    			int num = Integer.parseInt(equation[1]);
    			return compare(attr,num);
    		}
    		
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
    
    private boolean compare(double left , double right) {
    	switch(sign) {
    	case -2:{
    		return left < right;
    	}
    	case -1:{
    		return left <= right;
    	}
    	case 1:{
    		return left >= right;
    	}
    	case 2:{
    		return left > right;
    	}
    	default:{
    		return left == right;
    	}
    	}
    }
    
}
