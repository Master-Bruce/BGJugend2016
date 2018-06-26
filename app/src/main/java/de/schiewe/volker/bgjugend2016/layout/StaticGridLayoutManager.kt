package de.schiewe.volker.bgjugend2016.layout

import android.content.Context
import android.support.v7.widget.GridLayoutManager

class StaticGridLayoutManager(context: Context, spanCount:Int): GridLayoutManager(context, spanCount){
    override fun canScrollVertically(): Boolean {
        return false
    }
}