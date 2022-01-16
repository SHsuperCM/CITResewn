# Global Properties
<h6>Note that features marked in <em>italics</em> are unique to CIT Resewn.</h6>

Commonly known as `cit.properties`, Global Properties is not a cit but rather a config that is set 
for the entire resourcepack.

## Location

The pack's global properties can be either (ordered by priority. if one exists, the rest are ignored):

`assets/minecraft/citresewn/cit.properties`  
`assets/minecraft/optifine/cit.properties`  
`assets/minecraft/mcpatcher/cit.properties`

## Properties

All properties (and `cit.properties` itself) are optional.  

| Key | Value Type | Description | Default |
| --- | --- | --- | --- |
| `useGlint` | [**Boolean**]{Either: &#10 true / false|right} | If false, removes the default glint texture from every item. | `true` |
| `cap` | Any positive whole number | Controls the maximum allowed layers of CIT Enchantments per item. | None |
| `method` | [**Literal**]{Either: &#10 average / layered / cycle|right} | Determines how different CIT Enchantments mix:<br>`average`:<br>`layered`:<br>`cycle`: | `average` |
| `fade` | Any non-negative number | Amount of time in seconds to fade between CIT Enchantments when method is set to `cycle`. | `0.5` |
