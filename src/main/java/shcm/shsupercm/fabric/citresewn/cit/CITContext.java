package shcm.shsupercm.fabric.citresewn.cit;

import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class CITContext {
    public final ItemStack stack;
    public final World world;
    public final LivingEntity entity;

    private Map<Identifier, Integer> enchantments = null;

    public CITContext(ItemStack stack, World world, LivingEntity entity) {
        this.stack = stack;
        this.world = world == null ? MinecraftClient.getInstance().world : world;
        this.entity = entity;
    }

    public Map<Identifier, Integer> enchantments() {
        if (this.enchantments == null) {
            this.enchantments = new LinkedHashMap<>();
            for (NbtElement nbtElement : stack.isOf(Items.ENCHANTED_BOOK) ? EnchantedBookItem.getEnchantmentNbt(stack) : stack.getEnchantments())
                this.enchantments.put(EnchantmentHelper.getIdFromNbt((NbtCompound) nbtElement), EnchantmentHelper.getLevelFromNbt((NbtCompound) nbtElement));
        }
        return this.enchantments;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CITContext that &&
                Objects.equals(this.stack, that.stack) &&
                Objects.equals(this.world, that.world) &&
                Objects.equals(this.entity, that.entity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stack, world, entity);
    }
}
