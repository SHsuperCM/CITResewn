package shcm.shsupercm.fabric.citresewn.defaults.cit.types;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import shcm.shsupercm.fabric.citresewn.api.CITTypeContainer;
import shcm.shsupercm.fabric.citresewn.cit.*;
import shcm.shsupercm.fabric.citresewn.defaults.cit.conditions.ConditionItems;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyKey;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class TypeElytra extends CITType {
    @Entrypoint(CITTypeContainer.ENTRYPOINT)
    public static final Container CONTAINER = new Container();

    public Identifier texture;

    @Override
    public Set<PropertyKey> typeProperties() {
        return Set.of(PropertyKey.of("texture"));
    }

    @Override
    public void load(List<CITCondition> conditions, PropertyGroup properties, ResourceManager resourceManager) throws CITParsingException {
        for (CITCondition condition : conditions)
            if (condition instanceof ConditionItems items)
                for (Item item : items.items)
                    if (!(item instanceof ElytraItem))
                        warn("Non elytra item type condition", null, properties);

        texture = resolveAsset(properties.identifier, properties.getLastWithoutMetadata("citresewn", "texture"), "textures", ".png", resourceManager);
        if (texture == null)
            throw new CITParsingException("Texture not specified", properties, -1);
    }

    public static class Container extends CITTypeContainer<TypeElytra> {
        public Container() {
            super(TypeElytra.class, TypeElytra::new, "elytra");
        }

        public final List<Function<LivingEntity, ItemStack>> getItemInSlotCompatRedirects = new ArrayList<>();

        public Set<CIT<TypeElytra>> loaded = new HashSet<>();

        @Override
        public void load(List<CIT<TypeElytra>> parsedCITs) {
            loaded.addAll(parsedCITs);
        }

        @Override
        public void dispose() {
            loaded.clear();
        }

        public CIT<TypeElytra> getCIT(CITContext context) {
            return ((CITCacheElytra) (Object) context.stack).citresewn$getCacheTypeElytra().get(context).get();
        }

        public CIT<TypeElytra> getRealTimeCIT(CITContext context) {
            for (CIT<TypeElytra> cit : loaded)
                if (cit.test(context))
                    return cit;

            return null;
        }

        public ItemStack getVisualElytraItem(LivingEntity entity) {
            for (Function<LivingEntity, ItemStack> redirect : getItemInSlotCompatRedirects) {
                ItemStack stack = redirect.apply(entity);
                if (stack != null)
                    return stack;
            }

            return entity.getEquippedStack(EquipmentSlot.CHEST);
        }
    }

    public interface CITCacheElytra {
        CITCache.Single<TypeElytra> citresewn$getCacheTypeElytra();
    }
}
