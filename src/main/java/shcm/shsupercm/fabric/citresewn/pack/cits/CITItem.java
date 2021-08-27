package shcm.shsupercm.fabric.citresewn.pack.cits;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import shcm.shsupercm.fabric.citresewn.ex.CITLoadException;
import shcm.shsupercm.fabric.citresewn.ex.CITParseException;
import shcm.shsupercm.fabric.citresewn.pack.CITPack;
import shcm.shsupercm.fabric.citresewn.pack.ResewnTextureIdentifier;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CITItem extends CIT {
    public Map<Identifier, Identifier> assetIdentifiers = new HashMap<>();
    public Map<Identifier, JsonUnbakedModel> unbakedAssets = new HashMap<>();

    public BakedModel bakedModel = null;
    public Map<BakedModel, BakedModel> subItems = null;

    public CITItem(CITPack pack, Identifier identifier, Properties properties) throws CITParseException {
        super(pack, identifier, properties);
        try {
            Identifier assetIdentifier = resolvePath(identifier, properties.getProperty("texture"), ".png", pack.resourcePack);
            if (assetIdentifier != null)
                assetIdentifiers.put(null, assetIdentifier);

            assetIdentifier = resolvePath(identifier, properties.getProperty("model"), ".json", pack.resourcePack);
            if (assetIdentifier != null)
                assetIdentifiers.put(null, assetIdentifier);

            for (Object o : properties.keySet())
                if (o instanceof String property && property.startsWith("texture.")) {
                    Identifier subIdentifier = resolvePath(identifier, properties.getProperty(property), ".png", pack.resourcePack);
                    if (subIdentifier == null)
                        throw new Exception("Cannot resolve path for " + property);
                    assetIdentifiers.put(new Identifier("minecraft", "item/" + property.substring(8)), subIdentifier);
                }

            for (Object o : properties.keySet())
                if (o instanceof String property && property.startsWith("model.")) {
                    Identifier subIdentifier = resolvePath(identifier, properties.getProperty(property), ".json", pack.resourcePack);
                    if (subIdentifier == null)
                        throw new Exception("Cannot resolve path for " + property);
                    assetIdentifiers.put(new Identifier("minecraft", "item/" + property.substring(6)), subIdentifier);
                }

            if (assetIdentifiers.size() == 0)
                throw new Exception("Cannot resolve path for model/texture");
        } catch (Exception e) {
            throw new CITParseException(pack.resourcePack, identifier, (e.getClass() == Exception.class ? "" : e.getClass().getSimpleName() + ": ") + e.getMessage());
        }
    }

    public void loadUnbakedAssets(ResourceManager resourceManager) throws CITLoadException {
        try {
            Identifier baseIdentifier = assetIdentifiers.remove(null);

            if (baseIdentifier != null)
                unbakedAssets.put(null, loadUnbakedAsset(resourceManager, baseIdentifier));

            for (Map.Entry<Identifier, Identifier> assetEntry : assetIdentifiers.entrySet())
                unbakedAssets.put(assetEntry.getKey(), loadUnbakedAsset(resourceManager, assetEntry.getValue()));

            assetIdentifiers = null;
        } catch (Exception e) {
            throw new CITLoadException(pack.resourcePack, propertiesIdentifier, (e.getClass() == Exception.class ? "" : e.getClass().getSimpleName() + ": ") + e.getMessage());
        }
    }

    private JsonUnbakedModel loadUnbakedAsset(ResourceManager resourceManager, Identifier identifier) throws Exception {
        InputStream is = null;
        try {
            JsonUnbakedModel json;
            if (identifier.getPath().endsWith(".json")) {
                json = JsonUnbakedModel.deserialize(IOUtils.toString(is = resourceManager.getResource(identifier).getInputStream(), StandardCharsets.UTF_8));
                json.id = identifier.toString(); json.id = json.id.substring(0, json.id.length() - 5);
                return json;
            } else if (identifier.getPath().endsWith(".png")) {
                json = new JsonUnbakedModel(new Identifier("minecraft", "item/generated"), new ArrayList<>(), ImmutableMap.of("layer0", Either.left(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new ResewnTextureIdentifier(identifier)))), true, JsonUnbakedModel.GuiLight.ITEM, ModelTransformation.NONE, new ArrayList<>());
                json.id = identifier.toString(); json.id = json.id.substring(0, json.id.length() - 4);
                return json;
            }
        } finally {
            IOUtils.closeQuietly(is);
        }
        throw new Exception("Unknown asset type");
    }
}
