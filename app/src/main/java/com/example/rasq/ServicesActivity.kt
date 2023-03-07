package com.example.rasq

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.rasq.databinding.ActivityServicesBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
class ServicesActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityServicesBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        replaceFragment(HomeFragment.newInstance("",""))
        binding = ActivityServicesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bttmMenuNav: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bttmMenuNav.setSelectedItemId(R.id.homeMenu)
        binding.bottomNavigationView.setOnItemSelectedListener {menuItem ->
            when (menuItem.itemId) {
                R.id.homeMenu -> {

                    replaceFragment(HomeFragment.newInstance("",""))
                    true
                }
                R.id.eventMenu -> {

                    replaceFragment(EventFragment.newInstance("",""))
                    true
                }
                R.id.quoteMenu -> {
                    replaceFragment(QuoteFragment.newInstance("",""))
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(pFragment: Fragment)
    {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentFrameLayout, pFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

}