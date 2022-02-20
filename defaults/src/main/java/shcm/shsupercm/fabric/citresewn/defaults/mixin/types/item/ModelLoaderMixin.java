package shcm.shsupercm.fabric.citresewn.defaults.mixin.types.item;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.cit.ActiveCITs;
import shcm.shsupercm.fabric.citresewn.cit.CIT;
import shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeItem;
import shcm.shsupercm.fabric.citresewn.defaults.common.ResewnItemModelIdentifier;

import java.util.*;

import static shcm.shsupercm.fabric.citresewn.CITResewn.info;
import static shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeItem.CONTAINER;

@Mixin(ModelLoader.class)
public class ModelLoaderMixin {
    @Shadow @Final private Set<Identifier> modelsToLoad;
    @Shadow @Final private Map<Identifier, UnbakedModel> modelsToBake;
    @Shadow @Final private Map<Identifier, UnbakedModel> unbakedModels;
    @Shadow @Final private Map<Identifier, BakedModel> bakedModels;

    @Inject(method = "<init>", at =
    @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V"))
    public void citresewn$addTypeItemModels(ResourceManager resourceManager, BlockColors blockColors, Profiler profiler, int i, CallbackInfo ci) {
        profiler.swap("citresewn:type_item_models");
        if (!ActiveCITs.isActive())
            return;

        info("Loading item CIT models...");
        for (CIT<TypeItem> cit : CONTAINER.loaded)
            try {
                cit.type.loadUnbakedAssets(resourceManager);

                for (JsonUnbakedModel unbakedModel : cit.type.unbakedAssets.values()) {
                    ResewnItemModelIdentifier id = new ResewnItemModelIdentifier(unbakedModel.id);
                    this.unbakedModels.put(id, unbakedModel);
                    this.modelsToLoad.addAll(unbakedModel.getModelDependencies());
                    this.modelsToBake.put(id, unbakedModel);
                }
            } catch (Exception e) {
                CITResewn.logErrorLoading("Errored loading model in " + cit.propertiesIdentifier + " from " + cit.packName);
                e.printStackTrace();
            }

        TypeItem.GENERATED_SUB_CITS_SEEN.clear();
    }

    @Inject(method = "upload", at = @At("RETURN"))
    public void citresewn$linkTypeItemModels(TextureManager textureManager, Profiler profiler, CallbackInfoReturnable<SpriteAtlasManager> cir) {
        if (!ActiveCITs.isActive())
            return;

        profiler.push("citresewn:type_item_linking");
        info("Linking baked models to item CITs...");

        for (CIT<TypeItem> cit : CONTAINER.loaded) {
            for (Map.Entry<List<ModelOverride.Condition>, JsonUnbakedModel> citModelEntry : cit.type.unbakedAssets.entrySet()) {
                if (citModelEntry.getKey() == null) {
                    cit.type.bakedModel = this.bakedModels.get(new ResewnItemModelIdentifier(citModelEntry.getValue().id));
                } else {
                    BakedModel bakedModel = bakedModels.get(new ResewnItemModelIdentifier(citModelEntry.getValue().id));
                    if (bakedModel == null)
                        CITResewn.logWarnLoading("Skipping sub cit: Failed loading model for \"" + citModelEntry.getValue().id + "\" in " + cit.propertiesIdentifier + " from " + cit.packName);
                    else
                        cit.type.bakedSubModels.override(citModelEntry.getKey(), bakedModel);
                }
            }
            cit.type.unbakedAssets = null;
        }

        profiler.pop();
    }

    @ModifyArg(method = "loadModelFromJson", at =
    @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceManager;getResource(Lnet/minecraft/util/Identifier;)Lnet/minecraft/resource/Resource;"))
    public Identifier citresewn$fixDuplicatePrefixSuffix(Identifier original) {
        if (original.getPath().startsWith("models/models/") && original.getPath().endsWith(".json.json") && original.getPath().contains("cit"))
            return new Identifier(original.getNamespace(), original.getPath().substring(7, original.getPath().length() - 5));

        return original;
    }
}
