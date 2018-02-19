package com.devs.musicalharmonization.singletons

import com.devs.musicalharmonization.Note

/**
 *  2/20/2016.
 */
object Key {

    //todo avoid this singleton
    var COUNT = 0
    var SHARP = true
    var startNote: Note.NoteName = Note.NoteName.c
}
