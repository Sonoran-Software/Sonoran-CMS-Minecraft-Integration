package com.sonoransoftware.cmswhitelist;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class OptionsHolder
{
    public static class Common
    {

        public final ForgeConfigSpec.ConfigValue<String> CommunityID;
        public final ForgeConfigSpec.ConfigValue<String> CommunityAPIKey;
        public final ForgeConfigSpec.ConfigValue<String> Identifier;
        public final ForgeConfigSpec.ConfigValue<Integer> ServerID;


        public Common(ForgeConfigSpec.Builder builder)
        {
            builder.push("category1");
            this.CommunityID = builder.comment("This is your SonoranCMS' Community ID.")
                    .worldRestart().define("Community ID", "");
            this.CommunityAPIKey = builder.comment("This is your SonoranCMS' API key.")
                    .define("Community API Key", "");
            this.Identifier = builder.comment("Which identifier to use, either UUID or Username. UUID is preferred.").define("Identifier Type", "UUID");
            this.ServerID = builder.comment("The server ID found in SonoranCMS that this whitelist should check against.").define("Server ID", 1);
            builder.pop();
        }
    }

    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static //constructor
    {
        Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = commonSpecPair.getLeft();
        COMMON_SPEC = commonSpecPair.getRight();
    }
}
