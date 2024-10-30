package shcm.shsupercm.fabric.citresewn.cit.model;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;
import shcm.shsupercm.fabric.citresewn.cit.resource.CITResources;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class CITModelsAccess {
    private final Set<RequestEntry> modelRequests = new HashSet<>();

    public CITResources.CITModels build() {
        CITResources.CITModels models = new CITResources.CITModels(new HashSet<>(), new HashMap<>(), new HashMap<>());

        for (RequestEntry entry : modelRequests) {
            models.extraModels().add(entry.modelPath());

            if (entry.modelResolver() != null)
                models.modelResolvers().put(entry.modelPath(), entry.modelResolver());

            if (entry.bakedModelReceiver() != null)
                models.bakedModelReceivers().put(entry.modelPath(), entry.bakedModelReceiver());
        }

        return models;
    }

    /**
     * Asks the model loader to load a standard extra model.
     * @param modelPath path of the model normalized to assets/namespace/models/
     * @return a simple supplier for the model before baking for use within unique custom model resolvers
     */
    public Function<ModelResolver.Context, UnbakedModel> requestModel(Identifier modelPath) {
        modelRequests.add(new RequestEntry(modelPath, null, null));
        return context -> context.getOrLoadModel(modelPath);
    }

    /**
     * Asks the model loader to load a standard extra model.
     * @param modelPath path of the model normalized to assets/namespace/models/
     * @param bakedModelReceiver consumer that receives the model once it was baked or missing model if baking has failed
     * @return a simple supplier for the model before baking for use within unique custom model resolvers
     */
    public Function<ModelResolver.Context, UnbakedModel> requestModel(Identifier modelPath, Consumer<BakedModel> bakedModelReceiver) {
        modelRequests.add(new RequestEntry(modelPath, null, bakedModelReceiver));
        return context -> context.getOrLoadModel(modelPath);
    }

    /**
     * Asks the model loader to load an extra model and dictate how to create said model.
     * @param modelName a name or path to identify the model by in logs, however it becomes unique when inserted to the model loader so it cannot be retreived using the same identifier
     * @param modelResolver called once to create the unbaked model
     * @param bakedModelReceiver consumer that receives the model once it was baked or missing model if baking has failed
     * @return a simple supplier for the model before baking for use within other unique custom model resolvers
     */
    public Function<ModelResolver.Context, UnbakedModel> requestUniqueModel(Identifier modelName, ModelResolver modelResolver, Consumer<BakedModel> bakedModelReceiver) {
        Identifier modelPath = Identifier.of("citresewn", "cit_generated/" + this.modelRequests.size() + "/" + modelName.getNamespace() + "/" + modelName.getPath());
        modelRequests.add(new RequestEntry(modelPath, modelResolver, bakedModelReceiver));
        return context -> context.getOrLoadModel(modelPath);
    }

    private record RequestEntry(Identifier modelPath, ModelResolver modelResolver, Consumer<BakedModel> bakedModelReceiver) { }
}
