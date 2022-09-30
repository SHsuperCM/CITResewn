package shcm.shsupercm.fabric.citresewn.defaults.mixin.types.elytra;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shcm.shsupercm.fabric.citresewn.cit.CIT;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeElytra;

import static shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeElytra.CONTAINER;

@Mixin(ElytraFeatureRenderer.class)
public class ElytraFeatureRendererMixin {
    @Shadow @Mutable @Final private static Identifier SKIN;

    private final static Identifier citresewn$ORIGINAL_SKIN = SKIN;

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At("HEAD"))
    public void citresewn$render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LivingEntity livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if (!CONTAINER.active())
            return;

        ItemStack equippedStack = CONTAINER.getVisualElytraItem(livingEntity);
        if (!equippedStack.isOf(Items.ELYTRA))
            return;

        CIT<TypeElytra> cit = CONTAINER.getCIT(new CITContext(equippedStack, livingEntity.getWorld(), livingEntity));
        SKIN = cit == null ? citresewn$ORIGINAL_SKIN : cit.type.texture;
    }

    /**
     * Fix cape elytra skin replacing cit elytra.
     */
    @ModifyVariable(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "STORE"))
    public Identifier citresewn$overrideCapeElytra(Identifier used) {
        return SKIN != citresewn$ORIGINAL_SKIN && SKIN != used ? SKIN : used;
    }
}