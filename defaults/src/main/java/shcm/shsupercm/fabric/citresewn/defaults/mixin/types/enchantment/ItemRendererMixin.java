package shcm.shsupercm.fabric.citresewn.defaults.mixin.types.enchantment;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeEnchantment;

import static shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeEnchantment.CONTAINER;

@Mixin(value = ItemRenderer.class, priority = 200)
public class ItemRendererMixin {
    @Inject(method = "getModel", at = @At("HEAD"))
    private void citresewn$enchantment$setAppliedContext(ItemStack stack, World world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        if (CONTAINER.active())
            CONTAINER.setContext(new CITContext(stack, world, entity));
    }

    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At("HEAD"))
    private void citresewn$enchantment$startApplyingItem(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        if (CONTAINER.active())
            CONTAINER.apply();
    }

    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At("RETURN"))
    private void citresewn$enchantment$stopApplyingItem(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        if (CONTAINER.active())
            CONTAINER.setContext(null);
    }

    @Inject(method = "getArmorGlintConsumer", cancellable = true, at = @At("RETURN"))
    private static void citresewn$enchantment$getArmorGlintConsumer(VertexConsumerProvider provider, RenderLayer layer, /*? <1.21 {*//*boolean solid, *//*?}*/boolean glint, CallbackInfoReturnable<VertexConsumer> cir) {
        if (!CONTAINER.shouldApply())
            return;

        VertexConsumer vertexConsumer = /*? <1.21 {*//*solid ? TypeEnchantment.GlintRenderLayer.ARMOR_GLINT.tryApply(cir.getReturnValue(), layer, provider) : *//*?}*/TypeEnchantment.GlintRenderLayer.ARMOR_ENTITY_GLINT.tryApply(cir.getReturnValue(), layer, provider);
        if (vertexConsumer != null)
            cir.setReturnValue(vertexConsumer);
    }

    @Inject(method = /*? <1.20 {*//*"getCompassGlintConsumer"*//*?} else {*/"getDynamicDisplayGlintConsumer"/*?}*/, cancellable = true, at = @At("RETURN"))
    private static void citresewn$enchantment$getDynamicDisplayGlintConsumer(VertexConsumerProvider provider, RenderLayer layer, MatrixStack.Entry entry, CallbackInfoReturnable<VertexConsumer> cir) {
        if (!CONTAINER.shouldApply())
            return;
        VertexConsumer vertexConsumer = TypeEnchantment.GlintRenderLayer.GLINT.tryApply(null, layer, provider);
        if (vertexConsumer != null)
            cir.setReturnValue(VertexConsumers.union(new OverlayVertexConsumer(vertexConsumer, /*? >=1.21 {*/entry/*?} else {*//*entry.getPositionMatrix(), entry.getNormalMatrix()*//*?}*/, 1f), cir.getReturnValue()));
    }

    //? <1.21 {
    /*@Inject(method = /^? <1.20 {^/"getDirectCompassGlintConsumer"/^?} else {^//^"getDirectDynamicDisplayGlintConsumer"^//^?}^/, cancellable = true, at = @At("RETURN"))
    private static void citresewn$enchantment$getDirectDynamicDisplayGlintConsumer(VertexConsumerProvider provider, RenderLayer layer, MatrixStack.Entry entry, CallbackInfoReturnable<VertexConsumer> cir) {
        if (!CONTAINER.shouldApply())
            return;
        VertexConsumer vertexConsumer = TypeEnchantment.GlintRenderLayer.DIRECT_GLINT.tryApply(null, layer, provider);
        if (vertexConsumer != null)
            cir.setReturnValue(VertexConsumers.union(new OverlayVertexConsumer(vertexConsumer, entry.getPositionMatrix(), entry.getNormalMatrix(), 1f), cir.getReturnValue()));
    }
    *///?}

    @Inject(method = "getItemGlintConsumer", cancellable = true, at = @At("RETURN"))
    private static void citresewn$enchantment$getItemGlintConsumer(VertexConsumerProvider provider, RenderLayer layer, boolean solid, boolean glint, CallbackInfoReturnable<VertexConsumer> cir) {
        if (!CONTAINER.shouldApply())
            return;
        VertexConsumer vertexConsumer = MinecraftClient.isFabulousGraphicsOrBetter() && layer == TexturedRenderLayers.getItemEntityTranslucentCull() ? TypeEnchantment.GlintRenderLayer.GLINT_TRANSLUCENT.tryApply(cir.getReturnValue(), layer, provider) : (solid ? TypeEnchantment.GlintRenderLayer.GLINT.tryApply(cir.getReturnValue(), layer, provider) : TypeEnchantment.GlintRenderLayer.ENTITY_GLINT.tryApply(cir.getReturnValue(), layer, provider));
        if (vertexConsumer != null)
            cir.setReturnValue(vertexConsumer);
    }

    @Inject(method = "getDirectItemGlintConsumer", cancellable = true, at = @At("RETURN"))
    private static void citresewn$enchantment$getDirectItemGlintConsumer(VertexConsumerProvider provider, RenderLayer layer, boolean solid, boolean glint, CallbackInfoReturnable<VertexConsumer> cir) {
        if (!CONTAINER.shouldApply())
            return;
        VertexConsumer vertexConsumer = /*? <1.21 {*//*solid ? TypeEnchantment.GlintRenderLayer.DIRECT_GLINT.tryApply(cir.getReturnValue(), layer, provider) : *//*?}*/TypeEnchantment.GlintRenderLayer.DIRECT_ENTITY_GLINT.tryApply(cir.getReturnValue(), layer, provider);
        if (vertexConsumer != null)
            cir.setReturnValue(vertexConsumer);
    }
}