package tronka.seedsplitter;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class SeedSplitter implements ModInitializer {

    private static final Map<String, Long> seedMap = new HashMap<>();
    @Override
    public void onInitialize() {
        Path configFile = FabricLoader.getInstance().getConfigDir().resolve("seed-splitter.txt");
        if (!configFile.toFile().exists()) {
            try {
                Files.createDirectories(FabricLoader.getInstance().getConfigDir());

                Files.writeString(configFile, "minecraft:the_end 0\nminecraft:the_nether 0");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            throw new RuntimeException("Would you like to edit the seed splitter config?");
        }
        try {
            for (String line : Files.readString(configFile).split("\n")) {
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split(" ");
                if (parts.length != 2) {
                    throw new RuntimeException("Invalid seed-splitter config");
                }
                long seed = Long.parseLong(parts[1]);
                seedMap.put(parts[0], seed);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static long getSeed(MinecraftServer server, ResourceKey<Level> registryKey) {
        String key = registryKey.identifier().toString();
        if (seedMap.containsKey(key)) {
            return seedMap.get(key);
        }
        return server.getWorldGenSettings().options().seed();
    }
}
