package io.shcm.fabric.citresewn.pack.cit.effect;

import com.mojang.serialization.Codec;

public class CITEffect {
    public static final Codec<CITEffect> CODEC = Codec.unit(new CITEffect());
}
