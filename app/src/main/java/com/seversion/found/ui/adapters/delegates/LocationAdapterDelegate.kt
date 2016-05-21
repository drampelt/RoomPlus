package com.seversion.found.ui.adapters.delegates

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates2.AbsListItemAdapterDelegate
import kotlinx.android.synthetic.main.list_item_location.view.*
import com.seversion.found.R
import com.seversion.found.data.models.Location
import com.seversion.found.inflate
import com.seversion.found.ui.adapters.LocationAdapter
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onLongClick

/**
 * Created by Daniel on 2016-04-18.
 */

class LocationAdapterDelegate(val locationAdapter: LocationAdapter) : AbsListItemAdapterDelegate<Location, Location, LocationAdapterDelegate.LocationViewHolder>() {
    override fun isForViewType(item: Location, items: MutableList<Location>?, position: Int): Boolean {
        return item is Location
    }

    override fun onCreateViewHolder(parent: ViewGroup): LocationViewHolder = LocationViewHolder(parent.inflate(R.layout.list_item_location))

    override fun onBindViewHolder(item: Location, viewHolder: LocationViewHolder) {
        val selected = locationAdapter.isSelected(item)
        viewHolder.bind(item, selected)
    }

    inner class LocationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(location: Location, selected: Boolean) {
            with(itemView) {
                name.text = location.name
                learningText.visibility = if (selected) View.VISIBLE else View.GONE
                progress.visibility = if (selected) View.VISIBLE else View.GONE

                onClick { locationAdapter.toggle(location) }
                onLongClick {
                    locationAdapter.delete(location)
                    return@onLongClick true
                }
            }
        }
    }
}
