package net.shangkostudio.synccraft;

import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import net.minecraft.world.level.storage.LevelResource;

@Mod(SyncCraft.MOD_ID)
public class SyncCraft {
    public static final String MOD_ID = "synccraft";
    private static final Logger LOGGER = LogUtils.getLogger();

    public SyncCraft() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new PlayerDataHandler());

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Initialization code here
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Client-specific setup code here
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class PlayerDataHandler {

        @SubscribeEvent
        public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
            if (!Config.syncPlayerData.get()) return;
            ServerPlayer player = (ServerPlayer) event.getEntity();
            Path configPath = Paths.get("config/SyncCraftSavedData");

            try {
                VerifyFileHandler.ensureDirectoryExists(configPath);
                LoadDataHandler.loadPlayerData(player, configPath);
                ApplyDataHandler.syncWorldData(player.getServer(), configPath, true);
            } catch (IOException e) {
                LOGGER.error("Error during player login", e);
            }
        }

        @SubscribeEvent
        public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
            if (!Config.syncPlayerData.get()) return;
            ServerPlayer player = (ServerPlayer) event.getEntity();
            Path configPath = Paths.get("config/SyncCraftSavedData");

            try {
                VerifyFileHandler.ensureDirectoryExists(configPath);
                SaveDataHandler.savePlayerData(player, configPath);
                ApplyDataHandler.syncWorldData(player.getServer(), configPath, false);
            } catch (IOException e) {
                LOGGER.error("Error during player logout", e);
            }
        }
    }

}
