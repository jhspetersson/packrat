# Changelog

## [Unreleased]

### Changed

- Enable parallel stateless, text, and min/max gatherers.
- Relax generic bounds for comparators, `equalChunks`, and `flatMapIf`.
- Reduce allocations in buffering, counting, and unique-element gatherers.
- Release oversized chunk buffers while reusing smaller buffers.
- Grow fixed-window buffers lazily.
- Stream single-element unique drops lazily.
- Document null handling and parallel `limit()` and `zip` workarounds.

### Removed

- Public implementation classes. Use `Packrat` factories instead.

### Fixed

- Fix `flatMapIf`/`zip` cancellation and close zipped streams.
- Terminate empty-result operations and empty-stream repetition promptly.
- Correct null handling and reject null ordering keys.
- Fix counter and ring-buffer index overflows.
- Match indexed window results to JDK gatherers.

## [0.2.3] - 2026-04-12

### Added

- `even()` and `odd()` index filters.
- `dropLastUnique`, `dropLastUniqueBy`, and `dropLastBy`.

### Changed

- Require Java 25 or later.

### Removed

- `sample(n, maxSpan)`. Use `sample(n)` instead.

### Fixed

- Preserve encounter order in `atLeast` and `atMost`.
- Correct reservoir sampling and parallel sample sizes.
- Prevent `dropLast` element loss and preserve `lastUnique` recency.
- Isolate mutable state when reusing gatherers.

## [0.2.2] - 2026-03-08

### Added

- `lastUniqueBy` for mapped-key selection.

### Changed

- Extend JSpecify nullness annotations to gatherer implementations.

## [0.2.1] - 2025-11-16

### Added

- Ordering validators with mapped-value and custom-exception variants.

## [0.2.0] - 2025-10-31

### Added

- `repeat` and `identity` gatherers.
- `filterEntries` and `removeEntries`.
- Indexed fixed and sliding windows.
- `randomFilter` for probabilistic filtering.
- `equalChunksBy` and chunk equality comparators.
- JSpecify nullness annotations.

### Fixed

- Handle zero counts in `last`, `lastUnique`, and `dropLast`.

## [0.1.0] - 2025-04-27

### Added

- Initial collection of Java Stream gatherers.
