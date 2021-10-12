package shcm.shsupercm.fabric.citresewn.mixin.citenchantment;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.pack.cits.CITEnchantment;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Inject(method = "getHeldItemModel", at = @At("TAIL"))
    private void setAppliedContext(ItemStack stack, World world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        if (CITResewn.INSTANCE.activeCITs != null)
            CITResewn.INSTANCE.activeCITs.setEnchantmentAppliedContextCached(stack, world, entity);
    }

    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At("TAIL"))
    private void clearAppliedContext(ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        if (CITResewn.INSTANCE.activeCITs != null)
            CITResewn.INSTANCE.activeCITs.setEnchantmentAppliedContextCached(null, null, null);
    }


    @Inject(method = "getArmorGlintConsumer", cancellable = true, at = @At("RETURN"))
    private static void getArmorGlintConsumer(VertexConsumerProvider provider, RenderLayer layer, boolean solid, boolean glint, CallbackInfoReturnable<VertexConsumer> cir) {
        VertexConsumer vertexConsumer = solid ? CITEnchantment.GlintRenderLayer.ARMOR_GLINT.tryApply(cir.getReturnValue(), provider) : CITEnchantment.GlintRenderLayer.ARMOR_ENTITY_GLINT.tryApply(cir.getReturnValue(), provider);
        if (vertexConsumer != null)
            cir.setReturnValue(vertexConsumer);
    }

    @Inject(method = "getCompassGlintConsumer", cancellable = true, at = @At("RETURN"))
    private static void getCompassGlintConsumer(VertexConsumerProvider provider, RenderLayer layer, MatrixStack.Entry entry, CallbackInfoReturnable<VertexConsumer> cir) {
        VertexConsumer vertexConsumer = CITEnchantment.GlintRenderLayer.GLINT.tryApply(null, provider);
        if (vertexConsumer != null)
            cir.setReturnValue(VertexConsumers.union(new OverlayVertexConsumer(vertexConsumer, entry.getModel(), entry.getNormal()), cir.getReturnValue()));
    }

    @Inject(method = "getDirectCompassGlintConsumer", cancellable = true, at = @At("RETURN"))
    private static void getDirectCompassGlintConsumer(VertexConsumerProvider provider, RenderLayer layer, MatrixStack.Entry entry, CallbackInfoReturnable<VertexConsumer> cir) {
        VertexConsumer vertexConsumer = CITEnchantment.GlintRenderLayer.DIRECT_GLINT.tryApply(null, provider);
        if (vertexConsumer != null)
            cir.setReturnValue(VertexConsumers.union(new OverlayVertexConsumer(vertexConsumer, entry.getModel(), entry.getNormal()), cir.getReturnValue()));
    }

    @Inject(method = "getItemGlintConsumer", cancellable = true, at = @At("RETURN"))
    private static void getItemGlintConsumer(VertexConsumerProvider provider, RenderLayer layer, boolean solid, boolean glint, CallbackInfoReturnable<VertexConsumer> cir) {
        VertexConsumer vertexConsumer = MinecraftClient.isFabulousGraphicsOrBetter() && layer == TexturedRenderLayers.getItemEntityTranslucentCull() ? CITEnchantment.GlintRenderLayer.GLINT_TRANSLUCENT.tryApply(cir.getReturnValue(), provider) : (solid ? CITEnchantment.GlintRenderLayer.GLINT.tryApply(cir.getReturnValue(), provider) : CITEnchantment.GlintRenderLayer.ENTITY_GLINT.tryApply(cir.getReturnValue(), provider));
        if (vertexConsumer != null)
            cir.setReturnValue(vertexConsumer);
    }

    @Inject(method = "getDirectItemGlintConsumer", cancellable = true, at = @At("RETURN"))
    private static void getDirectItemGlintConsumer(VertexConsumerProvider provider, RenderLayer layer, boolean solid, boolean glint, CallbackInfoReturnable<VertexConsumer> cir) {
        VertexConsumer vertexConsumer = solid ? CITEnchantment.GlintRenderLayer.DIRECT_GLINT.tryApply(cir.getReturnValue(), provider) : CITEnchantment.GlintRenderLayer.DIRECT_ENTITY_GLINT.tryApply(cir.getReturnValue(), provider);
        if (vertexConsumer != null)
            cir.setReturnValue(vertexConsumer);
    }
}