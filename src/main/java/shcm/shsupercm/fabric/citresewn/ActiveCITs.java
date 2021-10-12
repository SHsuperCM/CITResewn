package shcm.shsupercm.fabric.citresewn;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import shcm.shsupercm.fabric.citresewn.config.CITResewnConfig;
import shcm.shsupercm.fabric.citresewn.pack.cits.*;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ActiveCITs {
    public final Collection<CIT> cits;
    public final Map<Item, List<CITItem>> citItems = new HashMap<>();
    public final Map<ArmorItem, List<CITArmor>> citArmor = new HashMap<>();
    public final List<CITElytra> citElytra = new ArrayList<>();
    public final List<List<CITEnchantment>> citEnchantments = new ArrayList<>();


    public ActiveCITs(Collection<CIT> cits) {
        this.cits = cits;

        Map<Integer, List<CITEnchantment>> citEnchantmentLayers = new TreeMap<>(); // order citEnchantments by layers

        for (CIT cit : cits.stream().sorted(Comparator.<CIT>comparingInt(cit -> cit.weight).reversed().thenComparing(cit -> cit.propertiesIdentifier.toString())).collect(Collectors.toList())) {
            if (cit instanceof CITItem item)
                for (Item type : item.items)
                    citItems.computeIfAbsent(type, t -> new ArrayList<>()).add(item);
            else if (cit instanceof CITArmor armor)
                for (Item type : armor.items)
                    if (type instanceof ArmorItem armorType)
                        citArmor.computeIfAbsent(armorType, t -> new ArrayList<>()).add(armor);
                    else
                        CITResewn.logErrorLoading("Ignoring item type: " + Registry.ITEM.getId(type) + " is not armor in " + cit.pack.resourcePack.getName() + " -> " + cit.propertiesIdentifier.toString());
            else if (cit instanceof CITElytra elytra)
                citElytra.add(elytra);
            else if (cit instanceof CITEnchantment enchantment)
                citEnchantmentLayers.computeIfAbsent(enchantment.layer, l -> new ArrayList<>()).add(enchantment);
        }

        for (List<CITEnchantment> layer : citEnchantmentLayers.values()) {
            for (CITEnchantment enchantment : layer)
                enchantment.activate();
            citEnchantments.add(layer);
        }
    }

    public void dispose() {
        for (CIT cit : cits)
            cit.dispose();
        cits.clear();
        citItems.clear();
        citArmor.clear();
        citElytra.clear();
        citEnchantments.clear();
    }

    public CITItem getCITItem(ItemStack stack, World world, LivingEntity entity) {
        Hand hand = entity != null && stack == entity.getOffHandStack() ? Hand.OFF_HAND : Hand.MAIN_HAND;

        List<CITItem> citItems = this.citItems.get(stack.getItem());
        if (citItems != null)
            for (CITItem citItem : citItems)
                if (citItem.test(stack, hand, world, entity, true))
                    return citItem;
        return null;
    }

    public CITElytra getCITElytra(ItemStack stack, World world, LivingEntity livingEntity) {
        for (CITElytra citElytra : citElytra)
            if (citElytra.test(stack, Hand.MAIN_HAND, world, livingEntity, true))
                return citElytra;
        return null;
    }

    public CITArmor getCITArmor(ItemStack stack, World world, LivingEntity livingEntity) {
        Item item = stack.getItem();
        if (item instanceof ArmorItem) {
            List<CITArmor> citArmor = this.citArmor.get(item);
            if (citArmor != null)
                for (CITArmor armor : citArmor)
                    if (armor.test(stack, null, world, livingEntity, true))
                        return armor;
        }
        return null;
    }

    public List<CITEnchantment> getCITEnchantment(ItemStack stack, World world, LivingEntity livingEntity) {
        Hand hand = livingEntity != null && stack == livingEntity.getOffHandStack() ? Hand.OFF_HAND : Hand.MAIN_HAND;

        List<CITEnchantment> applied = new ArrayList<>();

        for (List<CITEnchantment> layer : this.citEnchantments)
            for (CITEnchantment cit : layer)
                if (cit.test(stack, hand, world, livingEntity, false)) {
                    applied.add(cit);
                    break;
                }

        return applied;
    }

    public BakedModel getItemModelCached(ItemStack stack, World world, LivingEntity entity, int seed) {
        BakedModel bakedModel = null;

        Supplier<CITItem> realtime = () -> getCITItem(stack, world, entity);

        //noinspection ConstantConditions
        CITItem citItem = CITResewnConfig.INSTANCE().cache_ms == 0 ? realtime.get() : ((CITItem.Cached) (Object) stack).citresewn_getCachedCITItem(realtime);

        if (citItem != null)
            bakedModel = citItem.getItemModel(stack, (ClientWorld) world, entity, seed);

        return bakedModel;
    }

    public Identifier getElytraTextureCached(ItemStack stack, World world, LivingEntity livingEntity) {
        Supplier<CITElytra> realtime = () -> getCITElytra(stack, world, livingEntity);

        //noinspection ConstantConditions
        CITElytra citElytra = CITResewnConfig.INSTANCE().cache_ms == 0 ? realtime.get() : ((CITElytra.Cached) (Object) stack).citresewn_getCachedCITElytra(realtime);

        if (citElytra != null)
            return citElytra.textureIdentifier;

        return null;
    }

    public Map<String, Identifier> getArmorTexturesCached(ItemStack stack, World world, LivingEntity livingEntity) {
        Supplier<CITArmor> realtime = () -> getCITArmor(stack, world, livingEntity);

        //noinspection ConstantConditions
        CITArmor citArmor = CITResewnConfig.INSTANCE().cache_ms == 0 ? realtime.get() : ((CITArmor.Cached) (Object) stack).citresewn_getCachedCITArmor(realtime);

        if (citArmor != null)
            return citArmor.textures;

        return null;
    }

    public void setEnchantmentAppliedContextCached(ItemStack stack, World world, LivingEntity entity) {
        if (stack == null) {
            CITEnchantment.appliedContext = null;
            return;
        }

        Supplier<List<CITEnchantment>> realtime = () -> getCITEnchantment(stack, world, entity);

        //noinspection ConstantConditions
        List<CITEnchantment> citEnchantments = CITResewnConfig.INSTANCE().cache_ms == 0 ? realtime.get() : ((CITEnchantment.Cached) (Object) stack).citresewn_getCachedCITEnchantment(realtime);

        if (citEnchantments == null || citEnchantments.isEmpty()) {
            CITEnchantment.appliedContext = null;
            return;
        }

        CITEnchantment.appliedContext = citEnchantments;
    }
}
