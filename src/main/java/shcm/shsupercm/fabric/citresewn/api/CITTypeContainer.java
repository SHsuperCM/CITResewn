package shcm.shsupercm.fabric.citresewn.api;

import shcm.shsupercm.fabric.citresewn.cit.CIT;
import shcm.shsupercm.fabric.citresewn.cit.CITType;

import java.util.List;
import java.util.function.Supplier;

public abstract class CITTypeContainer<T extends CITType> implements CITDisposable {
    public static final String ENTRYPOINT = "citresewn:type";
    public final Class<T> type;
    public final Supplier<T> createType;
    public final String id;

    public CITTypeContainer(Class<T> type, Supplier<T> createType, String id) {
        this.type = type;
        this.createType = createType;
        this.id = id;
    }

    public abstract void load(List<CIT<T>> parsedCITs);

    @SuppressWarnings("unchecked")
    public final void loadUntyped(List<?> parsedCITs) {
        load((List<CIT<T>>) parsedCITs);
    }
}
