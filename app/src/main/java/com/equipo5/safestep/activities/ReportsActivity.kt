package com.equipo5.safestep.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.equipo5.safestep.R
import com.equipo5.safestep.fragments.ReportsListFragment
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_reports.*

class ReportsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        setSupportActionBar(toolbarReportsFragment)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_close_24)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val fragment = ReportsListFragment()
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            .replace(R.id.reportsFragmentContainer , fragment)
        transaction.commit()

        showReports(getString(R.string.last_reports_title))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    showReports(tab.text.toString())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
    }

    fun showReports (action: String) {
        val fragment = ReportsListFragment()

        val bundle = Bundle()

        bundle.putString("operation", action)

        fragment.arguments = bundle

        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            .replace(R.id.reportsFragmentContainer , fragment)

        transaction.commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}