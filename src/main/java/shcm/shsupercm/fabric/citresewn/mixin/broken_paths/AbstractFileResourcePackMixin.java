package shcm.shsupercm.fabric.citresewn.mixin.broken_paths;

import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ZipResourcePack;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipFile;

@Mixin(AbstractFileResourcePack.class)
public abstract class AbstractFileResourcePackMixin implements ResourcePack {
    @Shadow @Final protected File base;

    @SuppressWarnings({"unchecked", "ConstantConditions", "EqualsBetweenInconvertibleTypes"})
    @Inject(method = "parseMetadata(Lnet/minecraft/resource/metadata/ResourceMetadataReader;)Ljava/lang/Object;", cancellable = true, at = @At("RETURN"))
    public <T extends PackResourceMetadata> void parseMetadata(ResourceMetadataReader<T> metaReader, CallbackInfoReturnable<T> cir) {
        if (cir.getReturnValue() != null)
            try {
                if (this.getClass().equals(ZipResourcePack.class)) {
                    try (ZipFile zipFile = new ZipFile(base)) {
                        zipFile.stream()
                                .forEach(entry -> {
                                    if (entry.getName().startsWith("assets"))
                                        new Identifier("minecraft", entry.getName());
                                });
                    }
                } else if (this.getClass().equals(DirectoryResourcePack.class)) {
                    final Path assets = new File(base, "assets").toPath();
                    Files.walk(assets)
                            .forEach(path -> new Identifier("minecraft", assets.relativize(path).toString().replace('\\', '/')));
                }
            } catch (InvalidIdentifierException e) {
                cir.setReturnValue((T) new PackResourceMetadata(cir.getReturnValue().getDescription(), Integer.MAX_VALUE - 53));
            } catch (Exception ignored) {}
    }
}
