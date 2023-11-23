package com.codingub.locationtracking.utils

import android.media.AudioAttributes
import android.media.SoundPool

object SoundManager {

    private val soundPool: SoundPool

    private const val MAX_STREAMS: Int = 5
    private const val VOLUME: Float = 1f

    const val sound_click: String = "Click"
    const val sound_ultra: String = "Ultra"
    private val sounds: MutableMap<String, Int> = mutableMapOf()

    var isMuted: Boolean = false

    init{
        val audioAttr = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setAudioAttributes(audioAttr)
            .setMaxStreams(MAX_STREAMS)
            .build()

        sounds.apply {
            put(sound_click, loadSound(sound_click))
        }
    }

    private fun loadSound(name: String): Int = soundPool.load(AssetUtil.soundAfd(name),1)

    fun playSound(soundKey: String){
 //       if (!UserConfig.isSoundsEnabled || isMuted) return
        val soundId = sounds[soundKey]
        soundId?.let { soundPool.play(it, VOLUME, VOLUME, 1, 0, 1f) }
    }
}