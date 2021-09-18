package shcm.shsupercm.fabric.citresewn.mixin.cititem;

import net.minecraft.client.MinecraftClient;
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
import shcm.shsupercm.fabric.citresewn.config.CITResewnConfig;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Inject(method = "getHeldItemModel", cancellable = true, at = @At("HEAD"))
    public void getItemModel(ItemStack stack, World world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        if (!CITResewnConfig.INSTANCE().enabled || CITResewn.INSTANCE.activeCITs == null)
            return;

        BakedModel citModel = CITResewn.INSTANCE.activeCITs.getItemModel(stack, world == null ? MinecraftClient.getInstance().world : world, entity, seed);
        if (citModel != null)
            cir.setReturnValue(citModel);
    }
}
