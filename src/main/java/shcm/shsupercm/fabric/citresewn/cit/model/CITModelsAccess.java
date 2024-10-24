package shcm.shsupercm.fabric.citresewn.cit.model;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import shcm.shsupercm.fabric.citresewn.cit.resource.CITResources;

import java.util.function.Consumer;

public class CITModelsAccess {
    private final ResourceManager manager;

    public CITModelsAccess(ResourceManager manager) {
        this.manager = manager;
    }

    public void requestModel(Identifier path, Consumer<BakedModel> bakedModelReceiver) {
        requestModel(path, ModelFinder.DEFAULT, bakedModelReceiver);
    }

    public void requestModel(Identifier path, ModelFinder modelResolver, Consumer<BakedModel> modelReceiver) {

    }

    public CITResources.CITModels build() {
        return new CITResources.CITModels();
    }

    public interface ModelFinder {
        ModelFinder DEFAULT = context -> {
            return null;
        };

        UnbakedModel resolve(ModelResolver.Context context);
    }
}
