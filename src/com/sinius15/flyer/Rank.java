package com.sinius15.flyer;

/**
 * This is an object that represents an rank within the flying plugin. Every
 * Rank has a name, a fly-time and a cooldown. <br>
 * The name is a represantative name. This is specified within the configuration
 * file.<br>
 * The fly-time is the time(in seconds) the player can fly.<br>
 * Whe the fly-time is over, the cooldown(in seconds) kicks in. While this
 * cooldown is running, you can not fly.<br>
 * <br>
 * Evry rank has different fly-times and cooldowns.
 * 
 * @author Sinius15
 * 
 */
public class Rank {

	String name;
	int flyTime, cooldown;

	public Rank(String name, int flyTime, int cooldown) {
		this.name = name;
		this.flyTime = flyTime;
		this.cooldown = cooldown;
	}

	@Override
	public String toString() {
		return "Rank [name=" + name + ", flyTime=" + flyTime + ", cooldown="
				+ cooldown + "]";
	}

}
