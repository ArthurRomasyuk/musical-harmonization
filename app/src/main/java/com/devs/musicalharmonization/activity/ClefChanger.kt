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
import android.widget.ImageButton
import com.devs.musicalharmonization.singletons.ClefSetting
import com.devs.musicalharmonization.R
import com.devs.musicalharmonization.midi.unused.Clef

class ClefChanger : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clef_changer)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)


        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        //Clef Buttons
        val treble = findViewById<ImageButton>(R.id.treble_clef_button)
        treble.setOnClickListener { ClefSetting.clef = Clef.TREBLE }

        val alto = findViewById<ImageButton>(R.id.alto_clef_button)
        alto.setOnClickListener { ClefSetting.clef = Clef.ALTO }
        val bass = findViewById<ImageButton>(R.id.bass_clef_button)
        bass.setOnClickListener { ClefSetting.clef = Clef.BASS }
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
        menuInflater.inflate(R.menu.clef_changer, menu)
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
