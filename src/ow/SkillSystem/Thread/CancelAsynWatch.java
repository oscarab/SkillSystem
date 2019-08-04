package ow.SkillSystem.Thread;

import org.spigotmc.AsyncCatcher;

public class CancelAsynWatch extends Thread{
	
	public void run() {
		
		while (true) {
			
            try {
              
              while(!AsyncCatcher.enabled) {
                  Thread.sleep(6000);
              }
              
              AsyncCatcher.enabled = false;
              
            }
            catch (InterruptedException e) {
              e.printStackTrace();
            } 
          }
		
	}

}
