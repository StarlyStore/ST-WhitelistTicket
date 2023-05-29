package net.starly.whitelist.util;

import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.UUID;

public class PlayerUtil {

    public static UUID getPlayerUUID(String playerName) {
        String uuid = "";
        try (
                InputStream urlStream = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName).openStream();
                Scanner scanner = new Scanner(urlStream);
        ) {
            String response = scanner.useDelimiter("\\A").next();
            uuid = response.substring(response.indexOf("id") + 7, response.indexOf("id") + 39);
            uuid = uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" + uuid.substring(20);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return UUID.fromString(uuid);
    }

    public static boolean hasMinecraftAccount(String username) {
        try (
                InputStream urlStream = new URL("https://api.mojang.com/users/profiles/minecraft/" + username).openStream();
                Scanner scanner = new Scanner(urlStream);
        ) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
