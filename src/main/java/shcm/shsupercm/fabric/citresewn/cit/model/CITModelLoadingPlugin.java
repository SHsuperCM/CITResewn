package shcm.shsupercm.fabric.citresewn.cit.model;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;

public class CITModelLoadingPlugin implements PreparableModelLoadingPlugin<CITResources.CITModels> {
    @Override
    public void onInitializeModelLoader(CITResources.CITModels data, ModelLoadingPlugin.Context plugin) {
        // todo add/bake models from data
        // todo capture and store baked models
    }
}
