package shcm.shsupercm.fabric.citresewn.defaults.mixin.common;

import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpriteAtlasTexture.class)
public class SpriteAtlasTextureMixin {
    @Shadow @Final private static String PNG_EXTENSION;

    @Inject(method = "getTexturePath", cancellable = true, at = @At("HEAD"))
    public void citresewn$forceAbsoluteTextureIdentifier(Identifier id, CallbackInfoReturnable<Identifier> cir) {
        if (id.getPath().endsWith(PNG_EXTENSION))
            cir.setReturnValue(id);
    }
}
