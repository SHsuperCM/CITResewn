package shcm.shsupercm.fabric.citresewn.mixin.cititem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.config.CITResewnConfig;
import shcm.shsupercm.fabric.citresewn.pack.cits.CITItem;

import java.lang.ref.WeakReference;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Shadow @Final private ItemModels models;

    private static WeakReference<BakedModel> mojankCITModel = null;

    @Inject(method = "getHeldItemModel", cancellable = true, at = @At("HEAD"))
    private void getItemModel(ItemStack stack, World world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        if (!CITResewnConfig.INSTANCE().enabled || CITResewn.INSTANCE.activeCITs == null)
            return;

        BakedModel citModel = CITResewn.INSTANCE.activeCITs.getItemModelCached(stack, world == null ? MinecraftClient.getInstance().world : world, entity, seed);
        if (citModel != null)
            cir.setReturnValue(citModel);
    }

    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At("HEAD"))
    private void fixMojankCITsContext(ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        mojankCITModel = null;
        if (((CITItem.Cached) (Object) stack).citresewn_isMojankCIT()) {
            boolean bl = renderMode == ModelTransformation.Mode.GUI || renderMode == ModelTransformation.Mode.GROUND || renderMode == ModelTransformation.Mode.FIXED;
            if (bl)
                mojankCITModel = new WeakReference<>(model);
            else { // rendered in hand model of trident/spyglass
                if (stack.isOf(Items.TRIDENT))
                    mojankCITModel = new WeakReference<>(this.models.getModelManager().getModel(new ModelIdentifier("minecraft:trident_in_hand#inventory")));
                else if (stack.isOf(Items.SPYGLASS))
                    mojankCITModel = new WeakReference<>(this.models.getModelManager().getModel(new ModelIdentifier("minecraft:spyglass_in_hand#inventory")));
            }
        } else
            mojankCITModel = null;
    }

    @ModifyVariable(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At(value = "LOAD", ordinal = 0, target = "Lnet/minecraft/client/render/model/BakedModel;getTransformation()Lnet/minecraft/client/render/model/json/ModelTransformation;"), argsOnly = true)
    private BakedModel fixMojankCITs(BakedModel original) {
        if (mojankCITModel != null)
            return mojankCITModel.get();

        return original;
    }
}
