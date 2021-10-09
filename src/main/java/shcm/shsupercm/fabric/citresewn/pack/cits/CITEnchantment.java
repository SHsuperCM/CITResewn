package shcm.shsupercm.fabric.citresewn.pack.cits;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import shcm.shsupercm.fabric.citresewn.ex.CITParseException;
import shcm.shsupercm.fabric.citresewn.mixin.citenchantment.BufferBuilderStorageAccessor;
import shcm.shsupercm.fabric.citresewn.mixin.citenchantment.RenderPhaseAccessor;
import shcm.shsupercm.fabric.citresewn.pack.CITPack;

import java.util.*;
import java.util.function.Consumer;

public class CITEnchantment extends CIT {
    public final Identifier textureIdentifier;
    public final float speed, rotation, duration;
    public final int layer;
    public final Blend blend;

    public final Map<GlintRenderLayer, RenderLayer> renderLayers = new EnumMap<>(GlintRenderLayer.class);

    public CITEnchantment(CITPack pack, Identifier identifier, Properties properties) throws CITParseException {
        super(pack, identifier, properties);
        try {
            textureIdentifier = resolvePath(identifier, properties.getProperty("texture"), ".png", id -> pack.resourcePack.contains(ResourceType.CLIENT_RESOURCES, id));
            if (textureIdentifier == null)
                throw new Exception("Cannot resolve texture");

            layer = Integer.parseInt(properties.getProperty("layer", "0"));

            blend = Blend.valueOf(properties.getProperty("blend", "add").toUpperCase(Locale.ENGLISH));

            speed = Float.parseFloat(properties.getProperty("speed", "0"));

            rotation = Float.parseFloat(properties.getProperty("rotation", "0"));

            duration = Float.max(0f, Float.parseFloat(properties.getProperty("duration", "0")));
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

    @Override
    public void dispose() {
        for (RenderLayer renderLayer : renderLayers.values())
            ((BufferBuilderStorageAccessor) MinecraftClient.getInstance().getBufferBuilders()).entityBuilders().remove(renderLayer);
    }

    public enum Blend {
        ADD,
        SUBTRACT,
        MULTIPLY,
        DODGE,
        BURN,
        SCREEN,
        REPLACE,
        OVERLAY,
        ALPHA;
    }

    public enum GlintRenderLayer {
        ARMOR_GLINT("armor_glint", layer -> layer
                .shader(RenderPhaseAccessor.ARMOR_GLINT_SHADER())
                .writeMaskState(RenderPhaseAccessor.COLOR_MASK())
                .cull(RenderPhaseAccessor.DISABLE_CULLING())
                .depthTest(RenderPhaseAccessor.EQUAL_DEPTH_TEST())
                .transparency(RenderPhaseAccessor.GLINT_TRANSPARENCY())
                .texturing(RenderPhaseAccessor.GLINT_TEXTURING())
                .layering(RenderPhaseAccessor.VIEW_OFFSET_Z_LAYERING())),
        ARMOR_ENTITY_GLINT("armor_entity_glint", layer -> layer
                .shader(RenderPhaseAccessor.ARMOR_ENTITY_GLINT_SHADER())
                .writeMaskState(RenderPhaseAccessor.COLOR_MASK())
                .cull(RenderPhaseAccessor.DISABLE_CULLING())
                .depthTest(RenderPhaseAccessor.EQUAL_DEPTH_TEST())
                .transparency(RenderPhaseAccessor.GLINT_TRANSPARENCY())
                .texturing(RenderPhaseAccessor.ENTITY_GLINT_TEXTURING())
                .layering(RenderPhaseAccessor.VIEW_OFFSET_Z_LAYERING())),
        GLINT_TRANSLUCENT("glint_translucent", layer -> layer
                .shader(RenderPhaseAccessor.TRANSLUCENT_GLINT_SHADER())
                .writeMaskState(RenderPhaseAccessor.COLOR_MASK())
                .cull(RenderPhaseAccessor.DISABLE_CULLING())
                .depthTest(RenderPhaseAccessor.EQUAL_DEPTH_TEST())
                .transparency(RenderPhaseAccessor.GLINT_TRANSPARENCY())
                .target(RenderPhaseAccessor.ITEM_TARGET())
                .texturing(RenderPhaseAccessor.GLINT_TEXTURING())),
        GLINT("glint", layer -> layer
                .shader(RenderPhaseAccessor.GLINT_SHADER())
                .writeMaskState(RenderPhaseAccessor.COLOR_MASK())
                .cull(RenderPhaseAccessor.DISABLE_CULLING())
                .depthTest(RenderPhaseAccessor.EQUAL_DEPTH_TEST())
                .transparency(RenderPhaseAccessor.GLINT_TRANSPARENCY())
                .texturing(RenderPhaseAccessor.GLINT_TEXTURING())),
        DIRECT_GLINT("glint_direct", layer -> layer
                .shader(RenderPhaseAccessor.DIRECT_GLINT_SHADER())
                .writeMaskState(RenderPhaseAccessor.COLOR_MASK())
                .cull(RenderPhaseAccessor.DISABLE_CULLING())
                .depthTest(RenderPhaseAccessor.EQUAL_DEPTH_TEST())
                .transparency(RenderPhaseAccessor.GLINT_TRANSPARENCY())
                .texturing(RenderPhaseAccessor.GLINT_TEXTURING())),
        ENTITY_GLINT("entity_glint", layer -> layer
                .shader(RenderPhaseAccessor.ENTITY_GLINT_SHADER())
                .writeMaskState(RenderPhaseAccessor.COLOR_MASK())
                .cull(RenderPhaseAccessor.DISABLE_CULLING())
                .depthTest(RenderPhaseAccessor.EQUAL_DEPTH_TEST())
                .transparency(RenderPhaseAccessor.GLINT_TRANSPARENCY())
                .target(RenderPhaseAccessor.ITEM_TARGET())
                .texturing(RenderPhaseAccessor.ENTITY_GLINT_TEXTURING())),
        DIRECT_ENTITY_GLINT("entity_glint_direct", layer -> layer
                .shader(RenderPhaseAccessor.DIRECT_ENTITY_GLINT_SHADER())
                .writeMaskState(RenderPhaseAccessor.COLOR_MASK())
                .cull(RenderPhaseAccessor.DISABLE_CULLING())
                .depthTest(RenderPhaseAccessor.EQUAL_DEPTH_TEST())
                .transparency(RenderPhaseAccessor.GLINT_TRANSPARENCY())
                .texturing(RenderPhaseAccessor.ENTITY_GLINT_TEXTURING()));

        public final String name;
        protected final Consumer<RenderLayer.MultiPhaseParameters.Builder> setup;

        GlintRenderLayer(String name, Consumer<RenderLayer.MultiPhaseParameters.Builder> setup) {
            this.name = name;
            this.setup = setup;
        }

        public RenderLayer build(CITEnchantment enchantment) {
            //noinspection ConstantConditions
            RenderLayer.MultiPhaseParameters.Builder layer = RenderLayer.MultiPhaseParameters.builder()
                    .texture(new RenderPhase.Texture(enchantment.textureIdentifier, true, false));

            this.setup.accept(layer);

            return RenderLayer.of("citresewn:enchantment_" + this.name + ":" + enchantment.propertiesIdentifier.toString(),
                    VertexFormats.POSITION_TEXTURE,
                    VertexFormat.DrawMode.QUADS,
                    256,
                    layer.build(false));
        }
    }
}