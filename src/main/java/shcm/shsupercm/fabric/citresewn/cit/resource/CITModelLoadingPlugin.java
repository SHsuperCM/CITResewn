package shcm.shsupercm.fabric.citresewn.cit.resource;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class CITModelLoadingPlugin implements ModelLoadingPlugin {
    public volatile CITResources.Models models;

    public void apply(CITResources.Models models) {
        this.models = models;
    }

    @Override
    public void onInitializeModelLoader(Context pluginContext) {
        new String();
    }
}
