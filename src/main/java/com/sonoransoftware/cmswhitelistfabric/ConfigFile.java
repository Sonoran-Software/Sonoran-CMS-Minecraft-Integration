package com.sonoransoftware.cmswhitelistfabric;

import com.oroarmor.config.Config;
import com.oroarmor.config.ConfigItem;
import com.oroarmor.config.ConfigItemGroup;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.util.List;

import static com.google.common.collect.ImmutableList.of;

public class ConfigFile extends Config {
    public static final ConfigItemGroup mainGroup = new ConfigGroupLevel1();

    public static final List<ConfigItemGroup> configs = of(mainGroup);

    public ConfigFile() {
        super(configs, new File(FabricLoader.getInstance().getConfigDir().toFile(), "sonorancms.json"), "cmswhitelistfabric");
    }

    public static class ConfigGroupLevel1 extends ConfigItemGroup {
        public static final ConfigItem<String> CommunityID = new ConfigItem<>("Community_ID", "", "This is your SonoranCMS' Community ID.");

        public static final ConfigItem<String> CommunityAPIKey = new ConfigItem<>("Community_API_Key", "", "This is your SonoranCMS' API key.");

        public static final ConfigItem<String> Identifier = new ConfigItem<>("Identifier", "UUID", "Which identifier to use, either UUID or Username. UUID is preferred.");

        public static final ConfigItem<Integer> ServerID = new ConfigItem<>("Server_ID", 1, "The server ID found in SonoranCMS that this whitelist should check against.");

        public ConfigGroupLevel1() {
            super(of(CommunityID, CommunityAPIKey, Identifier, ServerID), "CMS_Settings");
        }
    }
}
