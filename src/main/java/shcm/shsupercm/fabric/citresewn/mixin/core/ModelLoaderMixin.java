package shcm.shsupercm.fabric.citresewn.mixin.core;

import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shcm.shsupercm.fabric.citresewn.ActiveCITs;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.config.CITResewnConfig;
import shcm.shsupercm.fabric.citresewn.pack.CITPack;
import shcm.shsupercm.fabric.citresewn.pack.CITParser;
import shcm.shsupercm.fabric.citresewn.pack.cits.CIT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static shcm.shsupercm.fabric.citresewn.CITResewn.info;

@Mixin(value = ModelLoader.class, priority = 999)
public abstract class ModelLoaderMixin {
    @Shadow @Final private ResourceManager resourceManager;

    @Inject(method = "addModel", at = @At("TAIL"))
    public void initCITs(ModelIdentifier eventModelId, CallbackInfo ci) { if (eventModelId != ModelLoader.MISSING_ID) return;
        if (CITResewn.INSTANCE.activeCITs != null) {
            info("Clearing active CITs..");
            CITResewn.INSTANCE.activeCITs.dispose();
            CITResewn.INSTANCE.activeCITs = null;
        }

        if (!CITResewnConfig.INSTANCE().enabled)
            return;

        info("Parsing CITs...");
        List<CITPack> parsedPacks = CITParser.parseCITs(resourceManager.streamResourcePacks().collect(Collectors.toCollection(ArrayList::new)));
        List<CIT> parsed = parsedPacks.stream().flatMap(pack -> pack.cits.stream()).collect(Collectors.toCollection(ArrayList::new));

        if (parsed.size() > 0) {
            info("Activating CITs...");
            CITResewn.INSTANCE.activeCITs = new ActiveCITs(parsedPacks, parsed);
        } else
            info("No cit packs found.");
    }
}
