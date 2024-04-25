package net.shangkostudio.synccraft;

// File: LoadDataHandler.java
import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LoadDataHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void loadPlayerData(ServerPlayer player, Path configPath) throws IOException {
        Path filePath = configPath.resolve(player.getStringUUID() + ".dat");
        if (Files.exists(filePath)) {
            CompoundTag playerData = NbtIo.readCompressed(new FileInputStream(filePath.toFile()));
            player.load(playerData);
            LOGGER.info("Player data applied from config for " + player.getName().getString());
            refreshPlayerState(player);
        }
    }

    private static void refreshPlayerState(ServerPlayer player) {
        player.connection.send(new ClientboundSetHealthPacket(player.getHealth(), player.getFoodData().getFoodLevel(), player.getFoodData().getSaturationLevel()));

        // Collect all syncable attributes into a list and send them
        player.getAttributes().getSyncableAttributes().forEach(attribute -> {
            player.connection.send(new ClientboundUpdateAttributesPacket(player.getId(), java.util.Collections.singletonList(attribute)));
        });

        player.connection.send(new ClientboundPlayerAbilitiesPacket(player.getAbilities()));
        player.server.getPlayerList().sendAllPlayerInfo(player); // This ensures all visual aspects are updated client-side.
        LOGGER.info("Player state refreshed for " + player.getName().getString());
    }
}
