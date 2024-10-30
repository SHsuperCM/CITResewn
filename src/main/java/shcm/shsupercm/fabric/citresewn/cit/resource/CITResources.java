package shcm.shsupercm.fabric.citresewn.cit.resource;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.Identifier;
import shcm.shsupercm.fabric.citresewn.cit.CIT;

import java.util.*;
import java.util.function.Consumer;

public record CITResources(
        CITData citData,
        CITModels models) {
    public static final CITResources EMPTY = new CITResources(CITData.EMPTY, CITModels.EMPTY);

    public record CITData(
            GlobalProperties globalProperties,
            Map<CITIdentifier, CIT<?>> cits) {
        public static final CITData EMPTY = new CITData(new GlobalProperties(), Map.of());

    }

    public record CITModels(Set<Identifier> extraModels,
                            Map<Identifier, ModelResolver> modelResolvers,
                            Map<Identifier, Consumer<BakedModel>> bakedModelReceivers) {
        public static final CITModels EMPTY = new CITModels();

        public CITModels() {
            this(new HashSet<>(), new HashMap<>(), new HashMap<>());
        }
    }
}
