package shcm.shsupercm.fabric.citresewn.mixin;

import net.fabricmc.fabric.impl.resource.loader.GroupResourcePack;
import net.minecraft.resource.ResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(GroupResourcePack.class)
public interface GroupResourcePackAccessor {
    @Accessor
    List<ResourcePack> getPacks();
}
