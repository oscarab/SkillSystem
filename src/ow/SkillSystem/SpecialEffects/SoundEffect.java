package ow.SkillSystem.SpecialEffects;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;

public class SoundEffect {
	private Sound sound;
	private float volume;
	private float pitch;
	
	public SoundEffect(String name , float volume , float pitch) {
		sound = Sound.valueOf(name);
		this.volume =volume;
		this.pitch = pitch;
	}
	
	public void play(World world , Location location) {
		world.playSound(location, sound, volume, pitch);
	}

}
