package shcm.shsupercm.fabric.citresewn.cit;

import net.minecraft.item.ItemStack;
import shcm.shsupercm.fabric.citresewn.config.CITResewnConfig;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Runtime cache for a CIT type to be stored in type implementation specific locations. (usually ducked onto {@link ItemStack}s)
 * @see Single
 * @see MultiList
 */
public abstract class CITCache<T extends CITType> {
    /**
     * Cache refresh staggering offset
     */
    private static byte offset = 0;

    /**
     * The last time in epoch milliseconds that this cache invalidated its stored CIT.
     */
    public long lastCachedStamp = 0;

    /**
     * Common implementation of a single CIT per holder({@link ItemStack}) caching.
     */
    public static class Single<T extends CITType> extends CITCache<T> {
        /**
         * A reload-safe reference to the CIT that was last selected for this holder.
         */
        protected WeakReference<CIT<T>> cit = null;

        /**
         * Real time Context -> CIT supplier for this cache for invalidated holders.
         */
        protected final Function<CITContext, CIT<T>> realtime;

        public Single(Function<CITContext, CIT<T>> realtime) {
            this.realtime = realtime;
        }

        /**
         * Retrieves the CIT reference associated with this cache and invalidates it every config-defined interval.
         *
         * @see CITResewnConfig#cache_ms
         * @param context context to check
         * @return reference to the CIT or reference to null if no CIT applied
         */
        public WeakReference<CIT<T>> get(CITContext context) {
            if (this.cit == null)
                this.lastCachedStamp = System.currentTimeMillis() + CITResewnConfig.INSTANCE.cache_ms + (offset += 4);

            if (this.cit == null || System.currentTimeMillis() - this.lastCachedStamp >= CITResewnConfig.INSTANCE.cache_ms) {
                this.cit = new WeakReference<>(this.realtime.apply(context));
                this.lastCachedStamp = System.currentTimeMillis();
            }

            return this.cit;
        }
    }

    /**
     * Common implementation of multiple CITs per holder({@link ItemStack}) caching.
     */
    public static class MultiList<T extends CITType> extends CITCache<T> {
        /**
         * List of reload-safe references to CITs that were last selected for this holder.
         */
        protected List<WeakReference<CIT<T>>> cit = null;

        /**
         * Real time Context -> CIT list supplier for this cache for invalidated holders.
         */
        protected final Function<CITContext, List<CIT<T>>> realtime;

        public MultiList(Function<CITContext, List<CIT<T>>> realtime) {
            this.realtime = realtime;
        }

        /**
         * Retrieves the CIT references associated with this cache and invalidates them every config-defined interval.
         *
         * @see CITResewnConfig#cache_ms
         * @param context context to check
         * @return list of references to CITs or empty list no CIT applied
         */
        public List<WeakReference<CIT<T>>> get(CITContext context) {
            if (this.cit == null)
                this.lastCachedStamp = System.currentTimeMillis() + CITResewnConfig.INSTANCE.cache_ms + (offset += 4);

            if (this.cit == null || System.currentTimeMillis() - this.lastCachedStamp >= CITResewnConfig.INSTANCE.cache_ms) {
                this.cit = new ArrayList<>();
                for (CIT<T> realtimeCIT : this.realtime.apply(context))
                    this.cit.add(new WeakReference<>(realtimeCIT));
                this.lastCachedStamp = System.currentTimeMillis();
            }

            return cit;
        }
    }
}
