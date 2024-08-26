package shcm.shsupercm.fabric.citresewn.defaults.cit.types;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Either;
import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.api.CITTypeContainer;
import shcm.shsupercm.fabric.citresewn.cit.*;
import shcm.shsupercm.fabric.citresewn.defaults.cit.conditions.ConditionItems;
import shcm.shsupercm.fabric.citresewn.defaults.common.ResewnItemModelIdentifier;
import shcm.shsupercm.fabric.citresewn.defaults.mixin.types.item.JsonUnbakedModelAccessor;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyKey;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ///// PORTED FROM BETA \\\\\
 * This shit was ported from the
 * beta and will be rewritten at
 * some point!
 * \\\\\                  /////
 */
public class TypeItem extends CITType {
    @Entrypoint(CITTypeContainer.ENTRYPOINT)
    public static final Container CONTAINER = new Container();

    private static final String GENERATED_SUB_CITS_PREFIX = "sub_cititem_generated_";
    public static final Set<Identifier> GENERATED_SUB_CITS_SEEN = new HashSet<>();

    private final List<Item> items = new ArrayList<>();

    public Map<Identifier, Identifier> assetIdentifiers = new LinkedHashMap<>();
    public Map<List<ModelOverride.Condition>, JsonUnbakedModel> unbakedAssets = new LinkedHashMap<>();
    private Map<String, Either<SpriteIdentifier, String>> textureOverrideMap = new HashMap<>();
    private boolean isTexture = false;

    public BakedModel bakedModel = null;
    public CITOverrideList bakedSubModels = new CITOverrideList();

    @Override
    public Set<PropertyKey> typeProperties() {
        return Set.of(PropertyKey.of("model"), PropertyKey.of("texture"), PropertyKey.of("tile"));
    }

    @Override
    public void load(List<CITCondition> conditions, PropertyGroup properties, ResourceManager resourceManager) throws CITParsingException {
        for (CITCondition condition : conditions)
            if (condition instanceof ConditionItems conditionItems)
                items.addAll(Arrays.asList(conditionItems.items));

        if (this.items.size() == 0)
            try {
                Identifier propertiesName = Identifier.tryParse(properties.stripName());
                if (!Registries.ITEM.containsId(propertiesName))
                    throw new Exception();
                Item item = Registries.ITEM.get(propertiesName);
                conditions.add(new ConditionItems(item));
                this.items.add(item);
            } catch (Exception ignored) {
                throw new CITParsingException("Not targeting any item type", properties, -1);
            }

        Identifier assetIdentifier;
        PropertyValue modelProp = properties.getLastWithoutMetadata("citresewn", "model");
        boolean containsTexture = modelProp == null && !properties.get("citresewn", "texture", "tile").isEmpty();

        if (!containsTexture) {
            assetIdentifier = resolveAsset(properties.identifier, modelProp, "models", ".json", resourceManager);
            if (assetIdentifier != null)
                assetIdentifiers.put(null, assetIdentifier);
            else if (modelProp != null) {
                assetIdentifier = resolveAsset(properties.identifier, modelProp, "models", ".json", resourceManager);
                if (assetIdentifier != null)
                    assetIdentifiers.put(null, assetIdentifier);
            }
        }

        for (PropertyValue property : properties.get("citresewn", "model")) {
            Identifier subIdentifier = resolveAsset(properties.identifier, property, "models", ".json", resourceManager);
            if (subIdentifier == null)
                throw new CITParsingException("Cannot resolve path", properties, property.position());

            String subItem = property.keyMetadata();
            Identifier subItemIdentifier = fixDeprecatedSubItem(subItem, properties, property.position());
            assetIdentifiers.put(subItemIdentifier == null ? Identifier.of("minecraft", "item/" + subItem) : subItemIdentifier, subIdentifier);
        }

        if (assetIdentifiers.size() == 0) { // attempt to load texture
            isTexture = true;
            PropertyValue textureProp = properties.getLastWithoutMetadata("citresewn", "texture", "tile");
            assetIdentifier = resolveAsset(properties.identifier, textureProp, "textures", ".png", resourceManager);
            if (assetIdentifier != null)
                assetIdentifiers.put(null, assetIdentifier);

            for (PropertyValue property : properties.get("citresewn", "texture", "tile")) {
                if (property.keyMetadata() == null)
                    continue;
                Identifier subIdentifier = resolveAsset(properties.identifier, property, "textures", ".png", resourceManager);
                if (subIdentifier == null)
                    throw new CITParsingException("Cannot resolve path", properties, property.position());

                String subItem = property.keyMetadata();
                Identifier subItemIdentifier = fixDeprecatedSubItem(subItem, properties, property.position());
                assetIdentifiers.put(subItemIdentifier == null ? Identifier.of("minecraft", "item/" + subItem) : subItemIdentifier, subIdentifier);
            }
        } else { // attempt to load textureOverrideMap from textures
            PropertyValue textureProp = properties.getLastWithoutMetadata("citresewn", "texture", "tile");
            if (textureProp != null) {
                assetIdentifier = resolveAsset(properties.identifier, textureProp, "textures", ".png", resourceManager);
                if (assetIdentifier != null)
                    textureOverrideMap.put(null, Either.left(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, assetIdentifier)));
                else
                    throw new CITParsingException("Cannot resolve path", properties, textureProp.position());
            }

            for (PropertyValue property : properties.get("citresewn", "texture", "tile")) {
                textureProp = property;
                Identifier subIdentifier = resolveAsset(properties.identifier, textureProp, "textures", ".png", resourceManager);
                if (subIdentifier == null)
                    throw new CITParsingException("Cannot resolve path", properties, property.position());

                textureOverrideMap.put(property.keyMetadata(), Either.left(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, subIdentifier)));
            }
        }

        if (assetIdentifiers.size() == 0)
            throw new CITParsingException("Could not resolve a replacement model/texture", properties, -1);
    }

    public void loadUnbakedAssets(ResourceManager resourceManager) throws Exception {
        try {
            if (isTexture) {
                JsonUnbakedModel itemJson = getModelForFirstItemType(resourceManager);
                if (((JsonUnbakedModelAccessor) itemJson).getTextureMap().size() > 1) { // use(some/all of) the asset identifiers to build texture override in layered models
                    textureOverrideMap = ((JsonUnbakedModelAccessor) itemJson).getTextureMap();
                    Identifier defaultAsset = assetIdentifiers.get(null);
                    textureOverrideMap.replaceAll((layerName, originalTextureEither) -> {
                        Identifier textureIdentifier = assetIdentifiers.remove(originalTextureEither.map(SpriteIdentifier::getTextureId, Identifier::tryParse));
                        if (textureIdentifier != null)
                            return Either.left(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, textureIdentifier));
                        if (defaultAsset != null)
                            return Either.left(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, defaultAsset));
                        return null;
                    });

                    if (assetIdentifiers.size() == 0 || (assetIdentifiers.size() == 1 && assetIdentifiers.containsKey(null))) {
                        unbakedAssets.put(null, itemJson);
                        return;
                    }
                }

                Identifier baseIdentifier = assetIdentifiers.remove(null);

                if (baseIdentifier != null) {
                    unbakedAssets.put(null, loadUnbakedAsset(resourceManager, baseIdentifier, null));

                    if (this.items.contains(Items.GOAT_HORN) && !assetIdentifiers.containsKey(Identifier.of("minecraft", "item/tooting_goat_horn"))) // HOTFIX: TOOTING GOAT HORN WITH TEXTURE ASSET
                        assetIdentifiers.put(Identifier.of("minecraft", "item/tooting_goat_horn"), baseIdentifier);
                }

                if (!assetIdentifiers.isEmpty()) { // contains sub models
                    LinkedHashMap<Identifier, List<ModelOverride.Condition>> overrideConditions = new LinkedHashMap<>();
                    for (Item item : this.items) {
                        Identifier itemIdentifier = Registries.ITEM.getId(item);
                        overrideConditions.put(Identifier.of(itemIdentifier.getNamespace(), "item/" + itemIdentifier.getPath()), Collections.emptyList());

                        Identifier itemModelIdentifier = Identifier.of(itemIdentifier.getNamespace(), "models/item/" + itemIdentifier.getPath() + ".json");
                        try (Reader resourceReader = new InputStreamReader(resourceManager.getResource(itemModelIdentifier).orElseThrow().getInputStream())) {
                            JsonUnbakedModel itemModelJson = JsonUnbakedModel.deserialize(resourceReader);

                            if (itemModelJson.getOverrides() != null && !itemModelJson.getOverrides().isEmpty())
                                for (ModelOverride override : itemModelJson.getOverrides())
                                    overrideConditions.put(override.getModelId(), override.streamConditions().toList());
                        }
                    }

                    ArrayList<Identifier> overrideModels = new ArrayList<>(overrideConditions.keySet());
                    Collections.reverse(overrideModels);

                    for (Identifier overrideModel : overrideModels) {
                        Identifier replacement = assetIdentifiers.remove(overrideModel);
                        if (replacement == null)
                            continue;

                        List<ModelOverride.Condition> conditions = overrideConditions.get(overrideModel);

                        if (overrideModel != null) {
                            GENERATED_SUB_CITS_SEEN.add(replacement);
                            replacement = Identifier.of(replacement.getNamespace(), GENERATED_SUB_CITS_PREFIX + GENERATED_SUB_CITS_SEEN.size() + "_" + replacement.getPath());
                        }

                        unbakedAssets.put(conditions, loadUnbakedAsset(resourceManager, replacement, overrideModel));
                    }
                }
            } else { // isModel
                Identifier baseIdentifier = assetIdentifiers.remove(null);

                if (baseIdentifier != null) {
                    if (!GENERATED_SUB_CITS_SEEN.add(baseIdentifier)) // cit generated duplicate
                        baseIdentifier = Identifier.of(baseIdentifier.getNamespace(), GENERATED_SUB_CITS_PREFIX + GENERATED_SUB_CITS_SEEN.size() + "_" + baseIdentifier.getPath());
                    GENERATED_SUB_CITS_SEEN.add(baseIdentifier);

                    JsonUnbakedModel model = loadUnbakedAsset(resourceManager, baseIdentifier, null);
                    unbakedAssets.put(null, model);

                    if (model.getOverrides().size() > 0 && textureOverrideMap.size() > 0) {
                        LinkedHashMap<Identifier, List<ModelOverride.Condition>> overrideConditions = new LinkedHashMap<>();

                        for (ModelOverride override : model.getOverrides())
                            overrideConditions.put(override.getModelId(), override.streamConditions().toList());

                        ArrayList<Identifier> overrideModels = new ArrayList<>(overrideConditions.keySet());
                        Collections.reverse(overrideModels);

                        for (Identifier overrideModel : overrideModels) {
                            Identifier replacement = resolveAsset(baseIdentifier, overrideModel.toString(), "models", ".json", resourceManager);
                            if (replacement != null) {
                                String subTexturePath = replacement.toString().substring(0, replacement.toString().lastIndexOf('.'));
                                final String subTextureName = subTexturePath.substring(subTexturePath.lastIndexOf('/') + 1);

                                replacement = baseIdentifier;
                                if (!GENERATED_SUB_CITS_SEEN.add(replacement)) // cit generated duplicate
                                    replacement = Identifier.of(replacement.getNamespace(), GENERATED_SUB_CITS_PREFIX + GENERATED_SUB_CITS_SEEN.size() + "_" + replacement.getPath());
                                GENERATED_SUB_CITS_SEEN.add(replacement);

                                JsonUnbakedModel jsonModel = loadUnbakedAsset(resourceManager, replacement, null);
                                jsonModel.getOverrides().clear();

                                ((JsonUnbakedModelAccessor) jsonModel).getTextureMap().replaceAll((layerName, texture) -> {
                                    if (layerName != null)
                                        try {
                                            for (String subTexture : textureOverrideMap.keySet())
                                                if (subTextureName.equals(subTexture))
                                                    return textureOverrideMap.get(subTexture);
                                        } catch (Exception ignored) { }
                                    return texture;
                                });

                                unbakedAssets.put(overrideConditions.get(overrideModel), jsonModel);
                            }
                        }
                    }
                }

                if (!assetIdentifiers.isEmpty()) { // contains sub models
                    LinkedHashMap<Identifier, List<ModelOverride.Condition>> overrideConditions = new LinkedHashMap<>();
                    for (Item item : this.items) {
                        Identifier itemIdentifier = Registries.ITEM.getId(item);
                        overrideConditions.put(Identifier.of(itemIdentifier.getNamespace(), "item/" + itemIdentifier.getPath()), Collections.emptyList());

                        Identifier itemModelIdentifier = Identifier.of(itemIdentifier.getNamespace(), "models/item/" + itemIdentifier.getPath() + ".json");
                        try (Reader resourceReader = new InputStreamReader( resourceManager.getResource(itemModelIdentifier).orElseThrow().getInputStream())) {
                            JsonUnbakedModel itemModelJson = JsonUnbakedModel.deserialize(resourceReader);

                            if (itemModelJson.getOverrides() != null && !itemModelJson.getOverrides().isEmpty())
                                for (ModelOverride override : itemModelJson.getOverrides())
                                    overrideConditions.put(override.getModelId(), override.streamConditions().toList());
                        }
                    }

                    ArrayList<Identifier> overrideModels = new ArrayList<>(overrideConditions.keySet());
                    Collections.reverse(overrideModels);

                    for (Identifier overrideModel : overrideModels) {
                        Identifier replacement = assetIdentifiers.remove(overrideModel);
                        if (replacement == null)
                            continue;

                        if (!GENERATED_SUB_CITS_SEEN.add(replacement)) // cit generated duplicate
                            replacement = Identifier.of(replacement.getNamespace(), GENERATED_SUB_CITS_PREFIX + GENERATED_SUB_CITS_SEEN.size() + "_" + replacement.getPath());
                        GENERATED_SUB_CITS_SEEN.add(replacement);

                        List<ModelOverride.Condition> conditions = overrideConditions.get(overrideModel);
                        unbakedAssets.put(conditions, loadUnbakedAsset(resourceManager, replacement, null));
                    }
                }
            }
        } finally {
            assetIdentifiers = null;
            textureOverrideMap = null;
        }
    }

    private JsonUnbakedModel loadUnbakedAsset(ResourceManager resourceManager, Identifier assetIdentifier, Identifier overrideModel) throws Exception {
        final Identifier identifier;
        {
            Identifier possibleIdentifier = assetIdentifier;
            while (possibleIdentifier.getPath().startsWith(GENERATED_SUB_CITS_PREFIX))
                possibleIdentifier = Identifier.of(possibleIdentifier.getNamespace(), possibleIdentifier.getPath().substring(possibleIdentifier.getPath().substring(GENERATED_SUB_CITS_PREFIX.length()).indexOf('_') + GENERATED_SUB_CITS_PREFIX.length() + 1));
            identifier = possibleIdentifier;
        }
        JsonUnbakedModel json;
        if (identifier.getPath().endsWith(".json")) {
            try (InputStream is = resourceManager.getResource(identifier).orElseThrow().getInputStream()) {
                json = JsonUnbakedModel.deserialize(IOUtils.toString(is, StandardCharsets.UTF_8));
                json.id = assetIdentifier.toString();
                json.id = json.id.substring(0, json.id.length() - 5);

                ((JsonUnbakedModelAccessor) json).getTextureMap().replaceAll((layer, original) -> {
                    Optional<SpriteIdentifier> left = original.left();
                    if (left.isPresent()) {
                        Identifier resolvedIdentifier = resolveAsset(identifier, left.get().getTextureId().getPath(), "textures", ".png", resourceManager);
                        if (resolvedIdentifier != null)
                            return Either.left(new SpriteIdentifier(left.get().getAtlasId(), resolvedIdentifier));
                    }
                    return original;
                });

                if (textureOverrideMap.size() > 0) {
                    Map<String, Either<SpriteIdentifier, String>> jsonTextureMap = ((JsonUnbakedModelAccessor) json).getTextureMap();
                    if (jsonTextureMap.size() == 0)
                        jsonTextureMap.put("layer0", null);

                    final Either<SpriteIdentifier, String> defaultTextureOverride = textureOverrideMap.get(null);
                    if (defaultTextureOverride != null)
                        jsonTextureMap.replaceAll((layerName, spriteIdentifierStringEither) -> defaultTextureOverride);

                    //jsonTextureMap.putAll(textureOverrideMap);
                    jsonTextureMap.replaceAll((layerName, texture) -> {
                        if (layerName != null)
                            try {
                                String[] split = texture.map(id -> id.getTextureId().getPath(), s -> s).split("/");
                                String textureName = split[split.length - 1];
                                if (textureName.endsWith(".png"))
                                    textureName = textureName.substring(0, textureName.length() - 4);
                                return Objects.requireNonNull(textureOverrideMap.get(textureName));
                            } catch (Exception ignored) { }
                        return texture;
                    });
                    jsonTextureMap.values().removeIf(Objects::isNull);
                }

                Identifier parentId = ((JsonUnbakedModelAccessor) json).getParentId();
                if (parentId != null) {
                    String[] parentIdPathSplit = parentId.getPath().split("/");
                    if (parentId.getPath().startsWith("./") || (parentIdPathSplit.length > 2 && parentIdPathSplit[1].equals("cit"))) {
                        parentId = resolveAsset(identifier, parentId.getPath(), "models", ".json", resourceManager);
                        if (parentId != null)
                            ((JsonUnbakedModelAccessor) json).setParentId(ResewnItemModelIdentifier.pack(parentId));
                    }
                }

                json.getOverrides().replaceAll(override -> {
                    String[] modelIdPathSplit = override.getModelId().getPath().split("/");
                    if (override.getModelId().getPath().startsWith("./") || (modelIdPathSplit.length > 2 && modelIdPathSplit[1].equals("cit"))) {
                        Identifier resolvedOverridePath = resolveAsset(identifier, override.getModelId().getPath(), "models", ".json", resourceManager);
                        if (resolvedOverridePath != null)
                            return new ModelOverride(ResewnItemModelIdentifier.pack(resolvedOverridePath), override.streamConditions().collect(Collectors.toList()));
                    }

                    return override;
                });

                return json;
            }
        } else if (identifier.getPath().endsWith(".png")) {
            json = overrideModel == null ? getModelForFirstItemType(resourceManager) : getModelFromOverrideModel(resourceManager, overrideModel);
            if (json == null)
                json = new JsonUnbakedModel(Identifier.of("minecraft", "item/generated"), new ArrayList<>(), ImmutableMap.of("layer0", Either.left(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, identifier))), true, JsonUnbakedModel.GuiLight.ITEM, ModelTransformation.NONE, new ArrayList<>());
            json.getOverrides().clear();
            json.id = assetIdentifier.toString();
            json.id = json.id.substring(0, json.id.length() - 4);

            ((JsonUnbakedModelAccessor) json).getTextureMap().replaceAll((layerName, originalTextureEither) -> {
                if (textureOverrideMap.size() > 0) {
                    Either<SpriteIdentifier, String> textureOverride = textureOverrideMap.get(layerName);
                    if (textureOverride == null)
                        textureOverride = textureOverrideMap.get(null);
                    return textureOverride == null ? originalTextureEither : textureOverride;
                } else
                    return Either.left(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, identifier));
            });
            return json;
        }

        throw new Exception("Unknown asset type");
    }

    public Identifier fixDeprecatedSubItem(String subItem, PropertyGroup properties, int position) {
        if (subItem == null)
            return null;
        String replacement = switch (subItem) {
            case "bow_standby" -> "bow";
            case "crossbow_standby" -> "crossbow";
            case "potion_bottle_drinkable" -> "potion";
            case "potion_bottle_splash" -> "splash_potion";
            case "potion_bottle_lingering" -> "lingering_potion";


            default -> null;
        };

        if (replacement != null) {
            CITResewn.logWarnLoading(properties.messageWithDescriptorOf("Warning: Using deprecated sub item id \"" + subItem + "\" instead of \"" + replacement + "\"", position));

            return Identifier.of("minecraft", "item/" + replacement);
        }

        return null;
    }

    private JsonUnbakedModel getModelForFirstItemType(ResourceManager resourceManager) {
        Identifier firstItemIdentifier = Registries.ITEM.getId(this.items.iterator().next()), firstItemModelIdentifier = Identifier.of(firstItemIdentifier.getNamespace(), "models/item/" + firstItemIdentifier.getPath() + ".json");
        try (InputStream is = resourceManager.getResource(firstItemModelIdentifier).orElseThrow().getInputStream()) {
            JsonUnbakedModel json = JsonUnbakedModel.deserialize(IOUtils.toString(is, StandardCharsets.UTF_8));

            if (((JsonUnbakedModelAccessor) json).getParentId().equals(Identifier.of("minecraft", "item/template_spawn_egg"))) { // HOTFIX: Fixes not being able to change spawn eggs using texture cits
                try (InputStream parentInputStream = resourceManager.getResource(Identifier.of("minecraft", "models/item/template_spawn_egg.json")).orElseThrow().getInputStream()) {
                    json = JsonUnbakedModel.deserialize(IOUtils.toString(parentInputStream, StandardCharsets.UTF_8));
                    ((JsonUnbakedModelAccessor) json).getTextureMap().remove("layer1"); // PARITY
                }
            }

            if (!GENERATED_SUB_CITS_SEEN.add(firstItemModelIdentifier)) // cit generated duplicate
                firstItemModelIdentifier = Identifier.of(firstItemModelIdentifier.getNamespace(), GENERATED_SUB_CITS_PREFIX + GENERATED_SUB_CITS_SEEN.size() + "_" + firstItemModelIdentifier.getPath());
            GENERATED_SUB_CITS_SEEN.add(firstItemModelIdentifier);

            json.id = firstItemModelIdentifier.toString();
            json.id = json.id.substring(0, json.id.length() - 5);
            return json;
        } catch (Exception e) {
            return null;
        }
    }

    private JsonUnbakedModel getModelFromOverrideModel(ResourceManager resourceManager, Identifier overrideModel) {
        Identifier modelIdentifier = Identifier.of(overrideModel.getNamespace(), "models/" + overrideModel.getPath() + ".json");
        try (InputStream is = resourceManager.getResource(modelIdentifier).orElseThrow().getInputStream()) {
            JsonUnbakedModel json = JsonUnbakedModel.deserialize(IOUtils.toString(is, StandardCharsets.UTF_8));

            if (!GENERATED_SUB_CITS_SEEN.add(modelIdentifier)) // cit generated duplicate
                modelIdentifier = Identifier.of(modelIdentifier.getNamespace(), GENERATED_SUB_CITS_PREFIX + GENERATED_SUB_CITS_SEEN.size() + "_" + modelIdentifier.getPath());
            GENERATED_SUB_CITS_SEEN.add(modelIdentifier);

            json.id = modelIdentifier.toString();
            json.id = json.id.substring(0, json.id.length() - 5);
            return json;
        } catch (Exception e) {
            return null;
        }
    }

    public BakedModel getItemModel(CITContext context, int seed) {
        ClientWorld world = context.world instanceof ClientWorld clientWorld ? clientWorld : null;
        // get sub items or bakedModel if no sub item matches @Nullable
        BakedModel bakedModel = bakedSubModels.apply(this.bakedModel, context.stack, world, context.entity, seed);

        // apply model overrides
        if (bakedModel != null && bakedModel.getOverrides() != null)
            bakedModel = bakedModel.getOverrides().apply(bakedModel, context.stack, world, context.entity, seed);

        return bakedModel;
    }

    public static class CITOverrideList extends ModelOverrideList {
        public void override(List<ModelOverride.Condition> key, BakedModel bakedModel) {
            Set<Identifier> conditionTypes = new LinkedHashSet<>(Arrays.asList(this.conditionTypes));
            for (ModelOverride.Condition condition : key)
                conditionTypes.add(condition.getType());
            this.conditionTypes = conditionTypes.toArray(new Identifier[0]);

            this.overrides = Arrays.copyOf(this.overrides, this.overrides.length + 1);

            Object2IntMap<Identifier> object2IntMap = new Object2IntOpenHashMap<>();
            for(int i = 0; i < this.conditionTypes.length; ++i)
                object2IntMap.put(this.conditionTypes[i], i);

            this.overrides[this.overrides.length - 1] = new BakedOverride(
                    key.stream()
                            .map((condition) -> new InlinedCondition(object2IntMap.getInt(condition.getType()), condition.getThreshold()))
                            .toArray(InlinedCondition[]::new)
                    , bakedModel);
        }
    }

    public static class Container extends CITTypeContainer<TypeItem> {
        public Container() {
            super(TypeItem.class, TypeItem::new, "item");
        }

        public Set<CIT<TypeItem>> loaded = new HashSet<>();
        public Map<Item, Set<CIT<TypeItem>>> loadedTyped = new IdentityHashMap<>();

        @Override
        public void load(List<CIT<TypeItem>> parsedCITs) {
            loaded.addAll(parsedCITs);
            for (CIT<TypeItem> cit : parsedCITs)
                for (CITCondition condition : cit.conditions)
                    if (condition instanceof ConditionItems items)
                        for (Item item : items.items)
                            if (item != null)
                                loadedTyped.computeIfAbsent(item, i -> new LinkedHashSet<>()).add(cit);
        }

        @Override
        public void dispose() {
            loaded.clear();
            loadedTyped.clear();
        }

        public CIT<TypeItem> getCIT(CITContext context, int seed) {
            return ((CITCacheItem) (Object) context.stack).citresewn$getCacheTypeItem().get(context).get();
        }

        public CIT<TypeItem> getRealTimeCIT(CITContext context) {
            Set<CIT<TypeItem>> loadedForItemType = loadedTyped.get(context.stack.getItem());
            if (loadedForItemType != null)
                for (CIT<TypeItem> cit : loadedForItemType)
                    if (cit.test(context))
                        return cit;

            return null;
        }
    }

    public interface CITCacheItem {
        CITCache.Single<TypeItem> citresewn$getCacheTypeItem();
    }

    public interface BakedModelManagerMixinAccess {
        void citresewn$forceMojankModel(BakedModel model);
    }
}