package io.shcm.fabric.citresewn.pack.cit;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.shcm.fabric.citresewn.pack.cit.condition.AllCondition;
import io.shcm.fabric.citresewn.pack.cit.condition.CITCondition;
import io.shcm.fabric.citresewn.pack.cit.condition.ConstantCondition;
import io.shcm.fabric.citresewn.pack.cit.effect.CITModifier;
import net.minecraft.util.dynamic.Codecs;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public record CIT(CITCondition condition, Collection<CITModifier> modifiers) {
    public static final Codec<CIT> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.either(CITCondition.CODEC, CITCondition.CODEC.listOf())
                    .xmap(either -> either.map(Function.identity(), AllCondition::new),
                          condition -> condition instanceof AllCondition all ?
                                  Either.right(List.copyOf(all.conditions())) :
                                  Either.left(condition))
                    .optionalFieldOf("when", ConstantCondition.Fails.INSTANCE).forGetter(CIT::condition),
            Codecs.listOrSingle(CITModifier.CODEC)
                    .optionalFieldOf("apply", List.of()).forGetter(cit -> List.copyOf(cit.modifiers()))
    ).apply(instance, CIT::new));
    public static final Codec<List<CIT>> CODEC_MULTIPLE = Codecs.listOrSingle(CODEC);
}
