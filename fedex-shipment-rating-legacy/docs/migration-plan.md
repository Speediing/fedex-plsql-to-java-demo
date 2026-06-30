# Migration plan

## Phase 1: Characterize (done in repo)

- [x] Document scenarios in `scenarios/`
- [x] Capture expected outputs in `docs/expected-ratings.csv`
- [x] Add disabled `CharacterizationTest` in modern service

## Phase 2: Extract domain

- [x] Glossary (`domain-glossary.md`)
- [x] Rule catalog with traceability
- [x] Decision tables
- [ ] Workshop with stakeholders on ambiguous rules (demo talking point)

## Phase 3: Rebuild in Java (live demo)

- [x] Domain types and `ReferenceData` scaffolded
- [ ] Implement `RatingEngine` with policies
- [ ] Remove `@Disabled` from characterization tests

## Phase 4: Verify parity

- [ ] All 9 scenarios pass in `CharacterizationTest`
- [ ] Optional: run same inputs through Oracle Docker and diff

## Phase 5: Strangler rollout

- [x] Feature flag `migration.feature-flags.java-enabled-accounts` wired
- [ ] Route flagged accounts to Java after parity proven
- [ ] Retire PL/SQL package incrementally

## What we deliberately did NOT do

- Line-by-line PL/SQL to Java translation
- Preserve `g_last_zone` shared package state
- Keep audit inserts inside the rating calculation
