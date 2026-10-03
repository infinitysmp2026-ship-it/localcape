package dev.localcape.mixin;

import dev.localcape.CapeConfig;
import dev.localcape.LocalCapeClient;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Both live players and Flashback's replayed players are rendered by AvatarRenderer, so patching the
 * render state's skin here covers normal play and replay playback alike. Cape physics values
 * (capeFlap/capeLean) are already computed by vanilla from the entity's cloak history.
 */
@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin {
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void localcape$applyCape(Avatar avatar, AvatarRenderState state, float partialTick, CallbackInfo ci) {
        CapeConfig cfg = CapeConfig.get();
        if (!cfg.enabled || !LocalCapeClient.isMine(avatar)) return;
        state.skin = state.skin.with(LocalCapeClient.capePatch());
        if (!cfg.physics) {
            state.capeFlap = 0f;
            state.capeLean = 0f;
            state.capeLean2 = 0f;
        }
    }
}
