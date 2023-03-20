package shcm.shsupercm.fabric.citresewn.defaults.mixin.types.item;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformationMode;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shcm.shsupercm.fabric.citresewn.cit.CIT;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeItem;

import java.lang.ref.WeakReference;

import static shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeItem.CONTAINER;

/**
 * Do not go through this class, it looks awful because it was ported from a "proof of concept".<br>
 * The whole type will be rewritten at some point.
 */
@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Shadow @Final private ItemModels models;

    private WeakReference<BakedModel> citresewn$mojankCITModel = null;

    @Inject(method = "getModel", cancellable = true, at = @At("HEAD"))
    private void citresewn$getItemModel(ItemStack stack, World world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        if (!CONTAINER.active())
            return;

        CITContext context = new CITContext(stack, world, entity);
        CIT<TypeItem> cit = CONTAINER.getCIT(context, seed);
        citresewn$mojankCITModel = null;
        if (cit != null) {
            BakedModel citModel = cit.type.getItemModel(context, seed);

            if (citModel != null) {
                if (stack.isOf(Items.TRIDENT) || stack.isOf(Items.SPYGLASS)) {
                    citresewn$mojankCITModel = new WeakReference<>(citModel);
                } else
                    cir.setReturnValue(citModel);
            }
        }
    }

    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At("HEAD"))
    private void citresewn$fixMojankCITsContext(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        if (!CONTAINER.active() || citresewn$mojankCITModel == null)
            return;

        if (renderMode == ModelTransformationMode.GUI || renderMode == ModelTransformationMode.GROUND || renderMode == ModelTransformationMode.FIXED)
            ((TypeItem.BakedModelManagerMixinAccess) this.models.getModelManager()).citresewn$forceMojankModel(citresewn$mojankCITModel.get());

        citresewn$mojankCITModel = null;
    }
}
