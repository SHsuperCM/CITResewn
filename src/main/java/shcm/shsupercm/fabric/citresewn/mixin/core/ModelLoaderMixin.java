package shcm.shsupercm.fabric.citresewn.mixin.core;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shcm.shsupercm.fabric.citresewn.ActiveCITs;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.config.CITResewnConfig;
import shcm.shsupercm.fabric.citresewn.pack.CITParser;
import shcm.shsupercm.fabric.citresewn.pack.ResewnItemModelIdentifier;
import shcm.shsupercm.fabric.citresewn.pack.ResewnTextureIdentifier;
import shcm.shsupercm.fabric.citresewn.pack.cits.CIT;
import shcm.shsupercm.fabric.citresewn.pack.cits.CITItem;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static shcm.shsupercm.fabric.citresewn.CITResewn.info;

@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {
    @Shadow @Final private ResourceManager resourceManager;
    @Shadow @Final private Set<Identifier> modelsToLoad;
    @Shadow @Final private Map<Identifier, UnbakedModel> unbakedModels;
    @Shadow @Final private Map<Identifier, UnbakedModel> modelsToBake;
    @Shadow @Final private Map<Identifier, BakedModel> bakedModels;
    @Shadow protected abstract JsonUnbakedModel loadModelFromJson(Identifier id) throws IOException;

    private Map<Identifier, BakedModel> citOverrideCacheMap = new HashMap<>();

    @Inject(method = "addModel", at = @At("TAIL"))
    public void loadCIT(ModelIdentifier eventModelId, CallbackInfo ci) { if (eventModelId != ModelLoader.MISSING_ID) return;
        if (CITResewn.INSTANCE.activeCITs != null) {
            info("Clearing active CITs..");
            CITResewn.INSTANCE.activeCITs.dispose();
            CITResewn.INSTANCE.activeCITs = null;
        }

        if (!CITResewnConfig.INSTANCE().enabled)
            return;

        info("Loading CIT Resewn..");

        info("Parsing CITs...");
        Collection<CIT> parsed = CITParser.parseCITs(resourceManager.streamResourcePacks().collect(Collectors.toCollection(ArrayList::new)));

        if (parsed.size() > 0) {
            info("Loading CITItem models..");
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
                        CITResewn.logErrorLoading(e.getMessage());
                    }
                }

            info("Activating CITs...");
            CITResewn.INSTANCE.activeCITs = new ActiveCITs(parsed);
        } else
            info("No cit packs found.");
    }

    @Inject(method = "bake", at = @At("RETURN"))
    public void onBake(Identifier id, ModelBakeSettings settings, CallbackInfoReturnable<BakedModel> cir) {
        if (CITResewn.INSTANCE.activeCITs == null)
            return;

        this.citOverrideCacheMap.put(id, cir.getReturnValue());
    }

    @Inject(method = "upload", at = @At("RETURN"))
    public void linkBakedModels(TextureManager textureManager, Profiler profiler, CallbackInfoReturnable<SpriteAtlasManager> cir) {
        if (CITResewn.INSTANCE.activeCITs == null)
            return;

        profiler.push("citresewn_linking");
        info("Linking baked models to CITItems...");

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
                            CITResewn.logErrorLoading("Skipping sub cit: No such sub item model \"" + citModelEntry.getKey().getPath().substring(5) + "\" in " + citItem.pack.resourcePack.getName() + " -> " + citItem.propertiesIdentifier.getPath());
                        }
                    }
                }
                citItem.unbakedAssets = null;
            }
        }

        this.citOverrideCacheMap = null;

        profiler.pop();
    }


    @Inject(method = "loadModelFromJson", cancellable = true, at = @At("HEAD"))
    public void forceLiteralResewnModelIdentifier(Identifier id, CallbackInfoReturnable<JsonUnbakedModel> cir) {
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
                            Identifier resolvedIdentifier = CIT.resolvePath(id, originalPath, ".png", identifier -> resourceManager.containsResource(identifier));
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
                        parentId = CIT.resolvePath(id, parentId.getPath(), ".json", identifier -> resourceManager.containsResource(identifier));
                        if (parentId != null)
                            ((JsonUnbakedModelAccessor) json).setParentId(new ResewnItemModelIdentifier(parentId));
                    }
                }

                cir.setReturnValue(json);
            } catch (Exception ignored) {
            } finally {
                IOUtils.closeQuietly(is, resource);
            }
        }
    }

    @Redirect(method = "loadModelFromJson", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceManager;getResource(Lnet/minecraft/util/Identifier;)Lnet/minecraft/resource/Resource;"))
    public Resource getResource(ResourceManager resourceManager, Identifier id) throws IOException {
        if (id.getPath().endsWith(".json.json") && id.getPath().contains("cit"))
            return resourceManager.getResource(new Identifier(id.getNamespace(), id.getPath().substring(7, id.getPath().length() - 5)));
        return resourceManager.getResource(id);
    }
}
