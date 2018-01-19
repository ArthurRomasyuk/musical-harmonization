package com.devs.musicalharmonization.activity

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatDrawableManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import com.devs.musicalharmonization.*
import com.devs.musicalharmonization.midi.MidiFile
import com.devs.musicalharmonization.midi.MidiTrack
import com.devs.musicalharmonization.midi.events.Tempo
import com.devs.musicalharmonization.midi.events.TimeSignature
import com.devs.musicalharmonization.midi.unused.ProgramChange
import com.devs.musicalharmonization.singletons.LastRhythm
import com.devs.musicalharmonization.singletons.MusicStore
import com.github.clans.fab.FloatingActionMenu
import java.io.File
import java.io.IOException
import java.util.*

class Composition : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    internal var REGULAR_COLOR: Int = Color.RED
    internal val SELECTED_COLOR: Int = Color.rgb(229, 115, 115)


    private fun nameToNum(note: Note): Int {
        var noteNum = -1
        if (note.name == Note.NoteName.c) {
            noteNum = 12 * note.octave
        } else if (note.name == Note.NoteName.cs) {
            noteNum = 12 * note.octave + 1
        } else if (note.name == Note.NoteName.d) {
            noteNum = 12 * note.octave + 2
        } else if (note.name == Note.NoteName.ds) {
            noteNum = 12 * note.octave + 3
        } else if (note.name == Note.NoteName.e) {
            noteNum = 12 * note.octave + 4
        } else if (note.name == Note.NoteName.f) {
            noteNum = 12 * note.octave + 5
        } else if (note.name == Note.NoteName.fs) {
            noteNum = 12 * note.octave + 6
        } else if (note.name == Note.NoteName.g) {
            noteNum = 12 * note.octave + 7
        } else if (note.name == Note.NoteName.gs) {
            noteNum = 12 * note.octave + 8
        } else if (note.name == Note.NoteName.a) {
            noteNum = 12 * note.octave + 9
        } else if (note.name == Note.NoteName.`as`) {
            noteNum = 12 * note.octave + 10
        } else if (note.name == Note.NoteName.b) {
            noteNum = 12 * note.octave + 11
        }
        return noteNum
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_composition)

        //PERMISSIONS:

        val hasPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_WRITE_STORAGE)
        }


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        //MIDI Composition


        // System.gc()
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val o = prepareMelody()
            val mp = MediaPlayer()
            try {
                mp.setDataSource(o.path)
            } catch (e: IOException) {
                System.err.println("Couldn't init media player")
            }

            mp.setOnPreparedListener { mp ->
                mp.start()
                Log.i("MP", "Playing MIDI")
            }
            mp.setOnCompletionListener { mp -> mp.release() }

            try {
                mp.prepare()
            } catch (e: Exception) {
                Log.e("MP", "Error with media player prepare")
            }
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val rhythmMenu: FloatingActionMenu = findViewById<FloatingActionMenu>(R.id.composition_rhythm_menu)
        REGULAR_COLOR = rhythmMenu.menuButtonColorNormal
        //Loading fabs
        val fab_sixteenth: com.github.clans.fab.FloatingActionButton = findViewById<com.github.clans.fab.FloatingActionButton>(R.id.composition_sixteenth_note)
        fab_sixteenth.setImageDrawable(
                AppCompatDrawableManager.get().getDrawable(this, R.drawable.music_note_sixteenth)
        )
        fab_sixteenth.setOnClickListener {
            LastRhythm.value = .25
            rhythmMenu.close(true)
        }

        val fab_eighth: com.github.clans.fab.FloatingActionButton = findViewById<com.github.clans.fab.FloatingActionButton>(R.id.composition_eighth_note)
        fab_eighth.setImageDrawable(
                AppCompatDrawableManager.get().getDrawable(this, R.drawable.music_note_eighth)
        )
        fab_eighth.setOnClickListener {
            LastRhythm.value = .5
            rhythmMenu.close(true)
        }
        val fab_quarter: com.github.clans.fab.FloatingActionButton = findViewById<com.github.clans.fab.FloatingActionButton>(R.id.composition_quarter_note)
        fab_quarter.setImageDrawable(
                AppCompatDrawableManager.get().getDrawable(this, R.drawable.music_note_quarter)
        )
        fab_quarter.setOnClickListener {
            LastRhythm.value = 1.0
            rhythmMenu.close(true)
        }
        val fab_half: com.github.clans.fab.FloatingActionButton = findViewById<com.github.clans.fab.FloatingActionButton>(R.id.composition_half_note)
        fab_half.setImageDrawable(
                AppCompatDrawableManager.get().getDrawable(this, R.drawable.music_note_half)
        )
        fab_half.setOnClickListener {
            LastRhythm.value = 2.0
            rhythmMenu.close(true)
        }
        val fab_whole: com.github.clans.fab.FloatingActionButton = findViewById<com.github.clans.fab.FloatingActionButton>(R.id.composition_whole_note)
        fab_whole.setImageDrawable(
                AppCompatDrawableManager.get().getDrawable(this, R.drawable.music_note_whole)
        )
        fab_whole.setOnClickListener {
            LastRhythm.value = 4.0
            rhythmMenu.close(true)
        }


        var set: AnimatorSet = AnimatorSet()

        var scaleOutX: ObjectAnimator = ObjectAnimator.ofFloat(rhythmMenu.menuIconView, "scaleX", 1.0f, 0.2f)
        var scaleOutY: ObjectAnimator = ObjectAnimator.ofFloat(rhythmMenu.menuIconView, "scaleY", 1.0f, 0.2f)
        var scaleInX: ObjectAnimator = ObjectAnimator.ofFloat(rhythmMenu.menuIconView, "scaleX", 0.2f, 1.0f)
        var scaleInY: ObjectAnimator = ObjectAnimator.ofFloat(rhythmMenu.menuIconView, "scaleY", 0.2f, 1.0f)

        scaleOutX.duration = 50
        scaleOutY.duration = 50
        scaleInX.duration = 150
        scaleInY.duration = 150

        scaleInX.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationStart(animation: Animator) {
                rhythmMenu.menuIconView.setImageResource(if (rhythmMenu.isOpened)
                    R.drawable.ic_audiotrack_24dp_white
                else
                    R.drawable.ic_close_white_24dp)
                if (!rhythmMenu.isOpened) {
                    when (LastRhythm.value) {
                        4.0 -> fab_whole.colorNormal = SELECTED_COLOR
                        2.0 -> fab_half.colorNormal = SELECTED_COLOR
                        1.0 -> fab_quarter.colorNormal = SELECTED_COLOR
                        .5 -> fab_eighth.colorNormal = SELECTED_COLOR
                        .25 -> fab_sixteenth.colorNormal = SELECTED_COLOR
                    }
                } else {
                    fab_whole.colorNormal = REGULAR_COLOR
                    fab_half.colorNormal = REGULAR_COLOR
                    fab_quarter.colorNormal = REGULAR_COLOR
                    fab_eighth.colorNormal = REGULAR_COLOR
                    fab_sixteenth.colorNormal = REGULAR_COLOR


                }
            }
        })
        set.play(scaleOutX).with(scaleOutY)
        set.play(scaleInX).with(scaleInY).after(scaleOutX)
        set.interpolator = OvershootInterpolator(2f)

        rhythmMenu.iconToggleAnimatorSet = set

    }

    private fun prepareMelody(): File {
        val tempoTrack = MidiTrack()
        val noteTrack = MidiTrack()

        //TODO: Make the timesignatures dynamic
        val ts = TimeSignature()
        ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION)

        //TODO: Make BPM Dynamic
        val tempo = Tempo()
        tempo.bpm = 60f

        tempoTrack.insertEvent(ts)
        tempoTrack.insertEvent(tempo)
        var lastR = 0.0
        var tick: Long = 0
        var tempTick: Long = 0

        for (chord in MusicStore.sheet) {
            var chordMargin: Long = 0
            for (note in chord) {
                tempTick = (tick + 480 * lastR).toLong()

                noteTrack.insertNote(0, nameToNum(note), 100, tempTick + chordMargin, 120 * note.rhythm.toLong())
                print("Kill me")
                chordMargin += 1
                lastR = note.rhythm
            }
            //TODO: This won't work for polyrhythmic chords lmao
            tick = tempTick
        }


        val tracks = ArrayList<MidiTrack>()
        tracks.add(tempoTrack)
        noteTrack.insertEvent(ProgramChange(0, 0, 1))
        tracks.add(noteTrack)

        val midi = MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks)
        val sdCard = Environment.getExternalStorageDirectory()
        val o = File(sdCard, "music.mid")
        try {
            midi.writeToFile(o)

            Toast.makeText(this, "File WRITTEN!, find it here: " + "/sdcard/Music/music.mid", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            System.err.println(e)
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()

        }
        return o
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            val sendBackToMain = Intent(this, MainActivity::class.java)
            startActivity(sendBackToMain)
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_undo) {
            if (MusicStore.activeNotes.size == 0) {
                if (MusicStore.sheet.size > 0) {
                    MusicStore.sheet.removeAt(MusicStore.sheet.size - 1)
                }
            } else {
                MusicStore.activeNotes.clear()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_keys) {
            val sendToKeys = Intent(this, KeyChanger::class.java)
            startActivity(sendToKeys)

        } else if (id == R.id.nav_clefs) {
            val sendToClefs = Intent(this, ClefChanger::class.java)
            startActivity(sendToClefs)
        } else if (id == R.id.nav_view_composition) {
            val sendToComp = Intent(this, Composition::class.java)
            startActivity(sendToComp)
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.singletap) {

        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_WRITE_STORAGE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //reload my activity with permission granted or use the features what required the permission
                } else {
                    Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    fun nextInput() {
        object : Thread() {
            override fun run() {
                MusicStore.sheet.add(MusicStore.activeNotes)
                //   System.gc()
                for (note in MusicStore.activeNotes) {
                    val mediaPlayer = MediaPlayer()
                    try {
                        mediaPlayer.setDataSource(applicationContext, Uri.parse("android.resource://com.devs.musicalharmonization/raw/" + note.name!!.toString() + Integer.toString(note.octave)))
                    } catch (e: Exception) {

                    }

                    Log.i("Media Playing:", "Player created!")
                    mediaPlayer.setOnPreparedListener { player -> player.start() }
                    mediaPlayer.setOnCompletionListener { mp -> mp.release() }
                    try {
                        mediaPlayer.prepareAsync()
                    } catch (e: Exception) {

                    }


                }

                MusicStore.activeNotes = ArrayList<Note>()
            }
        }.start()
    }

    companion object {

        private val REQUEST_WRITE_STORAGE = 112
    }

}
