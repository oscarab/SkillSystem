package ow.SkillSystem.skills;

import java.util.List;

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
  private SkillEffect effect;
  
  private int duration = 0;
  private String exduration = "0";
  
  //技能延迟效果
  private int delay = 0;
  
  //条件未满足时执行的效果
  private SkillEffect oeffect;
  
  public SkillSingleExecution(String arg) {
	  //特殊技能条，延迟
	  if(arg.startsWith("Delay")) {
		  delay = Integer.parseInt(arg.split(":")[1]);
		  return;
	  }
	  
	  //初始化技能条详细信息
	  String[] args = arg.split("#");
	  condition = new SkillCondition(args[0]);
	  target = new SkillTarget(args[1]);
	  effect = new SkillEffect(args[2]);
	  exduration = args[3];
	  if(args.length > 4) {oeffect = new SkillEffect(args[4]);}
	  
	//检查是否是可持续的技能执行
	  if(!Main.util.canHasDuration(condition, target, effect)) {
		  duration = -1;
	  }

  }
  
  public SkillCondition getCondition() {
	  return condition;
  }
  public SkillEffect getSkillEffect() {
	  return effect;
  }
  public SkillEffect getSkilloEffect() {
	  return oeffect;
  }
  
  public int getDelay() {
	  return delay;
  }
  
  /**
   * 开始执行技能
   * @param self 执行技能条的玩家
   */
  public int run(Player self) {
	  //技能条中的目标
	  List<LivingEntity> entities = target.getTarget(self);
	  
	  //准心所指目标
	  LivingEntity entity = new SkillUtil().getTargetEntity(self, 3.0);
	  duration = Main.util.getIntNumber(exduration, self);
	  
	  //判断是否满足马上执行条件
	  if(condition.isNeedSign()) {
		  
		  SPlayer player = OnlineData.getSPlayer(self);

		  player.addExecution(this, duration);
		  OnlineData.players.put(self.getUniqueId(), player);
		  
	  }else {
		  
		  if( entities.size() > 0 && condition.getCondition().contains("Target") && entity != null) {
			  //若条件中需要判断目标的信息
			  
			  if(condition.check(self, entity)) {
				  
				  //涉及到跳转效果
				  if(effect.getEffect().equalsIgnoreCase("Goto")) {
					  return 1;
				  }
				  effect.run(entities , self , duration);
				  
			  }else {
				  //条件不满足执行后续操作
				  
				  if(oeffect != null) {
					  if(oeffect.getEffect().equalsIgnoreCase("Goto")) {return 2;}
					  oeffect.run(entities , self , duration);
				  }
				  
			  }
			  
		  }else{
			  
			  if(condition.check(self, self) && entities.size() > 0) {
				  
				  //涉及到跳转效果
				  if(effect.getEffect().equalsIgnoreCase("Goto")) {
					  return 1;
				  }
				  effect.run(entities , self , duration);
				  
			  }else {
				  
				  //条件不满足执行后续操作
				  if(oeffect != null) {
					  if(oeffect.getEffect().equalsIgnoreCase("Goto")) {return 2;}
					  oeffect.run(entities , self , duration);
				  }
				  
			  }
			  
		  }
		  
	  }

	  
	  return 0;
  }
  
  //强行执行，无视条件
  public void runWithoutCondition(Player self) {
	  List<LivingEntity> entities = target.getTarget(self);
	  
	  effect.run(entities , self ,duration);
  }
  
}
