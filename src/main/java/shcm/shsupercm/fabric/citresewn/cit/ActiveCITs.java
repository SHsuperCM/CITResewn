package shcm.shsupercm.fabric.citresewn.cit;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;
import shcm.shsupercm.fabric.citresewn.api.CITDisposable;
import shcm.shsupercm.fabric.citresewn.api.CITTypeContainer;
import shcm.shsupercm.fabric.citresewn.pack.GlobalProperties;
import shcm.shsupercm.fabric.citresewn.pack.PackParser;

import java.util.*;

public class ActiveCITs implements CITDisposable { private ActiveCITs() {}
    private static ActiveCITs active = null;

    public static ActiveCITs getActive() {
        return active;
    }

    public static boolean isActive() {
        return active != null;
    }

    public final GlobalProperties globalProperties = new GlobalProperties();

    public final Map<Class<? extends CITType>, List<CIT<?>>> cits = new IdentityHashMap<>();

    public static ActiveCITs load(ResourceManager resourceManager, Profiler profiler) {
        profiler.push("citresewn:disposing");
        if (active != null) {
            active.dispose();
            active = null;
        }

        ActiveCITs active = new ActiveCITs();

        profiler.swap("citresewn:load_global_properties");
        PackParser.loadGlobalProperties(resourceManager, active.globalProperties);
        active.globalProperties.callHandlers();

        profiler.swap("citresewn:load_cits");
        for (CIT<?> cit : PackParser.loadCITs(resourceManager))
            active.cits.computeIfAbsent(cit.type.getClass(), type -> new ArrayList<>()).add(cit);
        for (Map.Entry<Class<? extends CITType>, List<CIT<?>>> entry : active.cits.entrySet()) {
            entry.getValue().sort(Comparator.<CIT<?>>comparingInt(cit -> cit.weight).reversed().thenComparing(cit -> cit.propertiesIdentifier.toString()));
            for (CITTypeContainer<? extends CITType> typeContainer : CITRegistry.TYPES.values())
                if (typeContainer.type == entry.getKey()) {
                    typeContainer.loadUntyped(entry.getValue());
                    break;
                }
        }

        profiler.pop();

        return ActiveCITs.active = active;
    }

    @Override
    public void dispose() {
        for (CITDisposable disposable : FabricLoader.getInstance().getEntrypoints(CITDisposable.ENTRYPOINT, CITDisposable.class))
            disposable.dispose();

        for (CITTypeContainer<? extends CITType> typeContainer : CITRegistry.TYPES.values())
            typeContainer.dispose();
    }
}
