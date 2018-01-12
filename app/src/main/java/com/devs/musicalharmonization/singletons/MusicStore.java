package com.devs.musicalharmonization.singletons;

import com.devs.musicalharmonization.Note;

import java.util.ArrayList;

/**
 *  2/14/2016.
 */
public class MusicStore {
    public static ArrayList<ArrayList<Note>> sheet = new ArrayList();
    public static ArrayList<Note> activeNotes = new ArrayList<Note>();
}
