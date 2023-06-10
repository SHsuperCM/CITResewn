package shcm.shsupercm.fabric.citresewn.defaults.mixin.types.enchantment;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;

import static shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeEnchantment.CONTAINER;

@Mixin(ArmorFeatureRenderer.class)
public class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> {
    @Inject(method = "renderArmor", at = @At("HEAD"))
    private void citresewn$enchantment$setAppliedContextAndStartApplyingArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T livingEntity, EquipmentSlot armorSlot, int light, A model, CallbackInfo ci) {
        if (CONTAINER.active())
            CONTAINER.setContext(new CITContext(livingEntity.getEquippedStack(armorSlot), livingEntity.getWorld(), livingEntity)).apply();
    }

    @Inject(method = "renderArmor", at = @At("RETURN"))
    private void citresewn$enchantment$stopApplyingArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T livingEntity, EquipmentSlot armorSlot, int light, A model, CallbackInfo ci) {
        if (CONTAINER.active())
            CONTAINER.setContext(null);
    }
}
