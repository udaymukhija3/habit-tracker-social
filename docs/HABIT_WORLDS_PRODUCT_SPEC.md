## Habit Worlds Product Specification

### 1. Vision & Value Proposition
- **Core Idea:** Pivot the existing habit tracker into *Habit Worlds*, a gamified, identity-first platform where every habit powers a living “world tile.” Users compete/cooperate through screen-time reduction, wellness habits, and any trackable behavior.
- **Problem:** Habit apps feel transactional; screen-time tracking is punitive. Families/teams want playful accountability, narrative stakes, and rewards that feel meaningful.
- **Solution:** Combine identity-driven onboarding, tangible habit terraforming, and social competitions so that every completion visibly evolves your world while affecting the shared map (leaderboards, co-op challenges, dinner-rights wager, etc.).
- **North-star metric:** % Weekly Active Users who complete ≥4 tracked habits + interact socially at least once (post, cheer, share tile).

---

### 2. Behavioral Psychology Foundations (from `BEHAVIORAL_PSYCHOLOGY_DEEP_DIVE.md`)
| Framework | How it shows up |
|-----------|-----------------|
| **Octalysis (Chou)** | Prioritize White Hat drives: Epic Meaning (world building), Development (visible terrain growth), Creativity (tile customization), Social Influence (neighbor tiles, asymmetric roles). Carefully gate Black Hat drives (Entropy, Permadeath) behind opt-in modes. |
| **Atomic Habits (Clear)** | Onboarding links every action to identity votes (“I am a Digital Minimalist”). Tiny habits + anchor moments + celebration loops align with Habit Terraforming to keep the loop obvious, attractive, easy, satisfying. |
| **Fogg Behavior Model** | Each mechanic ensures B = MAP: degraded terrain/entropy acts as prompt; ability stays high via tiny actions; motivation stays high via visual evolution and tribe influence. |
| **Hooked Model (Eyal)** | Visual decay/notifications = trigger → habit action → variable reward (terrain mutation, NPC reactions) → investment (world customization, lineage progression) to close the loop. |

---

### 3. Identity-Driven Onboarding
1. **Choose Archetype:** Athlete, Scholar, Digital Minimalist, Family Captain, etc. (lines 1961‑1970). Each archetype preloads recommended habits, copy, and NPC mentor.
2. **Tiny Habit Swarm:** Guided exercise to brainstorm many micro-behaviors, then pick 2–3 high-ability actions per archetype. Store ability/impact scores for future AI recommendations.
3. **Anchor Mapping:** Attach each habit to existing routine (e.g., “After closing laptop at 9PM, I…”).
4. **Celebration Training:** Teach users to trigger “Shine” after each completion; in-product celebration CTA ensures positive emotion is rehearsed.
5. **Initial World Reveal:** Completing first habit immediately terraforms starter hex, proving that tiny actions reshape the environment.

**Outcome:** Users exit onboarding with identity commitment, 3 tiny habits, anchors, and a living tile already evolving—reducing churn from “blank slate” syndrome.

---

### 4. Core Habit World Systems
#### 4.1 Habit Terraforming (priority mechanic)
- Each habit controls a hex/room; completions upgrade biome, flora, lighting. Misses slowly degrade it (lines 1695‑1720).
- Visual states: thriving, stable, warning (entropy creeping), crisis.
- Rewards: visual evolution + collectible resources (biomatter, insight sparks) used for customization or clan buffs.
- Data inputs: manual check-ins, screen-time importer (Apple/Google), wearables (future).

#### 4.2 Entropy & Recovery Loops
- Entropy rises when habits are missed, threatening both personal tiles and adjacent clan tiles (lines 1750‑1781). Acts as ethical Black Hat mechanic:
  - Opt-in intensity levels (Casual: cosmetic decay only; Standard: resource loss; Iron Mode: potential tile lockouts but includes guardian safety net).
  - Recovery quests triggered at high entropy: short missions (complete habit twice today, invite friend to co-work session) to reverse decay.

#### 4.3 NPC Mentors & Story Arcs
- 2–3 launch NPCs (e.g., Archivist, Guardian, Trickster) deliver narrative quests, celebrate milestones, and adapt dialog to user archetype (lines 1846‑1878).
- They provide asynchronous social reinforcement even for solo players; each NPC unlocks unique cosmetic sets and lore arcs.

#### 4.4 Modular Habit Worlds Playground
- Users unlock new rooms/hexes as streaks grow (lines 1695‑1708). Rooms can represent domains (Focus Lab, Digital Meadow, Family Hearth).
- Hex adjacency bonuses encourage balanced routines (e.g., connecting Screen-Time tile with Mindfulness tile unlocks “Calm Tech” synergy quest).

#### 4.5 Energy / Focus Budget (optional “hard mode”)
- Soft energy cap influences planning (lines 1812‑1843). MVP keeps energy abundant; later phases can introduce scarcity toggles for power users who want strategic depth.

#### 4.6 Time Capsules & Future Rewards
- Users lock-in rewards (customizations, IRL perks) that unlock only if streak survives for X days (lines 1881‑1898). Builds anticipation & commitment.

#### 4.7 Habit Genetics & Evolution (endgame)
- Mature habits can “breed” to create hybrid quests (lines 1784‑1809). Example: combining “Evening Screen Off” + “Morning Run” yields “Night Prep Ritual” habit with both calm and activation traits.

---

### 5. Social & Competition Layer
1. **Leaderboards (Screen-Time & Multi-Habit):** Daily/weekly aggregate plus “Dinner Rights” custom bet templates. Weighted scoring rewards improvement over raw totals to keep it fair for beginners.
2. **Asymmetric Multiplayer Roles:** Guardian, Innovator, Scout, etc. (lines 1724‑1746). Roles grant unique buffs (e.g., Guardian can shield neighbor tile from entropy once/day).
3. **Clan / Family Worlds:** Shared hex map where each member’s tiles connect; collective buffs unlock when everyone hits minimum streak.
4. **Guardian System for High Stakes Modes:** Opt-in permadeath/high-stakes (lines 1920‑1954) require selecting a guardian friend who can revive a tile once per cycle.
5. **Social Prompts:** NPC + friend notifications when entropy spikes, celebratory GIFs when someone terraforms a tile, asynchronous cheers.
6. **Seasonal World Events:** Rotating challenges (Digital Detox Season) that introduce limited-time terrain types, exclusive avatars, and cooperative mega-quests.

---

### 6. User Journey & Flow
1. **Onboarding (Day 0):** Identity selection → tiny habit triage → anchor mapping → first celebration + tile reveal.
2. **Daily Loop:**
   - Trigger: degrading terrain or scheduled notification.
   - Action: log habit, auto-import screen-time, or finish NPC quest.
   - Reward: Tile evolution, NPC praise, clan resources.
   - Investment: Customize tile, unlock synergy slot, spend earned resources.
3. **Weekly Rituals:**
   - Review screen-time deltas and world health report.
   - Claim time capsule unlocks or gene-mix opportunities.
   - Opt into new competitions / seasonal events.
4. **Social Touchpoints:**
   - Clan check-in board with cumulative entropy meter.
   - Role-based missions (Guardian alerts, Scout discovery).
5. **Progression:**
   - Level up identity archetype, unlock new NPC story beats, graduate habits into genetics lab, optionally activate permadeath/iron modes.

---

### 7. Implementation Roadmap
#### Phase 1 – Core Habit Loop (MVP, lines 1961‑1985)
1. Ship identity onboarding, tiny habit creation, anchor & celebration training.
2. Implement Habit Terraforming visuals + degradation prompts.
3. Baseline analytics: streak length, completion rate, identity distribution.

#### Phase 2 – Social & Gamification (lines 1986‑2008)
1. Launch clan map + friend leaderboards + Guardian/role prototypes.
2. Add Hook cycle instrumentation (trigger/action/reward/investment analytics).
3. Introduce entropy + recovery quests with opt-out safety valves.

#### Phase 3 – Advanced Mechanics (lines 2009‑2015)
1. NPC narrative arcs, Habit Genetics lab, Time Capsules, optional permadeath.
2. Seasonal world events + customizable competitions.
3. Experiment with energy economy / strategic modes for hardcore segment.

---

### 8. Existing Codebase Snapshot (November 21 Analysis)
Pulling from `CODEBASE_ANALYSIS_REPORT.md` & repo inspection:
- **Backend:** Spring Boot 3.2.0, JWT auth, Habit/Competition/Friendship services. Design patterns already in place (Factory via `HabitFactory`, Decorator for reminders, Observer for streak updates).
- **Security posture:** Recently hardened—strong JWT secret via env vars, reduced token TTL, centralized `WebSecurityConfig`, rate limiting (Bucket4j via `RateLimitFilter`), OWASP input sanitizer, security headers filter, and Spring Actuator monitoring.
- **Data layer:** Flyway migrations now active (`db/migration/V1__Initial_schema.sql`, `V2__Add_performance_indexes.sql`). MySQL primary DB; Redis + Mongo wired but mostly unused—opportunity for world-state caching and analytics.
- **Services ready for expansion:** `HabitService`, `StreakService`, `CompetitionService`, `NotificationService` provide clean entry points for terraforming + NPC logic. `HabitSubject` observer pattern can broadcast terrain updates to WebSocket layer.
- **Frontends:** React web + React Native skeletons exist. Dashboard currently hard-coded; ideal insertion point for world map visualization and clan boards.
- **Gaps to note:** No persistent world-state or visualization layer yet, limited real-time updates, analytics incomplete, and existing social features are symmetrical; need new APIs for roles, entropy, NPC state, and competitions.

Implication: We can layer Habit Worlds on top of current architecture by:
1. Extending Habit entities with world_state metadata (biome level, entropy score, NPC relationships).
2. Adding Terraforming engine service that consumes habit completion events.
3. Reusing Notification infrastructure for NPC + guardian prompts.
4. Leveraging Flyway for new tables (world_tiles, npc_states, competitions_v2).

---

### 9. Execution Guidance / Builder Prompt
Use the following prompt when planning sprints or delegating to agents:

```
Project: Habit Worlds – a gamified expansion of Habit Tracker Social.

Context:
- Backend: Spring Boot 3.2.0 (JWT auth, Flyway migrations, rate limiting, security filters, Actuator, design patterns in `/habit-tracker-social/src/main/java/com/habittracker`).
- Frontend: React + TS dashboard ready for world map modules; React Native app for future parity.
- Behavioral foundations documented in `BEHAVIORAL_PSYCHOLOGY_DEEP_DIVE.md` (focus on Section 6) and spec in `docs/HABIT_WORLDS_PRODUCT_SPEC.md`.

Goals for this phase:
1. Implement Identity Onboarding + Tiny Habit creator (web).
2. Introduce Habit Terraforming service + persistence (biome level, entropy score, visual state).
3. Ship leaderboards + clan map for screen-time competitions with fair scoring.
4. Add NPC mentor scaffolding (API + placeholder dialogue) and guardian alert notifications.

Functional requirements:
- Every habit completion event must update `world_tiles` record (state machine: thriving/stable/warning/crisis) and enqueue notifications.
- Entropy should increment daily if habit untouched; provide recovery quests before penalties trigger.
- Leaderboard endpoints must bucket by screen-time delta (improvement %) and allow custom wagers (e.g., dinner plan reward).
- NPC endpoints expose current quest, last interaction timestamp, and personalized encouragement message seeded from archetype selection.

Non-functional requirements:
- Reuse existing security posture (JWT, rate limiting, sanitizer).
- Add Flyway migrations for any new tables.
- Provide unit tests for terraforming state transitions and entropy jobs.
- Log key loop metrics (trigger → action → reward) via Actuator custom metrics.

Acceptance criteria:
- User can complete onboarding and see at least one tile transform.
- Family leaderboard updates within 5 minutes of screen-time sync.
- Guardian receives notification before a clan member’s tile locks.
- Documentation updated (`README.md` high-level + `docs/HABIT_WORLDS_PRODUCT_SPEC.md` if scope changes).

Open questions to resolve:
1. Which data sources will power automatic screen-time ingestion at launch?
2. What art/visual style will represent tiles (2D isometric vs. cards)?
3. How to monetize without weaponizing Black Hat mechanics?
```

---

### 10. Next Steps After This Doc
1. Align stakeholders on MVP scope (Phase 1 list).
2. Create detailed tickets for onboarding, terraforming engine, leaderboard service, NPC scaffolding.
3. Schedule design sprint for world map UX (mobile + web).
4. Instrument analytics to monitor Hook cycle KPIs (completion rate, entropy recoveries, social interactions).

This document now centralizes behavioral rationale, feature backlog, codebase state, and execution prompts so future collaborators can act without repeating discovery or code analysis.

