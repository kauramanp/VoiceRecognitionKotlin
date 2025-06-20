# 🎙️ Voice Recording & WAV Upload Module (Android)
This module provides a complete voice recording experience using a Fragment and custom views in Android. Users can record voice, visualize the waveform in real time, play back the recording, and upload it as a .wav file.

# 🚀 Features
<li>🎧 Record voice with system microphone
<li>📈 Real-time audio waveform visualizations
<li>🖼️ Two custom visualizer views:
<li>AudioVisualizerView (gradient bars)
<li>VisualizerView (minimal stroke lines)
<li>▶️ Playback of recorded audio
<li>☁️ Upload audio as .wav file
<li>📦 Lightweight and modular, ready for reuse

# 🧱 Architecture
<li>Written in Kotlin

<li>Built around a Fragment-based UI

<li>Uses MediaRecorder or AudioRecord API (depending on your implementation)
<li>View drawing powered by custom View subclasses

# 🖼️ Visualizers
1. AudioVisualizerView<br>
A colorful, real-time audio visualizer using bars with dynamic height and gradients.

<u>Key Features:</u>
<li>Scales amplitude bars based on max recorded amplitude
<li>Ignores low/noise threshold to reduce clutter
<li>Draws bars with LinearGradient color
<li>Adjustable density and gap between bars<br>

<br>2. VisualizerView<br>
A minimal visualizer with green stroke lines. Designed for lightweight performance and classic audio line representation.

Key Features:
<li>Calculates pixel size from device DPI

<li>Fast rendering with simple vertical lines

<li>Great for low-performance environments

# 📂 File Upload (WAV)
After recording, the module encodes the audio into .wav format and uploads it via your backend API or storage logic.

<u>Sample Flow:</u>
<li>Start recording
<li>Visualize waveform in real time
<li>Stop recording
<li>Save as .wav
<li>Upload to server or Firebase

# 🔧 Customization
You can customize the following:
| Parameter         | Class                 | Description                                |
| ----------------- | --------------------- | ------------------------------------------ |
| `linePaint.color` | Both views            | Color of bars/lines                        |
| `gap`             | `AudioVisualizerView` | Gap between visual bars                    |
| `density`         | `AudioVisualizerView` | Number of bars visible                     |
| `stroke`          | Both views            | Thickness of visual lines                  |
| `minThreshold`    | `AudioVisualizerView` | Minimum amplitude to start drawing a bar   |
| `MAX_AMPLITUDE`   | Both views            | Max amplitude constant for scaling visuals |


 
