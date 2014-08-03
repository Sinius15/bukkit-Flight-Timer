package com.sinius15.flyer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FlyTimerUpdater implements Runnable {

	/**
	 * A function that should be called every second. This checks if all online
	 * players have a {@link PlayerState}. If this is not the case than it will
	 * create the {@link PlayerState}. Next it will update that
	 * {@link PlayerState} by calling PlayerState.update().
	 */

	@Override
	public void run() {

		for (Player p : Bukkit.getOnlinePlayers()) {
			PlayerState state = FlyTimer.getState(p);
			if (state == null) {
				FlyTimer.states.put(p.getUniqueId(), new PlayerState(p));
				state = FlyTimer.getState(p);
			}
			state.update();
		}
	}

}
