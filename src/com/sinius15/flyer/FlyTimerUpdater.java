package com.sinius15.flyer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FlyTimerUpdater implements Runnable {

	@Override
	public void run() {

		for (Player p : Bukkit.getOnlinePlayers()) {
			PlayerState state = FlyTimer.getState(p);
			if(state == null){
				FlyTimer.states.put(p.getUniqueId(), new PlayerState(p));
				state = FlyTimer.getState(p);
			}
			state.update();
		}
	}

}
