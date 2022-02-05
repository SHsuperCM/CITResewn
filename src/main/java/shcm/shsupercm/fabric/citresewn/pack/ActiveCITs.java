package shcm.shsupercm.fabric.citresewn.pack;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;
import shcm.shsupercm.fabric.citresewn.api.Disposable;

public class ActiveCITs implements Disposable { private ActiveCITs() {}
    private static ActiveCITs active = null;

    public static ActiveCITs getActive() {
        return active;
    }

    public static boolean isActive() {
        return active != null;
    }

    public final GlobalProperties globalProperties = new GlobalProperties();

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
        profiler.pop();

        return ActiveCITs.active = active;
    }

    @Override
    public void dispose() {
        for (Disposable disposable : FabricLoader.getInstance().getEntrypoints(Disposable.ENTRYPOINT, Disposable.class))
            disposable.dispose();


    }
}
