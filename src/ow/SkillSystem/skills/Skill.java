package ow.SkillSystem.skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import ow.SkillSystem.Main;

public class Skill {
  private String name;         //技能名字
  private List<String> description;         //技能描述
  private int cooldown;        //冷却时间
  private boolean needPermission = true;
  private String message;             //冷却未完成的提示语
  private boolean canKey = false;    //能否用按键触发
  private List<String> worlds = new ArrayList<>();      //禁止释放的世界
  
  //技能包管理
  private int packet = 0;
  private HashMap<Integer,List<SkillSingleExecution>> executions = new HashMap<>();

  public Skill(String name , int cooldown , boolean np , String msg ,boolean cank ,List<String> worlds , List<String> description) {
	  this.name = name;
	  this.cooldown = cooldown;
	  needPermission = np;
	  message = msg;
	  canKey = cank;
	  this.worlds = worlds;
	  this.description = description;
  }
  
  //导入单条技能执行
  public void setExecution(List<String> lists) {
	  packet ++;
	  List<SkillSingleExecution> exs = new ArrayList<>();
	  for(String arg : lists) {
		  SkillSingleExecution execution = new SkillSingleExecution(arg);
		  exs.add(execution);
	  }
	  executions.put(packet, exs);
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
  
  public List<String> getDescription(Player player) {
	  List<String> res = new ArrayList<>();
	  res.addAll(description);
	  for(int i = 0; i < res.size(); i++) {
		  res.set(i, Main.util.replaceAPI(res.get(i), player));
	  }
	  return res;
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
		  executions = this.executions.get(1);
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
		  
		  //执行技能条 ，检测返回值决定是否跳转
		  int isgo = execution.run(user);
		  if(isgo > 0) {
			  List<SkillSingleExecution> otherExecutions = new ArrayList<>();
			  List<SkillSingleExecution> gotoexecutions;
			  
			  if(isgo == 1) {
				  gotoexecutions = this.executions.get(execution.getSkillEffect().getGotoPacket());
				  otherExecutions.addAll(gotoexecutions.subList(execution.getSkillEffect().getGotoLine()-1, gotoexecutions.size()));
			  }else {
				  gotoexecutions = this.executions.get(execution.getSkilloEffect().getGotoPacket());
				  otherExecutions.addAll(gotoexecutions.subList(execution.getSkilloEffect().getGotoLine()-1, gotoexecutions.size()));
			  }

			  runSkill(user, otherExecutions);
			  return;
		  }	
		  
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
