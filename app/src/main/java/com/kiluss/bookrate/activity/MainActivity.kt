package com.kiluss.bookrate.activity

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.kiluss.bookrate.R
import com.kiluss.bookrate.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var navFragment: NavController
    private var backPressPreviousState : Boolean = false
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //Initialize the bottom navigation view
        //create bottom navigation view object
        navFragment = findNavController(R.id.navFragment)
        binding.bottomNavBar.setupWithNavController(navFragment)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)

        val item = menu?.findItem(R.id.action_search);
        val searchView = item?.actionView as SearchView

        // search queryTextChange Listener
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }
            override fun onQueryTextChange(query: String?): Boolean {
                Log.d("onQueryTextChange", "query: " + query)
                return true
            }
        })

        //Expand Collapse listener
        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                showToast("Action Collapse")
                return true
            }

            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                showToast("Action Expand")
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    private fun showToast(msg: String) {
        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        val id: Int = navFragment.currentDestination!!.id
        Log.e("fragment: ", id.toString())
        Log.e("HomeFragment: ", R.id.homeFragment.toString())
        if (id == R.id.homeFragment && !backPressPreviousState){
            backPressPreviousState = true
            Toast.makeText(this, "Press 1 more time to exit", Toast.LENGTH_SHORT).show()
            Handler().postDelayed({
                backPressPreviousState = false
            }, 3000)
        } else if (id != R.id.homeFragment){
            super.onBackPressed()
            backPressPreviousState = false
        } else if (id == R.id.homeFragment && backPressPreviousState) {
            super.onBackPressed()
        }
    }
}