package com.example.emanate

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class ListAdapter(
    private val context: Context,
    var nodeAlerts: List<String> = ArrayList(),
    val description: MutableList<String> = ArrayList(),
    val timeOfNotificationTrigger: MutableList<String> = ArrayList(),
) : BaseAdapter() {

    // Returns total number of items in the list
    override fun getCount(): Int {
        return description.size
    }

    // Returns list item at the position it's in
    override fun getItem(position: Int): Any {
        return position
    }

    // Returns the item id
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View? = convertView
        val viewHolder: ViewHolder

        // Inflating the layout for each list row
        if (view == null) {
            viewHolder = ViewHolder()
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
                    as LayoutInflater
            view = inflater.inflate(R.layout.list_item, parent, false)

            //Inserting the current item into the textView of each row
            viewHolder.nodeAlerts = view.findViewById(R.id.node_alert)
            viewHolder.description = view.findViewById(R.id.description)
            viewHolder.timeOfNotificationTrigger = view
                .findViewById(
                    R.id.time_of_notification_trigger
                )
            view!!.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        // Getting current item to be displayed
        viewHolder.nodeAlerts?.text = nodeAlerts[position]
        viewHolder.description?.text = description[position]
        viewHolder.timeOfNotificationTrigger?.text = timeOfNotificationTrigger[position]

        // Returns view for the current row
        return view
    }

    //ViewHolder class will hold the views for the list items
    private inner class ViewHolder {
        var nodeAlerts: TextView? = null
        var description: TextView? = null
        var timeOfNotificationTrigger: TextView? = null
    }
}