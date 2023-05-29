package net.starly.whitelist.dispatcher;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;
import java.util.function.Consumer;

public class ChatInputDispatcher implements Listener {

    private static final Map<UUID, Consumer<String>> chatListeners = new HashMap<>();

    public static void attachListener(UUID uniqueId, Consumer<String> chatListener) {
        chatListeners.put(uniqueId, chatListener);
    }

    public static Consumer<String> getChatListener(UUID uniqueId) {
        return chatListeners.get(uniqueId);
    }

    public static List<UUID> getKeys() {
        return new ArrayList<>(chatListeners.keySet());
    }

    public static void removeChatListener(UUID uniqueId) {
        chatListeners.remove(uniqueId);
    }


    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        dispatch(event);
    }

    private void dispatch(AsyncPlayerChatEvent event) {
        Consumer<String > callback = chatListeners.remove(event.getPlayer().getUniqueId());
        if (callback != null) {
            event.setCancelled(true);
            callback.accept(event.getMessage());
        }
    }
}
