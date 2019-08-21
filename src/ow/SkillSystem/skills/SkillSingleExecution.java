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
	 * 格式：  条件#目标#效果#执行持续时间
	 * None#Self#PotionEffect:Speed:20:1#0
	 * 无条件给自己加上速度药水效果等级1持续20秒
	 */
  private SkillCondition condition;
  private SkillTarget target;
  
  //半成品范围数值 ，尚未处理
  private String exradius = "0";
  
  private SkillEffect effect;
  
  private int duration = 0;
  private String exduration = "0";
  
  //技能延迟效果
  private int delay = 0;
  
  //技能跳转结束
  private boolean stop = false;
  
  public SkillSingleExecution(String arg) {
	  //特殊技能条，延迟
	  if(arg.startsWith("Delay")) {
		  delay = Integer.parseInt(arg.split(":")[1]);
		  return;
	  }
	  
	  //初始化技能条详细信息
	  String[] args = arg.split("#");
	  condition = new SkillCondition(args[0]);
	  setTarget(args[1]);
	  effect = new SkillEffect(args[2]);
	  exduration = args[3];
	  
	//检查是否是可持续的技能执行
	  if(!Main.util.canHasDuration(condition, target, effect)) {
		  duration = -1;
	  }        
	  
	  //标记为停止效果
	  if(effect.getEffect().equalsIgnoreCase("Stop")) {
		  stop = true;
	  }
  }
  
  //设置技能条的目标   例如  RaduisEntity:3.0 PointEntity:3.0
  private void setTarget(String part) {
	  if(part.contains(":")) {
		  exradius = part.split(":")[1];
		  target = SkillTarget.valueOf(part.split(":")[0]);
	  }else {
		  target = SkillTarget.valueOf(part);
	  }
  }
  
  public SkillCondition getCondition() {
	  return condition;
  }
  
  public int getDelay() {
	  return delay;
  }
  
  public boolean isStop() {
	  return stop;
  }
  
  //获取技能条释放的目标
  public List<LivingEntity> getTarget(Player self) {
	  List<LivingEntity> entities = new ArrayList<>();
	  
	  if(target.equals(SkillTarget.Self)) {
		  entities.add(self);
	  }else if(target.equals(SkillTarget.RaduisEntity)) {
		  
		  double radius = Main.util.getDoubleNumber(exradius, self);
		  for(Entity entity : self.getNearbyEntities(radius, radius, radius)) {
			  if(entity instanceof LivingEntity) {
				  entities.add((LivingEntity) entity);
			  }
		  }
		  
	  }else {
		  SkillUtil sutil = new SkillUtil();
		  double distance = Main.util.getDoubleNumber(exradius, self);
		  LivingEntity entity = sutil.getTargetEntity(self, distance);
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
	  double distance = Main.util.getDoubleNumber(exradius, self);
	  LivingEntity entity = new SkillUtil().getTargetEntity(self, distance);
	  duration = Main.util.getIntNumber(exduration, self);
	  
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
		  OnlineData.players.put(self.getUniqueId(), player);
	  }
  }
  
  //强行执行，无视条件
  public void runWithoutCondition(Player self) {
	  List<LivingEntity> entities = getTarget(self);
	  
	  effect.run(entities , self ,duration);
  }
  
}
