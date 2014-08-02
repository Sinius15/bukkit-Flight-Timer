package com.sinius15.flyer;

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
