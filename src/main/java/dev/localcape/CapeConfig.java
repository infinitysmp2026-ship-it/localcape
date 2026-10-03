package dev.localcape;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.*;
import java.util.*;

/** Plain JSON config: .minecraft/config/localcape.json */
public final class CapeConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("localcape.json");
    private static CapeConfig instance = new CapeConfig();

    public boolean enabled = true;
    public boolean physics = true;
    /** 1.0 = vanilla length. Clamped to 0.5 .. 2.0 */
    public float capeLength = 1.0f;
    /** Texture used for the cape: assets/localcape/textures/<name>.png (override via resource pack). */
    public String textureName = "cape";
    /**
     * UUIDs treated as "me". Filled automatically with every account you ever launch with, so recordings
     * made under any switched account still show the cape. You may also add UUIDs by hand.
     */
    public Set<String> myUuids = new LinkedHashSet<>();

    public static CapeConfig get() { return instance; }

    public static void load() {
        try {
            if (Files.exists(FILE)) {
                CapeConfig c = GSON.fromJson(Files.readString(FILE), CapeConfig.class);
                if (c != null) instance = c;
            }
        } catch (Exception e) { LocalCapeClient.LOG.warn("Could not read config", e); }
        if (instance.myUuids == null) instance.myUuids = new LinkedHashSet<>();
        instance.capeLength = Math.max(0.5f, Math.min(2.0f, instance.capeLength));
        save();
    }

    public static void save() {
        try { Files.writeString(FILE, GSON.toJson(instance)); }
        catch (Exception e) { LocalCapeClient.LOG.warn("Could not save config", e); }
    }
}
