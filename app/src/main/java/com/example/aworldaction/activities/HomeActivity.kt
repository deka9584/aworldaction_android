package com.example.aworldaction.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.aworldaction.R
import com.example.aworldaction.activities.auth.RegisterActivity
import com.example.aworldaction.activities.fragments.AccountFragment
import com.example.aworldaction.activities.fragments.ListFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val inProgressFragment = ListFragment.newInstance("inprogress")
        val favouritesFragment = ListFragment.newInstance("favourites")
        val completedFragment = ListFragment.newInstance("completed")
        val accountFragment = AccountFragment()
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_inprogress -> {
                    switchFragment(inProgressFragment)
                    return@setOnItemSelectedListener true
                }
                R.id.menu_favourites -> {
                    switchFragment(favouritesFragment)
                    return@setOnItemSelectedListener true
                }
                R.id.menu_new_campaign -> {
                    openCampaignForm()
                    return@setOnItemSelectedListener false
                }
                R.id.menu_completed -> {
                    switchFragment(completedFragment)
                    return@setOnItemSelectedListener true
                }
                R.id.menu_profile -> {
                    switchFragment(accountFragment)
                    return@setOnItemSelectedListener true
                }
                else -> return@setOnItemSelectedListener false
            }
        }

        switchFragment(inProgressFragment)
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun openCampaignForm() {
        val intent = Intent(this, CreateCampaignActivity::class.java)
        startActivity(intent)
    }
}