package dev.localcape;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.PlayerSkin;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

public final class LocalCapeClient implements ClientModInitializer {
    public static final String MOD_ID = "localcape";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);

    private static KeyMapping toggleKey;
    private static PlayerSkin.Patch cachedPatch;
    private static String cachedName;

    @Override
    public void onInitializeClient() {
        CapeConfig.load();
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.localcape.toggle", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,
                KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "main"))));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            rememberCurrentAccount(mc);
            while (toggleKey.consumeClick()) {
                CapeConfig c = CapeConfig.get();
                c.enabled = !c.enabled;
                CapeConfig.save();
                if (mc.player != null)
                    mc.player.displayClientMessage(Component.literal("Local cape: " + (c.enabled ? "ON" : "OFF")), true);
            }
        });
    }

    /** Remember the UUID of whichever account is currently logged in (account-agnostic by design). */
    private static void rememberCurrentAccount(Minecraft mc) {
        UUID id = mc.getUser().getProfileId();
        if (id != null && CapeConfig.get().myUuids.add(id.toString())) CapeConfig.save();
    }

    /** True for my live player and for any recorded/replayed entity that carries one of my account UUIDs. */
    public static boolean isMine(Avatar avatar) {
        if (!(avatar instanceof Entity e)) return false;
        Minecraft mc = Minecraft.getInstance();
        if (e == mc.player) return true;
        return CapeConfig.get().myUuids.contains(e.getUUID().toString());
    }

    public static PlayerSkin.Patch capePatch() {
        String name = CapeConfig.get().textureName;
        if (cachedPatch == null || !name.equals(cachedName)) {
            Identifier id = Identifier.fromNamespaceAndPath(MOD_ID, name); // -> textures/<name>.png
            cachedPatch = PlayerSkin.Patch.create(Optional.empty(),
                    Optional.of(new net.minecraft.core.ClientAsset.ResourceTexture(id)),
                    Optional.empty(), Optional.empty());
            cachedName = name;
        }
        return cachedPatch;
    }
}
