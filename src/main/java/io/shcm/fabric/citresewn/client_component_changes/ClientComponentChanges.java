package io.shcm.fabric.citresewn.client_component_changes;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentHolder;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ClientComponentChanges {
    public static final ClientComponentChanges EMPTY = new ClientComponentChanges(new Reference2ObjectArrayMap<>()) {
        @Override
        public <T> Optional<T> get(ComponentType<? extends T> type) {
            return null;
        }

        @Override
        public <T> T getOrDefault(ComponentType<? extends T> type, T original) {
            return original;
        }
    };

    private final Reference2ObjectMap<ComponentType<?>, Optional<?>> changedComponents;

    public ClientComponentChanges(Reference2ObjectMap<ComponentType<?>, Optional<?>> changedComponents) {
        this.changedComponents = changedComponents;
    }

    public static <T extends ComponentHolder> ClientComponentChanges fromHolder(T holder) {
        ClientComponentChanges componentChanges = new ClientComponentChanges(new Reference2ObjectArrayMap<>());

        if (holder instanceof ItemStack stack)
            ClientComponentChangesEvents.ITEMSTACK.invoker().resolveClientComponents(stack, componentChanges);

        return componentChanges;
    }

    public <T> Optional<T> get(ComponentType<? extends T> type) {
        return (Optional<T>) this.changedComponents.get(type);
    }

    public <T> T getOrDefault(ComponentType<? extends T> type, T original) {
        Optional<?> clientComponent = get(type);
        if (clientComponent != null)
            return (T) clientComponent.orElse(null);
        return original;
    }

    public Set<ComponentType<?>> componentTypes() {
        return this.changedComponents.keySet();
    }

    public Set<Map.Entry<ComponentType<?>, Optional<?>>> entrySet() {
        return this.changedComponents.entrySet();
    }

    public <T> void add(ComponentType<? extends T> type, T value) {
        this.changedComponents.put(type, Optional.of(value));
    }

    public <T> void remove(ComponentType<? extends T> type) {
        this.changedComponents.put(type, Optional.empty());
    }

    public <T> void reset(ComponentType<? extends T> type) {
        this.changedComponents.remove(type);
    }

    public void clear() {
        this.changedComponents.clear();
    }

    public void from(ComponentChanges changes) {
        for (Map.Entry<ComponentType<?>, Optional<?>> entry : changes.entrySet())
            this.changedComponents.put(entry.getKey(), entry.getValue());
    }
}
