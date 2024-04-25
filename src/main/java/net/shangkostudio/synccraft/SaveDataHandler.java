package net.shangkostudio.synccraft;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

public class SaveDataHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void savePlayerData(ServerPlayer player, Path configPath) throws IOException {
        // Save individual player data
        saveIndividualPlayerData(player, configPath);

        // Save all world data files into "worldData" folder
        saveWorldData(player, configPath);
    }

    private static void saveIndividualPlayerData(ServerPlayer player, Path configPath) throws IOException {
        Path sourceFilePath = player.server.getWorldPath(net.minecraft.world.level.storage.LevelResource.PLAYER_DATA_DIR).resolve(player.getStringUUID() + ".dat");
        Files.createDirectories(configPath);  // Ensure the directory exists
        Path targetFilePath = configPath.resolve(player.getStringUUID() + ".dat");

        if (Files.exists(sourceFilePath)) {
            Files.copy(sourceFilePath, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
            LOGGER.info("Player data file copied to config for " + player.getName().getString());
        } else {
            LOGGER.error("No player data file found to copy for " + player.getName().getString());
        }
    }

    private static void saveWorldData(ServerPlayer player, Path configPath) throws IOException {
        Path sourceDirectory = player.server.getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT).resolve("data");
        Path targetDirectory = configPath.resolve("worldData");

        Files.createDirectories(targetDirectory);  // Ensure the directory exists

        if (Files.exists(sourceDirectory) && Files.isDirectory(sourceDirectory)) {
            Files.list(sourceDirectory).forEach(sourceFile -> {
                try {
                    Path targetFile = targetDirectory.resolve(sourceFile.getFileName());
                    Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    LOGGER.info("World data file " + sourceFile.getFileName() + " copied to " + targetFile);
                } catch (IOException e) {
                    LOGGER.error("Failed to copy world data file: " + sourceFile.getFileName(), e);
                }
            });
        } else {
            LOGGER.error("World data directory does not exist or is not a directory.");
        }
    }
}
