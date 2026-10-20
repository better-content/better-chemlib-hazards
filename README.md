# Better ChemLib Hazards

## Playtest diagnostics

`bc.playtest.better_chemlib_hazards.gas` distinguishes invalid/fractional/unmapped/empty
preflight rejection from insufficient-loaded-capacity deferral. Native write invocation
reports the boundary's accepted-mass result and explicitly leaves source debit to the
caller; it does not claim independent block observation or disk durability. The old
unbounded fixed-emission DEBUG is replaced by fixed scalar30-second `nuclear` aggregates.
Focused nuclear TRACE uses only existing emission values and the causal native position.

TRACE needs the logger at TRACE plus JVM properties
`bc.playtest.trace.better_chemlib_hazards.dimension=namespace:path` and
`bc.playtest.trace.better_chemlib_hazards.pos=x,y,z`. Absent/invalid focus has no detail and
one status; first matching detail arms60 seconds/1000 records once per process, subject
to the ordinary120/minute burst20 bucket. All categories share256 aggregation keys and
2048-character records; native ticks summarize30-second windows and server stop clears
aggregation. INFO startup uses exact own-artifact source metadata/version when present,
UNKNOWN otherwise, plus integration presence. Standalone stays native INFO; pack profile
selects scoped DEBUG/`bc.playtest.level`. No inventory/NBT/seed/payload data is logged.
The six-case common contract and exact16:1 projection tests run with the source gate.

## Scope and authority

This repository owns its mod-specific behavior and authoring inputs. Read [local instructions](AGENTS.md)
and the [shared documentation/policy index](../../better-content-modpack/docs/README.md).


Better ChemLib Hazards is a Forge `1.20.1` bridge for the parts of ChemLib matter that
need world consequences: gas escape, radioactive decay, neutron-driven fission,
and radioactive-form emission profiles.

The design goal is emergent behavior from numeric traits and curve intercepts,
not hard-coded per-element special cases. Pack authors can tune chemical
identity through datapack JSON while the mod derives fallback traits from
ChemLib registry data.

## Current Features

- A one-way atmospheric boundary that turns escaped ChemLib gases into native
  AdPother pollutant blocks.
- A conserved bridge ratio of 16 Latent mass per AdPother unit. Release is
  preflighted atomically and rejects an unrepresentable fractional source,
  while AdPother remains the sole authority for ambient
  density, movement, wind, spreading, impacts, protection, detection,
  explosions, filtering, chimney routing, and cleanup.
- Gas item escape handling for item entities and player inventories.
- A narrow `AirtightInventory` API for owning machines to suppress gas escape
  while their sealed state is active. Block-entity mutations are queued for the
  next budgeted escape scan, so newly inserted gases do not wait for chunk reload.
- Heavy element neutron flux simulation for ChemLib element stacks.
- Data-driven fixed radioactive-family profiles selected by exact item/block ID
  or item/block tag, with independent radiation and radiogenic heat strengths.
- Persistent disturbance sidecars which keep natural hosted uranium/thorium ore
  inert while making mined, carried, dropped, contained, and replaced forms active.
- A read-only `LatentEmissionProfiles` API for stack and placed-form consumers;
  fixed Realistic Ores profiles do not create or require isotope NBT.
- No blocks, items, fluid stores, chemical capabilities, or generic reaction
  machines. Owning mods remain authoritative for installing and persisting
  containment and for processing.
- File-based datapack reload support for:
  - `data/better_chemlib_hazards/chemical_traits/*.json`
  - `data/better_chemlib_hazards/scheduler_profiles/default.json`
- Server tick budgets for gas-escape and nuclear workloads. Gas scans rotate
  block and entity holders within the existing allowance.
- Unit tests for numeric curves and emergent simulation math.

## Tech Stack

- Minecraft `1.20.1`
- Forge `47.4.13`
- Java `17`
- ChemLib `2.0.19`
- Heat Sync (mandatory typed thermal API)
- AdPother (mandatory atmospheric authority)

## Development

Common tasks:

```bash
./gradlew verifyFast
./gradlew verifyFull
./gradlew runClient
./gradlew runServer
```

The JVM unit coverage gate is intentionally focused on pure isotope, decay,
fission, gas-boundary, and scheduling logic. Forge event handlers are thin
integration boundaries. `verifyFast` runs the JVM coverage gate; `verifyFull`
also builds the runtime JAR and runs the reviewed headless Forge GameTest profile. It exercises native
ChemLib block placement through Forge's event path, isotope-preserving native loot and
sidecar consumption, and chunk reconciliation that retains enriched blocks, removes
stale entries, and discovers untracked blocks without duplicating material records.
The chunk case posts the real Forge lifecycle event for a loaded fixture chunk and
roundtrips SavedData; it does not claim a disk unload/reload while fixture tickets remain.

`gametest/profiles/full.txt` lists the required runtime IDs. During the active task every invocation keeps an
isolated fixture under `build/gametest/<run-token>/` with world files, logs, and
`execution.json`. Preparation creates an absent `server.properties` containing comments
only, so native defaults are unchanged and initial settings loading does not fail with
`NoSuchFileException`. Existing properties are never overwritten; the Minecraft-free
evidence guard tests both absence and preservation. The gate requires that run's token, a finished report, and exactly the
expected discovered and successfully executed tests. Retain failed fixtures, worlds, reports,
builds and caches across handoffs by default under the
[shared retention policy](../../better-content-modpack/docs/policies/generated-data.md).
Finishing activity deletes nothing. `verifyFast` also exercises
the execution-evidence parser against missing, incomplete, stale, malformed, duplicate,
and failed results without starting additional servers.

## Pack Configuration

Pack-side datapack examples are expected under:

```text
data/better_chemlib_hazards/chemical_traits/
data/better_chemlib_hazards/nuclear_forms/
data/better_chemlib_hazards/nuclear_decay/
data/better_chemlib_hazards/nuclear_phenomena/
data/better_chemlib_hazards/scheduler_profiles/
```

Traits expose volatility, thermal, instability, absorption, and scattering
levers. Scheduler profiles cap per-dimension gas and nuclear work. Nuclear form,
decay, and phenomena files define radioactive identity and consequences without
creating another processing system.

## Notes

- Mod metadata is sourced from `gradle.properties`.
- The mod deliberately has no block/item registry. Pack-specific processing and
  progression live in the consuming pack and the owning technology mods.
- Atmospheric conversion uses 16 Latent mass per whole AdPother unit and hands
  accepted matter off atomically; after that boundary AdPother is authoritative.
- Nuclear heat is offered only through Heat Sync. Heat that Heat Sync cannot
  accept remains in the simulated material's energy state.

## Community and support

For modpack and mod discussion, playtest feedback, and bug reports, join the [Better Content Discord](https://discord.gg/EkRnZbzqS9).

## Identity

The canonical identity is repository/artifact `better-chemlib-hazards`, mod ID and resource namespace `better_chemlib_hazards`, and Maven group `com.bettercontent`. Latent has a mandatory loader and typed binary dependency on Heat Sync, which owns the pack's thermal transport API; other consumers may use Latent's read-only emission API.
