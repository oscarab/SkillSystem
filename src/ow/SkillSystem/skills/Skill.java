package ow.SkillSystem.skills;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class Skill {
  private String name;         //技能名字
  private int cooldown;        //冷却时间
  private boolean needPermission = true;
  private String message;
  private boolean canKey = false;    //能否用按键触发
  
  private List<SkillSingleExecution> executions = new ArrayList<>();

  public Skill(String name , int cooldown , boolean np , String msg ,boolean cank) {
	  this.name = name;
	  this.cooldown = cooldown;
	  needPermission = np;
	  message = msg;
	  canKey = cank;
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
  
  /**
   * 技能执行
   * @param user 技能使用者
   */
  public void run(Player user) {
	  
	  for(SkillSingleExecution execution : executions) {
		  execution.run(user);
	  }

  }
}
