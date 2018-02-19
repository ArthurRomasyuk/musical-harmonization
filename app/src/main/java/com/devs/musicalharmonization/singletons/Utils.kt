package com.devs.musicalharmonization.singletons

import android.os.Environment
import android.util.Log
import com.devs.musicalharmonization.Note
import com.devs.musicalharmonization.TreeNode
import com.devs.musicalharmonization.midi.MidiFile
import com.devs.musicalharmonization.midi.MidiTrack
import com.devs.musicalharmonization.midi.events.Tempo
import com.devs.musicalharmonization.midi.events.TimeSignature
import com.devs.musicalharmonization.midi.unused.ProgramChange
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.CountDownLatch

/**
 * @author Artur Romasiuk
 */
class Utils {

    companion object {
        var wasUndoPressed = false
        var wasCheckedHarmonyView = false
        var wasCheckedCadence = false
        var bpm = 60f
        var cadenceChordNumbers = HashSet<Int>()

        fun nameToNum(note: Note): Int {
            var noteNum = -1
            val pitchInOctava = 12
            if (note.name == Note.NoteName.c) {
                noteNum = pitchInOctava * note.octave
            } else if (note.name == Note.NoteName.cs) {
                noteNum = pitchInOctava * note.octave + 1
            } else if (note.name == Note.NoteName.d) {
                noteNum = pitchInOctava * note.octave + 2
            } else if (note.name == Note.NoteName.ds) {
                noteNum = pitchInOctava * note.octave + 3
            } else if (note.name == Note.NoteName.e) {
                noteNum = pitchInOctava * note.octave + 4
            } else if (note.name == Note.NoteName.f) {
                noteNum = pitchInOctava * note.octave + 5
            } else if (note.name == Note.NoteName.fs) {
                noteNum = pitchInOctava * note.octave + 6
            } else if (note.name == Note.NoteName.g) {
                noteNum = pitchInOctava * note.octave + 7
            } else if (note.name == Note.NoteName.gs) {
                noteNum = pitchInOctava * note.octave + 8
            } else if (note.name == Note.NoteName.a) {
                noteNum = pitchInOctava * note.octave + 9
            } else if (note.name == Note.NoteName.`as`) {
                noteNum = pitchInOctava * note.octave + 10
            } else if (note.name == Note.NoteName.b) {
                noteNum = pitchInOctava * note.octave + 11
            }
            return noteNum
        }

        fun nameToPitch(noteName: Note.NoteName): Int {
            var noteNum = -1
            val pitchInOctava = 12
            val stub = 0
            val name = noteName
            if (name == Note.NoteName.c) {
                noteNum = stub
            } else if (name == Note.NoteName.cs) {
                noteNum = stub + 1
            } else if (name == Note.NoteName.d) {
                noteNum = stub + 2
            } else if (name == Note.NoteName.ds) {
                noteNum = stub + 3
            } else if (name == Note.NoteName.e) {
                noteNum = stub + 4
            } else if (name == Note.NoteName.f) {
                noteNum = stub + 5
            } else if (name == Note.NoteName.fs) {
                noteNum = stub + 6
            } else if (name == Note.NoteName.g) {
                noteNum = stub + 7
            } else if (name == Note.NoteName.gs) {
                noteNum = stub + 8
            } else if (name == Note.NoteName.a) {
                noteNum = stub + 9
            } else if (name == Note.NoteName.`as`) {
                noteNum = stub + 10
            } else if (name == Note.NoteName.b) {
                noteNum = stub + 11
            }
            return noteNum
        }


        fun prepareNote(noteForPreparing: Note): File {
            val tempoTrack = MidiTrack()
            val noteTrack = MidiTrack()

            //TODO: Make the timesignatures dynamic
            val ts = TimeSignature()
            ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION)

            //TODO: Make BPM Dynamic
            val tempo = Tempo()
            tempo.bpm = bpm

            tempoTrack.insertEvent(ts)
            tempoTrack.insertEvent(tempo)
            var lastR = 0.0
            var tick: Long = 0
            var tempTick: Long = 0
            var chordMargin: Long = 0
            tempTick = (tick + 480 * lastR).toLong()

            noteTrack.insertNote(0, nameToNum(noteForPreparing), 100, tempTick + chordMargin, (120 * noteForPreparing.rhythm).toLong())
            print("Kill me")
            chordMargin += 1
            lastR = noteForPreparing.rhythm
            //TODO: This won't work for polyrhythmic chords lmao
            tick = tempTick

            val tracks = ArrayList<MidiTrack>()
            tracks.add(tempoTrack)
            noteTrack.insertEvent(ProgramChange(0, 0, 1))
            tracks.add(noteTrack)

            val midi = MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks)
            val sdCard = Environment.getExternalStorageDirectory()
            val o = File(sdCard, "music.mid")
            try {
                midi.writeToFile(o)

//                Toast.makeText(this, "File WRITTEN!, find it here: " + "/sdcard/Music/music.mid", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                System.err.println(e)
//                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()

            }
            return o
        }

        fun prepareMelody(prepareForMusicSheet: Boolean): File {
            val tempoTrack = MidiTrack()
            val noteTrack = MidiTrack()

            //TODO: Make the timesignatures dynamic
            val ts = TimeSignature()
            ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION)

            //TODO: Make BPM Dynamic
            val tempo = Tempo()
            tempo.bpm = bpm

            tempoTrack.insertEvent(ts)
            tempoTrack.insertEvent(tempo)
            var lastR = 0.0
            var tick: Long = 0
            var tempTick: Long = 0
            if (prepareForMusicSheet) {
                val startSignal = CountDownLatch(1)
                Utils.determineChord(startSignal)
                startSignal.await()
                for (chord in MusicStore.sheetAfterGarmonization) {
                    var chordMargin: Long = 0
                    for (note in chord) {
//                        if(lastR==0.0){
//                            lastR = note.rhythm
//                        }
                        lastR = note.rhythm
                        tempTick = (tick + 480 * lastR).toLong()

//                        noteTrack.insertNote(0, nameToNum(note), 100, tempTick + chordMargin, (120 * note.rhythm).toLong())
                        noteTrack.insertNote(0, note.pitch, 100, tempTick + chordMargin, (120 * note.rhythm).toLong())
                        print("Kill me")
//                        chordMargin += 1
//                        lastR = note.rhythm
                    }
                    //TODO: This won't work for polyrhythmic chords lmao
                    tick = tempTick
                }
            } else {
                var chordMargin: Long = 0
                for (note in MusicStore.activeNotes) {
                    tempTick = (tick + 480 * lastR).toLong()

                    noteTrack.insertNote(0, nameToNum(note), 100, tempTick + chordMargin, (120 * note.rhythm).toLong())
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

//                Toast.makeText(this, "File WRITTEN!, find it here: " + "/sdcard/Music/music.mid", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                System.err.println(e)
//                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()

            }
            return o
        }

        var notesForDegree: ArrayList<Note.NoteName?> = arrayListOf(Note.NoteName.c, Note.NoteName.d, Note.NoteName.e, Note.NoteName.f, Note.NoteName.g, Note.NoteName.a, Note.NoteName.b)
        var minorPitchForDegree: ArrayList<Int> = arrayListOf(0, 0, 2, 3, 5, 7, 8, 10, 12)
        var majorPitchForDegree: ArrayList<Int> = arrayListOf(0, 0, 2, 4, 5, 7, 9, 11, 12)
        var pitchForDegree: ArrayList<Int> = majorPitchForDegree


//        fun determineDegree() {
//
//            var number: Int = notesForDegree.indexOf(Key.startNote)
//            var temporaryArray: ArrayList<Note.NoteName?> = ArrayList()
//            for (determineIndexWithMinDifference in number..notesForDegree.size - 1) {
//                temporaryArray.add(notesForDegree.get(determineIndexWithMinDifference))
//            }
//            for (determineIndexWithMinDifference in 0..number - 1) {
//                temporaryArray.add(notesForDegree.get(determineIndexWithMinDifference))
//            }
//            notesForDegree = temporaryArray
//        }

        fun determineDegreeForNote(note: Note) {
            var pitch = nameToPitch(Key.startNote)
            val i = note.pitch % 12
            when (i) {
                pitch % 12 -> {
                    note.degree = 1
                }
                (pitch + pitchForDegree[2]) % 12 -> {
                    note.degree = 2
                }
                (pitch + pitchForDegree[3]) % 12 -> {
                    note.degree = 3
                }
                (pitch + pitchForDegree[4]) % 12 -> {
                    note.degree = 4
                }
                (pitch + pitchForDegree[5]) % 12 -> {
                    note.degree = 5
                }
                (pitch + pitchForDegree[6]) % 12 -> {
                    note.degree = 6
                }
                (pitch + pitchForDegree[7]) % 12 -> {
                    note.degree = 7
                }
            }
        }

//        fun determineChord() {
//            var previousNote: Note? = null
//            var previousChord: ArrayList<Note> = ArrayList()
//            var previousPreviousChord: ArrayList<Note> = ArrayList()
//            MusicStore.sheetAfterGarmonization = ArrayList()
//            for (i in MusicStore.sheet.indices) {
//                if (MusicStore.sheet[i].size == 1) {
//                    var temporaryChord: ArrayList<Note> = ArrayList()
//                    var temporaryChordPitch: ArrayList<Int> = ArrayList()
//                    for (note in MusicStore.sheet[i]) {
//                        temporaryChord.add(note)
//                        temporaryChordPitch.add(note.pitch)
//                        determineChordForNote(note, previousNote)
//                        var elements = ArrayList<Note>()
//                        if ((MusicStore.sheet.size - 1) >= (i + 2)) {
//                            determineChordForNote(MusicStore.sheet[i + 1][0], note)
//                            determineChordForNote(MusicStore.sheet[i + 2][0], MusicStore.sheet[i + 1][0])
//                            elements = harmonizeNote(note, temporaryChordPitch, previousChord, previousPreviousChord, MusicStore.sheet[i + 1][0], MusicStore.sheet[i + 2][0])
//                        } else if ((MusicStore.sheet.size - 1) >= (i + 1)) {
//                            determineChordForNote(MusicStore.sheet[i + 1][0], note)
//                            elements = harmonizeNote(note, temporaryChordPitch, previousChord, previousPreviousChord, MusicStore.sheet[i + 1][0])
//                        } else {
//                            elements = harmonizeNote(note, temporaryChordPitch, previousChord, previousPreviousChord)
//                        }
//                        temporaryChord.addAll(elements)
//                        previousNote = note
//
//                    }
//
//                    MusicStore.sheetAfterGarmonization.add(temporaryChord)
//                    if (previousChord.size != 0)
//                        previousPreviousChord = previousChord
//                    previousChord = temporaryChord
//                }
//            }
//        }

        fun determineChord(countDownLatch: CountDownLatch) {
            Thread(Runnable {
                var previousNote: Note? = null
                var previousChord: ArrayList<Note> = ArrayList()
                var previousPreviousChord: ArrayList<Note> = ArrayList()
                MusicStore.sheetAfterGarmonization = ArrayList()
                var possibleSolutionsTree: TreeNode<ArrayList<Note>> = TreeNode(ArrayList())
                var mapForBranch: HashMap<TreeNode<ArrayList<Note>>, ArrayList<ArrayList<Note>>> = HashMap()

                for (i in MusicStore.sheet.indices) {
                    for (note in MusicStore.sheet[i]) {
                        determineChordForNote(note, previousNote)
                        if (i == 0) {
                            var allPossibleChords = harmonizeNote(note, previousChord, previousPreviousChord)
                            for (possibleSolution in allPossibleChords) {
                                possibleSolutionsTree.addChild(possibleSolution)
                            }
                        } else {
                            for (branch in possibleSolutionsTree) {
                                if (branch.level == i) {
                                    previousChord = branch.data
                                    if (branch.level > 2) {
                                        previousPreviousChord = branch.parent.data
                                    }
                                    var allPossibleChordsForBranch = harmonizeNote(note, previousChord, previousPreviousChord)
                                    mapForBranch.put(branch, allPossibleChordsForBranch)
                                }
                            }
                            for (branch in mapForBranch.keys) {
                                val searchCriteria = object : Comparable<ArrayList<Note>> {
                                    override fun compareTo(treeData: ArrayList<Note>): Int {
                                        if (treeData == null)
                                            return 1
                                        val nodeOk = treeData.containsAll(branch.data)
                                        return if (nodeOk) 0 else 1
                                    }
                                }
                                val found = possibleSolutionsTree.findTreeNode(searchCriteria)

                                for (possibleSolution in mapForBranch.get(branch)!!) {
                                    found.addChild(possibleSolution)
                                }
                            }
                        }
                        previousNote = note

                    }
                }

                var list: ArrayList<ArrayList<Note>> = ArrayList()
                for (branch in possibleSolutionsTree) {
                    if (branch.isLeaf())
                        if (branch.level == MusicStore.sheet.size) {
                            createMusicStoreSheetFromTree(branch, list)
                            break
                        }
                }
                Collections.reverse(list);
                MusicStore.sheetAfterGarmonization.addAll(list)
                countDownLatch.countDown()
            }).start()
        }

        private fun createMusicStoreSheetFromTree(treeNode: TreeNode<ArrayList<Note>>, list: ArrayList<ArrayList<Note>>) {
            if (!treeNode.isRoot) {
                list.add(treeNode.data)
                createMusicStoreSheetFromTree(treeNode.parent, list)
            }
        }


        private fun determineChordForNote(note: Note, previousNote: Note?) {
            if (note.chord != Note.Chord.K && previousNote?.chord != Note.Chord.K)
                when (note.degree) {
                    1 -> {
                        if (previousNote != null) {
                            when (previousNote.chord) {
                                Note.Chord.T -> {
                                    note.chord = Note.Chord.S
                                }
                                Note.Chord.D, Note.Chord.S -> {
                                    note.chord = Note.Chord.T
                                }
                            }
                        } else {
                            note.chord = Note.Chord.T
                        }
                    }
                    2 -> {
                        note.chord = Note.Chord.D
                    }
                    3 -> {
                        note.chord = Note.Chord.T
                    }
                    4 -> {
                        note.chord = Note.Chord.S
                    }
                    5 -> {
                        if (previousNote != null) {
                            when (previousNote.chord) {
                                Note.Chord.T, Note.Chord.S -> {
                                    note.chord = Note.Chord.D
                                }
                                Note.Chord.D -> {
                                    note.chord = Note.Chord.T
                                }
                            }
                        } else {
                            note.chord = Note.Chord.T
                        }
                    }
                    6 -> {
                        note.chord = Note.Chord.S
                    }
                    7 -> {
                        note.chord = Note.Chord.D
                    }
                }
        }

        private fun harmonizeNote(note: Note, temporaryChordPitch: ArrayList<Int>, previousChord: ArrayList<Note>,
                                  previousPreviousChord: ArrayList<Note>, nextNote: Note, nextNextNote: Note): ArrayList<Note> {
            var generatedNotesForHarmony = generateNotesForHarmony(note)
            var generatedNotesForHarmonyForNextChord = generateNotesForHarmony(nextNote)
            var generatedNotesForHarmonyForNextNextChord = generateNotesForHarmony(nextNextNote)

            var harmony: Boolean = false
            var harmonyForNextChord: Boolean = false
            var harmonyForNextNextChord: Boolean = false
            var temporaryChordPitch: ArrayList<Int> = ArrayList()
            temporaryChordPitch.add(note.pitch)
            var possibleSolutions: HashSet<ArrayList<Int>> = HashSet()

            loop@ for (chordPitch in generatedNotesForHarmony) {
                temporaryChordPitch.addAll(chordPitch)
                val createVoicesForChord = createVoicesForChord(note, temporaryChordPitch)
                var temporaryChord: ArrayList<Note> = ArrayList()
                temporaryChord.add(note)
                temporaryChord.addAll(createVoicesForChord)
                harmony = checkHarmonyRules(temporaryChordPitch, previousChord, previousPreviousChord, note)
                if (harmony) {
                    var nextChordPitch: ArrayList<Int> = ArrayList()
                    nextChordPitch.add(nextNote.pitch)
                    for (chordPitchForNextChord in generatedNotesForHarmonyForNextChord) {
                        nextChordPitch.addAll(chordPitchForNextChord)
                        val createVoicesForNextChord = createVoicesForChord(nextNote, nextChordPitch)
                        var temporaryNextChord: ArrayList<Note> = ArrayList()
                        temporaryNextChord.add(nextNote)
                        temporaryNextChord.addAll(createVoicesForNextChord)
                        harmonyForNextChord = checkHarmonyRules(nextChordPitch, temporaryChord, previousChord, nextNote)
                        if (harmonyForNextChord) {
                            var nextNextChordPitch: ArrayList<Int> = ArrayList()
                            nextNextChordPitch.add(nextNextNote.pitch)
                            for (chordPitchForNextNextChord in generatedNotesForHarmonyForNextNextChord) {
                                nextNextChordPitch.addAll(chordPitchForNextNextChord)
                                harmonyForNextNextChord = checkHarmonyRules(nextNextChordPitch, temporaryNextChord, temporaryChord, nextNextNote)
                                if (harmonyForNextNextChord) {
                                    var firstVoicePitch: Int = temporaryChordPitch[0]
                                    var secondVoicePitch: Int = temporaryChordPitch[1]
                                    var thirdVoicePitch: Int = temporaryChordPitch[2]
                                    var fourthVoicePitch: Int = temporaryChordPitch[3]
                                    possibleSolutions.add(arrayListOf(firstVoicePitch, secondVoicePitch, thirdVoicePitch, fourthVoicePitch))
                                    nextNextChordPitch.removeAt(3)
                                    nextNextChordPitch.removeAt(2)
                                    nextNextChordPitch.removeAt(1)
                                } else {
                                    nextNextChordPitch.removeAt(3)
                                    nextNextChordPitch.removeAt(2)
                                    nextNextChordPitch.removeAt(1)
                                }
                            }
                            nextChordPitch.removeAt(3)
                            nextChordPitch.removeAt(2)
                            nextChordPitch.removeAt(1)
                        } else {
                            nextChordPitch.removeAt(3)
                            nextChordPitch.removeAt(2)
                            nextChordPitch.removeAt(1)
                        }
                    }
                    temporaryChordPitch.removeAt(3)
                    temporaryChordPitch.removeAt(2)
                    temporaryChordPitch.removeAt(1)
                } else {
                    temporaryChordPitch.removeAt(3)
                    temporaryChordPitch.removeAt(2)
                    temporaryChordPitch.removeAt(1)
                }
            }
            var elements = ArrayList<Note>()
            var indexWithMinDiff: Int = determineIndexWithMinDifferenceForSet(possibleSolutions)
            if (possibleSolutions.size > 0) {
                elements = createVoicesForChord(note, possibleSolutions.elementAt(indexWithMinDiff))
            }

            return elements
        }

        private fun determineIndexWithMinDifference(possibleSolutions: ArrayList<ArrayList<Int>>): Int {
            var indexWithMinDiff: Int = 0
            if (possibleSolutions.size > 0) {
                var minDifference = 0
                var index: Int = 0

                for (solution in possibleSolutions) {
                    var difference = (solution[0] - solution[1]) + (solution[1] - solution[2]) + (solution[2] - solution[3])
                    if (minDifference == 0) {
                        minDifference = difference
                    } else if (minDifference > difference) {
                        minDifference = difference
                        indexWithMinDiff = index
                    }
                    index++
                }
            }
            return indexWithMinDiff
        }

        private fun determineIndexWithMinDifferenceForSet(possibleSolutions: HashSet<ArrayList<Int>>): Int {
            var indexWithMinDiff: Int = 0
            if (possibleSolutions.size > 0) {
                var minDifference = 0
                var index: Int = 0

                for (solution in possibleSolutions) {
                    var difference = (solution[0] - solution[1]) + (solution[1] - solution[2]) + (solution[2] - solution[3])
                    if (minDifference == 0) {
                        minDifference = difference
                    } else if (minDifference > difference) {
                        minDifference = difference
                        indexWithMinDiff = index
                    }
                    index++
                }
            }
            return indexWithMinDiff
        }

        private fun harmonizeNote(note: Note, temporaryChordPitch: ArrayList<Int>, previousChord: ArrayList<Note>,
                                  previousPreviousChord: ArrayList<Note>, nextNote: Note): ArrayList<Note> {
            var generatedNotesForHarmony = generateNotesForHarmony(note)
            var generatedNotesForHarmonyForNextChord = generateNotesForHarmony(nextNote)
            var harmony: Boolean = false
            var harmonyForNextChord: Boolean = false
            var temporaryChordPitch: ArrayList<Int> = ArrayList()
            temporaryChordPitch.add(note.pitch)
            var possibleSolutions: ArrayList<ArrayList<Int>> = ArrayList()
            loop@ for (chordPitch in generatedNotesForHarmony) {
                temporaryChordPitch.addAll(chordPitch)
                val createVoicesForChord = createVoicesForChord(note, temporaryChordPitch)
                var temporaryChord: ArrayList<Note> = ArrayList()
                temporaryChord.add(note)
                temporaryChord.addAll(createVoicesForChord)
                harmony = checkHarmonyRules(temporaryChordPitch, previousChord, previousPreviousChord, note)
                if (harmony) {
                    var nextChordPitch: ArrayList<Int> = ArrayList()
                    nextChordPitch.add(nextNote.pitch)
                    for (chordPitchForNextChord in generatedNotesForHarmonyForNextChord) {
                        nextChordPitch.addAll(chordPitchForNextChord)
                        harmonyForNextChord = checkHarmonyRules(nextChordPitch, temporaryChord, previousChord, nextNote)
                        if (harmonyForNextChord) {
                            var firstVoicePitch: Int = temporaryChordPitch[0]
                            var secondVoicePitch: Int = temporaryChordPitch[1]
                            var thirdVoicePitch: Int = temporaryChordPitch[2]
                            var fourthVoicePitch: Int = temporaryChordPitch[3]
                            possibleSolutions.add(arrayListOf(firstVoicePitch, secondVoicePitch, thirdVoicePitch, fourthVoicePitch))
                            nextChordPitch.removeAt(3)
                            nextChordPitch.removeAt(2)
                            nextChordPitch.removeAt(1)
                        } else {
                            nextChordPitch.removeAt(3)
                            nextChordPitch.removeAt(2)
                            nextChordPitch.removeAt(1)
                        }
                    }
                    temporaryChordPitch.removeAt(3)
                    temporaryChordPitch.removeAt(2)
                    temporaryChordPitch.removeAt(1)
                } else {
                    temporaryChordPitch.removeAt(3)
                    temporaryChordPitch.removeAt(2)
                    temporaryChordPitch.removeAt(1)
                }
            }

            var elements = ArrayList<Note>()
            var indexWithMinDiff: Int = determineIndexWithMinDifference(possibleSolutions)
            if (possibleSolutions.size > 0) {
                elements = createVoicesForChord(note, possibleSolutions[indexWithMinDiff])
            }
            return elements
        }

        private fun harmonizeNote(note: Note, previousChord: ArrayList<Note>, previousPreviousChord: ArrayList<Note>): ArrayList<ArrayList<Note>> {
            var generatedNotesForHarmony = generateNotesForHarmony(note)
            var temporaryChordPitch: ArrayList<Int> = ArrayList()
            temporaryChordPitch.add(note.pitch)
            var possibleSolutions: ArrayList<ArrayList<Int>> = ArrayList()
            for (chordPitch in generatedNotesForHarmony) {
                temporaryChordPitch.addAll(chordPitch)
                if (checkHarmonyRules(temporaryChordPitch, previousChord, previousPreviousChord, note)) {
                    var firstVoicePitch: Int = temporaryChordPitch[0]
                    var secondVoicePitch: Int = temporaryChordPitch[1]
                    var thirdVoicePitch: Int = temporaryChordPitch[2]
                    var fourthVoicePitch: Int = temporaryChordPitch[3]
                    possibleSolutions.add(arrayListOf(firstVoicePitch, secondVoicePitch, thirdVoicePitch, fourthVoicePitch))
                    temporaryChordPitch.removeAt(3)
                    temporaryChordPitch.removeAt(2)
                    temporaryChordPitch.removeAt(1)
                } else {
                    temporaryChordPitch.removeAt(3)
                    temporaryChordPitch.removeAt(2)
                    temporaryChordPitch.removeAt(1)
                }
            }

//            var possibleChord = ArrayList<Note>()
            var allPossibleChords = ArrayList<ArrayList<Note>>()
//            var indexWithMinDiff: Int = determineIndexWithMinDifference(possibleSolutions)
            if (possibleSolutions.size > 0) {
                for (possibleSolution in possibleSolutions) {
//                    possibleChord = createVoicesForChord(note, possibleSolution)
                    allPossibleChords.add(createVoicesForChord(note, possibleSolution))
                }
            }
            return allPossibleChords
        }

        private fun createVoicesForChord(note: Note, temporaryChordPitch: ArrayList<Int>): ArrayList<Note> {
            if (temporaryChordPitch.size > 1) {
                var note1 = Note(note.x, 0f, 0)
                //                                            note1.changeOctave(note.octave - 1)
                note1.pitch = temporaryChordPitch[1]
                note1.rhythm = note.rhythm
                determineY(note, note1)
                var note4 = Note(note.x, 0f, 0)
                //                                            note4.changeOctave(note.octave)
                note4.pitch = temporaryChordPitch[2]
                note4.rhythm = note.rhythm

                determineY(note, note4)
                var note6 = Note(note.x, 0f, 0)
                //                                            note6.changeOctave(note.octave - 1)
                note6.pitch = temporaryChordPitch[3]
                note6.rhythm = note.rhythm
                determineY(note, note6)
//                val elements = arrayListOf(note1, note4, note6)
                val elements = arrayListOf(note, note1, note4, note6)
                return elements
            } else {
                return ArrayList()
            }
        }

        private fun checkHarmonyRules(temporaryChordPitch: ArrayList<Int>, previousChord: ArrayList<Note>, previousPreviousChord: ArrayList<Note>, note: Note): Boolean {
            var previousChordPitch: ArrayList<Int> = ArrayList()
            if (previousChord.size != 0) {
                for (note in previousChord) {
                    previousChordPitch.add(note.pitch)
                }
            }
            var isFirstVoiceHighest: Boolean = false
            var isDistanceBetweenNotesRight: Boolean = false
            var oneVoiceGoesInOtherDirection: Boolean = false
            var rightDistanceBetweenVoiceInDifferentChords: Boolean = false
            var distanceIn7Seminotes: Boolean = false
            var distanceIn12Seminotes: Boolean = false
            var distanceIn24Seminotes: Boolean = false
            var sameChordWithSameNotes: Boolean = false
            var differenceInVoice4: Boolean = false
            var rightVoice4ForChordType: Boolean = false
            var twiceDegree5InChordK: Boolean = false
            var allVoicesAreDifferent: Boolean = false


            val distance1 = temporaryChordPitch[0] - temporaryChordPitch[1] <= 12
            val distance2 = temporaryChordPitch[1] - temporaryChordPitch[2] <= 12
            val distance3 = temporaryChordPitch[2] - temporaryChordPitch[3] <= 24
            if (distance1 && distance2 && distance3) {
                isDistanceBetweenNotesRight = true
            }
            if (temporaryChordPitch[0] > temporaryChordPitch[1]) {
                isFirstVoiceHighest = true
            }

            if (!(temporaryChordPitch[0].compareTo(temporaryChordPitch[1]) == 0 || temporaryChordPitch[0].compareTo(temporaryChordPitch[2]) == 0
                    || temporaryChordPitch[0].compareTo(temporaryChordPitch[3]) == 0 || temporaryChordPitch[1].compareTo(temporaryChordPitch[2]) == 0
                    || temporaryChordPitch[1].compareTo(temporaryChordPitch[3]) == 0 || temporaryChordPitch[2].compareTo(temporaryChordPitch[3]) == 0)) {
                allVoicesAreDifferent = true
            }


            var noteForCheckDegreeForVoice4: Note = Note(0f, 0f, 0)
            noteForCheckDegreeForVoice4.pitch = temporaryChordPitch[3]
            determineDegreeForNote(noteForCheckDegreeForVoice4)

            when (note.chord) {
                Note.Chord.T ->
                    if (noteForCheckDegreeForVoice4.degree == 1) {
                        rightVoice4ForChordType = true
                    }
                Note.Chord.S ->
                    if (noteForCheckDegreeForVoice4.degree == 4) {
                        rightVoice4ForChordType = true
                    }
                Note.Chord.D ->
                    if (noteForCheckDegreeForVoice4.degree == 5) {
                        rightVoice4ForChordType = true
                    }
                Note.Chord.K ->
                    if (noteForCheckDegreeForVoice4.degree == 5) {
                        rightVoice4ForChordType = true
                    }
            }
            var noteForCheckDegree5ForVoice2: Note = Note(0f, 0f, 0)
            noteForCheckDegree5ForVoice2.pitch = temporaryChordPitch[1]
            determineDegreeForNote(noteForCheckDegree5ForVoice2)
            var noteForCheckDegree5ForVoice3: Note = Note(0f, 0f, 0)
            noteForCheckDegree5ForVoice3.pitch = temporaryChordPitch[2]
            determineDegreeForNote(noteForCheckDegree5ForVoice3)

            if (note.chord == Note.Chord.K && note.degree != 5) {
                if (noteForCheckDegree5ForVoice2.degree == 5 || noteForCheckDegree5ForVoice3.degree == 5)
                    twiceDegree5InChordK = true
            } else {
                twiceDegree5InChordK = true
            }

            if (previousChord.size > 1) {
                val directionDec1 = temporaryChordPitch[0] < previousChordPitch[0]
                val directionDec2 = temporaryChordPitch[1] < previousChordPitch[1]
                val directionDec3 = temporaryChordPitch[2] < previousChordPitch[2]
                val directionDec4 = temporaryChordPitch[3] < previousChordPitch[3]

                val directionInc1 = temporaryChordPitch[0] > previousChordPitch[0]
                val directionInc2 = temporaryChordPitch[1] > previousChordPitch[1]
                val directionInc3 = temporaryChordPitch[2] > previousChordPitch[2]
                val directionInc4 = temporaryChordPitch[3] > previousChordPitch[3]

                if (!((directionDec1 && directionDec2 && directionDec3 && directionDec4) || (directionInc1 && directionInc2 && directionInc3 && directionInc4))) {
                    oneVoiceGoesInOtherDirection = true
                }

                val distanceBetweenVoicesInDifferentChords1 = temporaryChordPitch[0] >= previousChordPitch[1]
                val distanceBetweenVoicesInDifferentChords2 = temporaryChordPitch[1] >= previousChordPitch[2]
                val distanceBetweenVoicseInDifferentChords3 = temporaryChordPitch[2] >= previousChordPitch[3]
                if (distanceBetweenVoicesInDifferentChords1 || distanceBetweenVoicesInDifferentChords2 || distanceBetweenVoicseInDifferentChords3) {
                    rightDistanceBetweenVoiceInDifferentChords = true
                }

                val distanceBetweenVoicesInChord1 = Math.abs(temporaryChordPitch[0] - temporaryChordPitch[1])
                val distanceBetweenVoicesInChord2 = Math.abs(temporaryChordPitch[1] - temporaryChordPitch[2])
                val distanceBetweenVoicesInChord3 = Math.abs(temporaryChordPitch[2] - temporaryChordPitch[3])

                val distanceBetweenVoicesInPreviousChord1 = Math.abs(previousChordPitch[0] - previousChordPitch[1])
                val distanceBetweenVoicesInPreviousChord2 = Math.abs(previousChordPitch[1] - previousChordPitch[2])
                val distanceBetweenVoicesInPreviousChord3 = Math.abs(previousChordPitch[2] - previousChordPitch[3])

                if (!((distanceBetweenVoicesInChord1 == 7 && distanceBetweenVoicesInChord1 == distanceBetweenVoicesInPreviousChord1) ||
                        (distanceBetweenVoicesInChord2 == 7 && distanceBetweenVoicesInChord2 == distanceBetweenVoicesInPreviousChord2) ||
                        (distanceBetweenVoicesInChord3 == 7 && distanceBetweenVoicesInChord3 == distanceBetweenVoicesInPreviousChord3))) {
                    distanceIn7Seminotes = true
                }

                if (!((distanceBetweenVoicesInChord1 == 12 && distanceBetweenVoicesInChord1 == distanceBetweenVoicesInPreviousChord1) ||
                        (distanceBetweenVoicesInChord2 == 12 && distanceBetweenVoicesInChord2 == distanceBetweenVoicesInPreviousChord2) ||
                        (distanceBetweenVoicesInChord3 == 12 && distanceBetweenVoicesInChord3 == distanceBetweenVoicesInPreviousChord3))) {
                    distanceIn12Seminotes = true
                }

                if (!((distanceBetweenVoicesInChord1 == 24 && distanceBetweenVoicesInChord1 == distanceBetweenVoicesInPreviousChord1) ||
                        (distanceBetweenVoicesInChord2 == 24 && distanceBetweenVoicesInChord2 == distanceBetweenVoicesInPreviousChord2) ||
                        (distanceBetweenVoicesInChord3 == 24 && distanceBetweenVoicesInChord3 == distanceBetweenVoicesInPreviousChord3) ||
                        (distanceBetweenVoicesInChord1 + distanceBetweenVoicesInChord2 == 24 && distanceBetweenVoicesInChord1 + distanceBetweenVoicesInChord2
                                == distanceBetweenVoicesInPreviousChord1 + distanceBetweenVoicesInPreviousChord2) ||
                        (distanceBetweenVoicesInChord2 + distanceBetweenVoicesInChord3 == 24 && distanceBetweenVoicesInChord2 + distanceBetweenVoicesInChord3
                                == distanceBetweenVoicesInPreviousChord2 + distanceBetweenVoicesInPreviousChord3))) {
                    distanceIn24Seminotes = true
                }
                if (!(temporaryChordPitch[0] == previousChordPitch[0] && temporaryChordPitch[1] == previousChordPitch[1]
                        && temporaryChordPitch[2] == previousChordPitch[2] && temporaryChordPitch[3] == previousChordPitch[3])) {
                    sameChordWithSameNotes = true
                }
            } else {
                oneVoiceGoesInOtherDirection = true
                rightDistanceBetweenVoiceInDifferentChords = true
                distanceIn7Seminotes = true
                distanceIn12Seminotes = true
                distanceIn24Seminotes = true
                sameChordWithSameNotes = true
            }
            if (previousPreviousChord.size > 1 && previousChord.size > 1) {
                val difference1 = previousChord[3].pitch - previousPreviousChord[3].pitch
                val difference2 = temporaryChordPitch[3] - previousChord[3].pitch
                val difference = difference1 + difference2
                if (!(difference >= 6)) {
                    differenceInVoice4 = true
                }
            } else {
                differenceInVoice4 = true
            }

            return isFirstVoiceHighest && rightVoice4ForChordType && isDistanceBetweenNotesRight && oneVoiceGoesInOtherDirection && rightDistanceBetweenVoiceInDifferentChords && distanceIn7Seminotes &&
                    distanceIn12Seminotes && distanceIn24Seminotes && sameChordWithSameNotes && differenceInVoice4 && twiceDegree5InChordK && allVoicesAreDifferent
        }

        fun generateNotesForHarmony(note: Note): ArrayList<ArrayList<Int>> {

            var pitchForChord: ArrayList<ArrayList<Int>> = ArrayList()
            var notesWithDistanceInOneOctave: ArrayList<ArrayList<Int>> = ArrayList()

            when (note.chord) {
                Note.Chord.T -> {
                    when (note.degree) {
                        1 -> {
                            for (i in 0..2) {
                                var pitch1 = note.pitch - (12 * i) + pitchForDegree[5] - pitchForDegree[note.degree]
                                var pitch2 = note.pitch - (12 * i) + pitchForDegree[3] - pitchForDegree[note.degree]
                                var pitch3 = note.pitch - (12 * i) + pitchForDegree[1] - pitchForDegree[note.degree]
                                notesWithDistanceInOneOctave.add(arrayListOf(pitch1, pitch2, pitch3))
                            }
                        }
                        3 -> {
                            for (i in 0..2) {
                                var pitch1 = note.pitch - (12 * i) + pitchForDegree[1] - pitchForDegree[note.degree]
                                var pitch2 = note.pitch - (12 * i) + pitchForDegree[5] - pitchForDegree[note.degree]
                                var pitch3 = note.pitch - (12 * i) + pitchForDegree[1] - pitchForDegree[note.degree]
                                notesWithDistanceInOneOctave.add(arrayListOf(pitch1, pitch2, pitch3))
                            }
                        }
                        5 -> {
                            for (i in 0..2) {
                                var pitch1 = note.pitch - (12 * i) + pitchForDegree[3] - pitchForDegree[note.degree]
                                var pitch2 = note.pitch - (12 * i) + pitchForDegree[1] - pitchForDegree[note.degree]
                                var pitch3 = note.pitch - (12 * i) + pitchForDegree[1] - pitchForDegree[note.degree]
                                notesWithDistanceInOneOctave.add(arrayListOf(pitch1, pitch2, pitch3))
                            }
                        }
                    }
                }
                Note.Chord.S -> {
                    when (note.degree) {
                        1 -> {
                            for (i in 0..2) {
                                var pitch1 = note.pitch - (12 * i) + pitchForDegree[6] - pitchForDegree[note.degree]
                                var pitch2 = note.pitch - (12 * i) + pitchForDegree[4] - pitchForDegree[note.degree]
                                var pitch3 = note.pitch - (12 * i) + pitchForDegree[4] - pitchForDegree[note.degree]
                                notesWithDistanceInOneOctave.add(arrayListOf(pitch1, pitch2, pitch3))
                            }
                        }
                        4 -> {
                            for (i in 0..2) {
                                var pitch1 = note.pitch - (12 * i) + pitchForDegree[1] - pitchForDegree[note.degree]
                                var pitch2 = note.pitch - (12 * i) + pitchForDegree[4] - pitchForDegree[note.degree]
                                var pitch3 = note.pitch - (12 * i) + pitchForDegree[6] - pitchForDegree[note.degree]
                                notesWithDistanceInOneOctave.add(arrayListOf(pitch1, pitch2, pitch3))
                            }
                        }
                        6 -> {
                            for (i in 0..2) {
                                var pitch1 = note.pitch - (12 * i) + pitchForDegree[4] - pitchForDegree[note.degree]
                                var pitch2 = note.pitch - (12 * i) + pitchForDegree[1] - pitchForDegree[note.degree]
                                var pitch3 = note.pitch - (12 * i) + pitchForDegree[4] - pitchForDegree[note.degree]
                                notesWithDistanceInOneOctave.add(arrayListOf(pitch1, pitch2, pitch3))
                            }
                        }
                    }
                }
                Note.Chord.D -> {
                    when (note.degree) {
                        2 -> {
                            for (i in 0..2) {
                                var pitch1 = note.pitch - (12 * i) + pitchForDegree[7] - pitchForDegree[note.degree]
                                var pitch2 = note.pitch - (12 * i) + pitchForDegree[5] - pitchForDegree[note.degree]
                                var pitch3 = note.pitch - (12 * i) + pitchForDegree[5] - pitchForDegree[note.degree]
                                notesWithDistanceInOneOctave.add(arrayListOf(pitch1, pitch2, pitch3))
                            }
                        }
                        5 -> {
                            for (i in 0..2) {
                                var pitch1 = note.pitch - (12 * i) + pitchForDegree[2] - pitchForDegree[note.degree]
                                var pitch2 = note.pitch - (12 * i) + pitchForDegree[7] - pitchForDegree[note.degree]
                                var pitch3 = note.pitch - (12 * i) + pitchForDegree[5] - pitchForDegree[note.degree]
                                notesWithDistanceInOneOctave.add(arrayListOf(pitch1, pitch2, pitch3))
                            }
                        }
                        7 -> {
                            for (i in 0..2) {
                                var pitch1 = note.pitch - (12 * i) + pitchForDegree[5] - pitchForDegree[note.degree]
                                var pitch2 = note.pitch - (12 * i) + pitchForDegree[2] - pitchForDegree[note.degree]
                                var pitch3 = note.pitch - (12 * i) + pitchForDegree[5] - pitchForDegree[note.degree]
                                notesWithDistanceInOneOctave.add(arrayListOf(pitch1, pitch2, pitch3))
                            }
                        }
                    }
                }
                Note.Chord.K -> {
                    when (note.degree) {
                        1 -> {
                            for (i in 0..2) {
                                var pitch1 = note.pitch - (12 * i) + pitchForDegree[5] - pitchForDegree[note.degree]
                                var pitch2 = note.pitch - (12 * i) + pitchForDegree[5] - pitchForDegree[note.degree]
                                var pitch3 = note.pitch - (12 * i) + pitchForDegree[3] - pitchForDegree[note.degree]
                                notesWithDistanceInOneOctave.add(arrayListOf(pitch1, pitch2, pitch3))
                            }
                        }
                        3 -> {
                            for (i in 0..2) {
                                var pitch1 = note.pitch - (12 * i) + pitchForDegree[1] - pitchForDegree[note.degree]
                                var pitch2 = note.pitch - (12 * i) + pitchForDegree[5] - pitchForDegree[note.degree]
                                var pitch3 = note.pitch - (12 * i) + pitchForDegree[5] - pitchForDegree[note.degree]
                                notesWithDistanceInOneOctave.add(arrayListOf(pitch1, pitch2, pitch3))
                            }
                        }
                        5 -> {
                            for (i in 0..2) {
                                var pitch1 = note.pitch - (12 * i) + pitchForDegree[3] - pitchForDegree[note.degree]
                                var pitch2 = note.pitch - (12 * i) + pitchForDegree[1] - pitchForDegree[note.degree]
                                var pitch3 = note.pitch - (12 * i) + pitchForDegree[5] - pitchForDegree[note.degree]
                                notesWithDistanceInOneOctave.add(arrayListOf(pitch1, pitch2, pitch3))
                            }
                        }
                    }
                }
            }
//            Log.i("generateNotesForHarmony", notesWithDistanceInOneOctave.toString())

            for (x in 0..2) {
                var first: Int
                var second: Int
                var third: Int
                first = notesWithDistanceInOneOctave[x][0]
                for (y in 0..2) {
                    second = notesWithDistanceInOneOctave[y][1]
                    for (z in 0..2) {
                        third = notesWithDistanceInOneOctave[z][2]
                        val pitchArray = arrayListOf(first, second, third)
                        Collections.sort(pitchArray, Collections.reverseOrder());
                        pitchForChord.add(pitchArray)
                    }
                }
            }


//            Log.i("generateNotesForHarmony", pitchForChord.toString())

            return pitchForChord;
        }

        fun determineY(note: Note, note1: Note) {
            determineDegreeForNote(note1)
            val i = note.pitch - note1.pitch
            when {
                i >= 24 -> {
                    when {
                        note.degree - note1.degree >= 0 -> note1.y = note.y + ((note.degree - note1.degree + 14) * note.subInt)
                        note.degree - note1.degree < 0 -> note1.y = note.y + ((note.degree - note1.degree + 21) * note.subInt)
                    }
                }
                i >= 12 -> {
                    when {
                        note.degree - note1.degree >= 0 -> note1.y = note.y + ((note.degree - note1.degree + 7) * note.subInt)
                        note.degree - note1.degree < 0 -> note1.y = note.y + ((note.degree - note1.degree + 14) * note.subInt)
                    }
                }
                else -> {
                    when {
                        note.degree - note1.degree >= 0 -> note1.y = note.y + ((note.degree - note1.degree) * note.subInt)
                        note.degree - note1.degree < 0 -> note1.y = note.y + ((note.degree - note1.degree + 7) * note.subInt)
                    }
                }
            }
        }
    }
}