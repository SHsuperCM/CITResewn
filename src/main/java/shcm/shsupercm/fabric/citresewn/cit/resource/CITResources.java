package shcm.shsupercm.fabric.citresewn.cit.resource;

import shcm.shsupercm.fabric.citresewn.cit.CIT;
import shcm.shsupercm.fabric.citresewn.pack.GlobalProperties;

import java.util.Map;

public record CITResources(
        CITData citData,
        CITModels models) {
    public static final CITResources EMPTY = new CITResources(CITData.EMPTY, CITModels.EMPTY);

    public record CITData(
            GlobalProperties globalProperties,
            Map<CITIdentifier, CIT<?>> cits) {
        public static final CITData EMPTY = new CITData(new GlobalProperties(), Map.of());

    }

    public record CITModels() {
        public static final CITModels EMPTY = new CITModels();
    }
}
