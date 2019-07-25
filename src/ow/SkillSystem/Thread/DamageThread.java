package ow.SkillSystem.Thread;

import ow.SkillSystem.data.OnlineData;

public class DamageThread extends Thread{
	
	public void run() {
		
		while(true) {
			OnlineData.setTime1();
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}

}
