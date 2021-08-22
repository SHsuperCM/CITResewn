package shcm.shsupercm.fabric.citresewn.mixin;

import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shcm.shsupercm.fabric.citresewn.CITResewn;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Inject(method = "getHeldItemModel", cancellable = true, at = @At("RETURN"))
    public void getItemModel(ItemStack stack, World world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        if (CITResewn.INSTANCE.activeCITs == null)
            return;

        BakedModel citModel = CITResewn.INSTANCE.activeCITs.getItemModel(stack, cir.getReturnValue());
        if (citModel != null)
            cir.setReturnValue(citModel);
    }
}
