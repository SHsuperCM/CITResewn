package shcm.shsupercm.fabric.citresewn;

import io.github.apace100.cosmetic_armor.CosmeticArmor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import shcm.shsupercm.fabric.citresewn.config.CITResewnConfigScreenFactory;

import java.util.function.Function;
import java.util.function.Predicate;

public final class OptionalCompat {
    private static final OptionalCompat INSTANCE = new OptionalCompat(s -> FabricLoader.getInstance().isModLoaded(s));

    public final CompatClothConfig compatClothConfig;

    public final CompatCosmeticArmor compatCosmeticArmor;

    private OptionalCompat(Predicate<String> isLoaded) {
        compatClothConfig = isLoaded.test("cloth-config2") ? CompatClothConfig.impl() : null;
        compatCosmeticArmor = isLoaded.test("cosmetic-armor") ? CompatCosmeticArmor.impl() : null;
    }

    public static Function<Screen, Screen> getModConfigScreenFactory() {
        if (INSTANCE.compatClothConfig != null) {
            return INSTANCE.compatClothConfig.getModConfigScreenFactory();
        }

        return parent -> new NoticeScreen(() -> MinecraftClient.getInstance().setScreen(parent), Text.of("CIT Resewn"), Text.of("CIT Resewn requires Cloth Config 2 to be able to show the config."));
    }

    public static ItemStack getCosmeticArmor(ItemStack original, LivingEntity entity, EquipmentSlot slot, boolean elytra) {
        if (INSTANCE.compatCosmeticArmor != null) {
            ItemStack stackInCosmeticSlot = INSTANCE.compatCosmeticArmor.getStackInCosmeticSlot(entity, slot);
            if (!stackInCosmeticSlot.isEmpty() && (!elytra || stackInCosmeticSlot.isOf(Items.ELYTRA)))
                return stackInCosmeticSlot;
        }

        return original;
    }

    /**
     * Compatibility with 'cloth-config2': Custom gui for CITResewn's config
     */
    public interface CompatClothConfig {
        private static CompatClothConfig impl() {
            return () -> CITResewnConfigScreenFactory::create;
        }

        Function<Screen, Screen> getModConfigScreenFactory();
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
