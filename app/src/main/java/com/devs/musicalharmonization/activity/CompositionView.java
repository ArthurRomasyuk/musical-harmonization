package com.devs.musicalharmonization.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.devs.musicalharmonization.R;
import com.devs.musicalharmonization.singletons.DensityMetrics;
import com.devs.musicalharmonization.singletons.MusicStore;
import com.devs.musicalharmonization.Note;
import com.devs.musicalharmonization.NoteBitmap;

import java.util.ArrayList;

/**
 *  2/14/2016.
 */
public class CompositionView extends View {
    private static final String TAG = "CompositionView";
    Paint paint = new Paint();
    Context conx;
    public final float NOTE_WIDTH = 150;
    public final float NOTE_HEIGHT = 100;
    private float STAFF_WIDTH = MusicStore.sheet.size() * 600 + 500;
    public CompositionView(Context con){
        super(con);
        conx = con;
        setFocusable(true);
        setFocusableInTouchMode(true);

        paint.setColor(Color.BLACK);
    }
    public CompositionView(Context con, AttributeSet attrs){
        super(con, attrs);
        conx = con;
        setFocusable(true);
        setFocusableInTouchMode(true);

        paint.setColor(Color.BLACK);
    }
    public CompositionView(Context con, AttributeSet attrs, int defStyle){
        super(con, attrs, defStyle);
        conx = con;
        setFocusable(true);
        setFocusableInTouchMode(true);

        paint.setColor(Color.BLACK);
    }
    @Override
   // @TargetApi(21)
    public void onDraw(Canvas c){
        paint.setStrokeWidth(10);
        for (int i = 2; i < 7; i++){
            c.drawLine(20, DensityMetrics.getSpaceHeight() * i + DensityMetrics.Companion.getToolbarHeight(),STAFF_WIDTH, DensityMetrics.getSpaceHeight() * i + DensityMetrics.Companion.getToolbarHeight(), paint);
        }
        float drawX = 320;
        for (ArrayList<Note> chord : MusicStore.sheet){
            for (Note note : chord){
                int noteHeadID = 0;
                if (note.getRhythm() == 2){
                    noteHeadID = R.drawable.half_note_head;

                }else{
                    noteHeadID = R.drawable.quarter_note_head;
                }

                Drawable noteHead =  getResources().getDrawable(noteHeadID);
                Bitmap nh = NoteBitmap.getBitmap(noteHead);
                c.drawBitmap(Bitmap.createScaledBitmap(nh, (int) (DensityMetrics.getSpaceHeight() * 1.697), (int) DensityMetrics.getSpaceHeight(), true), (int) drawX , note.getY() - DensityMetrics.getSpaceHeight() / 2, paint);

            }
            drawX += 600;
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension((int) STAFF_WIDTH, 1500);

    }

}
