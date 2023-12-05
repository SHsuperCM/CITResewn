package shcm.shsupercm.fabric.citresewn.defaults.mixin.types.enchantment;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeEnchantment;
import java.util.Map;

@Mixin(BufferBuilderStorage.class)
public class BufferBuilderStorageMixin implements TypeEnchantment.CITBufferBuilderStorage {
    private Map<RenderLayer, BufferBuilder> entityBuilders;
    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/VertexConsumerProvider;immediate(Ljava/util/Map;Lnet/minecraft/client/render/BufferBuilder;)Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;"
            )
    )
    public Map<RenderLayer, BufferBuilder> a(Map<RenderLayer, BufferBuilder> entityBuilders) {
        return this.entityBuilders = entityBuilders;
    }

    public Map<RenderLayer, BufferBuilder> citresewn$getEntityBuilders() {
        return entityBuilders;
    }
}
