package net.starly.whitelist.message;

import net.starly.core.util.PreCondition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;

public class MessageLoader {

    public static void load(FileConfiguration config) {
        MessageContext.getInstance().clear();
        Arrays.stream(MessageType.values()).forEach(type -> loadMessageSection(config.getConfigurationSection(type.getPath()), type));
    }

    private static void loadMessageSection(ConfigurationSection section, MessageType type) {
        PreCondition.nonNull(section, "메세지를 로드할 수 없습니다. : " + type.name());

        MessageContext msgContext = MessageContext.getInstance();
        section.getKeys(true).forEach(key -> {
            String message;
            if (section.isString(key)) message = section.getString(key).replace("\\n", "\n");
            else return;

            msgContext.setMessage(type, key, message);
        });
    }
}