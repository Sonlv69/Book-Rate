package com.kiluss.bookrate.viewmodel

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kiluss.bookrate.R

class MainActivityViewModel(context: Context) : ViewModel() {
    class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
                return MainActivityViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
    private val _notification: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    internal val notification: LiveData<String> = _notification

    init {

    }

    internal fun setNotification(context: Context, numberNotify: Int) {
        val navBottomView = (context as AppCompatActivity).findViewById<BottomNavigationView>(R.id.bottomNavBar)
        val badgeDrawable: BadgeDrawable? = navBottomView.getBadge(R.id.accountFollowingFragment)
        if (badgeDrawable == null) {
            navBottomView.getOrCreateBadge(R.id.accountFollowingFragment).number = numberNotify
        } else {
            val previousValue = badgeDrawable.number
            badgeDrawable.number = numberNotify
        }
    }
}