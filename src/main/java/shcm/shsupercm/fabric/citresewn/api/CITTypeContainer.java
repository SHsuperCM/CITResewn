package shcm.shsupercm.fabric.citresewn.api;

import shcm.shsupercm.fabric.citresewn.cit.ActiveCITs;
import shcm.shsupercm.fabric.citresewn.cit.CIT;
import shcm.shsupercm.fabric.citresewn.cit.CITType;
import shcm.shsupercm.fabric.citresewn.config.CITResewnConfig;

import java.util.List;
import java.util.function.Supplier;

public abstract class CITTypeContainer<T extends CITType> implements CITDisposable {
    public static final String ENTRYPOINT = "citresewn:type";
    public final Class<T> type;
    public final Supplier<T> createType;
    public final String id;

    protected boolean empty = true;

    public CITTypeContainer(Class<T> type, Supplier<T> createType, String id) {
        this.type = type;
        this.createType = createType;
        this.id = id;
    }

    protected abstract void load(List<CIT<T>> parsedCITs);

    @SuppressWarnings("unchecked")
    public final void loadUntyped(List<?> parsedCITs) {
        if (!parsedCITs.isEmpty())
            empty = false;
        load((List<CIT<T>>) parsedCITs);
    }

    public final void unload() {
        dispose();
        empty = true;
    }

    public boolean active() {
        return !empty && CITResewnConfig.INSTANCE.enabled && ActiveCITs.isActive();
    }
}
