package shcm.shsupercm.fabric.citresewn.cit;

import shcm.shsupercm.fabric.citresewn.config.CITResewnConfig;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class CITCache<T extends CITType> {
    public long lastCachedStamp = 0;

    public static class Single<T extends CITType> extends CITCache<T> {
        public WeakReference<CIT<T>> cit = null;
        public final Function<CITContext, CIT<T>> realtime;

        public Single(Function<CITContext, CIT<T>> realtime) {
            this.realtime = realtime;
        }

        public WeakReference<CIT<T>> get(CITContext context) {
            if (this.cit == null || System.currentTimeMillis() - this.lastCachedStamp >= CITResewnConfig.INSTANCE.cache_ms) {
                this.cit = new WeakReference<>(this.realtime.apply(context));
                this.lastCachedStamp = System.currentTimeMillis();
            }

            return this.cit;
        }
    }

    public static class MultiList<T extends CITType> extends CITCache<T> {
        public List<WeakReference<CIT<T>>> cit = null;
        public final Function<CITContext, List<CIT<T>>> realtime;

        public MultiList(Function<CITContext, List<CIT<T>>> realtime) {
            this.realtime = realtime;
        }

        public List<WeakReference<CIT<T>>> get(CITContext context) {
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
