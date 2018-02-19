package com.devs.musicalharmonization.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.devs.musicalharmonization.R;
import com.devs.musicalharmonization.midi.unused.Clef;
import com.devs.musicalharmonization.singletons.Bar;
import com.devs.musicalharmonization.singletons.ClefSetting;
import com.devs.musicalharmonization.singletons.DensityMetrics;
import com.devs.musicalharmonization.singletons.LastRhythm;
import com.devs.musicalharmonization.singletons.MusicStore;
import com.devs.musicalharmonization.Note;
import com.devs.musicalharmonization.NoteBitmap;
import com.devs.musicalharmonization.singletons.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * 2/14/2016.
 */
public class CompositionView extends View {
    private static final String TAG = "CompositionView";
    int accidental = 0;
    private GestureDetector mDetector = null;
    Note renderableNote = new Note(0f, 0f, 0);
    String DEBUG_TAG = "MusicDebug";
    float touchX = 0f;
    float touchY = 0f;
    boolean drawNote = false;
    boolean drawBarLine = false;
    Paint paint = new Paint();
    Context conx;
    public final float NOTE_WIDTH = 150;
    public final float NOTE_HEIGHT = 100;
    int width;
    int height;
    private float STAFF_WIDTH;
    DisplayMetrics displayMetrics;
    WindowManager manager;
    private File noteSound;
    private float distanceBetweenNotes;
    private Bitmap scaledClefBitmap;
    private Bitmap scaledBassClefBitmap;
    private float altoClefTop;
    private float trebleClefTop;
    private float bassClefTop;
    private float secondSheetBassClefTop;


    public CompositionView(Context con) {
        super(con);
        init(con);
    }

    public CompositionView(Context con, AttributeSet attrs) {
        super(con, attrs);
        init(con);
    }

    public CompositionView(Context con, AttributeSet attrs, int defStyle) {
        super(con, attrs, defStyle);
        init(con);
    }

    private void init(Context con) {
        conx = con;
        setFocusable(true);
        setFocusableInTouchMode(true);
        mDetector = new GestureDetector(this.getContext(), new mListener());
        displayMetrics = new DisplayMetrics();
        manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        paint.setColor(Color.BLACK);
        distanceBetweenNotes = 300;
        STAFF_WIDTH = MusicStore.sheet.size() * distanceBetweenNotes + width;


    }


    @Override
    // @TargetApi(21)
    public void onDraw(Canvas c) {
        float toolbarHeight = DensityMetrics.Companion.getToolbarHeight();
        DensityMetrics.setSpaceHeight((height - toolbarHeight) / 16);
        altoClefTop = toolbarHeight + 2 * DensityMetrics.getSpaceHeight();
        trebleClefTop = toolbarHeight + DensityMetrics.getSpaceHeight();
        bassClefTop = toolbarHeight + 2 * DensityMetrics.getSpaceHeight();
        secondSheetBassClefTop = toolbarHeight + 8 * DensityMetrics.getSpaceHeight();

        int altoClefHeight = 4 * (int) DensityMetrics.getSpaceHeight();
        int trebleClefHeight = 6 * (int) DensityMetrics.getSpaceHeight();
        int bassClefHeight = 3 * (int) DensityMetrics.getSpaceHeight();
        int dstWidth = 0;
        if (ClefSetting.clef == Clef.ALTO) {
            Bitmap clefBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.alto_clef);
            int scaledWidth = clefBitmap.getScaledWidth(DisplayMetrics.DENSITY_DEFAULT);
            int scaledHeight = clefBitmap.getScaledHeight(DisplayMetrics.DENSITY_DEFAULT);
            dstWidth = altoClefHeight * scaledWidth / scaledHeight;
            scaledClefBitmap = Bitmap.createScaledBitmap(clefBitmap, dstWidth,
                    altoClefHeight, true);
        } else if (ClefSetting.clef == Clef.TREBLE) {
            Bitmap clefBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.treble_clef);
            int scaledWidth = clefBitmap.getScaledWidth(DisplayMetrics.DENSITY_DEFAULT);
            int scaledHeight = clefBitmap.getScaledHeight(DisplayMetrics.DENSITY_DEFAULT);
            dstWidth = trebleClefHeight * scaledWidth / scaledHeight;
            scaledClefBitmap = Bitmap.createScaledBitmap(clefBitmap, dstWidth, trebleClefHeight, true);
        } else if (ClefSetting.clef == Clef.BASS) {
            Bitmap clefBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bass_clef);
            int scaledWidth = clefBitmap.getScaledWidth(DisplayMetrics.DENSITY_DEFAULT);
            int scaledHeight = clefBitmap.getScaledHeight(DisplayMetrics.DENSITY_DEFAULT);
            dstWidth = bassClefHeight * scaledWidth / scaledHeight;
            scaledClefBitmap = Bitmap.createScaledBitmap(clefBitmap, dstWidth, bassClefHeight, true);
        }

        Bitmap bassClefBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bass_clef);
        int bassClefScaledWidth = bassClefBitmap.getScaledWidth(DisplayMetrics.DENSITY_DEFAULT);
        int bassClefScaledHeight = bassClefBitmap.getScaledHeight(DisplayMetrics.DENSITY_DEFAULT);
        int bassClefWidth = bassClefHeight * bassClefScaledWidth / bassClefScaledHeight;
        scaledBassClefBitmap = Bitmap.createScaledBitmap(bassClefBitmap, bassClefWidth, bassClefHeight, true);
        manager.getDefaultDisplay().getMetrics(displayMetrics);
//        width = displayMetrics.widthPixels;
        STAFF_WIDTH = MusicStore.sheet.size() * distanceBetweenNotes + width;
//        height = displayMetrics.heightPixels;
//        DensityMetrics.setSpaceHeight((height - DensityMetrics.Companion.getToolbarHeight()) / 16);
        paint.setStrokeWidth(10);
        for (int i = 2; i < 13; i++) {
            if (i != 7) {
                c.drawLine(0, DensityMetrics.getSpaceHeight() * i + DensityMetrics.Companion.getToolbarHeight(), STAFF_WIDTH, DensityMetrics.getSpaceHeight() * i +
                        DensityMetrics.Companion.getToolbarHeight(), paint);
            }
        }

        if (ClefSetting.clef == Clef.ALTO) {
            c.drawBitmap(scaledClefBitmap, 20f, altoClefTop, paint);
        } else if (ClefSetting.clef == Clef.TREBLE) {
            c.drawBitmap(scaledClefBitmap, 1f, trebleClefTop, paint);
        } else if (ClefSetting.clef == Clef.BASS) {
            c.drawBitmap(scaledClefBitmap, 1f, bassClefTop, paint);
        }

        c.drawBitmap(scaledBassClefBitmap, 1f, secondSheetBassClefTop, paint);

        paint.setTextSize(DensityMetrics.getSpaceHeight() * 2);
        c.drawText(String.valueOf((int) Bar.topNumber), dstWidth + 20f, DensityMetrics.Companion.getToolbarHeight() + DensityMetrics.getSpaceHeight() * 3.5f, paint);
        c.drawText(String.valueOf((int) Bar.bottomNumber), dstWidth + 20f, DensityMetrics.Companion.getToolbarHeight() + DensityMetrics.getSpaceHeight() * 5.5f, paint);

        float drawX = 320;
        ArrayList<ArrayList<Note>> sheet;
//        for (ArrayList<Note> chord : MusicStore.sheet) {
        if (Utils.Companion.getWasCheckedHarmonyView()) {
            sheet = MusicStore.sheetAfterGarmonization;
        } else {
            sheet = MusicStore.sheet;
        }

        for (ArrayList<Note> chord : sheet) {
            int y = 0;
            for (Note note : chord) {
                int noteHeadID = 0;
//                if (note.getRhythm() == 2) {
//                    noteHeadID = R.drawable.half_note_head;
//
//                } else {
//                    noteHeadID = R.drawable.quarter_note_head;
//                }
                note.setDrawX(drawX);
                int noteWidth = (int) (DensityMetrics.getSpaceHeight() * 3);
                int noteHeight = (int) (DensityMetrics.getSpaceHeight() * 3.5);
                if (y % 2 == 0) {
                    if (note.getRhythm() == 0.25) {
                        noteHeadID = R.drawable.note_sixteenth;
                    } else if (note.getRhythm() == 0.5) {
                        noteHeadID = R.drawable.note_eighth;
                    } else if (note.getRhythm() == 1) {
                        noteHeadID = R.drawable.note_quarter;
                    } else if (note.getRhythm() == 2) {
                        noteHeadID = R.drawable.note_half;
                    } else if (note.getRhythm() == 4) {
                        noteHeadID = R.drawable.note_whole;
                    }

                    Drawable noteHead = ContextCompat.getDrawable(getContext(), noteHeadID);
                    noteHead.setColorFilter(Color.BLACK, android.graphics.PorterDuff.Mode.SRC_IN);
                    ;
                    Bitmap nh = NoteBitmap.getBitmap(noteHead);
                    Bitmap noteHeadBitmap = Bitmap.createScaledBitmap(nh, noteWidth, noteHeight, true);
                    c.drawBitmap(noteHeadBitmap, (int) drawX, note.getY() - noteWidth, paint);
                } else {
                    if (note.getRhythm() == 0.25) {
                        noteHeadID = R.drawable.note_sixteenth_down;
                    } else if (note.getRhythm() == 0.5) {
                        noteHeadID = R.drawable.note_eighth_down;
                    } else if (note.getRhythm() == 1) {
                        noteHeadID = R.drawable.note_quarter_down;
                    } else if (note.getRhythm() == 2) {
                        noteHeadID = R.drawable.note_half_down;
                    } else if (note.getRhythm() == 4) {
                        noteHeadID = R.drawable.note_whole_down;
                    }

                    Drawable noteHead = ContextCompat.getDrawable(getContext(), noteHeadID);
                    noteHead.setColorFilter(Color.BLACK, android.graphics.PorterDuff.Mode.SRC_IN);
                    Bitmap nh = NoteBitmap.getBitmap(noteHead);
                    Bitmap noteHeadBitmap = Bitmap.createScaledBitmap(nh, noteWidth, noteHeight, true);
                    c.drawBitmap(noteHeadBitmap, (int) drawX, note.getY() - (int) (DensityMetrics.getSpaceHeight() * 0.5), paint);
                }
                if (note.isLastInBar()) {
                    c.drawLine(drawX + DensityMetrics.getSpaceHeight() * 3, DensityMetrics.getSpaceHeight() * 2 + DensityMetrics.Companion.getToolbarHeight(),
                            drawX + DensityMetrics.getSpaceHeight() * 3, DensityMetrics.getSpaceHeight() * 6 + DensityMetrics.Companion.getToolbarHeight(), paint);
                    c.drawLine(drawX + DensityMetrics.getSpaceHeight() * 3, DensityMetrics.getSpaceHeight() * 8 + DensityMetrics.Companion.getToolbarHeight(),
                            drawX + DensityMetrics.getSpaceHeight() * 3, DensityMetrics.getSpaceHeight() * 12 + DensityMetrics.Companion.getToolbarHeight(), paint);
                }
                paint.setTextSize(120);
                if (note.getChord() != null)
                    switch (note.getChord()) {
                        case T:
                            c.drawText("T", drawX + noteWidth / 2, DensityMetrics.Companion.getToolbarHeight() + DensityMetrics.getSpaceHeight(), paint);
                            break;
                        case S:
                            c.drawText("S", drawX + noteWidth / 2, DensityMetrics.Companion.getToolbarHeight() + DensityMetrics.getSpaceHeight(), paint);
                            break;
                        case D:
                            c.drawText("D", drawX + noteWidth / 2, DensityMetrics.Companion.getToolbarHeight() + DensityMetrics.getSpaceHeight(), paint);
                            break;
                        case K:
                            c.drawText("K", drawX + noteWidth / 2, DensityMetrics.Companion.getToolbarHeight() + DensityMetrics.getSpaceHeight(), paint);
                            break;
                    }
                y++;
            }
            drawX += distanceBetweenNotes;
        }

        if (drawNote) {
            requestLayout();
            drawNoteHead(renderableNote, c);
            drawNote = false;
        }

        if (Utils.Companion.getWasUndoPressed()) {
            requestLayout();
            Utils.Companion.setWasUndoPressed(false);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension((int) STAFF_WIDTH, height);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = false;
        if (!Utils.Companion.getWasCheckedHarmonyView()) {
            try {
                result = mDetector.onTouchEvent(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!result) {
                Log.d("What", "is this gesture?");
            }

            float x = event.getX();
            float y = event.getY() - DensityMetrics.Companion.getToolbarHeight();
            //rhythmBar.setVisibility(View.GONE);
            if (!Utils.Companion.getWasCheckedCadence()) {
                if (x > (MusicStore.sheet.size() * distanceBetweenNotes) && y < (DensityMetrics.getSpaceHeight() * 7) && y > DensityMetrics.Companion.getToolbarHeight()) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            touchX = x;
                            touchY = y;
                            break;
                        case MotionEvent.ACTION_UP:
                            drawNote = true;
                            renderableNote = new Note(touchX, touchY, accidental);
                            noteSound = Utils.Companion.prepareNote(renderableNote);
                            double rhythmSum = 0;
                            double barRhythmSum = 0;
                            Bar.activeBarNotes.add(renderableNote);
                            for (Note barNote : Bar.activeBarNotes) {
                                barRhythmSum += barNote.getRhythm() / 4;
                            }
                            double barNumber = Bar.topNumber / Bar.bottomNumber;
                            if (barRhythmSum > barNumber) {
                                Toast.makeText(getContext(), "Note with wrong rhythm. Change note rhytm or bar number",
                                        Toast.LENGTH_LONG).show();
                                Bar.activeBarNotes.remove(Bar.activeBarNotes.size() - 1);
                            } else {
                                MusicStore.activeNotes.add(renderableNote);
                                MusicStore.sheet.add(MusicStore.activeNotes);
                                MusicStore.activeNotes = new ArrayList<Note>();
                                for (ArrayList<Note> chord : MusicStore.sheet) {
                                    for (Note note : chord) {
                                        rhythmSum += note.getRhythm();
                                        double v = rhythmSum / 4;
                                        boolean b = v % barNumber == 0;
                                        if (rhythmSum != 0 && b) {
                                            note.setLastInBar(true);
                                            Bar.activeBarNotes = new ArrayList<Note>();
                                        }
                                    }
                                }
                            }
                            invalidate();
                            break;
                    }
                }
            } else {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchX = x;
                        touchY = y;
                        break;
                    case MotionEvent.ACTION_UP:
                        ArrayList<Note> chordWithCadence;
                        for (int a = 0; a < MusicStore.sheet.size(); a++) {
                            Note chordNote = MusicStore.sheet.get(a).get(0);
                            float startDrawX = chordNote.getDrawX()+DensityMetrics.getSpaceHeight() * 1.5f - distanceBetweenNotes/2;
                            float lastDrawX = chordNote.getDrawX()+DensityMetrics.getSpaceHeight() * 1.5f + distanceBetweenNotes/2;
                            if (x > startDrawX && x < lastDrawX) {
//                                Utils.Companion.getCadenceChordNumbers().add(a);
                                Note noteForCadence = MusicStore.sheet.get(a).get(0);
                                if (noteForCadence.getChord() == Note.Chord.T||(noteForCadence.getChord() == Note.Chord.D&&noteForCadence.getDegree()==5)||
                                        (noteForCadence.getChord() == Note.Chord.S&&noteForCadence.getDegree()==1)) {
                                    if (a != 0 && a != MusicStore.sheet.size() - 1) {
                                        Note.Chord previousNoteChord = MusicStore.sheet.get(a - 1).get(0).getChord();
                                        Note.Chord nextNoteChord = MusicStore.sheet.get(a + 1).get(0).getChord();
                                        if ((previousNoteChord == Note.Chord.T || previousNoteChord == Note.Chord.S)
                                                && nextNoteChord == Note.Chord.D) {
                                            noteForCadence.setChord(Note.Chord.K);
                                            invalidate();
                                        }
                                    } else if (a != 0 && a == MusicStore.sheet.size() - 1) {
                                        Note.Chord previousNoteChord = MusicStore.sheet.get(a - 1).get(0).getChord();
                                        if ((previousNoteChord == Note.Chord.T || previousNoteChord == Note.Chord.S)) {
                                            noteForCadence.setChord(Note.Chord.K);
                                            invalidate();
                                        }

                                    }
                                }
                                break;

                            }
                        }
                        break;
                }
            }
        }
        return result;
    }


    private void drawNoteHead(Note note, Canvas canvas) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(noteSound.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i("Media Playing:", "Player created!");
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();
            }
        });
        try {
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Gotta love gradle build times. Thanks Kotlin lmao
        if (note.getY() - DensityMetrics.Companion.getToolbarHeight() <= DensityMetrics.getSpaceHeight() * 2) {
            canvas.drawLine(note.getX() - 200, DensityMetrics.getSpaceHeight() + DensityMetrics.Companion.getToolbarHeight(), note.getX() + 200, DensityMetrics.getSpaceHeight() + DensityMetrics.Companion.getToolbarHeight(), paint);
        }
        if (note.getY() - DensityMetrics.Companion.getToolbarHeight() >= DensityMetrics.getSpaceHeight() * 6) {
            canvas.drawLine(note.getX() - 200, DensityMetrics.getSpaceHeight() * 7 + DensityMetrics.Companion.getToolbarHeight(), note.getX() + 200, DensityMetrics.getSpaceHeight() * 7 + DensityMetrics.Companion.getToolbarHeight(), paint);
        }
        //canvas.drawOval(note.x - NOTE_WIDTH, note.y - DensityMetrics.spaceHeight / 2, note.x + NOTE_WIDTH, note.y + DensityMetrics.spaceHeight / 2, paint);
        Bitmap headBmap;
        if (note.getRhythm() == 2.0) {
            headBmap = NoteBitmap.hnh;

        } else {
            headBmap = NoteBitmap.qnh;
        }


        int left = (int) (note.getX() - DensityMetrics.getSpaceHeight() * 1.697 / 2);
        canvas.drawBitmap(Bitmap.createScaledBitmap(headBmap, (int) (DensityMetrics.getSpaceHeight() * 1.697), (int) DensityMetrics.getSpaceHeight(), true), left, note.getY() - DensityMetrics.getSpaceHeight() / 2, paint);
        if (LastRhythm.dot) {
            //TODO: I don't want these hardcoded obviously
            canvas.drawCircle(note.getX() + 20, note.getY() + 20, 5f, paint);
            LastRhythm.dot = false;
        }
        if ((accidental == 1 && note.getName() != Note.NoteName.b && note.getName() != Note.NoteName.e) || note.getAccidental() == 1) {
            Drawable vd = ContextCompat.getDrawable(getContext(), R.drawable.sharp);
            Bitmap b = NoteBitmap.getBitmap(vd);
            canvas.drawBitmap(Bitmap.createScaledBitmap(b, (int) (NOTE_HEIGHT * 3 / 2), ((int) NOTE_HEIGHT) * 3, true), note.getX() - NOTE_WIDTH * 2, note.getY() - NOTE_HEIGHT * 3 / 2, paint);
        } else if (accidental == -1 && note.getName() != Note.NoteName.f && note.getName() != Note.NoteName.c) {
            Drawable vd = ContextCompat.getDrawable(getContext(), R.drawable.flat);
            Bitmap b = NoteBitmap.getBitmap(vd);
            canvas.drawBitmap(Bitmap.createScaledBitmap(b, (int) (NOTE_HEIGHT * 1.35), ((int) NOTE_HEIGHT) * 3, true), note.getX() - NOTE_WIDTH * 2, note.getY() - NOTE_HEIGHT * 3 / 2, paint);
        }

        accidental = 0;
    }


    class mListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent ev1, MotionEvent ev2, float velX, float velY) {
            if (velY > 5000) {
                accidental = -1;
            }
            if (velY < -5000) {
                accidental = 1;
            }
            if (velX < -5000) {
                LastRhythm.dot = true;
            }

            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.d(DEBUG_TAG, "onDown: " + e.toString());
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.d(DEBUG_TAG, "onLongPress: " + e.toString());
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            Log.d(DEBUG_TAG, "onShowPress: " + e.toString());
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(DEBUG_TAG, "onSingleTapUp: " + e.toString());
            return true;
        }

    }

}
