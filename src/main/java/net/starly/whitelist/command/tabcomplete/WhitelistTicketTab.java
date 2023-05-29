package net.starly.whitelist.command.tabcomplete;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WhitelistTicketTab implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        List<String> completions =  new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("starly.whitelist.reload")) completions.add("리로드");
            if (sender.hasPermission("starly.whitelist.set")) completions.add("설정");
        }

        return StringUtil.copyPartialMatches(args[args.length - 1], completions, new ArrayList<>());
    }
}
