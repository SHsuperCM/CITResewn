package shcm.shsupercm.fabric.citresewn.mixin;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shcm.shsupercm.fabric.citresewn.CITResewn;

import java.lang.ref.WeakReference;

@Mixin(ElytraFeatureRenderer.class)
public class ElytraFeatureRendererMixin {
    private WeakReference<ItemStack> elytraItemCached = new WeakReference<>(null);
    private WeakReference<LivingEntity> livingEntityCached = new WeakReference<>(null);

    @Inject(method = "render", cancellable = true, at = @At("HEAD"))
    public void injectCIT(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LivingEntity livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if (CITResewn.INSTANCE.activeCITs == null)
            return;

        this.elytraItemCached = new WeakReference<>(livingEntity.getEquippedStack(EquipmentSlot.CHEST));
        this.livingEntityCached = new WeakReference<>(livingEntity);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getArmorCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    public RenderLayer getArmorCutoutNoCull(Identifier originalIdentifier) {
        ItemStack itemStack = this.elytraItemCached.get();
        LivingEntity livingEntity = livingEntityCached.get();
        if (itemStack != null && livingEntity != null) {
            Identifier elytraTexture = CITResewn.INSTANCE.activeCITs.getElytraTexture(itemStack, livingEntity.world, livingEntity);
            if (elytraTexture != null)
                return RenderLayer.getArmorCutoutNoCull(elytraTexture);
        }

        return RenderLayer.getArmorCutoutNoCull(originalIdentifier);
    }
}
