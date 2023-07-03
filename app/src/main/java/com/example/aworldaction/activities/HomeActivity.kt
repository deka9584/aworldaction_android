package com.example.aworldaction.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ActionMode
import android.widget.LinearLayout
import com.example.aworldaction.R
import com.example.aworldaction.activities.fragments.AccountFragment
import com.example.aworldaction.activities.fragments.ListFragment
import com.example.aworldaction.settings.AppSettings

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val inProgressFragment = ListFragment.newInstance("inprogress")
        val favouritesFragment = ListFragment.newInstance("favourites")
        val completedFragment = ListFragment.newInstance("completed")
        val accountFragment = AccountFragment()

        val inprogressBtn: LinearLayout? = findViewById(R.id.inprogressBtn)
        val favouritesBtn: LinearLayout? = findViewById(R.id.favouritesBtn)
        val completedBtn: LinearLayout? = findViewById(R.id.completedBtn)
        val accountBtn: LinearLayout? = findViewById(R.id.accountBtn)

        inprogressBtn?.setOnClickListener {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, inProgressFragment)
                .commit()
        }

        favouritesBtn?.setOnClickListener {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, favouritesFragment)
                .commit()
        }

        completedBtn?.setOnClickListener {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, completedFragment)
                .commit()
        }

        accountBtn?.setOnClickListener {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, accountFragment)
                .commit()
        }

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, inProgressFragment)
            .commit()
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}