package de.novusmc.bedwars.phase;

import de.novusmc.bedwars.BedWars;
import lombok.Getter;
import org.bukkit.Bukkit;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public abstract class GamePhase implements Runnable {

    @Getter
    protected GamePhaseHandler handler;

    @Getter
    protected int task;

    @Getter
    protected long startTime;

    public GamePhase(GamePhaseHandler handler) {
        this.handler = handler;
        this.startTime = System.currentTimeMillis();
    }

    public void start() {
        this.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(BedWars.getInstance(), this, 0, 20);
    }

    public void end() {
        Bukkit.getScheduler().cancelTask(task);
    }

    public abstract Type getType();

    public enum Type {
        LOBBY, INGAME, ENDING
    }

}
