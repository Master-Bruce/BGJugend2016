package de.schiewe.volker.bgjugend2016.layout

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager

class StaticGridLayoutManager(context: Context, spanCount:Int): GridLayoutManager(context, spanCount){
    override fun canScrollVertically(): Boolean {
        return false
    }
}