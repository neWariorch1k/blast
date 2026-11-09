package com.game.blastred.mixin;

import com.game.blastred.BlastCache;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EquipmentLayerRenderer.class)
public class EquipmentLayerRendererMixin {

    @Unique
    private static final Identifier BLASTRED_DIAMOND =
            Identifier.fromNamespaceAndPath("blastred", "textures/entity/equipment/humanoid_leggings/diamond_red.png");

    @Unique
    private static final Identifier BLASTRED_NETHERITE =
            Identifier.fromNamespaceAndPath("blastred", "textures/entity/equipment/humanoid_leggings/netherite_red.png");

    @Inject(method = "renderLayers", at = @At("HEAD"), cancellable = true)
    private void blastred$renderRed(EquipmentClientInfo.LayerType layerType, ResourceKey<?> assetId, Model<?> model,
                                    Object state, ItemStack stack, PoseStack poseStack,
                                    SubmitNodeCollector collector, int lightCoords, int outlineColor,
                                    CallbackInfo ci) {
        if (layerType != EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS) {
            return;
        }

        boolean hasBlast = blastred$hasBlastProtection(stack) || BlastCache.get();
        if (!hasBlast) {
            return;
        }

        Identifier texture = stack.is(Items.NETHERITE_LEGGINGS) ? BLASTRED_NETHERITE : BLASTRED_DIAMOND;

        @SuppressWarnings("unchecked")
        Model<Object> typedModel = (Model<Object>) model;
        collector.order(1).submitModel(
                typedModel,
                state,
                poseStack,
                RenderTypes.armorCutoutNoCull(texture),
                lightCoords,
                OverlayTexture.NO_OVERLAY,
                -1,
                null,
                outlineColor,
                null
        );
        ci.cancel();
    }

    @Unique
    private static boolean blastred$hasBlastProtection(ItemStack stack) {
        ItemEnchantments enchantments = stack.get(DataComponents.ENCHANTMENTS);
        if (enchantments == null || enchantments.isEmpty()) {
            return false;
        }
        for (Holder<Enchantment> enchantment : enchantments.keySet()) {
            if (enchantment.is(Enchantments.BLAST_PROTECTION)) {
                return true;
            }
        }
        return false;
    }
}
