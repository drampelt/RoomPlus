package com.seversion.found.ui.adapters

import android.app.Activity
import com.hannesdorfmann.adapterdelegates2.ListDelegationAdapter
import com.seversion.found.data.models.Location
import com.seversion.found.ui.adapters.delegates.LocationAdapterDelegate

/**
 * Created by Daniel on 2016-04-18.
 */

class LocationAdapter(val selectionListener: SelectionListener) : ListDelegationAdapter<List<Location>>() {

    private var selectedIndex = -1

    init {
        delegatesManager.addDelegate(LocationAdapterDelegate(this))
    }

    fun isSelected(location: Location): Boolean {
        return items.indexOf(location) == selectedIndex
    }

    fun toggle(location: Location) {
        val oldIndex = selectedIndex
        selectedIndex = items.indexOf(location)
        if (oldIndex == selectedIndex && selectedIndex >= 0) {
            selectedIndex = -1
            selectionListener.onDeselect()
        } else {
            if (!selectionListener.onSelect(location)) {
                selectedIndex = oldIndex
                return
            }
            notifyItemChanged(selectedIndex)
        }
        notifyItemChanged(oldIndex)
    }

    fun deselect() {
        if (selectedIndex >= 0) {
            notifyItemChanged(selectedIndex)
            selectedIndex = -1
            selectionListener.onDeselect()
        }
    }

    fun delete(location: Location) {
        selectionListener.onDelete(location)
    }

    interface SelectionListener {
        fun onSelect(location: Location): Boolean
        fun onDeselect()
        fun onDelete(location: Location)
    }
}
