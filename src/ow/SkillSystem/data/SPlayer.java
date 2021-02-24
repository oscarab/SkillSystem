package ow.SkillSystem.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;

import ow.SkillSystem.Main;
import ow.SkillSystem.skills.Skill;
import ow.SkillSystem.skills.SkillSingleExecution;

public class SPlayer {
   private UUID uuid;
   
   //正开启的技能，对应冷却时间
   private HashMap<Skill,Integer> skills = new HashMap<>();
   
   /*可持续的标记单个技能执行，对应持续的时间
    * 若大于0为持续性
    * 若小于0为下次性
    */
   private HashMap<SkillSingleExecution,Integer>  executions = new HashMap<>();
   
   //按键所对应的技能
   private HashMap<Integer , Skill> keyBoard = new HashMap<>();
   
   //属性点
   private HashMap<String, Integer> attribute = new HashMap<>();
   
   public SPlayer(Player p) {
	   uuid = p.getUniqueId();
   }
   
   public Player getPlayer() {
	   return Bukkit.getPlayer(uuid);
   }
   
   //增加或设置属性点
   public void setAttribute(String name, int point) {
	   attribute.put(name, point);
   }
   
   //移除属性点
   public void removeAttribute(String name) {
	   if(attribute.get(name) != null) {
		   attribute.put(name, null);
	   }
   }
   
   //获取对应的属性点
   public int getAttribute(String name) {
	   return attribute.get(name) == null? 0 : attribute.get(name);
   }
   
   //判断属性点是否存在
   public boolean isAttribute(String name) {
	   return attribute.get(name) != null;
   }
   
   //设置技能冷却
   public void setCoolDown(Skill skill, int time) {
	   skills.put(skill, time);
   }
   
   //获取技能冷却
   public int getCoolDown(Skill skill) {
	   return skills.get(skill) == null? 0 : skills.get(skill);
   }
   
   //新添技能对应的按键
   public void addKeyBoardSetting(int key , Skill skill) {
	   keyBoard.put(key, skill);
   }
   
   //获取按键所对应的技能
   public Skill getKeyBoardSkill(int key) {
	   return keyBoard.get(key);
   }
   
   //获取技能对应的按键
   public int getKeyBoardSkill(Skill skill) {
	   Iterator<Integer> keys = keyBoard.keySet().iterator();
	   
	   while(keys.hasNext()) {
		   int key = keys.next();
		   Skill sk = keyBoard.get(key);
		   
		   if(skill.equals(sk)) {
			   return key;
		   }
	   }
	   
	   return -1;
   }
   
   //移除技能的绑定
   public void removeKeyBoardSkill(Skill skill) {
	   Iterator<Integer> keys = keyBoard.keySet().iterator();
	   
	   while(keys.hasNext()) {
		   int key = keys.next();
		   Skill sk = keyBoard.get(key);
		   
		   if(skill.equals(sk)) {
			   keyBoard.put(key, null);
		   }
	   }
   }
   
   //保存键盘情况与属性点
   public void saveData(){
	   
	   try {
		Main.handle.savePlayerYML(keyBoard, attribute , getPlayer());
	} catch (IOException e) {
		e.printStackTrace();
	}
	   
   }
   
   //让玩家执行技能
   public void setSkill(Skill skill) {
	   
	   Player player = getPlayer();
	   
	   if(!Main.ready) return;
	   
	   if(skill.getIsNeedPermission() && !player.hasPermission("SkillSystem."+skill.getName())) {
		   player.sendMessage("§4你没有使用此技能的权限！");
		   return;
	   }
	   
	   if(skill.getBanWorlds() != null && skill.getBanWorlds().contains(player.getWorld().getName())) {
		   player.sendMessage("§4本世界你不能使用这个技能！");
		   return;
	   }
	   
	   //检查是否在领地内
	   if(Main.Residence) {
		   Location loc = player.getLocation();
		   ClaimedResidence res = Residence.getInstance().getResidenceManager().getByLoc(loc);
		   
		   if(res != null) {
			   ResidencePermissions perms = res.getPermissions();
			   if(!perms.playerHas(player, Flags.pvp, true)) {
				   player.sendMessage("§4此处你不能使用技能！");
				   return;
			   }
		   }
		   
	   }
	   
	   //检查是否冷却完毕
	   if(!isSkillCooldown(skill)) {
		   skills.put(skill, skill.getCooldown());
		   
		   Main.util.runSkill(this, skill);
	   }else {
		   player.sendMessage(skill.getMessage().replace("%cooldown%", ""+skills.get(skill)));
	   }
	   
   }
   
   //技能是否在冷却
   public boolean isSkillCooldown (Skill skill) {
	   return skills.keySet().contains(skill);
   }
   
   //增添标记的技能条
   public void addExecution(SkillSingleExecution execution , int time) {
	   executions.put(execution, time);
   }
   
   //执行在时间内的标记技能条
   public void runExecution(String type) {
	   Iterator<SkillSingleExecution> it = executions.keySet().iterator();

	   while(it.hasNext()) {

		   SkillSingleExecution arg =  it.next();
		   String condition = arg.getCondition().getCondition();
		   Player player = getPlayer();
		   
		   if((type.equals("KILL") && condition.contains("Kill"))||
				   condition.contains("Attack")) {

			   arg.runWithoutCondition(player);
			   
			   //处理掉下次性的技能条
			   if(condition.contains("Next")) {
				   it.remove();
			   }

		   }
	   }
	   
   }
   
   //处理单条执行的倒计时问题
   public void handleExecutionTime() {
	   Iterator<SkillSingleExecution> it = executions.keySet().iterator();
	   
	   while(it.hasNext()) {
		   SkillSingleExecution arg = (SkillSingleExecution) it.next();
		   int time = executions.get(arg);
		   
		   if(arg.getCondition().getCondition().contains("Next")) {
			   continue;
		   }
		   
		   time--;
		   if(time > 0) {
			   executions.put(arg, time);
		   }else {
			   it.remove();
		   }
		   
	   }
	   
   }
   
   //处理技能的倒计时问题
   public void handleSkillTime() {
	   Iterator<Skill> it = skills.keySet().iterator();
	   
	   while(it.hasNext()) {
		   Skill arg = (Skill) it.next();
		   int time = skills.get(arg);
		   time--;
		   if(time > 0) {
			   skills.put(arg, time);
		   }else {
			   it.remove();
		   }
	   }
   }
   
}
