package io.shcm.fabric.citresewn.pack.cit.condition;

import com.mojang.serialization.Codec;
import io.shcm.fabric.util.CodecsUtil;
import net.minecraft.item.ItemStack;
import java.util.function.Predicate;

public abstract class CITCondition implements Predicate<ItemStack> {
    public static final Codec<CITCondition> CODEC = Codec.recursive("CITCondition", base ->
            CodecsUtil.mutuallyExclusiveOptions(condition -> (Codec<CITCondition>) condition.codec(base),
                    ConstantCondition.CODEC,
                    AllCondition.CODEC.apply(base),
                    AnyCondition.CODEC.apply(base),
                    UnlessCondition.CODEC.apply(base)));

    public abstract Codec<? extends CITCondition> codec(Codec<CITCondition> base);
}
