package shcm.shsupercm.fabric.citresewn.cit;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Objects;

public class CITContext {
    public final ItemStack stack;
    public final World world;
    public final LivingEntity entity;

    public CITContext(ItemStack stack, World world, LivingEntity entity) {
        this.stack = stack;
        this.world = world == null ? MinecraftClient.getInstance().world : world;
        this.entity = entity;
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
