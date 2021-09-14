package shcm.shsupercm.fabric.citresewn;

import io.github.apace100.cosmetic_armor.CosmeticArmor;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.function.Predicate;

public final class OptionalCompat {
    public static OptionalCompat INSTANCE() {
        return CITResewn.INSTANCE.compat;
    }

    public final CompatCosmeticArmor compatCosmeticArmor;

    public OptionalCompat(Predicate<String> isLoaded) {
        compatCosmeticArmor = isLoaded.test("cosmetic-armor") ? CompatCosmeticArmor.impl() : null;
    }

    public static ItemStack getCosmeticArmor(ItemStack original, LivingEntity entity, EquipmentSlot slot, boolean elytra) {
        if (INSTANCE().compatCosmeticArmor != null) {
            ItemStack stackInCosmeticSlot = INSTANCE().compatCosmeticArmor.getStackInCosmeticSlot(entity, slot);
            if (!stackInCosmeticSlot.isEmpty() && (!elytra || stackInCosmeticSlot.isOf(Items.ELYTRA)))
                return stackInCosmeticSlot;
        }

        return original;
    }

    /**
     * Compatibility with 'cosmetic-armor': Display cits for cosmetic armors instead of equipped armors
     */
    public interface CompatCosmeticArmor {
        private static CompatCosmeticArmor impl() {
            return CosmeticArmor::getCosmeticArmor;
        }

        ItemStack getStackInCosmeticSlot(LivingEntity entity, EquipmentSlot slot);
    }
}
