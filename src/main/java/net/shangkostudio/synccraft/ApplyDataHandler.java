package net.shangkostudio.synccraft;

import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;

public class ApplyDataHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String[] worldSpecificFiles = {
            "blueprint_storage.dat", "endofherobrine.dat", "irons_spellbooks.dat", "JMPlayerSettings.dat", "sophisticatedbackpacks.dat"
    };

    public static void syncWorldData(MinecraftServer server, Path configPath, boolean isLogin) {
        try {
            Path worldDataPath = server.getWorldPath(LevelResource.ROOT).resolve("data");
            for (String fileName : worldSpecificFiles) {
                Path configFilePath = configPath.resolve(fileName);
                Path worldFilePath = worldDataPath.resolve(fileName);

                if (isLogin && Files.exists(configFilePath)) {
                    Files.copy(configFilePath, worldFilePath, StandardCopyOption.REPLACE_EXISTING);
                    LOGGER.info("Restored " + fileName + " from config to world");
                } else if (!isLogin && Files.exists(worldFilePath)) {
                    Files.copy(worldFilePath, configFilePath, StandardCopyOption.REPLACE_EXISTING);
                    LOGGER.info("Backed up " + fileName + " from world to config");
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to sync world-specific data", e);
        }
    }
}
