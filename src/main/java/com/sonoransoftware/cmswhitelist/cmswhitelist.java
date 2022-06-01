package com.sonoransoftware.cmswhitelist;

import com.google.gson.Gson;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.network.NetworkRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("cmswhitelist")
public class cmswhitelist {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public cmswhitelist() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, OptionsHolder.COMMON_SPEC);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // some example code to dispatch IMC to another mod
    }

    private void processIMC(final InterModProcessEvent event) {
        // some example code to receive and process InterModComms from other mods

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // do something when the server starts

    }

    @SubscribeEvent
    public void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getPlayer();
        String uuid = player.getStringUUID();
        if (OptionsHolder.COMMON.Identifier.get().toLowerCase(Locale.ROOT).equals("username")) {
            uuid = player.getName().getString();
        }
        try {
            URL url = new URL("https://api.sonorancms.com/servers/verify_whitelist");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            String jsonInputString = "{\"id\": \""+ OptionsHolder.COMMON.CommunityID.get() +"\", \"key\": \"" + OptionsHolder.COMMON.CommunityAPIKey.get() + "\", \"type\": \"VERIFY_WHITELIST\", \"data\": [{\"apiId\": \""+ uuid +"\",\"serverId\": " + OptionsHolder.COMMON.ServerID.get() + "}]}";
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream());
            wr.writeBytes(jsonInputString);
            int responseStream = connection.getResponseCode();
            if (responseStream != 200) {
                player.connection.disconnect(new TextComponent("You are not whitelisted through Sonoran CMS.\nUUID: " + uuid + " Username: " + player.getName().getString()));
            } else {
                player.sendMessage(new TextComponent(uuid), player.getUUID());
            }
        } catch(Exception e) {
            player.connection.disconnect(new TextComponent("Could not contact the Sonoran CMS server for whitelist. Please contact your server administrators or https://support.sonoransoftare.com"));
        }
    }
}
