package shcm.shsupercm.fabric.citresewn.mixin.core;

import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.fabric.impl.resource.loader.GroupResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

/* if (FabricLoader.getInstance().isModLoaded("fabric-resource-loader-v0")) */ @Mixin(GroupResourcePack.class)
public interface GroupResourcePackAccessor {
    @Accessor
    List<ModResourcePack> getPacks();
}
