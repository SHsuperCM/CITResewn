package shcm.shsupercm.fabric.citresewn.defaults.mixin.types.item;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import shcm.shsupercm.fabric.citresewn.cit.CITCache;
import shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeItem;

@Mixin(ItemStack.class)
public class ItemStackMixin implements TypeItem.CITCacheItem {
    private final CITCache.Single<TypeItem> citresewn$cacheTypeItem = new CITCache.Single<>(TypeItem.CONTAINER::getRealTimeCIT);

    @Override
    public CITCache.Single<TypeItem> citresewn$getCacheTypeItem() {
        return this.citresewn$cacheTypeItem;
    }
}
