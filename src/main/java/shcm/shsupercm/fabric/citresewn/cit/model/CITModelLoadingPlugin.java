package shcm.shsupercm.fabric.citresewn.cit.model;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.minecraft.client.render.model.ModelLoader;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.cit.resource.CITResources;

public class CITModelLoadingPlugin implements PreparableModelLoadingPlugin<CITResources.CITModels> {
    @Override
    public void onInitializeModelLoader(CITResources.CITModels data, ModelLoadingPlugin.Context plugin) {
        plugin.addModels(data.extraModels());

        plugin.resolveModel().register(context -> {
            try {
                if (context.id().getNamespace().equals("citresewn") && context.id().getPath().startsWith("cit_generated/"))
                    return data.modelResolvers().get(context.id()).resolveModel(context);
            } catch (Exception e) {
                CITResewn.logErrorLoading("Errored loading generated model '" + context.id() + "'");
                CITResewn.LOG.error(e);
                return context.getOrLoadModel(ModelLoader.MISSING_ID);
            }

            return null;
        });
    }
}
