# Retro Flip Clock (Web / React)

A productivity clock web application rewritten from Android Jetpack Compose into React + TypeScript + Tailwind CSS with high-performance 3D split-flap mechanical aesthetics.

## Features

- **Desk Clock Mode**:
  - Realistic 3D split-flap mechanical flip cards with gravity acceleration, tactile impact flex bounce, center seam dividers, and left/right mechanical hinge brackets.
  - Edge-to-edge numbers with selectable vintage typefaces (`Bebas Neue`, `Anton`, `Roboto Mono`).
  - Synthesized mechanical click and alarm chime sounds via Web Audio API with zero external asset dependencies.
  - Live hours, minutes, seconds, AM/PM, weekday and date indicators.
  - Real-time battery status and local weather temperature display.
  - Responsive layout: Stacked in portrait, side-by-side in landscape.
  - Floating dock with Ambient Dim Mode, 24H/12H quick toggle, sleep timer countdown, and settings modal.
  - Screen WakeLock integration to keep display awake during desk ambient mode.

- **World Clock**:
  - Home city live mini flip clock.
  - Saved international cities with local time, weather status, relative day indicator (Today/Tomorrow/Yesterday), and time zone difference offset.
  - Global cities search and selection modal sheet.

- **Stopwatch**:
  - High-precision 60 FPS millisecond counter (MM:SS.hundredths).
  - Split lap tracking with fastest lap highlighted in green and slowest in red.
  - Adaptive dual-column layout in landscape.

- **Focus Timer**:
  - Activity tag selector (Deep Work, Study, Coding, Reading, Writing, Meditation, or custom tags).
  - Pomodoro and quick presets (1m, 5m, 15m, 25m, 45m, 60m) as well as custom time picker.
  - Completed focus sessions logged to Focus History with total focus minutes summary.

- **Alarms**:
  - Multiple alarms with repeat rules (Once, Daily, or Custom day pills).
  - Configurable sound ringtones, ascending volume, vibration, and auto-delete.
  - Flip phone face-down to snooze gesture support (via device orientation).
  - In-app interactive ringing alert dialog with snooze and dismiss actions.

## Tech Stack

- React 19
- TypeScript
- Vite
- Tailwind CSS
- Web Audio API (in-memory audio synthesis)
