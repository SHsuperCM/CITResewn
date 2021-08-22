package shcm.shsupercm.fabric.citresewn.mixin;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shcm.shsupercm.fabric.citresewn.CITResewn;

import java.util.function.Function;

@Mixin(ModelOverrideList.class)
public class ModelOverrideListMixin {
    @Inject(method = "bakeOverridingModel", at = @At("RETURN"))
    public void onBakeOverridingModel(ModelLoader loader, JsonUnbakedModel parent, Function<Identifier, UnbakedModel> unbakedModelGetter, ModelOverride override, CallbackInfoReturnable<BakedModel> cir) {
        if (cir.getReturnValue() != null) {
            String[] split = override.getModelId().getPath().split("/");
            CITResewn.INSTANCE.bakedOverridesCache.put(cir.getReturnValue(), split[split.length - 1]);
        }
    }
}
