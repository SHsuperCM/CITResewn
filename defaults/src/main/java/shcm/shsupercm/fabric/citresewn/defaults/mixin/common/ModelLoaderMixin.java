package shcm.shsupercm.fabric.citresewn.defaults.mixin.common;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shcm.shsupercm.fabric.citresewn.cit.CITType;
import shcm.shsupercm.fabric.citresewn.defaults.common.ResewnItemModelIdentifier;
import shcm.shsupercm.fabric.citresewn.defaults.common.ResewnTextureIdentifier;
import shcm.shsupercm.fabric.citresewn.defaults.mixin.types.item.JsonUnbakedModelAccessor;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Will be rewritten at some point.
 */
@Mixin(ModelLoader.class)
public class ModelLoaderMixin {
    @Shadow @Final private ResourceManager resourceManager;

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
}
