package shcm.shsupercm.fabric.citresewn.defaults.mixin.common;

import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpriteAtlasManager.AtlasPreparation.class)
public abstract class AtlasPreparationMixin {
    @Shadow public @Nullable abstract Sprite getSprite(Identifier id);

    @Inject(method = "getSprite", cancellable = true, at = @At("HEAD"))
    private void citresewn$unwrapTexturePaths(Identifier id, CallbackInfoReturnable<Sprite> cir) {
        if (id.getPath().startsWith("textures/") && id.getPath().endsWith(".png"))
            cir.setReturnValue(getSprite(new Identifier(id.getNamespace(), id.getPath().substring(9, id.getPath().length() - 4))));
    }
}
