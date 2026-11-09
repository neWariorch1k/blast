package com.game.blastred.mixin;

import com.game.blastred.BlastCache;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Before the armor layer is submitted, find the entity being rendered and remember
 * whether it carries the Blast Protection (leggings) attribute modifier.
 * After the layer is done, reset the flag.
 */
@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin {

    @Unique
    private static final Identifier BLAST_LEGS_ID =
            Identifier.fromNamespaceAndPath("minecraft", "enchantment.blast_protection/legs");

    @Inject(method = "submit", at = @At("HEAD"))
    private void blastred$before(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords,
                                 HumanoidRenderState state, float yRot, float xRot, CallbackInfo ci) {
        boolean hasBlast = false;
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level != null) {
            double x = state.x;
            double y = state.y;
            double z = state.z;
            for (Entity entity : level.entitiesForRendering()) {
                if (entity instanceof LivingEntity living
                        && Math.abs(entity.getX() - x) < 0.5D
                        && Math.abs(entity.getY() - y) < 0.5D
                        && Math.abs(entity.getZ() - z) < 0.5D) {
                    AttributeInstance instance = living.getAttributes().getInstance(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE);
                    hasBlast = instance != null && instance.hasModifier(BLAST_LEGS_ID);
                    break;
                }
            }
        }
        BlastCache.push(hasBlast);
    }

    @Inject(method = "submit", at = @At("TAIL"))
    private void blastred$after(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords,
                                HumanoidRenderState state, float yRot, float xRot, CallbackInfo ci) {
        BlastCache.clear();
    }
}
