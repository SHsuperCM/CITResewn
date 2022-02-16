package shcm.shsupercm.fabric.citresewn.defaults.mixin.types.item;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.io.IOUtils;
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
import shcm.shsupercm.fabric.citresewn.cit.CITType;
import shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeItem;
import shcm.shsupercm.fabric.citresewn.defaults.common.ResewnItemModelIdentifier;
import shcm.shsupercm.fabric.citresewn.defaults.common.ResewnTextureIdentifier;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static shcm.shsupercm.fabric.citresewn.CITResewn.info;
import static shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeItem.CONTAINER;

@Mixin(ModelLoader.class)
public class ModelLoaderMixin {
    @Shadow @Final private ResourceManager resourceManager;
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

        info("Loading CITItem models...");
        CONTAINER.loaded.values().stream() // todo remove streams usage
                .flatMap(Collection::stream)
                .distinct().forEach(cit -> {
                    try {
                        cit.type.loadUnbakedAssets(resourceManager);

                        for (JsonUnbakedModel unbakedModel : cit.type.unbakedAssets.values()) {
                            ResewnItemModelIdentifier id = new ResewnItemModelIdentifier(unbakedModel.id);
                            this.unbakedModels.put(id, unbakedModel);
                            this.modelsToLoad.addAll(unbakedModel.getModelDependencies());
                            this.modelsToBake.put(id, unbakedModel);
                        }
                    } catch (Exception e) {
                        CITResewn.logErrorLoading(e.getMessage());
                    }
                });

        TypeItem.GENERATED_SUB_CITS_SEEN.clear();
    }

    @Inject(method = "upload", at = @At("RETURN"))
    public void citresewn$linkTypeItemModels(TextureManager textureManager, Profiler profiler, CallbackInfoReturnable<SpriteAtlasManager> cir) {
        if (!ActiveCITs.isActive())
            return;

        profiler.push("citresewn:item_linking");
        info("Linking baked models to CITItems...");

        CONTAINER.loaded.values().stream() // todo remove streams usage
                .flatMap(Collection::stream)
                .distinct().forEach(cit -> {
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
        });

        profiler.pop();
    }


    @Inject(method = "loadModelFromJson", cancellable = true, at = @At("HEAD"))
    public void citresewn$forceLiteralResewnModelIdentifier(Identifier id, CallbackInfoReturnable<JsonUnbakedModel> cir) {
        if (id instanceof ResewnItemModelIdentifier) {
            InputStream is = null;
            Resource resource = null;
            try {
                JsonUnbakedModel json = JsonUnbakedModel.deserialize(IOUtils.toString(is = (resource = resourceManager.getResource(id)).getInputStream(), StandardCharsets.UTF_8));
                json.id = id.toString();
                json.id = json.id.substring(0, json.id.length() - 5);

                ((JsonUnbakedModelAccessor) json).getTextureMap().replaceAll((layer, original) -> {
                    Optional<SpriteIdentifier> left = original.left();
                    if (left.isPresent()) {
                        String originalPath = left.get().getTextureId().getPath();
                        String[] split = originalPath.split("/");
                        if (originalPath.startsWith("./") || (split.length > 2 && split[1].equals("cit"))) {
                            Identifier resolvedIdentifier = CITType.resolveAsset(id, originalPath, "textures", ".png", resourceManager);
                            if (resolvedIdentifier != null)
                                return Either.left(new SpriteIdentifier(left.get().getAtlasId(), new ResewnTextureIdentifier(resolvedIdentifier)));
                        }
                    }
                    return original;
                });

                Identifier parentId = ((JsonUnbakedModelAccessor) json).getParentId();
                if (parentId != null) {
                    String[] parentIdPathSplit = parentId.getPath().split("/");
                    if (parentId.getPath().startsWith("./") || (parentIdPathSplit.length > 2 && parentIdPathSplit[1].equals("cit"))) {
                        parentId = CITType.resolveAsset(id, parentId.getPath(), "models", ".json", resourceManager);
                        if (parentId != null)
                            ((JsonUnbakedModelAccessor) json).setParentId(new ResewnItemModelIdentifier(parentId));
                    }
                }

                json.getOverrides().replaceAll(override -> {
                    String[] modelIdPathSplit = override.getModelId().getPath().split("/");
                    if (override.getModelId().getPath().startsWith("./") || (modelIdPathSplit.length > 2 && modelIdPathSplit[1].equals("cit"))) {
                        Identifier resolvedOverridePath = CITType.resolveAsset(id, override.getModelId().getPath(), "models", ".json", resourceManager);
                        if (resolvedOverridePath != null)
                            return new ModelOverride(new ResewnItemModelIdentifier(resolvedOverridePath), override.streamConditions().collect(Collectors.toList()));
                    }

                    return override;
                });

                cir.setReturnValue(json);
            } catch (Exception ignored) {
            } finally {
                IOUtils.closeQuietly(is, resource);
            }
        }
    }

    @ModifyArg(method = "loadModelFromJson", at =
    @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceManager;getResource(Lnet/minecraft/util/Identifier;)Lnet/minecraft/resource/Resource;"))
    public Identifier citresewn$fixDuplicatePrefixSuffix(Identifier original) {
        if (original.getPath().startsWith("models/models/") && original.getPath().endsWith(".json.json") && original.getPath().contains("cit"))
            return new Identifier(original.getNamespace(), original.getPath().substring(7, original.getPath().length() - 5));

        return original;
    }
}
