package shcm.shsupercm.fabric.citresewn.mixin.citenchantment;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shcm.shsupercm.fabric.citresewn.config.CITResewnConfig;
import shcm.shsupercm.fabric.citresewn.pack.cits.CITEnchantment;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.function.Supplier;

@Mixin(ItemStack.class)
public class ItemStackMixin implements CITEnchantment.Cached {
    private WeakReference<List<CITEnchantment>> citresewn_cachedCITEnchantment = new WeakReference<>(null);
    private long citresewn_cacheTimeCITEnchantment = 0;

    @Override
    public List<CITEnchantment> citresewn_getCachedCITEnchantment(Supplier<List<CITEnchantment>> realtime) {
        if (System.currentTimeMillis() - citresewn_cacheTimeCITEnchantment >= CITResewnConfig.INSTANCE().cache_ms) {
            citresewn_cachedCITEnchantment = new WeakReference<>(realtime.get());
            citresewn_cacheTimeCITEnchantment = System.currentTimeMillis();
        }

        return citresewn_cachedCITEnchantment.get();
    }

    @Inject(method = "hasGlint", cancellable = true, at = @At("HEAD"))
    private void disableDefaultGlint(CallbackInfoReturnable<Boolean> cir) {
        if (CITEnchantment.appliedContext != null && !CITEnchantment.appliedContext.get(0).useGlint)
            cir.setReturnValue(false);
    }
}
