package shcm.shsupercm.fabric.citresewn.cit.resource;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class CITModelLoadingPlugin implements PreparableModelLoadingPlugin<CITResources.Models> {
    @Override
    public void onInitializeModelLoader(CITResources.Models data, ModelLoadingPlugin.Context pluginContext) {
        // todo add/bake models from data
        // todo capture and store baked models
    }
}
