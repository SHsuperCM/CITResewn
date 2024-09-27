package shcm.shsupercm.fabric.citresewn.mixin.broken_paths;

import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.InvalidIdentifierException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shcm.shsupercm.fabric.citresewn.config.BrokenPaths;

/**
 * Adds a resourcepack compatibility error message when broken paths are enabled and are detected in a pack.
 * @see BrokenPaths
 * @see ResourcePackCompatibilityMixin
 */
@Mixin(AbstractFileResourcePack.class)
public abstract class AbstractFileResourcePackMixin implements ResourcePack {

    @SuppressWarnings({"unchecked"})
    @Inject(method = "parseMetadata(Lnet/minecraft/resource/metadata/ResourceMetadataReader;)Ljava/lang/Object;", cancellable = true, at = @At("RETURN"))
    public <T extends PackResourceMetadata> void citresewn$brokenpaths$parseMetadata(ResourceMetadataReader<T> metaReader, CallbackInfoReturnable<T> cir) {
        if (cir.getReturnValue() != null) try {
            for (String namespace : getNamespaces(ResourceType.CLIENT_RESOURCES)) {
                findResources(ResourceType.CLIENT_RESOURCES, namespace, "", (identifier, inputStreamInputSupplier) -> {
                });
            }
        } catch (InvalidIdentifierException e) {
            cir.setReturnValue((T) new PackResourceMetadata(cir.getReturnValue()./*? >=1.20.4 {*/description()/*?} else {*//*getDescription()*//*?}*/, Integer.MAX_VALUE - 53/*? >=1.20.4 {*/, cir.getReturnValue().supportedFormats()/*?}*/));
        } catch (Exception ignored) { }
    }
}
