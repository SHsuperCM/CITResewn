package shcm.shsupercm.fabric.citresewn;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import shcm.shsupercm.fabric.citresewn.pack.cits.*;

import java.util.*;
import java.util.stream.Collectors;

public class ActiveCITs {
    public final Collection<CIT> cits;
    public final Map<Item, List<CITItem>> citItems = new HashMap<>();
    public final Map<ArmorItem, List<CITArmor>> citArmor = new HashMap<>();
    public final List<CITElytra> citElytra = new ArrayList<>();
    public final List<CITEnchantment> citEnchantments = new ArrayList<>();


    public ActiveCITs(Collection<CIT> cits) {
        this.cits = cits;

        for (CIT cit : cits.stream().sorted(Comparator.<CIT>comparingInt(cit -> cit.weight).reversed().thenComparing(cit -> cit.propertiesIdentifier.getPath())).collect(Collectors.toList())) {
            if (cit instanceof CITItem item)
                for (Item type : item.items)
                    citItems.computeIfAbsent(type, t -> new ArrayList<>()).add(item);
            else if (cit instanceof CITArmor armor)
                for (Item type : armor.items)
                    if (type instanceof ArmorItem armorType)
                        citArmor.computeIfAbsent(armorType, t -> new ArrayList<>()).add(armor);
                    else
                        CITResewn.LOG.error("Skipping item type: " + Registry.ITEM.getId(type) + " is not armor in " + cit.pack.resourcePack.getName() + " -> " + cit.propertiesIdentifier.getPath());
            else if (cit instanceof CITElytra)
                citElytra.add((CITElytra) cit);
            else if (cit instanceof CITEnchantment)
                citEnchantments.add((CITEnchantment) cit);
        }
    }

    public void dispose() {
        for (CIT cit : cits) {
            cit.dispose();
        }
        cits.clear();
        citItems.clear();
        citArmor.clear();
        citElytra.clear();
        citEnchantments.clear();
    }

    public BakedModel getItemModel(ItemStack stack, BakedModel model, World world, LivingEntity entity) {
        BakedModel bakedModel = null;
        Hand hand = entity != null && stack == entity.getOffHandStack() ? Hand.OFF_HAND : Hand.MAIN_HAND;

        List<CITItem> citItems = this.citItems.get(stack.getItem());
        if (citItems != null)
            for (CITItem citItem : citItems) {
                bakedModel = citItem.getItemModel(stack, hand, model, world, entity);
                if (bakedModel != null)
                    break;
            }

        return bakedModel;
    }

    public Identifier getElytraTexture(ItemStack stack, World world, LivingEntity livingEntity) {
        for (CITElytra citElytra : citElytra)
            if (citElytra.test(stack, Hand.MAIN_HAND, world, livingEntity))
                return citElytra.textureIdentifier;

        return null;
    }

    public Map<String, Identifier> getArmorTextures(ItemStack itemStack, World world, LivingEntity livingEntity) {
        Item item = itemStack.getItem();
        if (item instanceof ArmorItem) {
            List<CITArmor> citArmor = this.citArmor.get(item);
            if (citArmor != null)
                for (CITArmor armor : citArmor)
                    if (armor.test(itemStack, null, world, livingEntity))
                        return armor.textures;
        }
        return null;
    }
}
