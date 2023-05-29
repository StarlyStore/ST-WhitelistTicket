package net.starly.whitelist.dispatcher;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.function.Consumer;

public class PlayerQuitDispatcher implements Listener {

    private static final Map<UUID, Consumer<Player>> quitListeners = new HashMap<>();

    public static void attachListener(UUID uniqueId, Consumer<Player> quitListener) {
        quitListeners.put(uniqueId, quitListener);
    }

    public static Consumer<Player> getQuitListener(UUID uniqueId) {
        return quitListeners.get(uniqueId);
    }

    public static List<UUID> getKeys() {
        return new ArrayList<>(quitListeners.keySet());
    }

    public static void removeQuitListener(UUID uniqueId) {
        quitListeners.remove(uniqueId);
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        dispatch(event);
    }

    private void dispatch(PlayerQuitEvent event) {
        Consumer<Player> callback = quitListeners.remove(event.getPlayer().getUniqueId());
        if (callback != null) {
            callback.accept(event.getPlayer());
        }
    }
}
