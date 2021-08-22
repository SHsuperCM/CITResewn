package shcm.shsupercm.fabric.citresewn.pack.cits;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.Identifier;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.ex.CITParseException;
import shcm.shsupercm.fabric.citresewn.pack.CITPack;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CITItem extends CIT {
    private BakedModel bakedModel = null;
    private HashMap<String, BakedModel> subItems = null;

    public CITItem(CITPack pack, Identifier identifier, Properties properties) throws CITParseException {
        super(pack, identifier, properties);
        try {
            Identifier modelIdentifier = resolvePath(identifier, properties.getProperty("model"), ".json", pack.resourcePack);
            Map<String, Identifier> subIdentifiers = new HashMap<>();

            for (Object o : properties.keySet())
                if (o instanceof String property && property.startsWith("model.")) {
                    Identifier subIdentifier = resolvePath(identifier, properties.getProperty(property), ".json", pack.resourcePack);
                    if (subIdentifier == null)
                        throw new Exception("Cannot resolve path for " + property);
                    subIdentifiers.put(property.substring(5), subIdentifier);
                }

            if (modelIdentifier == null && subIdentifiers.size() == 0) {
                Identifier textureIdentifier = resolvePath(identifier, properties.getProperty("texture"), ".png", pack.resourcePack);

                for (Object o : properties.keySet())
                    if (o instanceof String property && property.startsWith("texture.")) {
                        Identifier subIdentifier = resolvePath(identifier, properties.getProperty(property), ".png", pack.resourcePack);
                        if (subIdentifier == null)
                            throw new Exception("Cannot resolve path for " + property);
                        subIdentifiers.put(property.substring(5), subIdentifier);
                    }

                if (textureIdentifier == null && subIdentifiers.size() == 0)
                    throw new Exception("Cannot resolve path for model/texture");

                // load textures
            }

            // load models
        } catch (Exception e) {
            throw new CITParseException(pack.resourcePack, identifier, (e.getClass() == Exception.class ? "" : e.getClass().getSimpleName() + ": ") + e.getMessage());
        }
    }

    public BakedModel getBakedModel(BakedModel originalModel) {

        String subItemName;
        if (originalModel == null || (subItemName = CITResewn.INSTANCE.bakedOverridesCache.get(originalModel)) == null)
            return this.bakedModel;

        return this.subItems == null ? this.bakedModel : this.subItems.getOrDefault(subItemName, this.bakedModel);
    }
}
