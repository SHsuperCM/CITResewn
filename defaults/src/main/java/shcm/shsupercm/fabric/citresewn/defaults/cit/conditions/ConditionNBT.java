package shcm.shsupercm.fabric.citresewn.defaults.cit.conditions;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import net.minecraft.nbt.*;
import net.minecraft.text.Text;
import shcm.shsupercm.fabric.citresewn.api.CITConditionContainer;
import shcm.shsupercm.fabric.citresewn.cit.CITCondition;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.cit.CITParsingException;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ConditionNBT extends CITCondition {
    @Entrypoint(CITConditionContainer.ENTRYPOINT)
    public static final CITConditionContainer<ConditionNBT> CONTAINER = new CITConditionContainer<>(ConditionNBT.class, ConditionNBT::new,
            "nbt");

    protected String[] path;

    protected StringMatcher matchString = null;
    protected NbtInt matchInteger = null;
    protected NbtByte matchByte = null;
    protected NbtFloat matchFloat = null;
    protected NbtDouble matchDouble = null;
    protected NbtLong matchLong = null;
    protected NbtShort matchShort = null;
    protected NbtCompound matchCompound = null;

    @Override
    public void load(PropertyValue value, PropertyGroup properties) throws CITParsingException {
        if (value.keyMetadata() == null || value.keyMetadata().isEmpty())
            throw new CITParsingException("Missing nbt path", properties, value.position());

        path = value.keyMetadata().split("\\.");
        for (String s : path)
            if (s.isEmpty())
                throw new CITParsingException("Path segment cannot be empty", properties, value.position());

        try {
            if (value.value().startsWith("regex:"))
                matchString = new StringMatcher.RegexMatcher(value.value().substring(6));
            else if (value.value().startsWith("iregex:"))
                matchString = new StringMatcher.IRegexMatcher(value.value().substring(7));
            else if (value.value().startsWith("pattern:"))
                matchString = new StringMatcher.PatternMatcher(value.value().substring(8));
            else if (value.value().startsWith("ipattern:"))
                matchString = new StringMatcher.IPatternMatcher(value.value().substring(9));
            else
                matchString = new StringMatcher.DirectMatcher(value.value());
        } catch (PatternSyntaxException e) {
            throw new CITParsingException("Malformatted regex expression", properties, value.position(), e);
        } catch (Exception ignored) { }
        try {
            if (value.value().startsWith("#"))
                matchInteger = NbtInt.of(Integer.parseInt(value.value().substring(1).toLowerCase(Locale.ENGLISH), 16));
            else if (value.value().startsWith("0x"))
                matchInteger = NbtInt.of(Integer.parseInt(value.value().substring(2).toLowerCase(Locale.ENGLISH), 16));
            else
                matchInteger = NbtInt.of(Integer.parseInt(value.value()));
        } catch (Exception ignored) { }
        try {
            matchByte = NbtByte.of(Byte.parseByte(value.value()));
        } catch (Exception ignored) { }
        try {
            matchFloat = NbtFloat.of(Float.parseFloat(value.value()));
        } catch (Exception ignored) { }
        try {
            matchDouble = NbtDouble.of(Double.parseDouble(value.value()));
        } catch (Exception ignored) { }
        try {
            matchLong = NbtLong.of(Long.parseLong(value.value()));
        } catch (Exception ignored) { }
        try {
            matchShort = NbtShort.of(Short.parseShort(value.value()));
        } catch (Exception ignored) { }
        try {
            matchCompound = StringNbtReader.parse(value.value());
        } catch (Exception ignored) { }
    }

    @Override
    public boolean test(CITContext context) {
        return testPath(context.stack.getNbt(), 0);
    }

    protected boolean testPath(NbtElement element, int pathIndex) {
        if (element == null)
            return false;

        if (pathIndex >= path.length)
            return testValue(element);

        final String path = this.path[pathIndex];
        if (path.equals("*")) {
            if (element instanceof NbtCompound compound) {
                for (NbtElement subElement : compound.entries.values())
                    if (testPath(subElement, pathIndex + 1))
                        return true;
            } else if (element instanceof NbtList list) {
                for (NbtElement subElement : list)
                    if (testPath(subElement, pathIndex + 1))
                        return true;
            }
        } else {
            if (element instanceof NbtCompound compound)
                return testPath(compound.get(path), pathIndex + 1);
            else if (element instanceof NbtList list) {
                if (path.equals("count"))
                    return testValue(NbtInt.of(list.size()));

                try {
                    return testPath(list.get(Integer.parseInt(path)), pathIndex + 1);
                } catch (NumberFormatException | IndexOutOfBoundsException ignored) { }
            }
        }

        return false;
    }

    private boolean testValue(NbtElement element) {
        try {
            if (element instanceof NbtString nbtString) //noinspection ConstantConditions
                return matchString.matches(nbtString.asString()) || matchString.matches(Text.Serializer.fromLenientJson(nbtString.asString()).getString());
            else if (element instanceof NbtInt nbtInt && matchInteger != null)
                return nbtInt.equals(matchInteger);
            else if (element instanceof NbtByte nbtByte && matchByte != null)
                return nbtByte.equals(matchByte);
            else if (element instanceof NbtFloat nbtFloat && matchFloat != null)
                return nbtFloat.equals(matchFloat);
            else if (element instanceof NbtDouble nbtDouble && matchDouble != null)
                return nbtDouble.equals(matchDouble);
            else if (element instanceof NbtLong nbtLong && matchLong != null)
                return nbtLong.equals(matchLong);
            else if (element instanceof NbtShort nbtShort && matchShort != null)
                return nbtShort.equals(matchShort);
            else if (element instanceof NbtCompound nbtCompound && matchCompound != null)
                return NbtHelper.matches(matchCompound, nbtCompound, true);

            if (element instanceof AbstractNbtNumber nbtNumber && !(matchString instanceof StringMatcher.DirectMatcher))
                return matchString.matches(String.valueOf(nbtNumber.numberValue()));
        } catch (Exception ignored) { }
        return false;
    }

    protected static abstract class StringMatcher {
        public abstract boolean matches(String value);

        public static class DirectMatcher extends StringMatcher {
            protected final String pattern;

            public DirectMatcher(String pattern) {
                this.pattern = pattern;
            }

            @Override
            public boolean matches(String value) {
                return pattern.equals(value);
            }
        }

        public static class RegexMatcher extends StringMatcher {
            protected final Pattern pattern;

            public RegexMatcher(String pattern) {
                this(Pattern.compile(pattern));
            }

            protected RegexMatcher(Pattern pattern) {
                this.pattern = pattern;
            }

            @Override
            public boolean matches(String value) {
                return this.pattern.matcher(value).matches();
            }
        }

        public static class PatternMatcher extends StringMatcher {
            protected final String pattern;

            public PatternMatcher(String pattern) {
                this.pattern = pattern;
            }

            @Override
            public boolean matches(String value) {
                return matchesPattern(value, this.pattern, 0, value.length(), 0, pattern.length());
            }

            /**
             * Author: Paul "prupe" Rupe<br>
             * Taken and modified from MCPatcher under public domain licensing.<br>
             * https://bitbucket.org/prupe/mcpatcher/src/1aa45839b2cd029143809edfa60ec59e5ef75f80/newcode/src/com/prupe/mcpatcher/mal/nbt/NBTRule.java#lines-269:301
             */
            protected boolean matchesPattern(String value, String pattern, int curV, int maxV, int curG, int maxG) {
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

                    if (!charsEqual(g, value.charAt(curV)))
                        return false;
                }
                return curG == maxG && curV == maxV;
            }

            protected boolean charsEqual(char p, char v) {
                return p == v;
            }
        }

        public static class IRegexMatcher extends RegexMatcher {
            public IRegexMatcher(String pattern) {
                super(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE));
            }
        }

        public static class IPatternMatcher extends PatternMatcher {
            public IPatternMatcher(String pattern) {
                super(pattern.toLowerCase(Locale.ROOT));
            }

            @Override
            protected boolean charsEqual(char p, char v) {
                return p == v || p == Character.toLowerCase(v);
            }
        }
    }
}
