package shcm.shsupercm.fabric.citresewn.pack.cits;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import org.lwjgl.opengl.GL11;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.config.CITResewnConfig;
import shcm.shsupercm.fabric.citresewn.ex.CITParseException;
import shcm.shsupercm.fabric.citresewn.mixin.citenchantment.BufferBuilderStorageAccessor;
import shcm.shsupercm.fabric.citresewn.mixin.citenchantment.RenderPhaseAccessor;
import shcm.shsupercm.fabric.citresewn.pack.CITPack;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.lwjgl.opengl.GL11.*;
import static com.mojang.blaze3d.systems.RenderSystem.*;

public class CITEnchantment extends CIT {
    public static List<CITEnchantment> appliedContext = null;
    public static boolean shouldApply = false;

    public final Identifier textureIdentifier;
    public final float speed, rotation, duration, r, g, b, a;
    public final int layer;
    public final boolean useGlint, blur;
    public final Blend blend;

    private final WrappedMethodIntensity methodIntensity = new WrappedMethodIntensity();
    private final MergeMethod method;

    public final Map<GlintRenderLayer, RenderLayer> renderLayers = new EnumMap<>(GlintRenderLayer.class);

    public CITEnchantment(CITPack pack, Identifier identifier, Properties properties) throws CITParseException {
        super(pack, identifier, properties);
        try {
            textureIdentifier = resolvePath(identifier, properties.getProperty("texture"), ".png", id -> pack.resourcePack.contains(ResourceType.CLIENT_RESOURCES, id));
            if (textureIdentifier == null)
                throw new Exception("Cannot resolve texture");

            speed = Float.parseFloat(properties.getProperty("speed", "1"));

            rotation = Float.parseFloat(properties.getProperty("rotation", "0"));

            duration = Float.max(0f, Float.parseFloat(properties.getProperty("duration", "0")));

            layer = Integer.parseInt(properties.getProperty("layer", "0"));

            r = Math.max(0f, Float.parseFloat(properties.getProperty("r", "1")));
            g = Math.max(0f, Float.parseFloat(properties.getProperty("g", "1")));
            b = Math.max(0f, Float.parseFloat(properties.getProperty("b", "1")));
            a = Math.max(0f, Float.parseFloat(properties.getProperty("a", "1")));

            useGlint = switch (properties.getProperty("useGlint", "false").toLowerCase(Locale.ENGLISH)) {
                case "true" -> true;
                case "false" -> false;
                default -> throw new Exception("useGlint is not a boolean");
            };

            blur = switch (properties.getProperty("blur", "true").toLowerCase(Locale.ENGLISH)) {
                case "true" -> true;
                case "false" -> false;
                default -> throw new Exception("blur is not a boolean");
            };

            blend = Blend.getBlend(properties.getProperty("blend", "add"));

            method = !enchantmentsAny && this.enchantments.size() > 0 ? pack.method : null;
        } catch (Exception e) {
            throw new CITParseException(pack.resourcePack, identifier, (e.getClass() == Exception.class ? "" : e.getClass().getSimpleName() + ": ") + e.getMessage());
        }
    }

    public void activate() {
        for (GlintRenderLayer glintLayer : GlintRenderLayer.values()) {
            RenderLayer renderLayer = glintLayer.build(this);

            renderLayers.put(glintLayer, renderLayer);
            ((BufferBuilderStorageAccessor) MinecraftClient.getInstance().getBufferBuilders()).entityBuilders().put(renderLayer, new BufferBuilder(renderLayer.getExpectedBufferSize()));
        }
    }

    public void applyMethod(ItemStack stack) {
        if (this.method != null) {
            Map<Identifier, Integer> stackEnchantments = new LinkedHashMap<>();
            for (NbtElement nbtElement : stack.isOf(Items.ENCHANTED_BOOK) ? EnchantedBookItem.getEnchantmentNbt(stack) : stack.getEnchantments())
                stackEnchantments.put(EnchantmentHelper.getIdFromNbt((NbtCompound) nbtElement), EnchantmentHelper.getLevelFromNbt((NbtCompound) nbtElement));

            this.methodIntensity.intensity = this.method.getIntensity(stackEnchantments, this);
        } else
            this.methodIntensity.intensity = 1f;
    }

    @Override
    public void dispose() {
        appliedContext = null;
        for (RenderLayer renderLayer : renderLayers.values())
            ((BufferBuilderStorageAccessor) MinecraftClient.getInstance().getBufferBuilders()).entityBuilders().remove(renderLayer);
    }

    public enum GlintRenderLayer {
        ARMOR_GLINT("armor_glint", 8f, layer -> layer
                .shader(RenderPhaseAccessor.ARMOR_GLINT_SHADER())
                .writeMaskState(RenderPhaseAccessor.COLOR_MASK())
                .cull(RenderPhaseAccessor.DISABLE_CULLING())
                .depthTest(RenderPhaseAccessor.EQUAL_DEPTH_TEST())
                .layering(RenderPhaseAccessor.VIEW_OFFSET_Z_LAYERING())),
        ARMOR_ENTITY_GLINT("armor_entity_glint", 0.16f, layer -> layer
                .shader(RenderPhaseAccessor.ARMOR_ENTITY_GLINT_SHADER())
                .writeMaskState(RenderPhaseAccessor.COLOR_MASK())
                .cull(RenderPhaseAccessor.DISABLE_CULLING())
                .depthTest(RenderPhaseAccessor.EQUAL_DEPTH_TEST())
                .layering(RenderPhaseAccessor.VIEW_OFFSET_Z_LAYERING())),
        GLINT_TRANSLUCENT("glint_translucent", 8f, layer -> layer
                .shader(RenderPhaseAccessor.TRANSLUCENT_GLINT_SHADER())
                .writeMaskState(RenderPhaseAccessor.COLOR_MASK())
                .cull(RenderPhaseAccessor.DISABLE_CULLING())
                .depthTest(RenderPhaseAccessor.EQUAL_DEPTH_TEST())
                .target(RenderPhaseAccessor.ITEM_TARGET())),
        GLINT("glint", 8f, layer -> layer
                .shader(RenderPhaseAccessor.GLINT_SHADER())
                .writeMaskState(RenderPhaseAccessor.COLOR_MASK())
                .cull(RenderPhaseAccessor.DISABLE_CULLING())
                .depthTest(RenderPhaseAccessor.EQUAL_DEPTH_TEST())),
        DIRECT_GLINT("glint_direct", 8f, layer -> layer
                .shader(RenderPhaseAccessor.DIRECT_GLINT_SHADER())
                .writeMaskState(RenderPhaseAccessor.COLOR_MASK())
                .cull(RenderPhaseAccessor.DISABLE_CULLING())
                .depthTest(RenderPhaseAccessor.EQUAL_DEPTH_TEST())),
        ENTITY_GLINT("entity_glint", 0.16f, layer -> layer
                .shader(RenderPhaseAccessor.ENTITY_GLINT_SHADER())
                .writeMaskState(RenderPhaseAccessor.COLOR_MASK())
                .cull(RenderPhaseAccessor.DISABLE_CULLING())
                .depthTest(RenderPhaseAccessor.EQUAL_DEPTH_TEST())
                .target(RenderPhaseAccessor.ITEM_TARGET())),
        DIRECT_ENTITY_GLINT("entity_glint_direct", 0.16f, layer -> layer
                .shader(RenderPhaseAccessor.DIRECT_ENTITY_GLINT_SHADER())
                .writeMaskState(RenderPhaseAccessor.COLOR_MASK())
                .cull(RenderPhaseAccessor.DISABLE_CULLING())
                .depthTest(RenderPhaseAccessor.EQUAL_DEPTH_TEST()));

        public final String name;
        private final Consumer<RenderLayer.MultiPhaseParameters.Builder> setup;
        private final float scale;

        GlintRenderLayer(String name, float scale, Consumer<RenderLayer.MultiPhaseParameters.Builder> setup) {
            this.name = name;
            this.scale = scale;
            this.setup = setup;
        }

        public RenderLayer build(CITEnchantment enchantment) {
            final float speed = enchantment.speed, rotation = enchantment.rotation, r = enchantment.r, g = enchantment.g, b = enchantment.b, a = enchantment.a;
            final WrappedMethodIntensity methodIntensity = enchantment.methodIntensity;
            //noinspection ConstantConditions
            RenderLayer.MultiPhaseParameters.Builder layer = RenderLayer.MultiPhaseParameters.builder()
                    .texture(new RenderPhase.Texture(enchantment.textureIdentifier, enchantment.blur, false))
                    .texturing(new RenderPhase.Texturing("citresewn_glint_texturing", () -> {
                        float l = Util.getMeasuringTimeMs() * CITResewnConfig.INSTANCE().citenchantment_scroll_multiplier * speed;
                        float x = (l % 110000f) / 110000f;
                        float y = (l % 30000f) / 30000f;
                        Matrix4f matrix4f = Matrix4f.translate(-x, y, 0.0f);
                        matrix4f.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rotation + 10f));
                        matrix4f.multiply(Matrix4f.scale(scale, scale, scale));
                        setTextureMatrix(matrix4f);

                        setShaderColor(r, g, b, a * methodIntensity.intensity);
                    }, () -> {
                        RenderSystem.resetTextureMatrix();

                        setShaderColor(1f, 1f, 1f, 1f);
                    }))
                    .transparency(enchantment.blend);

            this.setup.accept(layer);

            return RenderLayer.of("citresewn:enchantment_" + this.name + ":" + enchantment.propertiesIdentifier.toString(),
                    VertexFormats.POSITION_TEXTURE,
                    VertexFormat.DrawMode.QUADS,
                    256,
                    layer.build(false));
        }

        public VertexConsumer tryApply(VertexConsumer base, RenderLayer baseLayer, VertexConsumerProvider provider) {
            if (!shouldApply || appliedContext == null)
                return null;

            VertexConsumer[] layers = new VertexConsumer[Math.min(appliedContext.size(), CITResewn.INSTANCE.activeCITs.effectiveGlobalProperties.cap)];

            for (int i = 0; i < layers.length; i++)
                layers[i] = provider.getBuffer(appliedContext.get(i).renderLayers.get(GlintRenderLayer.this));

            provider.getBuffer(baseLayer); // refresh base layer for armor consumer

            return base == null ? VertexConsumers.union(layers) : VertexConsumers.union(VertexConsumers.union(layers), base);
        }
    }

    public static class Blend extends RenderPhase.Transparency {
        private final int src, dst, srcAlpha, dstAlpha;

        private Blend(String name, int src, int dst, int srcAlpha, int dstAlpha) {
            super(name + "_glint_transparency", null, null);
            this.src = src;
            this.dst = dst;
            this.srcAlpha = srcAlpha;
            this.dstAlpha = dstAlpha;
        }

        private Blend(String name, int src, int dst) {
            this(name, src, dst, GL_ZERO, GL_ONE);
        }

        @Override
        public void startDrawing() {
            enableBlend();
            blendFuncSeparate(src, dst, srcAlpha, dstAlpha);
        }

        @Override
        public void endDrawing() {
            defaultBlendFunc();
            disableBlend();
        }

        public static Blend getBlend(String blendString) throws BlendFormatException {
            try { //check named blending function
                return Named.valueOf(blendString.toUpperCase(Locale.ENGLISH)).blend;
            } catch (IllegalArgumentException ignored) { // create custom blending function
                try {
                    String[] split = blendString.split(" ");
                    int src, dst, srcAlpha, dstAlpha;
                    if (split.length == 2) {
                        src = parseGLConstant(split[0]);
                        dst = parseGLConstant(split[1]);
                        srcAlpha = GL_ZERO;
                        dstAlpha = GL_ONE;
                    } else if (split.length == 4) {
                        src = parseGLConstant(split[0]);
                        dst = parseGLConstant(split[1]);
                        srcAlpha = parseGLConstant(split[2]);
                        dstAlpha = parseGLConstant(split[3]);
                    } else
                        throw new Exception();

                    return new Blend("custom_" + src + "_" + dst + "_" + srcAlpha + "_" + dstAlpha, src, dst, srcAlpha, dstAlpha);
                } catch (Exception e) {
                    throw new BlendFormatException();
                }
            }
        }

        private enum Named {
            REPLACE(new Blend("replace", 0, 0) {
                @Override
                public void startDrawing() {
                    disableBlend();
                }
            }),
            GLINT(new Blend("glint", GL_SRC_COLOR, GL_ONE)),
            ALPHA(new Blend("alpha", GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)),
            ADD(new Blend("add", GL_SRC_ALPHA, GL_ONE)),
            SUBTRACT(new Blend("subtract", GL_ONE_MINUS_DST_COLOR, GL_ZERO)),
            MULTIPLY(new Blend("multiply", GL_DST_COLOR, GL_ONE_MINUS_SRC_ALPHA)),
            DODGE(new Blend("dodge", GL_ONE, GL_ONE)),
            BURN(new Blend("burn", GL_ZERO, GL_ONE_MINUS_SRC_COLOR)),
            SCREEN(new Blend("screen", GL_ONE, GL_ONE_MINUS_SRC_COLOR)),
            OVERLAY(new Blend("overlay", GL_DST_COLOR, GL_SRC_COLOR));

            public final Blend blend;

            Named(Blend blend) {
                this.blend = blend;
            }
        }

        private static int parseGLConstant(String s) throws Exception {
            try {
                return GL11.class.getDeclaredField(s).getInt(null);
            } catch (NoSuchFieldException ignored) { }

            return s.startsWith("0x") ? Integer.parseInt(s.substring(2), 16) : Integer.parseInt(s);
        }

        public static class BlendFormatException extends Exception {
            public BlendFormatException() {
                super("Not a valid blending method");
            }
        }
    }

    public enum MergeMethod {
        AVERAGE {
            @Override
            public float getIntensity(Map<Identifier, Integer> stackEnchantments, CITEnchantment cit) {
                Identifier enchantment = null;
                for (Identifier enchantmentMatch : cit.enchantments)
                    if (stackEnchantments.containsKey(enchantmentMatch)) {
                        enchantment = enchantmentMatch;
                        break;
                    }
                if (enchantment == null)
                    return 0f;

                float sum = 0f;
                for (Integer value : stackEnchantments.values())
                    sum += value;

                return (float) stackEnchantments.get(enchantment) / sum;
            }
        },
        LAYERED {
            @Override
            public float getIntensity(Map<Identifier, Integer> stackEnchantments, CITEnchantment cit) {
                Identifier enchantment = null;
                for (Identifier enchantmentMatch : cit.enchantments)
                    if (stackEnchantments.containsKey(enchantmentMatch)) {
                        enchantment = enchantmentMatch;
                        break;
                    }
                if (enchantment == null)
                    return 0f;

                float max = 0f;
                for (Integer value : stackEnchantments.values())
                    if (value > max)
                        max = value;

                return (float) stackEnchantments.get(enchantment) / max;
            }
        },
        CYCLE {
            @Override
            public float getIntensity(Map<Identifier, Integer> stackEnchantments, CITEnchantment cit) {
                return 1f;
            }
        };

        public abstract float getIntensity(Map<Identifier, Integer> stackEnchantments, CITEnchantment cit);
    }

    private static class WrappedMethodIntensity {
        public float intensity = 1f;
    }

    public interface Cached {
        List<CITEnchantment> citresewn_getCachedCITEnchantment(Supplier<List<CITEnchantment>> realtime);
    }
}