package io.shcm.fabric.citresewn.pack.cit.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class AllCondition extends CITCondition {
    public static final Function<Codec<CITCondition>, Codec<AllCondition>> CODEC = base -> RecordCodecBuilder.create(instance -> instance.group(
            base.listOf().xmap(AllCondition::new, all -> List.copyOf(all.conditions())).fieldOf("all").forGetter(Function.identity())
    ).apply(instance, Function.identity()));

    private final Collection<CITCondition> conditions;

    public AllCondition(Collection<CITCondition> conditions) {
        this.conditions = List.copyOf(conditions);
    }

    public AllCondition(CITCondition... conditions) {
        this.conditions = List.of(conditions);
    }

    public Collection<CITCondition> conditions() {
        return this.conditions;
    }

    @Override
    public Codec<? extends CITCondition> codec(Codec<CITCondition> base) {
        return CODEC.apply(base);
    }

    @Override
    public boolean test(ItemStack itemStack) {
        for (CITCondition condition : conditions())
            if (!condition.test(itemStack))
                return false;
        return true;
    }
}
