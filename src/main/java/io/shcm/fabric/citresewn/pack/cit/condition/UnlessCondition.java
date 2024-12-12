package io.shcm.fabric.citresewn.pack.cit.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;

public class UnlessCondition extends CITCondition {
    public static final Function<Codec<CITCondition>, Codec<UnlessCondition>> CODEC = base -> RecordCodecBuilder.create(instance -> instance.group(
            base.xmap(UnlessCondition::new, UnlessCondition::condition).fieldOf("unless").forGetter(Function.identity())
    ).apply(instance, Function.identity()));

    private final CITCondition condition;

    public UnlessCondition(CITCondition condition) {
        this.condition = condition;
    }

    public CITCondition condition() {
        return this.condition;
    }

    @Override
    public Codec<? extends CITCondition> codec(Codec<CITCondition> base) {
        return CODEC.apply(base);
    }

    @Override
    public boolean test(CITCondition.TestContext context) {
        return !condition().test(context);
    }
}
