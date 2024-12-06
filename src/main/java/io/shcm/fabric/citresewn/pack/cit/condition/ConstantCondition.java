package io.shcm.fabric.citresewn.pack.cit.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;

import java.util.function.Function;

public abstract class ConstantCondition extends CITCondition {
    public static final Codec<ConstantCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.xmap(b -> b ? Passes.INSTANCE : Fails.INSTANCE, cst -> cst == Passes.INSTANCE ? true : false).fieldOf("always").forGetter(Function.identity())
    ).apply(instance, cst -> cst));

    @Override
    public Codec<? extends CITCondition> codec(Codec<CITCondition> base) {
        return CODEC;
    }

    private ConstantCondition() {

    }

    public static class Passes extends ConstantCondition {
        public static final Passes INSTANCE = new Passes();

        @Override
        public boolean test(ItemStack itemStack) {
            return true;
        }
    }

    public static class Fails extends ConstantCondition {
        public static final Fails INSTANCE = new Fails();

        @Override
        public boolean test(ItemStack itemStack) {
            return false;
        }
    }
}
