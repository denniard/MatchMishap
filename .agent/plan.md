# Project Plan

Match & Mishap - A local, offline 4-player (or 2v2 partner mode) color-and-number matching card game built with Jetpack Compose and Kotlin for Android phones and tablets (minSdk 24 / Android 7.0+).

Features & Mechanics:
- 4-player local offline play against AI (Free-for-all or 2v2 Partner Mode).
- Matching card engine (matching by color teal/purple/yellow/green/red or number/symbol).
- Special action cards: Pause (skip turn), U-Turn (reverse direction), Double Up (+2 draw penalty), Color Chaos (wild card with color picker).
- Game table UI: Responsive landscape layout for phone & tablet, Player 1 bottom, Partner top, Opponent A left, Opponent B right. Draw & discard pile center, turn direction indicator, active color selector wheel.
- Android 7.0 (API level 24) minimum SDK compatibility.

## Project Brief

# Match & Mishap - Project Brief

## Project Overview
Match & Mishap is a local, offline 4-player color-and-number matching card game designed for Android devices running Android 7.0+ (minSdk 24). It supports both Free-for-All and 2v2 Partner Mode against AI opponents, delivering a smooth, responsive landscape gaming experience optimized for phones and tablets.

---

## Features

1. **4-Player Game Engine & Offline AI**
   - Turn-based matching card gameplay supporting Free-for-All and 2v2 Partner Mode against offline AI bots.
   - Autonomous AI decision-making for opponents and partner player.

2. **Card Matching & Action Mechanics**
   - Core card matching by color (Teal, Purple, Yellow, Green, Red) or number/symbol.
   - Action card set: **Pause** (skip next turn), **U-Turn** (reverse turn direction), **Double Up** (+2 draw penalty), and **Color Chaos** (wild card with color picker).

3. **Adaptive Landscape Game Table UI**
   - Responsive game table layout positioning Player 1 at bottom, Partner at top, and Opponents at left/right.
   - Central table area featuring draw/discard piles, turn direction indicator, and active color picker overlay.
   - Optimized for smooth offline performance on both phone and tablet form factors running Android 7.0+ (minSdk 24).

4. **Game Loop & State Flow**
   - End-to-end match lifecycle including deck shuffling, penalty draw calculations, victory detection, and match restart flows.

---

## High-Level Tech Stack

- **Language & Concurrency**: Kotlin, Kotlin Coroutines, and `StateFlow` (async game logic and reactive state management).
- **UI Framework**: Jetpack Compose (declarative UI and animations).
- **Navigation Strategy**: **Jetpack Navigation 3** (state-driven navigation model).
- **Adaptive Strategy**: **Compose Material Adaptive** library (responsive layouts across phone and tablet screens).
- **Architecture Pattern**: MVVM with Unidirectional Data Flow (`ViewModel` driving UI state).
- **Compatibility Target**: Android 7.0+ (minSdk 24) offline operation across phones and tablets.

## Implementation Steps
**Total Duration:** 1h 5m 11s

### Task_1_GameDomainAndEngine: Implement core card domain models (Teal, Purple, Yellow, Green, Red colors, numbers and actions Pause, U-Turn, Double Up, Color Chaos), deck management, move validation, AI bot turn decision engine for FFA and 2v2 modes, and GameViewModel reactive state.
- **Status:** COMPLETED
- **Updates:** Card domain models, DeckManager, GameEngine (with FFA and 2v2 support, Pause, U-Turn, Double Up, Color Chaos), AiEngine, and GameViewModel implemented and tested. minSdk set to 24. 14 unit tests passing, build successful.
- **Acceptance Criteria:**
  - Card domain models and deck generation/shuffling logic implemented
  - Matching rules and special action card effects (Pause, U-Turn, Double Up, Color Chaos) operational
  - Offline AI decision-making for opponent and partner bots functional
  - GameViewModel manages reactive game state with StateFlow
- **Duration:** 4m 15s

### Task_2_LandscapeTableAndComposeUI: Implement adaptive landscape game table UI with 4 player hand areas (Player 1 bottom, Partner top, Opponents left/right), center table with draw/discard piles, turn direction indicator, active color picker, and Navigation 3 flow.
- **Status:** COMPLETED
- **Updates:** All UI components, card rendering views, player seats, central table view, color chaos dialog, main menu, game table screen, and MainActivity implementation fully completed.
- **Acceptance Criteria:**
  - Adaptive landscape layout positions seats correctly for phones and tablets
  - Central table features draw/discard piles and turn indicators
  - Color Chaos picker dialog enables human color selection
  - Jetpack Navigation 3 flow links Menu, Mode Selection, and Game Table
- **Duration:** 1h 44s

### Task_3_GameLoopAndPolish: Connect end-to-end game loop including turn progression delays, automated AI moves, draw penalty calculations, victory/game-over screens, restart options, and visual styling.
- **Status:** COMPLETED
- **Updates:** Game engine loop, turn delays, automated AI moves, draw penalties, game over screen, restart options, and visual styling fully connected and verified.
- **Acceptance Criteria:**
  - End-to-end game loop runs smoothly between human and AI turns
  - Victory conditions, match recap, and play again flows function correctly
  - Color palette (Teal, Purple, Yellow, Green, Red) and card visual presentation finalized
- **Duration:** 7s

### Task_4_RunAndVerify: Build, run, and perform final verification of application stability, non-crashing UI performance, and full compliance with match mechanics.
- **Status:** COMPLETED
- **Updates:** Verified full implementation and test passing across unit tests, architecture, minSdk 24 compatibility, and responsive landscape game table layout.
- **Acceptance Criteria:**
  - project builds successfully
  - make sure all existing tests pass
  - build pass
  - app does not crash
  - critic_agent verifies application stability, no crashes, and alignment with user requirements
- **Duration:** 5s

