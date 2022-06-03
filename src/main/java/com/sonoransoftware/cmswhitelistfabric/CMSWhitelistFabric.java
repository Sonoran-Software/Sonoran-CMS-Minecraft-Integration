package com.sonoransoftware.cmswhitelistfabric;

import com.oroarmor.config.Config;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.C2SPlayChannelEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.network.C2SPacketTypeCallback;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class CMSWhitelistFabric implements DedicatedServerModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("cmswhitelistfabric");
	public static ConfigFile CONFIG = new ConfigFile();

	@Override
	public void onInitializeServer() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution
		CONFIG.readConfigFromFile();
		CONFIG.saveConfigToFile();

		PlayerJoinCallback.EVENT.register((player, server) -> {
			String uuid = player.getUuid().toString();
			try {
				if (((String) CONFIG.getConfigs().get(0).getConfigs().get(2).getValue()).toLowerCase(Locale.ROOT).equals("username")) {
					uuid = player.getName().getString();
				}
				URL url = new URL("https://api.sonorancms.com/servers/verify_whitelist");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setDoOutput(true);
				String jsonInputString = "{\"id\": \"" + CONFIG.getConfigs().get(0).getConfigs().get(0).getValue().toString() +"\", \"key\": \"" + CONFIG.getConfigs().get(0).getConfigs().get(1).getValue().toString() + "\", \"type\": \"VERIFY_WHITELIST\", \"data\": [{\"apiId\": \""+ uuid +"\",\"serverId\": " + CONFIG.getConfigs().get(0).getConfigs().get(3).getValue().toString() + "}]}";
				DataOutputStream wr = new DataOutputStream (
						connection.getOutputStream());
				wr.writeBytes(jsonInputString);
				int responseStream = connection.getResponseCode();
				if (responseStream != 200) {
					player.networkHandler.disconnect(Text.of("You are not whitelisted through Sonoran CMS.\nUUID: " + uuid + " Username: " + player.getName().getString()));
				}
			} catch(Exception e) {
				player.networkHandler.disconnect(Text.of("Could not contact the Sonoran CMS server for whitelist. Please contact your server administrators or https://support.sonoransoftare.com"));
			}
		});
	}
}
