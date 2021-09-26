# CIT Base
<h6>Note that features marked in <em>italics</em> are unique to CIT Resewn.</h6>

All CIT types, while having different effects, originate from CIT Base. 
Think of it as a parent for all other types and every type inherits its properties, 
sometimes overriding their behavior.

When a property is not specified, its default value is used instead, meaning all 
properties are optional by default. Some property keys have aliases, these were 
added for legacy support. Hover over keys to see if they have aliases, try not to 
use them when possible. Hover over the properties' types to see example values.

CITs are split into 2 parts: Conditions which determine how items are selected and 
Effects that change something about the item. The different types usually 
only add effects.

While CIT Resewn only provides the 4 types, other mods may add their own,
allowing resourcepack authors to customize their mods using CITs.

## Effects

| Key | Value Type | Description | Default |
| --- | --- | --- | --- |
| `type` | [**Literal**]{Either: &#10 item / armor / elytra / enchantment|right} | Which type to apply on this CIT. | `item` |

## Conditions

| Key | Value Type | Description | Default |
| --- | --- | --- | --- |
| [**`items`**]{Aliases: &#10 matchItems|right} | [**List of item ids. Namespaces(`minecraft:`) are optional.**]{Examples: &#10 apple &#10 minecraft:stick &#10 iron_ingot gold_ingot emerald &#10 diamond_hoe|right} | A collection of possible item matches for this cit. | Empty list |
| `stackSize` | [**Positive integer or a <br> Positive integer range**]{Examples: &#10 15 (only 15) &#10 1-3 (between 1 and 3) &#10 5- (5 or more) &#10 -8 (up to 8)|right} | Requires that the item matches a specific amount. <br>Supports open ended ranges. | Any |
| WIP | WIP | THIS TABLE IS MISSING PROPERTIES | WIP |