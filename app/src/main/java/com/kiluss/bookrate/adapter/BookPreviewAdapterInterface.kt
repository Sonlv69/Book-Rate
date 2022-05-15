package com.kiluss.bookrate.adapter

import android.view.View

interface BookPreviewAdapterInterface {
    fun onItemViewClick(pos : Int)
    fun onBookStateClick(pos : Int, view: View)
}