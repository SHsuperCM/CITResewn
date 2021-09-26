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
Effects that change something about the item. The different types usually 
only add effects.

While CIT Resewn only provides the 4 types, other mods may add their own,
allowing resourcepack authors to customize their mods using CITs.

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
| WIP | WIP | THIS TABLE IS MISSING PROPERTIES | WIP |
