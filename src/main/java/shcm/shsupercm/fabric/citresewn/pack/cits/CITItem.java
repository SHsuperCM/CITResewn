package shcm.shsupercm.fabric.citresewn.pack.cits;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.commons.io.IOUtils;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.ex.CITLoadException;
import shcm.shsupercm.fabric.citresewn.ex.CITParseException;
import shcm.shsupercm.fabric.citresewn.mixin.JsonUnbakedModelAccessor;
import shcm.shsupercm.fabric.citresewn.pack.CITPack;
import shcm.shsupercm.fabric.citresewn.pack.ResewnItemModelIdentifier;
import shcm.shsupercm.fabric.citresewn.pack.ResewnTextureIdentifier;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

public class CITItem extends CIT {
    public Map<Identifier, Identifier> assetIdentifiers = new LinkedHashMap<>();
    public Map<Identifier, JsonUnbakedModel> unbakedAssets = new HashMap<>();
    private boolean isTexture = false;

    public BakedModel bakedModel = null;
    public Map<BakedModel, BakedModel> subItems = null;

    public CITItem(CITPack pack, Identifier identifier, Properties properties) throws CITParseException {
        super(pack, identifier, properties);
        try {
            if (this.items.size() == 0)
                throw new Exception("CIT must target at least one item type");

            Identifier assetIdentifier = resolvePath(identifier, properties.getProperty("model"), ".json", pack.resourcePack);
            if (assetIdentifier != null)
                assetIdentifiers.put(null, assetIdentifier);

            for (Object o : properties.keySet())
                if (o instanceof String property && property.startsWith("model.")) {
                    Identifier subIdentifier = resolvePath(identifier, properties.getProperty(property), ".json", pack.resourcePack);
                    if (subIdentifier == null)
                        throw new Exception("Cannot resolve path for " + property);

                    String subItem = property.substring(6);
                    Identifier subItemIdentifier = fixDeprecatedSubItem(subItem);
                    assetIdentifiers.put(subItemIdentifier == null ? new Identifier("minecraft", "item/" + subItem) : subItemIdentifier, subIdentifier);
                }

            if (assetIdentifiers.size() == 0) {
                isTexture = true;
                assetIdentifier = resolvePath(identifier, properties.getProperty("texture"), ".png", pack.resourcePack);
                if (assetIdentifier != null)
                    assetIdentifiers.put(null, assetIdentifier);

                for (Object o : properties.keySet())
                    if (o instanceof String property && property.startsWith("texture.")) {
                        Identifier subIdentifier = resolvePath(identifier, properties.getProperty(property), ".png", pack.resourcePack);
                        if (subIdentifier == null)
                            throw new Exception("Cannot resolve path for " + property);

                        String subItem = property.substring(8);
                        Identifier subItemIdentifier = fixDeprecatedSubItem(subItem);
                        assetIdentifiers.put(subItemIdentifier == null ? new Identifier("minecraft", "item/" + subItem) : subItemIdentifier, subIdentifier);
                    }
            }

            if (assetIdentifiers.size() == 0)
                throw new Exception("Cannot resolve path for model/texture");
        } catch (Exception e) {
            throw new CITParseException(pack.resourcePack, identifier, (e.getClass() == Exception.class ? "" : e.getClass().getSimpleName() + ": ") + e.getMessage());
        }
    }

    public void loadUnbakedAssets(ResourceManager resourceManager) throws CITLoadException {
        try {
            if (isTexture) {
                Function<Void, JsonUnbakedModel> itemModelGenerator = new Function<>() {
                    private final Identifier firstItemIdentifier = Registry.ITEM.getId(items.iterator().next()), firstItemModelIdentifier = new Identifier(firstItemIdentifier.getNamespace(), "models/item/" + firstItemIdentifier.getPath() + ".json");
                    @Override
                    public JsonUnbakedModel apply(Void v) {
                        Resource itemModelResource = null;
                        try {
                            return JsonUnbakedModel.deserialize(IOUtils.toString((itemModelResource = resourceManager.getResource(firstItemModelIdentifier)).getInputStream(), StandardCharsets.UTF_8));
                        } catch (Exception e) {
                            return null;
                        } finally {
                            IOUtils.closeQuietly(itemModelResource);
                        }
                    }
                };

                JsonUnbakedModel itemJson = itemModelGenerator.apply(null);
                Map<String, Either<SpriteIdentifier, String>> textureOverrideMap = new HashMap<>();
                if (((JsonUnbakedModelAccessor) itemJson).getTextureMap().size() > 1) { // use(some/all of) the asset identifiers to build texture override in layered models
                    textureOverrideMap = ((JsonUnbakedModelAccessor) itemJson).getTextureMap();
                    Identifier defaultAsset = assetIdentifiers.get(null);
                    textureOverrideMap.replaceAll((layerName, originalTextureEither) -> {
                        Identifier textureIdentifier = assetIdentifiers.remove(originalTextureEither.map(SpriteIdentifier::getTextureId, Identifier::new));
                        if (textureIdentifier != null)
                            return Either.left(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new ResewnTextureIdentifier(textureIdentifier)));
                        if (defaultAsset != null)
                            return Either.left(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new ResewnTextureIdentifier(defaultAsset)));
                        return null;
                    });

                    if (assetIdentifiers.size() == 0 || (assetIdentifiers.size() == 1 && assetIdentifiers.containsKey(null))) {
                        unbakedAssets.put(null, itemJson);
                        return;
                    }
                }

                Identifier baseIdentifier = assetIdentifiers.remove(null);

                if (baseIdentifier != null)
                    unbakedAssets.put(null, loadUnbakedAsset(resourceManager, textureOverrideMap, itemModelGenerator, baseIdentifier));

                for (Map.Entry<Identifier, Identifier> assetEntry : assetIdentifiers.entrySet())
                    unbakedAssets.put(assetEntry.getKey(), loadUnbakedAsset(resourceManager, textureOverrideMap, itemModelGenerator, assetEntry.getValue()));
            } else {
                Identifier baseIdentifier = assetIdentifiers.remove(null);

                if (baseIdentifier != null)
                    unbakedAssets.put(null, loadUnbakedAsset(resourceManager, null, null, baseIdentifier));

                for (Map.Entry<Identifier, Identifier> assetEntry : assetIdentifiers.entrySet())
                    unbakedAssets.put(assetEntry.getKey(), loadUnbakedAsset(resourceManager, null, null, assetEntry.getValue()));
            }
        } catch (Exception e) {
            throw new CITLoadException(pack.resourcePack, propertiesIdentifier, (e.getClass() == Exception.class ? "" : e.getClass().getSimpleName() + ": ") + e.getMessage());
        } finally {
            assetIdentifiers = null;
        }
    }

    private JsonUnbakedModel loadUnbakedAsset(ResourceManager resourceManager, Map<java.lang.String, Either<SpriteIdentifier, String>> textureOverrideMap, Function<Void, JsonUnbakedModel> itemModelGenerator, Identifier identifier) throws Exception {
        JsonUnbakedModel json;
        if (identifier.getPath().endsWith(".json")) {
            InputStream is = null;
            Resource resource = null;
            try {
                json = JsonUnbakedModel.deserialize(IOUtils.toString(is = (resource = resourceManager.getResource(identifier)).getInputStream(), StandardCharsets.UTF_8));
                json.id = identifier.toString();
                json.id = json.id.substring(0, json.id.length() - 5);

                ((JsonUnbakedModelAccessor) json).getTextureMap().replaceAll((layer, original) -> {
                    Optional<SpriteIdentifier> left = original.left();
                    if (left.isPresent() && left.get().getTextureId().getPath().startsWith("./")) {
                        Identifier resolvedIdentifier = resolvePath(identifier, left.get().getTextureId().getPath(), ".png", pack.resourcePack);
                        if (resolvedIdentifier != null)
                            return Either.left(new SpriteIdentifier(left.get().getAtlasId(), new ResewnTextureIdentifier(resolvedIdentifier)));
                    }
                    return original;
                });

                Identifier parentId = ((JsonUnbakedModelAccessor) json).getParentId();
                if (parentId.getPath().startsWith("./")) {
                    parentId = resolvePath(identifier, parentId.getPath(), ".json", pack.resourcePack);
                    if (parentId != null)
                        ((JsonUnbakedModelAccessor) json).setParentId(new ResewnItemModelIdentifier(parentId));
                }

                return json;
            } finally {
                IOUtils.closeQuietly(is, resource);
            }
        } else if (identifier.getPath().endsWith(".png")) {
            json = itemModelGenerator.apply(null);
            if (json == null)
                json = new JsonUnbakedModel(new Identifier("minecraft", "item/generated"), new ArrayList<>(), ImmutableMap.of("layer0", Either.left(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new ResewnTextureIdentifier(identifier)))), true, JsonUnbakedModel.GuiLight.ITEM, ModelTransformation.NONE, new ArrayList<>());
            json.getOverrides().clear();
            json.id = identifier.toString();
            json.id = json.id.substring(0, json.id.length() - 4);

            ((JsonUnbakedModelAccessor) json).getTextureMap().replaceAll((layerName, originalTextureEither) -> {
                if (textureOverrideMap.size() > 0) {
                    Either<SpriteIdentifier, String> textureOverride = textureOverrideMap.get(layerName);
                    return textureOverride == null ? originalTextureEither : textureOverride;
                } else
                    return Either.left(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new ResewnTextureIdentifier(identifier)));
            });
            return json;
        }

        throw new Exception("Unknown asset type");
    }

    public Identifier fixDeprecatedSubItem(String subItem) {
        String replacement = switch (subItem) {
            case "bow_pulling_standby" -> "bow";
            case "potion_bottle_drinkable" -> "potion";
            case "potion_bottle_splash" -> "splash_potion";
            case "potion_bottle_lingering" -> "lingering_potion";


            default -> null;
        };

        if (replacement != null) {
            CITResewn.logWarnLoading("CIT Warning: Using deprecated sub item id \"" + subItem + "\" instead of \"" + replacement + "\" in " + pack.resourcePack.getName() + " -> " + propertiesIdentifier.getPath());

            return new Identifier("minecraft", "item/" + replacement);
        }

        return null;
    }

    public BakedModel getItemModel(ItemStack stack, Hand hand, BakedModel model, World world, LivingEntity entity) {
        if (test(stack, hand, world, entity)) {
            if (subItems != null) {
                BakedModel subModel = subItems.get(model);
                if (subModel != null)
                    return subModel;
            }

            return this.bakedModel;
        }

        return null;
    }
}
