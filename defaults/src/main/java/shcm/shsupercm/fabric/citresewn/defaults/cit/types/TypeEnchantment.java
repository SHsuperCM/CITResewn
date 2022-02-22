package shcm.shsupercm.fabric.citresewn.defaults.cit.types;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;
import shcm.shsupercm.fabric.citresewn.api.CITTypeContainer;
import shcm.shsupercm.fabric.citresewn.cit.*;
import shcm.shsupercm.fabric.citresewn.ex.CITParsingException;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyKey;

import java.lang.ref.WeakReference;
import java.util.*;

import static com.mojang.blaze3d.systems.RenderSystem.*;
import static org.lwjgl.opengl.GL11.*;

public class TypeEnchantment extends CITType {
    @Entrypoint(CITTypeContainer.ENTRYPOINT)
    public static final Container CONTAINER = new Container();

    @Override
    public Set<PropertyKey> typeProperties() {
        return Set.of(
                PropertyKey.of("texture"),
                PropertyKey.of("layer"),
                PropertyKey.of("speed"),
                PropertyKey.of("rotation"),
                PropertyKey.of("duration"),
                PropertyKey.of("blend"),
                PropertyKey.of("useGlint"),
                PropertyKey.of("blur"),
                PropertyKey.of("r"),
                PropertyKey.of("g"),
                PropertyKey.of("b"),
                PropertyKey.of("a"));
    }

    public Identifier texture;
    public float speed, rotation, duration, r, g, b, a;
    public int layer;
    public boolean useGlint, blur;
    public Blend blend;

    @Override
    public void load(List<CITCondition> conditions, PropertyGroup properties, ResourceManager resourceManager) throws CITParsingException {

    }

    public static class Container extends CITTypeContainer<TypeEnchantment> {
        public Container() {
            super(TypeEnchantment.class, TypeEnchantment::new, "enchantment");
        }

        public List<CIT<TypeEnchantment>> loaded = new ArrayList<>();
        public List<List<CIT<TypeEnchantment>>> loadedLayered = new ArrayList<>();

        @Override
        public void load(List<CIT<TypeEnchantment>> parsedCITs) {
            loaded.addAll(parsedCITs);

            Map<Integer, List<CIT<TypeEnchantment>>> layers = new HashMap<>();
            for (CIT<TypeEnchantment> cit : loaded)
                layers.computeIfAbsent(cit.type.layer, i -> new ArrayList<>()).add(cit);
            loadedLayered.clear();
            layers.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEachOrdered(layer -> loadedLayered.add(layer.getValue()));
        }

        @Override
        public void dispose() {
            loaded.clear();
            loadedLayered.clear();
        }

        public void apply(CITContext context) {
            if (context == null) {
                //todo clear
                return;
            }

            List<WeakReference<CIT<TypeEnchantment>>> cits = ((CITCacheEnchantment) (Object) context.stack).citresewn$getCacheTypeEnchantment().get(context);

            //todo apply
        }

        public List<CIT<TypeEnchantment>> getRealTimeCIT(CITContext context) {
            List<CIT<TypeEnchantment>> cits = new ArrayList<>();
            for (List<CIT<TypeEnchantment>> layer : loadedLayered)
                for (CIT<TypeEnchantment> cit : layer)
                    if (cit.test(context)) {
                        cits.add(cit);
                        break;
                    }

            return cits;
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

    public interface CITCacheEnchantment {
        CITCache.MultiList<TypeEnchantment> citresewn$getCacheTypeEnchantment();
    }
}
