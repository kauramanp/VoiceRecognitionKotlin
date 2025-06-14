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
1. AudioVisualizerView
A colorful, real-time audio visualizer using bars with dynamic height and gradients.

Key Features:
<li>Scales amplitude bars based on max recorded amplitude

<li>Ignores low/noise threshold to reduce clutter

<li>Draws bars with LinearGradient color

<li>Adjustable density and gap between bars
 
