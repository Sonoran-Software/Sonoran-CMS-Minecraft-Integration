package com.sonoransoftware.cmswhitelistplugin;

import jdk.javadoc.internal.doclets.formats.html.markup.Text;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public final class Cmswhitelistplugin extends JavaPlugin implements Listener {
    private FileConfiguration CONFIG = this.getConfig();
    @Override
    public void onEnable() {
        // Plugin startup logic
        CONFIG.addDefault("CommunityID", "");
        CONFIG.addDefault("CommunityAPIKey", "");
        CONFIG.addDefault("Identifier", "UUID");
        CONFIG.addDefault("ServerID", 1);
        CONFIG.options().copyDefaults(true);
        saveConfig();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        try {
            if (CONFIG.getString("Identifier").toLowerCase(Locale.ROOT).equals("username")) {
                uuid = player.getName();
            }
            URL url = new URL("https://api.sonorancms.com/servers/verify_whitelist");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            String jsonInputString = "{\"id\": \"" + CONFIG.getString("CommunityID") + "\", \"key\": \"" + CONFIG.getString("CommunityAPIKey") + "\", \"type\": \"VERIFY_WHITELIST\", \"data\": [{\"apiId\": \""+ uuid +"\",\"serverId\": " + CONFIG.getInt("ServerID") + "}]}";
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream());
            wr.writeBytes(jsonInputString);
            int responseStream = connection.getResponseCode();
            if (responseStream != 200) {
                player.kickPlayer("You are not whitelisted through Sonoran CMS.\nUUID: " + uuid + " Username: " + player.getName());
            }
        } catch(Exception e) {
            player.kickPlayer("Could not contact the Sonoran CMS server for whitelist. Please contact your server administrators or https://support.sonoransoftare.com");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
