package com.seversion.roomplus.ui.adapters

import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seversion.roomplus.R
import com.seversion.roomplus.data.models.Location
import kotlinx.android.synthetic.main.list_item_location.view.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onLongClick

/**
 * Created by Daniel on 2016-05-28.
 */

class LocationAdapter(private val selectionListener: SelectionListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        val TYPE_EMPTY = 0
        val TYPE_LOCATION = 1
    }

    var locations: List<Location> = emptyList()

    private var selectedIndex = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        val view: View
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            TYPE_EMPTY -> {
                view = inflater.inflate(R.layout.list_item_empty_state, parent, false)
                return EmptyStateHolder(view)
            }
            TYPE_LOCATION -> {
                view = inflater.inflate(R.layout.list_item_location, parent, false)
                return LocationViewHolder(view)
            }
        }

        return null
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (holder) {
            is LocationViewHolder -> {
                val location = locations[position]
                holder.bind(location, isSelected(location))
                holder.itemView.apply {
                    onClick { toggle(location) }
                    onLongClick {
                        delete(location)
                        true
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        if (locations.isEmpty()) {
            return 1
        } else {
            return locations.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (locations.isEmpty()) {
            return TYPE_EMPTY
        } else {
            return TYPE_LOCATION
        }
    }

    fun isSelected(location: Location): Boolean = locations.indexOf(location) == selectedIndex

    fun toggle(location: Location) {
        val oldIndex = selectedIndex
        selectedIndex = locations.indexOf(location)
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

    private class EmptyStateHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface SelectionListener {
        fun onSelect(location: Location): Boolean
        fun onDeselect()
        fun onDelete(location: Location)
    }

    private class LocationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(location: Location, selected: Boolean) {
            with(itemView) {
                name.text = location.name
                learningText.visibility = if (selected) View.VISIBLE else View.GONE
                progress.visibility = if (selected) View.VISIBLE else View.GONE
                if (selected) {
                    val drawable = AnimatedVectorDrawableCompat.create(itemView.context, R.drawable.animated_wifi)
                    progress.setImageDrawable(drawable)
                    drawable?.start()
                } else {
                    progress.setImageDrawable(null)
                }
            }
        }
    }

}
