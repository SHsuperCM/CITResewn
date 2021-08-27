package shcm.shsupercm.fabric.citresewn.mixin;

import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shcm.shsupercm.fabric.citresewn.ActiveCITs;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.pack.CITParser;
import shcm.shsupercm.fabric.citresewn.pack.ResewnItemModelIdentifier;
import shcm.shsupercm.fabric.citresewn.pack.cits.CIT;
import shcm.shsupercm.fabric.citresewn.pack.cits.CITItem;

import java.util.*;
import java.util.stream.Collectors;

@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {
    @Shadow @Final private ResourceManager resourceManager;
    @Shadow @Final private Set<Identifier> modelsToLoad;
    @Shadow @Final private Map<Identifier, UnbakedModel> unbakedModels;
    @Shadow @Final private Map<Identifier, UnbakedModel> modelsToBake;
    @Shadow @Final private Map<Identifier, BakedModel> bakedModels;

    private Map<Identifier, BakedModel> citOverrideCacheMap = new HashMap<>();

    @Inject(method = "addModel", at = @At("TAIL"))
    public void addCITModels(ModelIdentifier eventModelId, CallbackInfo ci) { if (eventModelId != ModelLoader.MISSING_ID) return;
        if (CITResewn.INSTANCE.activeCITs != null) {
            CITResewn.INSTANCE.activeCITs.dispose();
            CITResewn.INSTANCE.activeCITs = null;
        }

        Collection<CIT> parsed = CITParser.parse(resourceManager.streamResourcePacks().collect(Collectors.toCollection(ArrayList::new)));

        for (CIT cit : parsed)
            if (cit instanceof CITItem citItem) {
                try {
                    citItem.loadUnbakedAssets(resourceManager);

                    for (JsonUnbakedModel unbakedModel : citItem.unbakedAssets.values()) {
                        ResewnItemModelIdentifier id = new ResewnItemModelIdentifier(unbakedModel.id);
                        this.unbakedModels.put(id, unbakedModel);
                        this.modelsToLoad.addAll(unbakedModel.getModelDependencies());
                        this.modelsToBake.put(id, unbakedModel);
                    }
                } catch (Exception e) {
                    CITResewn.LOG.error(e.getMessage());
                }
            }

        if (parsed.size() > 0)
            CITResewn.INSTANCE.activeCITs = new ActiveCITs(parsed);
    }

    @Inject(method = "bake", at = @At("RETURN"))
    public void onBake(Identifier id, ModelBakeSettings settings, CallbackInfoReturnable<BakedModel> cir) {
        this.citOverrideCacheMap.put(id, cir.getReturnValue());
    }

    @Inject(method = "upload", at = @At("RETURN"))
    public void linkBakedModel(TextureManager textureManager, Profiler profiler, CallbackInfoReturnable<SpriteAtlasManager> cir) {
        profiler.push("citresewn_linking");

        if (CITResewn.INSTANCE.activeCITs != null) {
            for (CITItem citItem : CITResewn.INSTANCE.activeCITs.citItems.values().stream().flatMap(Collection::stream).distinct().collect(Collectors.toList())) {
                for (Map.Entry<Identifier, JsonUnbakedModel> citModelEntry : citItem.unbakedAssets.entrySet()) {
                    if (citModelEntry.getKey() == null) {
                        citItem.bakedModel = this.bakedModels.get(new ResewnItemModelIdentifier(citModelEntry.getValue().id));
                    } else {
                        if (citItem.subItems == null)
                            citItem.subItems = new HashMap<>();

                        BakedModel override = citOverrideCacheMap.get(citModelEntry.getKey());

                        if (override == null)
                            override = citOverrideCacheMap.get(new ModelIdentifier(citModelEntry.getKey().getNamespace(), citModelEntry.getKey().getPath().substring(5), "inventory"));

                        if (override != null)
                            citItem.subItems.put(override, this.bakedModels.get(new ResewnItemModelIdentifier(citModelEntry.getValue().id)));
                        else {
                            CITResewn.LOG.error("Skipping sub cit: No such sub item model \"" + citModelEntry.getKey().getPath().substring(5) + "\" in " + citItem.pack.resourcePack.getName() + " -> " + citItem.propertiesIdentifier.getPath());
                        }
                    }
                }
                citItem.unbakedAssets = null;
            }
        }

        this.citOverrideCacheMap = null;

        profiler.pop();
    }
}
