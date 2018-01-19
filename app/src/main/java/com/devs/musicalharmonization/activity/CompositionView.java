package com.devs.musicalharmonization.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.devs.musicalharmonization.R;
import com.devs.musicalharmonization.midi.unused.Clef;
import com.devs.musicalharmonization.singletons.ClefSetting;
import com.devs.musicalharmonization.singletons.DensityMetrics;
import com.devs.musicalharmonization.singletons.LastRhythm;
import com.devs.musicalharmonization.singletons.MusicStore;
import com.devs.musicalharmonization.Note;
import com.devs.musicalharmonization.NoteBitmap;

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
    Paint paint = new Paint();
    Context conx;
    public final float NOTE_WIDTH = 150;
    public final float NOTE_HEIGHT = 100;
    int width;
    int height;
    private float STAFF_WIDTH;
    DisplayMetrics displayMetrics;
    WindowManager manager;

    public CompositionView(Context con) {
        super(con);
        conx = con;
        setFocusable(true);
        setFocusableInTouchMode(true);
        mDetector = new GestureDetector(this.getContext(), new mListener());
        displayMetrics = new DisplayMetrics();
        manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        STAFF_WIDTH = MusicStore.sheet.size() * 600 + width;
        paint.setColor(Color.BLACK);
    }

    public CompositionView(Context con, AttributeSet attrs) {
        super(con, attrs);
        conx = con;
        setFocusable(true);
        setFocusableInTouchMode(true);
        mDetector = new GestureDetector(this.getContext(), new mListener());
        displayMetrics = new DisplayMetrics();
        manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        STAFF_WIDTH = MusicStore.sheet.size() * 600 + width;
        paint.setColor(Color.BLACK);
    }

    public CompositionView(Context con, AttributeSet attrs, int defStyle) {
        super(con, attrs, defStyle);
        conx = con;
        setFocusable(true);
        setFocusableInTouchMode(true);
        mDetector = new GestureDetector(this.getContext(), new mListener());
        displayMetrics = new DisplayMetrics();
        manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        STAFF_WIDTH = MusicStore.sheet.size() * 600 + width;
        paint.setColor(Color.BLACK);
    }



    @Override
    // @TargetApi(21)
    public void onDraw(Canvas c) {
        manager.getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        STAFF_WIDTH = MusicStore.sheet.size() * 600 + width;
        requestLayout();
        DensityMetrics.setSpaceHeight((height - DensityMetrics.Companion.getToolbarHeight()) / 16);
        paint.setStrokeWidth(10);
        for (int i = 2; i < 13; i++) {
            if(i!=7) {
                c.drawLine(0, DensityMetrics.getSpaceHeight() * i + DensityMetrics.Companion.getToolbarHeight(), STAFF_WIDTH, DensityMetrics.getSpaceHeight() * i +
                        DensityMetrics.Companion.getToolbarHeight(), paint);
            }
        }

        float altoClefTop = DensityMetrics.Companion.getToolbarHeight() + 2 * DensityMetrics.getSpaceHeight();
        float trebleClefTop = DensityMetrics.Companion.getToolbarHeight() + DensityMetrics.getSpaceHeight();
        float bassClefTop = DensityMetrics.Companion.getToolbarHeight() + 2* DensityMetrics.getSpaceHeight();
        float secondSheetBassClefTop = DensityMetrics.Companion.getToolbarHeight() + 8* DensityMetrics.getSpaceHeight();

        int altoClefHeight = 4 * (int) DensityMetrics.getSpaceHeight();
        int trebleClefHeight = 6 * (int) DensityMetrics.getSpaceHeight();
        int bassClefHeight = 3 * (int) DensityMetrics.getSpaceHeight();

        if (ClefSetting.clef == Clef.ALTO) {
            Bitmap clefBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.alto_clef);
            int scaledWidth = clefBitmap.getScaledWidth(DisplayMetrics.DENSITY_DEFAULT);
            int scaledHeight = clefBitmap.getScaledHeight(DisplayMetrics.DENSITY_DEFAULT);
            int dstWidth = altoClefHeight*scaledWidth/scaledHeight;
            Bitmap scaledClefBitmap = Bitmap.createScaledBitmap(clefBitmap, dstWidth,
                    altoClefHeight, true);
            c.drawBitmap(scaledClefBitmap, 20f, altoClefTop, paint);
        } else if (ClefSetting.clef == Clef.TREBLE) {
            Bitmap clefBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.treble_clef);
            int scaledWidth = clefBitmap.getScaledWidth(DisplayMetrics.DENSITY_DEFAULT);
            int scaledHeight = clefBitmap.getScaledHeight(DisplayMetrics.DENSITY_DEFAULT);
            int dstWidth = trebleClefHeight*scaledWidth/scaledHeight;
            Bitmap scaledClefBitmap = Bitmap.createScaledBitmap(clefBitmap, dstWidth, trebleClefHeight, true);
            c.drawBitmap(scaledClefBitmap, 1f, trebleClefTop, paint);
        } else if (ClefSetting.clef == Clef.BASS) {
            Bitmap clefBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bass_clef);
            int scaledWidth = clefBitmap.getScaledWidth(DisplayMetrics.DENSITY_DEFAULT);
            int scaledHeight = clefBitmap.getScaledHeight(DisplayMetrics.DENSITY_DEFAULT);
            int dstWidth = bassClefHeight*scaledWidth/scaledHeight;
            Bitmap scaledClefBitmap = Bitmap.createScaledBitmap(clefBitmap, dstWidth, bassClefHeight, true);
            c.drawBitmap(scaledClefBitmap, 1f, bassClefTop, paint);
        }

        Bitmap bassClefBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bass_clef);
        int bassClefScaledWidth = bassClefBitmap.getScaledWidth(DisplayMetrics.DENSITY_DEFAULT);
        int bassClefScaledHeight = bassClefBitmap.getScaledHeight(DisplayMetrics.DENSITY_DEFAULT);
        int bassClefWidth = bassClefHeight*bassClefScaledWidth/bassClefScaledHeight;
        Bitmap scaledBassClefBitmap = Bitmap.createScaledBitmap(bassClefBitmap, bassClefWidth, bassClefHeight, true);
        c.drawBitmap(scaledBassClefBitmap, 1f, secondSheetBassClefTop, paint);

        float drawX = 320;
        for (ArrayList<Note> chord : MusicStore.sheet) {
            for (Note note : chord) {
                int noteHeadID = 0;
                if (note.getRhythm() == 2) {
                    noteHeadID = R.drawable.half_note_head;

                } else {
                    noteHeadID = R.drawable.quarter_note_head;
                }

                Drawable noteHead = getResources().getDrawable(noteHeadID);
                Bitmap nh = NoteBitmap.getBitmap(noteHead);
                c.drawBitmap(Bitmap.createScaledBitmap(nh, (int) (DensityMetrics.getSpaceHeight() * 1.697), (int) DensityMetrics.getSpaceHeight(), true), (int) drawX, note.getY() - DensityMetrics.getSpaceHeight() / 2, paint);

            }
            drawX += 600;
        }


        if (drawNote) {
            drawNoteHead(renderableNote, c);
            drawNote = false;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension((int) STAFF_WIDTH, height);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = false;
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
        if(x>(MusicStore.sheet.size() * 600) && y<(DensityMetrics.getSpaceHeight() * 8)) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchX = x;
                    touchY = y;
                    break;
                case MotionEvent.ACTION_UP:
                    drawNote = true;
                    invalidate();
                    renderableNote = new Note(touchX, touchY, accidental);
                    boolean renderNote = true;
                    for (Note note : MusicStore.activeNotes) {
                        if (renderableNote.getName() == note.getName() && renderableNote.getOctave() == note.getOctave()) {
                            renderNote = false;
                            Log.d("Note", "Dupe note skipped!");
                        }
                    }
                    if (renderNote) {
                        MusicStore.activeNotes.add(renderableNote);
                    }
                    //if (Settings.piano) {

                    nextInput();
//                    invalidate();


                    //}
                    break;
            }
        }
        return result;
    }

    private void drawNoteHead(Note note, Canvas canvas) {
        MediaPlayer mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(getContext(), Uri.parse("android.resource://com.devs.musicalharmonization/raw/" + note.getName().toString() + Integer.toString(note.getOctave())));
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

        //Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.alto_clef);
        //c.drawBitmap(Bitmap.createScaledBitmap(b, (int) ((4 * (int) DensityMetrics.spaceHeight) / 1.5), 4 * (int) DensityMetrics.spaceHeight, true), 20, altoClef, paint);

    }


    void nextInput() {
        new Thread(new Runnable() {
            public void run() {
                MusicStore.sheet.add(MusicStore.activeNotes);
                //   System.gc()
//                for (Note note: MusicStore.activeNotes) {
//                    MediaPlayer mediaPlayer = new MediaPlayer();
//                    try {
//                        mediaPlayer.setDataSource(getContext(), Uri.parse("android.resource://com.devs.musicalharmonization/raw/" + note.getName().toString() + Integer.toString(note.getOctave())));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                    Log.i("Media Playing:", "Player created!");
//                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                        @Override
//                        public void onPrepared(MediaPlayer mediaPlayer) {
//                            mediaPlayer.start();
//                        }
//                    });
//                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        @Override
//                        public void onCompletion(MediaPlayer mediaPlayer) {
//                            mediaPlayer.release();
//                        }
//                    });
//                    try {
//                        mediaPlayer.prepareAsync();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
                MusicStore.activeNotes = new ArrayList<Note>() ;
            }
        }).start();
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
