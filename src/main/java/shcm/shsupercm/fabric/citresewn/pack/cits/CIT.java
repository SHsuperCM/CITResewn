package shcm.shsupercm.fabric.citresewn.pack.cits;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import shcm.shsupercm.fabric.citresewn.ex.CITParseException;
import shcm.shsupercm.fabric.citresewn.pack.CITPack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public abstract class CIT {
    public final Set<Item> items = new HashSet<>();

    public final Identifier assetIdentifier;
    public final boolean needsModel;

    public final int damageMin, damageMax;
    public final boolean damageAny, damageRange, damagePercentage;
    public final Integer damageMask;

    public final int stackMin, stackMax;
    public final boolean stackAny, stackRange;

    public CIT(CITPack pack, Identifier identifier, Properties properties) throws CITParseException {
        try {
            for (String itemId : (properties.getProperty("items", properties.getProperty("matchItems", " "))).split(" "))
                if (!itemId.isEmpty()) {
                    Item item = Registry.ITEM.get(new Identifier(itemId));
                    if (item == Items.AIR)
                        throw new Exception("Unknown item " + itemId);
                    this.items.add(item);
                }
            if (this.items.size() == 0 && !properties.getProperty("type", "item").equals("enchantment"))
                throw new Exception("CIT must target at least one item type");

            Identifier modelIdentifier = new Identifier(properties.getProperty("model", identifier.getPath().substring(0, identifier.getPath().length() - ".properties".length()) + ".json"));
            if (pack.resourcePack.contains(ResourceType.CLIENT_RESOURCES, modelIdentifier)) {
                this.assetIdentifier = modelIdentifier;
                this.needsModel = false;
            } else {
                String[] split = modelIdentifier.getPath().split("/");
                String parent = String.join("/", Arrays.copyOf(split, split.length - 1, String[].class));
                modelIdentifier = new Identifier(parent + "/" + modelIdentifier.getPath());
                if (pack.resourcePack.contains(ResourceType.CLIENT_RESOURCES, modelIdentifier)) {
                    this.assetIdentifier = modelIdentifier;
                    this.needsModel = false;
                } else {
                    Identifier textureIdentifier = new Identifier(properties.getProperty("texture", identifier.getPath().substring(0, identifier.getPath().length() - ".properties".length()) + ".png"));
                    if (!pack.resourcePack.contains(ResourceType.CLIENT_RESOURCES, textureIdentifier)) {
                        textureIdentifier = new Identifier(parent + "/" + textureIdentifier.getPath());

                        if (!pack.resourcePack.contains(ResourceType.CLIENT_RESOURCES, textureIdentifier))
                            throw new Exception("CIT must have either a texture or a model");
                    }

                    this.assetIdentifier = textureIdentifier;
                    this.needsModel = true;
                }
            }

            String damage = properties.getProperty("damage");
            if (damageAny = damage == null) {
                this.damageRange = false;
                this.damagePercentage = false;
                this.damageMin = 0;
                this.damageMax = 0;
            } else {
                if (damage.startsWith("-"))
                    throw new Exception("damage cannot be negative");

                if (this.damagePercentage = damage.contains("%"))
                    damage = damage.replace("%", "");

                if (damage.contains("-")) {
                    String[] split = damage.split("-");
                    if (split.length != 2)
                        throw new Exception("damage range must have 2 numbers");

                    if ((this.damageMin = Integer.parseInt(split[0])) > (this.damageMax = Integer.parseInt(split[1])))
                        throw new Exception("damage range min is higher than max");

                    this.damageRange = this.damageMin < this.damageMax;
                } else {
                    this.damageRange = true;
                    this.damageMin = this.damageMax = Integer.parseInt(damage);
                }
            }

            this.damageMask = Integer.parseInt(properties.getProperty("damageMask"));

            String stackSize = properties.getProperty("stackSize");
            if (stackAny = stackSize == null) {
                this.stackRange = false;
                this.stackMin = 0;
                this.stackMax = 0;
            } else {
                if (stackSize.startsWith("-"))
                    throw new Exception("stackSize cannot be negative");

                if (stackSize.contains("-")) {
                    String[] split = stackSize.split("-");
                    if (split.length != 2)
                        throw new Exception("stackSize range must have 2 numbers");

                    if ((this.stackMin = Integer.parseInt(split[0])) > (this.stackMax = Integer.parseInt(split[1])))
                        throw new Exception("stackSize range min is higher than max");

                    this.stackRange = this.stackMin < this.stackMax;
                } else {
                    this.stackRange = true;
                    this.stackMin = this.stackMax = Integer.parseInt(stackSize);
                }
            }
        } catch (Exception e) {
            throw new CITParseException(pack.resourcePack, identifier, e.getMessage());
        }
    }
}
