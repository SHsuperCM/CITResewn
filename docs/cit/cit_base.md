# CIT Base
<h6>Note that features marked in <em>italics</em> are unique to CIT Resewn.</h6>

All CIT types, while having different effects, originate from CIT Base. 
Think of it as a parent for all other types and every type inherits its properties, 
sometimes overriding their behavior.

When a property is not specified, its default value is used instead, meaning all 
properties are optional by default. Some property keys have aliases, these were 
added for legacy support. Hover over keys to see if they have aliases, try not to 
use them when possible.

CITs are split into 2 parts: Conditions which determine how items are selected and 
Effects that change something cosmetic about the item. The different types usually 
only add different effects.

While CIT Resewn only provides the 4 types, other mods may add their own,
allowing resourcepack authors to customize their mods using CITs.

## Effects

| Key | Type | Description | Default |
| --- | --- | --- | --- |
| `type` | Literal, either: <br>[`item`](/cit/cit_item) / [`armor`](/cit/cit_armor) / [`elytra`](/cit/cit_elytra) / [`enchantment`](/cit/cit_enchantment) | Which type to apply on this CIT. | [`item`](/cit/cit_item) |

## Conditions

| Key | Type | Description | Default |
| --- | --- | --- | --- |
| [**`items`**]{Aliases: &#10matchItems|right} | List of item ids. Namespaces(`minecraft:`) are optional. | A collection of possible item matches for this cit. | Empty list |