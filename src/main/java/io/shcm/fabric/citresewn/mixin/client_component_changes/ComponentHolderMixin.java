package io.shcm.fabric.citresewn.mixin.client_component_changes;

import io.shcm.fabric.citresewn.client_component_changes.MergedClientComponentMap;
import io.shcm.shsupercm.fabric.fletchingtable.api.MixinEnvironment;
import net.minecraft.component.ComponentHolder;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@MixinEnvironment(MixinEnvironment.Env.CLIENT)
@Mixin({ ItemStack.class  })
public abstract class ComponentHolderMixin implements ComponentHolder {
    @Inject(method = "getComponents", at = @At("RETURN"))
    private void citresewn$updateClientComponents(CallbackInfoReturnable<ComponentMap> cir) {
        if (cir.getReturnValue() instanceof MergedClientComponentMap clientComponents)
            clientComponents.citresewn$updateFromHolder(this);
    }
}
