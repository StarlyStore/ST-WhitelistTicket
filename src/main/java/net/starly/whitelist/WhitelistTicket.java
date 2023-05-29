package net.starly.whitelist;

import net.starly.core.bstats.Metrics;
import net.starly.whitelist.command.WhitelistTicketCmd;
import net.starly.whitelist.command.tabcomplete.WhitelistTicketTab;
import net.starly.whitelist.dispatcher.ChatInputDispatcher;
import net.starly.whitelist.dispatcher.PlayerQuitDispatcher;
import net.starly.whitelist.listener.PlayerInteractListener;
import net.starly.whitelist.message.MessageLoader;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.function.Consumer;

public class WhitelistTicket extends JavaPlugin {

    private static WhitelistTicket instance;
    public static WhitelistTicket getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        /* DEPENDENCY
         ──────────────────────────────────────────────────────────────────────────────────────────────────────────────── */
        if (!isPluginEnabled("ST-Core")) {
            getServer().getLogger().warning("[" + getName() + "] ST-Core 플러그인이 적용되지 않았습니다! 플러그인을 비활성화합니다.");
            getServer().getLogger().warning("[" + getName() + "] 다운로드 링크 : §fhttp://starly.kr/");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        /* SETUP
         ──────────────────────────────────────────────────────────────────────────────────────────────────────────────── */
        new Metrics(this, 12345); // TODO: 수정

        /* CONFIG
         ──────────────────────────────────────────────────────────────────────────────────────────────────────────────── */
        File messageConfigFile = new File(getDataFolder(), "message.yml");
        if (!messageConfigFile.exists()) saveResource("message.yml", false);
        MessageLoader.load(YamlConfiguration.loadConfiguration(messageConfigFile));

        /* COMMAND
         ──────────────────────────────────────────────────────────────────────────────────────────────────────────────── */
        PluginCommand whitelistTicketSetCommand = getServer().getPluginCommand("whitelist-ticket");
        whitelistTicketSetCommand.setExecutor(new WhitelistTicketCmd());
        whitelistTicketSetCommand.setTabCompleter(new WhitelistTicketTab());

        /* LISTENER
         ──────────────────────────────────────────────────────────────────────────────────────────────────────────────── */
        getServer().getPluginManager().registerEvents(new ChatInputDispatcher(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitDispatcher(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
    }

    @Override
    public void onDisable() {
        PlayerQuitDispatcher.getKeys().forEach(uniqueId -> {
            Player player = getServer().getPlayer(uniqueId);
            PlayerQuitDispatcher.getQuitListener(uniqueId).accept(player);
        });
    }

    private boolean isPluginEnabled(String name) {
        Plugin plugin = getServer().getPluginManager().getPlugin(name);
        return plugin != null && plugin.isEnabled();
    }
}
