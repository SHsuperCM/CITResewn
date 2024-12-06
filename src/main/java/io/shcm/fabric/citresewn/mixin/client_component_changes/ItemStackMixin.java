package io.shcm.fabric.citresewn.mixin.client_component_changes;

import io.shcm.fabric.citresewn.client_component_changes.MergedClientComponentMap;
import io.shcm.shsupercm.fabric.fletchingtable.api.MixinEnvironment;
import net.minecraft.component.ComponentHolder;
import net.minecraft.component.MergedComponentMap;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@MixinEnvironment(MixinEnvironment.Env.CLIENT)
@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ComponentHolder {
    @Shadow @Final private MergedComponentMap components;

    @Inject(method = "setCount", at = @At("RETURN"))
    private void citresewn$invalidateClientComponentsOnChangedStackCount(int count, CallbackInfo ci) {
        ((MergedClientComponentMap) (Object) this.components).citresewn$invalidateClientChanges();
    }
}
