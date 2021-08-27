package shcm.shsupercm.fabric.citresewn.mixin;

import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shcm.shsupercm.fabric.citresewn.pack.ResewnTextureIdentifier;

@Mixin(SpriteAtlasTexture.class)
public class SpriteAtlasTextureMixin {
    @Inject(method = "getTexturePath", cancellable = true, at = @At("HEAD"))
    public void forceLiteralResewnTextureIdentifier(Identifier id, CallbackInfoReturnable<Identifier> cir) {
        if (id instanceof ResewnTextureIdentifier)
            cir.setReturnValue(id);
    }
}
