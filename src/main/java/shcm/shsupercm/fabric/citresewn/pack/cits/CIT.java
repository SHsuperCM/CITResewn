package shcm.shsupercm.fabric.citresewn.pack.cits;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringEscapeUtils;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.ex.CITParseException;
import shcm.shsupercm.fabric.citresewn.mixin.core.NbtCompoundAccessor;
import shcm.shsupercm.fabric.citresewn.pack.CITPack;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public abstract class CIT {
    public final CITPack pack;
    public final Identifier propertiesIdentifier;

    public final Set<Item> items = new HashSet<>();

    public final int damageMin, damageMax;
    public final boolean damageAny, damageRange, damagePercentage;
    public final Integer damageMask;

    public final int stackMin, stackMax;
    public final boolean stackAny, stackRange;

    public final Set<Identifier> enchantments = new HashSet<>();
    public final List<Pair<Integer, Integer>> enchantmentLevels = new ArrayList<>();
    public final boolean enchantmentsAny, enchantmentLevelsAny;

    public final Hand hand;

    public final Predicate<NbtCompound> nbt;

    public final int weight;

    public CIT(CITPack pack, Identifier identifier, Properties properties) throws CITParseException {
        this.pack = pack;
        this.propertiesIdentifier = identifier;
        try {
            for (String itemId : (properties.getProperty("items", properties.getProperty("matchItems", " "))).split(" "))
                if (!itemId.isEmpty()) {
                    Identifier itemIdentifier = new Identifier(itemId);
                    if (!Registry.ITEM.containsId(itemIdentifier))
                        throw new Exception("Unknown item " + itemId);
                    this.items.add(Registry.ITEM.get(itemIdentifier));
                }
            if (this.items.isEmpty())
                try {
                    String id = propertiesIdentifier.getPath().substring(0, propertiesIdentifier.getPath().length() - 11);
                    String[] split = id.split("/");
                    id = split[split.length - 1];
                    Identifier itemId = new Identifier(propertiesIdentifier.getNamespace(), id);
                    if (Registry.ITEM.containsId(itemId))
                        this.items.add(Registry.ITEM.get(itemId));
                } catch (Exception ignored) { }

            String damage = properties.getProperty("damage");
            if (damageAny = damage == null) {
                this.damageRange = false;
                this.damagePercentage = false;
                this.damageMin = 0;
                this.damageMax = 0;
            } else {
                if (this.damagePercentage = damage.contains("%"))
                    damage = damage.replace("%", "");

                if (damage.contains("-")) {
                    String[] split = damage.split("-");
                    if (split.length > 2)
                        throw new Exception("damage range must have up to 2 numbers");

                    this.damageMin = split[0].isEmpty() ? Integer.MIN_VALUE : Integer.parseInt(split[0]);
                    this.damageMax = split.length == 1 ? Integer.MAX_VALUE : Integer.parseInt(split[1]);

                    if (this.damageMin > this.damageMax)
                        throw new Exception("damage range min is higher than max");

                    this.damageRange = this.damageMin < this.damageMax;
                } else {
                    this.damageRange = false;
                    this.damageMin = this.damageMax = Integer.parseInt(damage);
                }
            }

            this.damageMask = properties.containsKey("damageMask") ? Integer.parseInt(properties.getProperty("damageMask")) : null;

            String stackSize = properties.getProperty("stackSize");
            if (stackAny = stackSize == null) {
                this.stackRange = false;
                this.stackMin = 0;
                this.stackMax = 0;
            } else {
                if (stackSize.contains("-")) {
                    String[] split = stackSize.split("-");
                    if (split.length > 2)
                        throw new Exception("stackSize range must have up to 2 numbers");

                    this.stackMin = split[0].isEmpty() ? Integer.MIN_VALUE : Integer.parseInt(split[0]);
                    this.stackMax = split.length == 1 ? Integer.MAX_VALUE : Integer.parseInt(split[1]);

                    if (this.stackMin > this.stackMax)
                        throw new Exception("stackSize range min is higher than max");

                    this.stackRange = this.stackMin < this.stackMax;
                } else {
                    this.stackRange = false;
                    this.stackMin = this.stackMax = Integer.parseInt(stackSize);
                }
            }

            String enchantmentIDs = properties.getProperty("enchantments", properties.getProperty("enchantmentIDs"));
            if (!(this.enchantmentsAny = enchantmentIDs == null)) {
                for (String ench : enchantmentIDs.split(" ")) {
                    Identifier enchIdentifier = new Identifier(ench);
                    if (!Registry.ENCHANTMENT.containsId(enchIdentifier))
                        CITResewn.logWarnLoading("CIT Warning: Unknown enchantment " + enchIdentifier);
                    this.enchantments.add(enchIdentifier);
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
                        matchProperty = caseSensitive ? matchProperty.substring(8) : matchProperty.substring(9).toLowerCase(Locale.ENGLISH);
                        if ((path[path.length - 1].equals("Name") || path[path.length - 1].equals("Lore")) && !matchProperty.startsWith("{"))
                            matchProperty = "{\"text\":\"" + matchProperty + "\"}";
                        final String pattern = matchProperty;
                        match = s -> matchesPattern(caseSensitive ? s : s.toLowerCase(), pattern, 0, s.length(), 0, pattern.length());
                    } else if (matchProperty.startsWith(caseSensitive ? "regex:" : "iregex:")) {
                        matchProperty = caseSensitive ? matchProperty.substring(6) : matchProperty.substring(7).toLowerCase(Locale.ENGLISH);
                        if ((path[path.length - 1].equals("Name") || path[path.length - 1].equals("Lore")) && !matchProperty.startsWith("{"))
                            matchProperty = "\\{\"text\":\"" + matchProperty + "\"}";
                        final Pattern pattern = Pattern.compile(matchProperty);
                        match = s -> pattern.matcher(caseSensitive ? s : s.toLowerCase()).matches();
                    } else {
                        if ((path[path.length - 1].equals("Name") || path[path.length - 1].equals("Lore")) && !matchProperty.startsWith("{"))
                            matchProperty = "{\"text\":\"" + matchProperty + "\"}";
                        final String pattern = matchProperty;
                        match = s -> s.equals(pattern);
                    }

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
                                        } catch (Exception ignored) {
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

            this.weight = Integer.parseInt(properties.getProperty("weight", "0"));
        } catch (Exception e) {
            throw new CITParseException(pack.resourcePack, identifier, (e.getClass() == Exception.class ? "" : e.getClass().getSimpleName() + ": ") + e.getMessage());
        }
    }

    public boolean test(ItemStack stack, Hand hand, World world, LivingEntity entity) {
        if (!damageAny && stack.getItem().isDamageable()) {
            int damage = stack.getDamage();
            if (damageMask != null)
                damage &= damageMask;
            if (damagePercentage)
                damage = Math.round(100f * (float) stack.getDamage() / (float) stack.getMaxDamage());
            if (damageRange ? (damage < damageMin || damage > damageMax) : (damage != damageMin))
                return false;
        }

        if (!stackAny) {
            int count = stack.getCount();
            if (stackRange ? (count < stackMin || count > stackMax) : (count != stackMin))
                return false;
        }

        if (this.hand != null && this.hand != hand)
            return false;

        if (!enchantmentsAny) {
            Map<Identifier, Integer> stackEnchantments = new LinkedHashMap<>();
            for (NbtElement nbtElement : stack.isOf(Items.ENCHANTED_BOOK) ? EnchantedBookItem.getEnchantmentNbt(stack) : stack.getEnchantments())
                stackEnchantments.put(EnchantmentHelper.getIdFromNbt((NbtCompound) nbtElement), EnchantmentHelper.getLevelFromNbt((NbtCompound) nbtElement));

            boolean matches = false;
            for (Identifier enchantment : enchantments) {
                Integer level = stackEnchantments.get(enchantment);
                if (level != null)
                    if (enchantmentLevelsAny) {
                        if (level > 0) {
                            matches = true;
                            break;
                        }
                    } else
                        for (Pair<Integer, Integer> levelRange : enchantmentLevels)
                            if (level >= levelRange.getLeft() && level <= levelRange.getRight()) {
                                matches = true;
                                break;
                            }
            }

            if (!matches)
                return false;
        } else if (!enchantmentLevelsAny) {
            Collection<Integer> levels = new ArrayList<>();
            levels.add(0);
            for (NbtElement nbtElement : stack.isOf(Items.ENCHANTED_BOOK) ? EnchantedBookItem.getEnchantmentNbt(stack) : stack.getEnchantments())
                levels.add(EnchantmentHelper.getLevelFromNbt((NbtCompound) nbtElement));

            boolean matches = false;

            l: for (Integer level : levels) {
                for (Pair<Integer, Integer> levelRange : enchantmentLevels) {
                    if (level >= levelRange.getLeft() && level <= levelRange.getRight()) {
                        matches = true;
                        break l;
                    }
                }
            }

            if (!matches)
                return false;
        }

        return nbt == null || nbt.test(stack.getNbt());
    }

    public void dispose() {
        //stub
    }

    /**
     * Takes a defined path and resolves it to an identifier pointing to the resourcepack's path of the specified extension(returns null if no path can be resolved).<br>
     * If definedPath is null, will try to resolve a relative file with the same name as the propertyIdentifier with the extension, otherwise: <br>
     * definedPath will be formatted to replace "\\" with "/" the extension will be appended if not there already. <br>
     * It will first try using definedPath as an absolute path, if it cant resolve(or definedPath starts with ./), definedPath will be considered relative. <br>
     * Relative paths support going to parent directories using "..".
     */
    public static Identifier resolvePath(Identifier propertyIdentifier, String path, String extension, Predicate<Identifier> packContains) {
        if (path == null) {
            path = propertyIdentifier.getPath().substring(0, propertyIdentifier.getPath().length() - 11);
            if (!path.endsWith(extension))
                path = path + extension;
            Identifier pathIdentifier = new Identifier(propertyIdentifier.getNamespace(), path);
            return packContains.test(pathIdentifier) ? pathIdentifier : null;
        }

        Identifier pathIdentifier = new Identifier(path);

        path = pathIdentifier.getPath().replace('\\', '/');
        if (!path.endsWith(extension))
            path = path + extension;

        if (path.startsWith("./"))
            path = path.substring(2);
        else if (!path.contains("..")) {
            pathIdentifier = new Identifier(pathIdentifier.getNamespace(), path);
            if (packContains.test(pathIdentifier))
                return pathIdentifier;
            else if (path.startsWith("assets/")) {
                path = path.substring(7);
                int sep = path.indexOf('/');
                pathIdentifier = new Identifier(path.substring(0, sep), path.substring(sep + 1));
                if (packContains.test(pathIdentifier))
                    return pathIdentifier;
            }
            pathIdentifier = new Identifier(pathIdentifier.getNamespace(), switch (extension) {
                case ".png" -> "textures/";
                case ".json" -> "models/";

                /* UNREACHABLE FAILSAFE */
                default -> "";
            } + path);
            if (packContains.test(pathIdentifier))
                return pathIdentifier;
        }

        LinkedList<String> pathParts = new LinkedList<>(Arrays.asList(propertyIdentifier.getPath().split("/")));
        pathParts.removeLast();

        if (path.contains("/")) {
            for (String part : path.split("/")) {
                if (part.equals("..")) {
                    if (pathParts.size() == 0)
                        return null;
                    pathParts.removeLast();
                } else
                    pathParts.addLast(part);
            }
        } else
            pathParts.addLast(path);
        path = String.join("/", pathParts);

        pathIdentifier = new Identifier(propertyIdentifier.getNamespace(), path);

        return packContains.test(pathIdentifier) ? pathIdentifier : null;
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
