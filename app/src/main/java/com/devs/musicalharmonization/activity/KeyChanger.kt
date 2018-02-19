package com.devs.musicalharmonization.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import com.devs.musicalharmonization.Note
import com.devs.musicalharmonization.R
import com.devs.musicalharmonization.singletons.Key
import com.devs.musicalharmonization.singletons.Utils

class KeyChanger : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val sendBack = Intent(this, Composition::class.java)
        setContentView(R.layout.activity_key_changer)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)


        val C = findViewById<Button>(R.id.key_c)
        C.setOnClickListener {
            Key.COUNT = 0
            Key.SHARP = true
            Key.startNote = Note.NoteName.c
            startActivity(sendBack)
        }

        //FLATS
        val F = findViewById<Button>(R.id.key_f)
        F.setOnClickListener {
            Key.COUNT = 1
            Key.SHARP = false
            Key.startNote = Note.NoteName.f
            startActivity(sendBack)
        }

        val Bf = findViewById<Button>(R.id.key_bf)
        Bf.setOnClickListener {
            Key.COUNT = 2
            Key.SHARP = false
            Key.startNote = Note.NoteName.`as`
            startActivity(sendBack)
        }
        val Ef = findViewById<Button>(R.id.key_ef)
        Ef.setOnClickListener {
            Key.COUNT = 3
            Key.SHARP = false
            Key.startNote = Note.NoteName.ds
            startActivity(sendBack)
        }
        val Af = findViewById<Button>(R.id.key_af)
        Af.setOnClickListener {
            Key.COUNT = 4
            Key.SHARP = false
            Key.startNote = Note.NoteName.gs
            startActivity(sendBack)
        }
        val Df = findViewById<Button>(R.id.key_df)
        Df.setOnClickListener {
            Key.COUNT = 5
            Key.SHARP = false
            Key.startNote = Note.NoteName.cs
            startActivity(sendBack)
        }
        val Gf = findViewById<Button>(R.id.key_gf)
        Gf.setOnClickListener {
            Key.COUNT = 6
            Key.SHARP = false
            Key.startNote = Note.NoteName.fs
            startActivity(sendBack)
        }
        //SHARPS
        val G = findViewById<Button>(R.id.key_g)
        G.setOnClickListener {
            Key.COUNT = 1
            Key.SHARP = true
            Key.startNote = Note.NoteName.g
            startActivity(sendBack)
        }

        val D = findViewById<Button>(R.id.key_d)
        D.setOnClickListener {
            Key.COUNT = 2
            Key.SHARP = true
            Key.startNote = Note.NoteName.d
            startActivity(sendBack)
        }
        val A = findViewById<Button>(R.id.key_a)
        A.setOnClickListener {
            Key.COUNT = 3
            Key.SHARP = true
            Key.startNote = Note.NoteName.a
            startActivity(sendBack)
        }
        val E = findViewById<Button>(R.id.key_e)
        E.setOnClickListener {
            Key.COUNT = 4
            Key.SHARP = true
            Key.startNote = Note.NoteName.e
            startActivity(sendBack)
        }
        val B = findViewById<Button>(R.id.key_b)
        B.setOnClickListener {
            Key.COUNT = 5
            Key.SHARP = true
            Key.startNote = Note.NoteName.b
            startActivity(sendBack)
        }
        val Fs = findViewById<Button>(R.id.key_fs)
        Fs.setOnClickListener {
            Key.COUNT = 6
            Key.SHARP = true
            Key.startNote = Note.NoteName.fs
            startActivity(sendBack)
        }


        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        navigationView.menu.getItem(1).isChecked = true
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
        menuInflater.inflate(R.menu.key_changer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true
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
}
