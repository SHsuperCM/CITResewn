package io.shcm.fabric.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.function.Function;

public class CodecsUtil {
    public static <T> Codec<T> mutuallyExclusiveOptions(Function<T, Codec<T>> codecGetter, Codec<? extends T>... codecOptions) {
        return new Codec<T>() {
            @Override
            public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
                for (Codec<? extends T> codecOption : codecOptions) {
                    DataResult<Pair<T, T1>> result = codecOption.decode(ops, input).map(pair -> pair.mapFirst(val -> (T) val));
                    if (result.isSuccess())
                        return result;
                }

                return DataResult.error(() -> "Failed to parse mutually exclusive options, no codec matched the given input");
            }

            @Override
            public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
                return codecGetter.apply(input).encode(input, ops, prefix);
            }
        };
    }
}
