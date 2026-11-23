package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.R

enum class SoundCue {
    CORRECT,
    WRONG,
    COMPLETE,
}

class QuizSoundPlayer(context: Context) {
    private val appContext = context.applicationContext

    private val soundPool: SoundPool? = runCatching {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(attributes)
            .build()
    }.getOrNull()

    private var correctId: Int = 0
    private var wrongId: Int = 0
    private var completeId: Int = 0

    init {
        soundPool?.let { pool ->
            runCatching {
                correctId = pool.load(appContext, R.raw.sfx_correct, 1)
                wrongId = pool.load(appContext, R.raw.sfx_wrong, 1)
                completeId = pool.load(appContext, R.raw.sfx_complete, 1)
            }
        }
    }

    fun play(cue: SoundCue) {
        val pool = soundPool ?: return
        val soundId = when (cue) {
            SoundCue.CORRECT -> correctId
            SoundCue.WRONG -> wrongId
            SoundCue.COMPLETE -> completeId
        }
        if (soundId != 0) {
            runCatching {
                pool.play(soundId, 0.85f, 0.85f, 1, 0, 1.0f)
            }
        }
    }

    fun release() {
        runCatching { soundPool?.release() }
    }
}
