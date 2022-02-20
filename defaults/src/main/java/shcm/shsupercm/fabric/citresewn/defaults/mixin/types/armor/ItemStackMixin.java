package shcm.shsupercm.fabric.citresewn.defaults.mixin.types.armor;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import shcm.shsupercm.fabric.citresewn.cit.CITCache;
import shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeArmor;

@Mixin(ItemStack.class)
public class ItemStackMixin implements TypeArmor.CITCacheArmor {
    private final CITCache.Single<TypeArmor> citresewn$cacheTypeArmor = new CITCache.Single<>(TypeArmor.CONTAINER::getRealTimeCIT);

    @Override
    public CITCache.Single<TypeArmor> citresewn$getCacheTypeArmor() {
        return this.citresewn$cacheTypeArmor;
    }
}
