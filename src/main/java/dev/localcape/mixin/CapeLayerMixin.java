package dev.localcape.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.localcape.CapeConfig;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;

/** Optional cape length scaling (vertical only, anchored at the shoulders). */
@Mixin(CapeLayer.class)
public abstract class CapeLayerMixin {
    @WrapMethod(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V")
    private void localcape$length(PoseStack pose, SubmitNodeCollector collector, int light,
                                  AvatarRenderState state, float yRot, float xRot, Operation<Void> original) {
        float len = CapeConfig.get().capeLength;
        // Only touch capes that we injected (same texture namespace).
        boolean ours = state.skin != null && state.skin.cape() != null
                && state.skin.cape().id().getNamespace().equals("localcape");
        if (!ours || len == 1.0f) { original.call(pose, collector, light, state, yRot, xRot); return; }
        pose.pushPose();
        pose.scale(1.0f, len, 1.0f);
        original.call(pose, collector, light, state, yRot, xRot);
        pose.popPose();
    }
}
