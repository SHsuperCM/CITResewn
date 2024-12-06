package io.shcm.fabric.citresewn.mixin.client_component_changes;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.shcm.fabric.citresewn.client_component_changes.ClientComponentChanges;
import io.shcm.fabric.citresewn.client_component_changes.MergedClientComponentMap;
import io.shcm.shsupercm.fabric.fletchingtable.api.MixinEnvironment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.ComponentHolder;
import net.minecraft.component.ComponentType;
import net.minecraft.component.MergedComponentMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@MixinEnvironment(MixinEnvironment.Env.CLIENT)
@Mixin(MergedComponentMap.class)
public class MergedComponentMapMixin implements MergedClientComponentMap {
    private ClientComponentChanges citresewn$clientChanges = null;

    @Override
    public ClientComponentChanges citresewn$getClientChanges() {
        return this.citresewn$clientChanges == null ? ClientComponentChanges.EMPTY : this.citresewn$clientChanges;
    }

    @Override
    public void citresewn$updateFromHolder(ComponentHolder holder) {
        if (this.citresewn$clientChanges == null) {
            this.citresewn$clientChanges = ClientComponentChanges.EMPTY;

            if (MinecraftClient.getInstance().isOnThread())
                this.citresewn$clientChanges = ClientComponentChanges.fromHolder(holder);
        }
    }

    @Override
    public void citresewn$invalidateClientChanges() {
        this.citresewn$clientChanges = null;
    }

    @Inject(method = "onWrite", at = @At("RETURN"))
    private void citresewn$invalidateOnWrite(CallbackInfo ci) {
        citresewn$invalidateClientChanges();
    }

    @ModifyReturnValue(method = "get", at = @At("RETURN"))
    private <T> T citresewn$getClientOrComponent(T original, ComponentType<? extends T> type) {
        return citresewn$getClientChanges().getOrDefault(type, original);
    }
}
