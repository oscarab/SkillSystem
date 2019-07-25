package ow.SkillSystem.skills;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import ow.SkillSystem.Main;
import ow.SkillSystem.data.OnlineData;
import ow.SkillSystem.data.SPlayer;

public class SkillSingleExecution {     
	/*
	 * 技能单条执行示例
	 * 格式：  条件/目标/效果/执行持续时间
	 * None/Self/PotionEffect:Speed:20:1/0
	 * 无条件给自己加上速度药水效果等级1持续20秒
	 */
  private SkillCondition condition;
  private SkillTarget target;
  private double radius;
  private SkillEffect effect;
  private int duration = -1;
  
  public SkillSingleExecution(String arg) {
	  String[] args = arg.split("/");
	  condition = new SkillCondition(args[0]);
	  setTarget(args[1]);
	  effect = new SkillEffect(args[2]);
	  duration = Main.util.getIntNumber(args[3]);
	  
	  checkDuration();        //检查是否是可持续的技能执行
  }
  
  private void checkDuration() {
	  if(!Main.util.canHasDuration(condition, target, effect)) {
		  duration = -1;
	  }
  }
  
  //设置技能条的目标   例如  RaduisEntity:3.0
  private void setTarget(String part) {
	  if(part.contains(":")) {
		  radius = Main.util.getDoubleNumber(part);
		  target = SkillTarget.valueOf(part.split(":")[0]);
	  }else {
		  target = SkillTarget.valueOf(part);
	  }
  }
  
  public SkillCondition getCondition() {
	  return condition;
  }
  
  //获取技能条释放的目标
  public List<LivingEntity> getTarget(Player self) {
	  List<LivingEntity> entities = new ArrayList<>();
	  
	  if(target.equals(SkillTarget.Self)) {
		  entities.add(self);
	  }else if(target.equals(SkillTarget.RaduisEntity)) {
		  
		  for(Entity entity : self.getNearbyEntities(radius, radius, radius)) {
			  if(entity instanceof LivingEntity) {
				  entities.add((LivingEntity) entity);
			  }
		  }
		  
	  }else {
		  SkillUtil sutil = new SkillUtil();
		  LivingEntity entity = sutil.getTargetEntity(self);
		  if(entity != null) entities.add(entity);
	  }
	  
	  return entities;
  }
  
  /**
   * 开始执行技能
   * @param self 执行技能条的玩家
   */
  public void run(Player self) {
	  //技能条中的目标
	  List<LivingEntity> entities = getTarget(self);
	  //准心所指目标
	  LivingEntity entity = new SkillUtil().getTargetEntity(self);
	  
	  //判断是否满足马上执行条件
	  if( entities.size() > 0 && condition.getCondition().contains("Target") && entity != null && condition.check(self, entity)) {
		  //根据目标来执行效果
		  effect.run(entities , self , duration);
		  
	  }else if(entities.size() > 0 && condition.check(self, self)) {
		  effect.run(entities , self , duration);
		  
	  }//如果是攻击或击杀条件的，应该标记
	  else if(condition.isNeedSign()) {
		  SPlayer player = OnlineData.getSPlayer(self);

		  player.addExecution(this, duration);
		  OnlineData.players.put(self, player);
	  }
  }
  
  //强行执行，无视条件
  public void runWithoutCondition(Player self) {
	  List<LivingEntity> entities = getTarget(self);
	  
	  effect.run(entities , self ,duration);
  }
  
}
