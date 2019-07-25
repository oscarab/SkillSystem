package ow.SkillSystem.Thread;

import ow.SkillSystem.data.OnlineData;

public class DamagedThread extends Thread{
	
	public void run() {
		
		while(true) {
			
			OnlineData.setTime2();
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}

}
