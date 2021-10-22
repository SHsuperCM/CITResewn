package shcm.shsupercm.fabric.citresewn.mixin.cititem;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import shcm.shsupercm.fabric.citresewn.config.CITResewnConfig;
import shcm.shsupercm.fabric.citresewn.pack.cits.CITItem;

import java.lang.ref.WeakReference;
import java.util.function.Supplier;

@Mixin(ItemStack.class)
public class ItemStackMixin implements CITItem.Cached {
    private WeakReference<CITItem> citresewn_cachedCITItem = new WeakReference<>(null);
    private long citresewn_cacheTimeCITItem = 0;

    /**
     * Used by tridents and spy glasses to force their unique overrides to be applied correctly
     */
    private boolean citresewn_mojankCIT = false;

    @Override
    public CITItem citresewn_getCachedCITItem(Supplier<CITItem> realtime) {
        if (System.currentTimeMillis() - citresewn_cacheTimeCITItem >= CITResewnConfig.INSTANCE().cache_ms) {
            citresewn_cachedCITItem = new WeakReference<>(realtime.get());
            citresewn_cacheTimeCITItem = System.currentTimeMillis();
        }

        return citresewn_cachedCITItem.get();
    }

    @Override
    public boolean citresewn_isMojankCIT() {
        return citresewn_mojankCIT;
    }

    @Override
    public void citresewn_setMojankCIT(boolean mojankCIT) {
        citresewn_mojankCIT = mojankCIT;
    }
}
