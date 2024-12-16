package io.shcm.fabric.citresewn.pack.cit.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class AnyCondition extends CITCondition {
    public static final Function<Codec<CITCondition>, Codec<AnyCondition>> CODEC = base -> RecordCodecBuilder.create(instance -> instance.group(
            base.listOf().xmap(AnyCondition::new, all -> List.copyOf(all.conditions())).fieldOf("any").forGetter(Function.identity())
    ).apply(instance, Function.identity()));

    private final Collection<CITCondition> conditions;

    public AnyCondition(Collection<CITCondition> conditions) {
        this.conditions = List.copyOf(conditions);
    }

    public AnyCondition(CITCondition... conditions) {
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
    public boolean test(CITCondition.TestContext context) {
        for (CITCondition condition : conditions())
            if (condition.test(context))
                return true;
        return false;
    }
}
