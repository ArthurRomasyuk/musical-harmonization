package com.devs.musicalharmonization.singletons;

import com.devs.musicalharmonization.Note;

import java.util.ArrayList;

/**
 *  2/6/2016.
 */

//todo avoid this singleton
public class Bar {
    public static double topNumber = 4;
    public static double bottomNumber = 4;
    public static ArrayList<Note> activeBarNotes = new ArrayList<Note>();

}
