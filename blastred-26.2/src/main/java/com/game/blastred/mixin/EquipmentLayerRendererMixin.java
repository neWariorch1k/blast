package com.game.blastred.mixin;

import com.game.blastred.BlastCache;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
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
import org.spongepowered.asm.mixin.injection.Coerce;

/**
 * Replaces the rendering of the leggings layer with a red texture when the leggings
 * carry Blast Protection (or the wearer is known to have it, see {@link BlastCache}).
 */
@Mixin(EquipmentLayerRenderer.class)
public class EquipmentLayerRendererMixin {

    @Unique
    private static final Identifier BLASTRED_DIAMOND =
            Identifier.fromNamespaceAndPath("blastred", "textures/entity/equipment/humanoid_leggings/diamond_red.png");

    @Unique
    private static final Identifier BLASTRED_NETHERITE =
            Identifier.fromNamespaceAndPath("blastred", "textures/entity/equipment/humanoid_leggings/netherite_red.png");

    /**
     * Target: EquipmentLayerRenderer#renderLayers(LayerType, ResourceKey, Model, S, ItemStack,
     * PoseStack, SubmitNodeCollector, int, Identifier, int, int).
     * {@code layerType} is coerced to Object so the nested enum type does not have to be imported;
     * it is compared by constant name.
     */
    @Inject(method = "renderLayers", at = @At("HEAD"), cancellable = true)
    private void blastred$renderRed(@Coerce Object layerType, ResourceKey<?> assetId, Model<?> model, Object state,
                                    ItemStack stack, PoseStack poseStack, SubmitNodeCollector collector,
                                    int lightCoords, Identifier playerTexture, int outlineColor, int order,
                                    CallbackInfo ci) {
        if (!(layerType instanceof Enum<?> layer) || !"HUMANOID_LEGGINGS".equals(layer.name())) {
            return;
        }

        boolean hasBlast = blastred$hasBlastProtection(stack) || BlastCache.get();
        if (!hasBlast) {
            return;
        }

        Identifier texture = stack.is(Items.NETHERITE_LEGGINGS) ? BLASTRED_NETHERITE : BLASTRED_DIAMOND;

        @SuppressWarnings("unchecked")
        Model<Object> typedModel = (Model<Object>) model;
        collector.order(order).submitModel(
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
