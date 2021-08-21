package shcm.shsupercm.fabric.citresewn.pack.cits;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.nbt.*;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.StringEscapeUtils;
import shcm.shsupercm.fabric.citresewn.ex.CITParseException;
import shcm.shsupercm.fabric.citresewn.mixin.NbtCompoundAccessor;
import shcm.shsupercm.fabric.citresewn.pack.CITPack;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public abstract class CIT {
    public final Set<Item> items = new HashSet<>();

    public final Identifier assetIdentifier;
    public final boolean needsModel;

    public final int damageMin, damageMax;
    public final boolean damageAny, damageRange, damagePercentage;
    public final Integer damageMask;

    public final int stackMin, stackMax;
    public final boolean stackAny, stackRange;

    public final Set<Enchantment> enchantments = new HashSet<>();
    public final List<Pair<Integer, Integer>> enchantmentLevels = new ArrayList<>();
    public final boolean enchantmentsAny, enchantmentLevelsAny;

    public final Hand hand;

    public final Predicate<NbtCompound> nbt;

    public CIT(CITPack pack, Identifier identifier, Properties properties) throws CITParseException {
        try {
            for (String itemId : (properties.getProperty("items", properties.getProperty("matchItems", " "))).split(" "))
                if (!itemId.isEmpty()) {
                    Identifier itemIdentifier = new Identifier(itemId);
                    if (!Registry.ITEM.containsId(itemIdentifier))
                        throw new Exception("Unknown item " + itemId);
                    this.items.add(Registry.ITEM.get(itemIdentifier));
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

            String enchantmentIDs = properties.getProperty("enchantments", properties.getProperty("enchantmentIDs"));
            if (!(this.enchantmentsAny = enchantmentIDs == null)) {
                for (String ench : enchantmentIDs.split(" ")) {
                    Identifier enchIdentifier = new Identifier(ench);
                    if (!Registry.ENCHANTMENT.containsId(enchIdentifier))
                        throw new Exception("Unknown enchantment " + ench);
                    this.enchantments.add(Registry.ENCHANTMENT.get(enchIdentifier));
                }
            }

            String enchantmentLevelsProp = properties.getProperty("enchantmentLevels");
            if (!(this.enchantmentLevelsAny = enchantmentLevelsProp == null)) {
                for (String range : enchantmentLevelsProp.split(" ")) {
                    if (range.contains("-")) {
                        if (range.startsWith("-")) {
                            range = range.substring(1);
                            if (range.contains("-"))
                                throw new Exception("enchantmentLevels ranges must have up to 2 numbers each");
                            this.enchantmentLevels.add(new Pair<>(0, Integer.parseInt(range)));
                        } else if (range.endsWith("-")) {
                            range = range.substring(0, range.length() - 1);
                            if (range.contains("-"))
                                throw new Exception("enchantmentLevels ranges must have up to 2 numbers each");
                            this.enchantmentLevels.add(new Pair<>(Integer.parseInt(range), Integer.MAX_VALUE));
                        } else {
                            String[] split = range.split("-");
                            if (split.length != 2)
                                throw new Exception("enchantmentLevels ranges must have up to 2 numbers each");
                            Pair<Integer, Integer> minMaxPair = new Pair<>(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
                            if (minMaxPair.getLeft() > minMaxPair.getRight())
                                throw new Exception("enchantmentLevels range min is higher than max");
                            this.enchantmentLevels.add(minMaxPair);
                        }
                    } else {
                        int level = Integer.parseInt(range);
                        this.enchantmentLevels.add(new Pair<>(level, level));
                    }
                }
            }

            this.hand = switch (properties.getProperty("hand", "any")) {
                case "main" -> Hand.MAIN_HAND;
                case "off" -> Hand.OFF_HAND;
                default -> null;
            };

            List<Predicate<NbtCompound>> nbtPredicates = new ArrayList<>();
            for (Object o : properties.keySet())
                if (o instanceof String property && property.startsWith("nbt.")) {
                    final String[] path = property.substring(4).split("\\.");
                    final Predicate<String> match;

                    String matchProperty = StringEscapeUtils.unescapeJava(properties.getProperty(property));
                    final boolean caseSensitive = !matchProperty.startsWith("i");
                    if (matchProperty.startsWith(caseSensitive ? "pattern:" : "ipattern:")) {
                        final String pattern = caseSensitive ? matchProperty.substring(8) : matchProperty.substring(9).toLowerCase(Locale.ENGLISH);
                        match = s -> matchesPattern(caseSensitive ? s : s.toLowerCase(), pattern, 0, s.length(), 0, pattern.length());
                    } else if (matchProperty.startsWith(caseSensitive ? "regex:" : "iregex:")) {
                        final Pattern pattern = Pattern.compile(caseSensitive ? matchProperty.substring(5) : matchProperty.substring(6).toLowerCase(Locale.ENGLISH));
                        match = s -> pattern.matcher(caseSensitive ? s : s.toLowerCase()).matches();
                    } else
                        match = s -> s.equals(matchProperty);

                    nbtPredicates.add(new Predicate<NbtCompound>() {
                        public boolean test(NbtElement nbtElement, int index) {
                            if (index >= path.length) {
                                if (nbtElement instanceof NbtString nbtString) {
                                    return match.test(nbtString.asString());
                                } else if (nbtElement instanceof AbstractNbtNumber nbtNumber)
                                    return match.test(String.valueOf(nbtNumber.numberValue()));
                            } else {
                                String name = path[index];
                                if (name.equals("*")) {
                                    if (nbtElement instanceof NbtCompound nbtCompound) {
                                        for (NbtElement subElement : ((NbtCompoundAccessor) nbtCompound).getEntries().values())
                                            if (test(subElement, index + 1))
                                                return true;
                                    } else if (nbtElement instanceof NbtList nbtList) {
                                        for (NbtElement subElement : nbtList)
                                            if (test(subElement, index + 1))
                                                return true;
                                    }
                                } else {
                                    if (nbtElement instanceof NbtCompound nbtCompound) {
                                        NbtElement subElement = nbtCompound.get(name);
                                        return subElement != null && test(subElement, index + 1);
                                    } else if (nbtElement instanceof NbtList nbtList) {
                                        try {
                                            NbtElement subElement = nbtList.get(Integer.parseInt(name));
                                            return subElement != null && test(subElement, index + 1);
                                        } catch (NumberFormatException ignored) {
                                            return false;
                                        }
                                    }
                                }
                            }
                            return false;
                        }

                        @Override
                        public boolean test(NbtCompound nbtCompound) {
                            return test(nbtCompound, 0);
                        }
                    });
                }
            this.nbt = nbtCompound -> {
                for (Predicate<NbtCompound> predicate : nbtPredicates)
                    if(!predicate.test(nbtCompound))
                        return false;
                return true;
            };

        } catch (Exception e) {
            throw new CITParseException(pack.resourcePack, identifier, e.getMessage());
        }
    }

    /**
     * Author: Paul "prupe" Rupe<br>
     * Taken from MCPatcher under public domain licensing.<br>
     * https://bitbucket.org/prupe/mcpatcher/src/1aa45839b2cd029143809edfa60ec59e5ef75f80/newcode/src/com/prupe/mcpatcher/mal/nbt/NBTRule.java#lines-269:301
     */
    public static boolean matchesPattern(String value, String pattern, int curV, int maxV, int curG, int maxG) {
        for (; curG < maxG; curG++, curV++) {
            char g = pattern.charAt(curG);
            if (g == '*') {
                while (true) {
                    if (matchesPattern(value, pattern, curV, maxV, curG + 1, maxG)) {
                        return true;
                    }
                    if (curV >= maxV) {
                        break;
                    }
                    curV++;
                }
                return false;
            } else if (curV >= maxV) {
                break;
            } else if (g == '?') {
                continue;
            }
            if (g == '\\' && curG + 1 < maxG) {
                curG++;
                g = pattern.charAt(curG);
            }

            if (g != value.charAt(curV))
                return false;
        }
        return curG == maxG && curV == maxV;
    }
}
