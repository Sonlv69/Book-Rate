package com.kiluss.bookrate.activity

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.kiluss.bookrate.R
import com.kiluss.bookrate.data.model.LoginResponse
import com.kiluss.bookrate.data.model.MyBookState
import com.kiluss.bookrate.databinding.ActivityMainBinding
import com.kiluss.bookrate.network.api.BookService
import com.kiluss.bookrate.network.api.RetrofitClient
import com.kiluss.bookrate.viewmodel.MainActivityViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var navFragment: NavController
    private var backPressPreviousState: Boolean = false
    private lateinit var binding: ActivityMainBinding
    private var bookNumber: Int = 0
    private val viewModel: MainActivityViewModel by viewModels {
        MainActivityViewModel.ViewModelFactory(
            this
        )
    }
    private lateinit var apiAuthorized: BookService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Initialize the bottom navigation view
        //create bottom navigation view object
        navFragment = findNavController(R.id.navFragment)
        binding.bottomNavBar.setupWithNavController(navFragment)
        navFragment.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.homeFragment) {
                supportActionBar?.show()
            } else {
                supportActionBar?.hide()
            }
        }
        apiAuthorized = RetrofitClient.getInstance(this).getClientAuthorized(getLoginResponse(this).token.toString())
            .create(BookService::class.java)
        setUpViewModel()
        viewModel.getMyBookSize(this)
    }

    private fun setUpViewModel() {
        viewModel.notification.observe(this, getNotification)
    }

    private val getNotification = Observer<String> {

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)

        val item = menu?.findItem(R.id.action_search)
        val searchView = item?.actionView as SearchView

        // search queryTextChange Listener
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
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

    override fun onResume() {
        super.onResume()
        viewModel.getMyBookSize(this)
    }

    override fun onBackPressed() {
        val id: Int? = navFragment.currentDestination?.id
        if (id == R.id.homeFragment && !backPressPreviousState) {
            backPressPreviousState = true
            Toast.makeText(this, "Press one more time to exit", Toast.LENGTH_SHORT).show()
            Handler().postDelayed({
                backPressPreviousState = false
            }, 3000)
        } else if (id != R.id.homeFragment) {
            super.onBackPressed()
            backPressPreviousState = false
        } else if (id == R.id.homeFragment && backPressPreviousState) {
            super.onBackPressed()
        }
    }

    private fun getLoginResponse(context: Context) : LoginResponse {
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.saved_login_account_key),
            Context.MODE_PRIVATE
        )
        val gson = Gson()
        val json: String? = sharedPref.getString(context.getString(R.string.saved_login_account_key), "")
        return gson.fromJson(json, LoginResponse::class.java)
    }
}
