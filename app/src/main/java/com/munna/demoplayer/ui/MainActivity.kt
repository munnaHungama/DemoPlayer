package com.munna.demoplayer.ui

import android.app.PictureInPictureParams
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.View
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import com.munna.demoplayer.R
import com.munna.demoplayer.databinding.ActivityMainBinding
import com.munna.demoplayer.playermanager.PlayerManager

class MainActivity : AppCompatActivity(), ShortsFragment.UpdateUiForPIP {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 2
            override fun createFragment(position: Int): Fragment =
                if (position == 0) ShortsFragment() else DownloadsFragment()
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            tab.text = if (pos == 0) getString(R.string.tab_shorts) else getString(R.string.tab_downloads)
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (binding.tabLayout.selectedTabPosition != 0)
                    PlayerManager.getPlayer().pause()
                else
                    PlayerManager.getPlayer().play()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        // enter PIP when user leaves
        if (binding.tabLayout.selectedTabPosition == 0)
        if (Build.VERSION.SDK_INT >= 26) {
            val params = PictureInPictureParams.Builder()
                .setAspectRatio(Rational(9, 16))
                .build()
            enterPictureInPictureMode(params)
        }
    }


    override fun updateUiForPIP(isPIP: Boolean) {
        binding.tabLayout.visibility = if (isPIP) View.GONE else View.VISIBLE
        if (isPIP)
            supportActionBar?.hide()
        else
            supportActionBar?.show()
    }
}