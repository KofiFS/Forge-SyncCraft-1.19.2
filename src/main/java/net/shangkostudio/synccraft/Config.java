package net.shangkostudio.synccraft;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.BooleanValue syncPlayerData;
    public static final ForgeConfigSpec SPEC;

    static {
        syncPlayerData = BUILDER
                .comment("Enable synchronization of player data files.")
                .define("syncPlayerData", true);

        SPEC = BUILDER.build(); // Finalize the configuration spec here
    }
}
