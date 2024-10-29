package shcm.shsupercm.fabric.citresewn.cit.model;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import shcm.shsupercm.fabric.citresewn.cit.resource.CITResources;

import java.util.*;
import java.util.function.Consumer;

public class CITModelsAccess {
    private final ResourceManager manager;
    private final Set<RequestEntry> modelRequests = new HashSet<>();

    public CITModelsAccess(ResourceManager manager) {
        this.manager = manager;
    }

    public CITResources.CITModels build() {
        CITResources.CITModels models = new CITResources.CITModels(new HashSet<>(), new IdentityHashMap<>(), new IdentityHashMap<>());

        int generatedCount = 0;

        for (RequestEntry entry : modelRequests) {
            final Identifier modelPath = entry.modelPath();
            final ModelFinder modelFinder = entry.modelFinder();

            Identifier modelIdentifier = Identifier.of("citresewn", "generated/" + generatedCount++ + "/" + modelPath.getNamespace() + "/" + modelPath.getPath());
            models.extraModels().add(modelIdentifier);

            models.modelResolvers().put(modelIdentifier, resolverContext ->
                    modelFinder.resolve(modelPath, manager, resolverContext));

            models.bakedModelReceivers().put(modelIdentifier, entry.bakedModelReceiver());
        }

        return models;
    }

    public void requestModel(Identifier modelPath, Consumer<BakedModel> bakedModelReceiver) {
        requestModel(modelPath, ModelFinder.DEFAULT, bakedModelReceiver);
    }

    public void requestModel(Identifier modelPath, ModelFinder modelFinder, Consumer<BakedModel> modelReceiver) {
        modelRequests.add(new RequestEntry(modelPath, modelFinder, modelReceiver));
    }

    public interface ModelFinder {
        ModelFinder DEFAULT = (path, resourceManager, resolverContext) -> {
            return null;
        };

        UnbakedModel resolve(Identifier path, ResourceManager resourceManager, ModelResolver.Context resolverContext);
    }

    private record RequestEntry(Identifier modelPath, ModelFinder modelFinder, Consumer<BakedModel> bakedModelReceiver) { }
}
