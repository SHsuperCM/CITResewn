package shcm.shsupercm.fabric.citresewn.defaults.mixin.types.enchantment;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shcm.shsupercm.fabric.citresewn.cit.CITCache;
import shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeEnchantment;

@Mixin(ItemStack.class)
public class ItemStackMixin implements TypeEnchantment.CITCacheEnchantment {
    private final CITCache.MultiList<TypeEnchantment> citresewn$cacheTypeEnchantment = new CITCache.MultiList<>(TypeEnchantment.CONTAINER::getRealTimeCIT);

    @Override
    public CITCache.MultiList<TypeEnchantment> citresewn$getCacheTypeEnchantment() {
        return this.citresewn$cacheTypeEnchantment;
    }

    @Inject(method = "hasGlint", cancellable = true, at = @At("HEAD"))
    private void citresewn$enchantment$disableDefaultGlint(CallbackInfoReturnable<Boolean> cir) {
        if (TypeEnchantment.CONTAINER.shouldNotApplyDefaultGlint())
            cir.setReturnValue(false);
    }
}
