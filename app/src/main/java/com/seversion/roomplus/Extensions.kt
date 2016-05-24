package com.seversion.roomplus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by Daniel on 2016-04-18.
 */

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}
