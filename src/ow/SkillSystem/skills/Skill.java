package ow.SkillSystem.skills;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import ow.SkillSystem.Main;

public class Skill {
  private String name;         //技能名字
  private int cooldown;        //冷却时间
  private boolean needPermission = true;
  private String message;
  private boolean canKey = false;    //能否用按键触发
  private List<String> worlds = new ArrayList<>();      //禁止释放的世界
  
  private List<SkillSingleExecution> executions = new ArrayList<>();

  public Skill(String name , int cooldown , boolean np , String msg ,boolean cank ,List<String> worlds) {
	  this.name = name;
	  this.cooldown = cooldown;
	  needPermission = np;
	  message = msg;
	  canKey = cank;
	  this.worlds = worlds;
  }
  
  //导入单条技能执行
  public void setExecution(List<String> lists) {
	  for(String arg : lists) {
		  SkillSingleExecution execution = new SkillSingleExecution(arg);
		  executions.add(execution);
	  }
  }
  
  public int getCooldown() {
	  return cooldown;
  }
  
  //获取未冷却完毕的提示
  public String getMessage() {
	  return message;
  }
  
  public String getName() {
	  return name;
  }
  
  //是否可用键盘触发
  public boolean canUseKeyBoard() {
	  return canKey;
  }
  
  public boolean getIsNeedPermission() {
	  return needPermission;
  }
  
  public List<String> getBanWorlds(){
	  return worlds;
  }
  
  /**
   * 技能执行
   * @param user 技能使用者
   */
  public void runSkill(Player user , List<SkillSingleExecution> executions) {

	  if(executions == null) {
		  executions = this.executions;
	  }
	  
	  for(int i = 0 ; i < executions.size() ; i++) {
		  SkillSingleExecution execution = executions.get(i);
		  
		  //延迟
		  if(execution.getDelay() != 0) {
			  List<SkillSingleExecution> delayExecutions = new ArrayList<>();
			  delayExecutions.addAll(executions.subList(i+1, executions.size()));
			  
			  delayRun(delayExecutions, user , execution.getDelay());
			  return;
		  }
		  
		  executions.get(i).run(user);
	  }
  }
  
  private void delayRun(List<SkillSingleExecution> delayExecutions , Player user , int delay) {
	  Bukkit.getScheduler().runTaskLater(Main.plugin, new Runnable() {

		@Override
		public void run() {
			
			runSkill(user, delayExecutions);
			
		}
		  
	  }, delay);
  }
}
