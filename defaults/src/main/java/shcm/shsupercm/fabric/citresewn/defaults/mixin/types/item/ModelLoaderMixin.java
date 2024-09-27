package shcm.shsupercm.fabric.citresewn.defaults.mixin.types.item;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.cit.CIT;
import shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeItem;
import shcm.shsupercm.fabric.citresewn.defaults.common.ResewnItemModelIdentifier;

import java.util.*;
import java.util.function.BiFunction;

import static shcm.shsupercm.fabric.citresewn.CITResewn.info;
import static shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeItem.CONTAINER;

@Mixin(ModelLoader.class)
public class ModelLoaderMixin {
    @Shadow @Final private Map<Identifier, UnbakedModel> unbakedModels;
    @Shadow @Final private Map</*? >=1.21 {*/ModelIdentifier/*?} else {*//*Identifier*//*?}*/, UnbakedModel> modelsToBake;
    @Shadow @Final private Map</*? >=1.21 {*/ModelIdentifier/*?} else {*//*Identifier*//*?}*/, BakedModel> bakedModels;

    @Inject(method = "<init>", at =
    @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V"))
    public void citresewn$addTypeItemModels(BlockColors blockColors, Profiler profiler, Map map, Map map2, CallbackInfo ci) {
        profiler.swap("citresewn:type_item_models");
        if (!CONTAINER.active())
            return;

        info("Loading item CIT models...");
        for (CIT<TypeItem> cit : CONTAINER.loaded)
            try {
                cit.type.loadUnbakedAssets(MinecraftClient.getInstance().getResourceManager());

                for (JsonUnbakedModel unbakedModel : cit.type.unbakedAssets.values()) {
                    Identifier id = ResewnItemModelIdentifier.pack(Identifier.tryParse(unbakedModel.id));
                    this.unbakedModels.put(id, unbakedModel);
                    this.modelsToBake.put(/*? >=1.21 {*/ModelIdentifier.ofInventoryVariant(id)/*?} else {*//*id*//*?}*/, unbakedModel);
                }
            } catch (Exception e) {
                CITResewn.logErrorLoading("Errored loading model in " + cit.propertiesIdentifier + " from " + cit.packName);
                e.printStackTrace();
            }

        TypeItem.GENERATED_SUB_CITS_SEEN.clear();
    }

    @Inject(method = "bake", at = @At("RETURN"))
    public void citresewn$linkTypeItemModels(/*? <1.21 {*//*BiFunction<Identifier, SpriteIdentifier, Sprite> spriteLoader*//*?} else {*/ModelLoader.SpriteGetter spriteGetter/*?}*/, CallbackInfo ci) {
        if (!CONTAINER.active())
            return;

        info("Linking baked models to item CITs...");

        for (CIT<TypeItem> cit : CONTAINER.loaded) {
            for (Map.Entry<List<ModelOverride.Condition>, JsonUnbakedModel> citModelEntry : cit.type.unbakedAssets.entrySet()) {
                var modelIdentifier = /*? >=1.21 {*/ModelIdentifier.ofInventoryVariant(ResewnItemModelIdentifier.pack(Identifier.of(citModelEntry.getValue().id)))/*?} else {*//*ResewnItemModelIdentifier.pack(Identifier.tryParse(citModelEntry.getValue().id))*//*?}*/;
                if (citModelEntry.getKey() == null) {
                    cit.type.bakedModel = this.bakedModels.get(modelIdentifier);
                } else {
                    BakedModel bakedModel = this.bakedModels.get(modelIdentifier);
                    if (bakedModel == null)
                        CITResewn.logWarnLoading("Skipping sub cit: Failed loading model for \"" + citModelEntry.getValue().id + "\" in " + cit.propertiesIdentifier + " from " + cit.packName);
                    else
                        cit.type.bakedSubModels.override(citModelEntry.getKey(), bakedModel);
                }
            }
            cit.type.unbakedAssets = null;
        }
    }

    @ModifyArg(method = "loadModelFromJson", at =
    @At(value = "INVOKE", ordinal = 1, target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
    public Object citresewn$fixDuplicatePrefixSuffix(Object key) {
        Identifier original = (Identifier) key;
        if (CONTAINER.active() && original.getPath().startsWith("models/models/") && original.getPath().endsWith(".json.json") && original.getPath().contains("cit"))
            return Identifier.of(original.getNamespace(), original.getPath().substring(7, original.getPath().length() - 5));

        return original;
    }
}
