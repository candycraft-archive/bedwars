package de.novusmc.bedwars.phase;

import lombok.Getter;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class GamePhaseHandler {

    @Getter
    private static GamePhaseHandler instance;

    @Getter
    private GamePhase activePhase;

    @Getter
    private GamePhase.Type activePhaseType;

    public GamePhaseHandler() {
        instance = this;
    }

    public <T extends GamePhase> T startPhase(Class<? extends T> gamePhaseClass) {
        try {
            T phase = gamePhaseClass.getConstructor(GamePhaseHandler.class).newInstance(this);
            startPhase(phase);
            return phase;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T extends GamePhase> void startPhase(T gamePhase) {
        this.activePhase = gamePhase;
        this.activePhaseType = gamePhase.getType();
        this.activePhase.start();
    }

}
