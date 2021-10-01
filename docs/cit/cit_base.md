# CIT Base
<h6>Note that features marked in <em>italics</em> are unique to CIT Resewn.</h6>

All CIT types, while having different effects, originate from CIT Base. 
Think of it as a parent for all other types and every type inherits its properties, 
sometimes overriding their behavior.

When a property is not specified, its default value is used instead, meaning all 
properties are optional by default. 

Some property keys have aliases, these were 
added for legacy support. Hover over keys to see if they have aliases, try not to 
use them when possible.

**Hover over the properties' types to see example values.**

CITs are split into 2 parts: Conditions which determine how items are selected and 
Effects that change something about the item. All conditions must match for the CIT 
to apply.

## Effects

| Key | Value Type | Description | Default |
| --- | --- | --- | --- |
| `type` | [**Literal**]{Either: &#10 type=item / type=armor / type=elytra / type=enchantment|right} | Which type to apply on this CIT. <br> Either: [`item`](/cit/cit_item) / [`armor`](/cit/cit_armor) / [`elytra`](/cit/cit_elytra) / [`enchantment`](/cit/cit_enchantment) <br> Other mods may add different options. | `item` |

## Conditions

| Key | Value Type                    | Description | Default |
| --- | --- | --- | --- |
| `weight` | [**Integer**]{Examples: &#10 weight=1000 &#10 weight=5 &#10 weight=-3|right} | When multiple CITs match the same item, the CIT with the higher weight is chosen. <br> When multiple matching CITs have the same weight, the CIT file name/path is the tie breaker. | 0 |
| [**`items`**]{Aliases: &#10 matchItems|right} | [**List of items**]{Examples: &#10 items=apple &#10 items=minecraft:stick &#10 items=iron_ingot gold_ingot emerald &#10 items=diamond_hoe|right} | Requires that the item is either of the ones listed. <br> Separated by spaces. <br> Namespaces (`minecraft:`) are optional. <br> [**If not declared and the cit file name is a valid item id, it is used instead.**]{For example, iron_ingot.properties will automatically have items=iron_ingot|bottom} | None |
| `stackSize` | [**Positive integer or a range**]{Examples: &#10 stackSize=15 (only 15) &#10 stackSize=1-3 (between 1 and 3) &#10 stackSize=5- (5 or more) &#10 stackSize=-8 (up to 8)|right} | Requires that the item matches a specific amount. <br>Supports open ended ranges. | Any |
| `damage` | [**Positive integer or a range**]{Examples: &#10 damage=15 (only 15) &#10 damage=1-3 (between 1 and 3) &#10 damage=5- (5 or more) &#10 damage=-8 (up to 8) &#10 damage=50%- (50% or more)|right} | Requires that the item matches a specific damage. <br>Supports open ended ranges. <br> Supports percentages using `%`. | Any |
| `damageMask` | [**Integer**]{Examples: &#10 damageMask=324 &#10 damageMask=1038|right} | Applies a binary mask on the item's damage prior to checking `damage` (does `damage` match item damage & `damageMask`). <br> **Useless with modern items, added just for legacy support.** | Any |
| `hand` | [**Literal**]{Either: &#10 hand=main / hand=off / hand=any} | Requires that the item is in a specific hand. <br> Either: `main` / `off` for mainhand or offhand | Any |
| [**`enchantments`**]{Aliases: &#10 enchantmentIDs|right} | [**List of enchantments**]{Examples: &#10 enchantments=sharpness &#10 enchantments=minecraft:power &#10 enchantments=unbreaking mending &#10 enchantments=bane_of_arthropods|right} | Requires that the item has at least one of the enchantments listed. <br> Separated by spaces. <br> Namespaces (`minecraft:`) are optional. | Any |
| `enchantmentLevels` | [**List of levels/ranges**]{Examples: &#10 enchantmentLevels=5 (only 5) &#10 enchantmentLevels=1- (1 or more) &#10 enchantmentLevels=-2 (up to 2) &#10 enchantmentLevels=1-3 5 (between 1 and 3 or 5) &#10 enchantmentLevels=1 3 (1 or 3)|right} | Requires that one of the item's enchantments matches one of the levels listed. <br> When `enchantments` is declared, requires that either one of `enchantments` matches the levels. <br> Separated by spaces. | Any |
| [`nbt.`[**<ins>  path  </ins>**]{Path from the root of the nbt, use numbers for list index and * to match any nbt entry.|right}](#nbt-path) | [**[NBT Match](#nbt-match)**]{Examples: &#10 nbt.display.Name=Cool Item &#10 nbt.CustomModelData=5 &#10 nbt.display.Name=regex:.+'s Cool Item &#10 nbt.display.Name=ipattern:The knife of * |right} | Requires that the item matches the specified NBT predicate. <br> [**Multiple nbt conditions are allowed.**]{As long as the paths are unique.|bottom} <br> Accepts literal, pattern and regex checks, along with ignore-case alternatives. <br> When path is `display.Name` or `display.Lore.__`, the nbt is json evaluated when the match value is not a json. <br> See [NBT Match](#nbt-match) for more info.  | None |

<br>
<br>
<hr>

## NBT Path

The `nbt` property cannot be declared by itself, there must be a unique nbt tag 
path to target for every `nbt` condition.

The path starts from the item's `tag` root and is separated by dots. For example:  
```properties
nbt.CustomModelData=53
```
will match the nbt for `{tag:{CustomModelData:53}}`.  
And
```properties
nbt.display.Name={"text":"Some Item's Name"}
```
will match the nbt for `{tag:{display:{Name:"{\"text\":\"Some Item's Name\"}"}}}`.

When a path crosses an nbt list, the next part of the path is the index. 
Paths also support wildcards using `*` for both nbt lists and compounds.

So to match the first lore line you'd use:
```properties
nbt.display.Lore.0=The first line of the lore
```
And to match any lore line you'd use:
```properties
nbt.display.Lore.*=Any line in the lore
```

## NBT Match

There are multiple options for how you could do this. Either by literal 
values, matching patterns or by matching regex. Pattern and regex matching 
also provide case-insensitive options.

To match literally, the value is used normally. So to match the item name 
"`Some Name`", you'd write:
```properties
nbt.display.Name=Some Name
```

To match a pattern, add the `pattern:` prefix or the `ipattern:` prefix for 
case-insensitivity.  
Patterns allow for both wild cards and wild characters, declared using `*` and 
`?` respectively.  
To match [**a name before "`'s Sword`"**]{For example: SHsuperCM's Sword}, you'd write:
```properties
nbt.display.Name=pattern:*'s Sword
```
To match [**any one character after "`Part `"**]{For example: Part C}, you'd write:
```properties
nbt.display.Name=pattern:Part ?
```
Multiple wild cards and characters are allowed.

To match regex, add the `regex:` prefix or the `iregex:` prefix for case-insensitivity.  
For example, to match any of the following: "`First Item`" / "`Second Item`" / "`Third Item`", 
you'd write:
```properties
nbt.display.Name=regex:^(First|Second|Third) Item$
```

#### Matching json

When matching either `nbt.display.Name` or `nbt.display.Lore.___`, the match part can be 
either a json or not. When not matching a json, the json in the item is evaluated into its 
plain text representation. For example `{"text":"Some Item's Name"}` is evaluated and matches
correctly against `Some Item's Name`.

This means that to match an item that's been named: "`Some Item's Name`", you can either do:
```properties
nbt.display.Name={"text":"Some Item's Name"}
```
or
```properties
nbt.display.Name=Some Item's Name
```

Note that this is supported for both patterns and regex as well by prefixing before the json if present.

<hr>