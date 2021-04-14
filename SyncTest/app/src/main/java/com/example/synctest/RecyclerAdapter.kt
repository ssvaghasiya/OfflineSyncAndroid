package com.example.synctest

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(mContext: Context) :
    RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>() {

    private var data = mutableListOf<Contact>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_home, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.Name?.text = data[position].name
        var sync_status = data[position].syncStatus
        if (sync_status == DbContract().SYNC_STATUS_OK) {
            holder.Sync_Status?.setImageResource(R.drawable.checked)
        } else {
            holder.Sync_Status?.setImageResource(R.drawable.sync)
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var Sync_Status: ImageView? = itemView.findViewById(R.id.imageView)
        var Name: TextView? = itemView.findViewById(R.id.textView)
    }

    fun addAll(data: MutableList<Contact>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    fun add(data: Contact) {
        this.data.add(data)
        notifyDataSetChanged()
    }

    fun clear() {
        this.data.clear()
        notifyDataSetChanged()
    }
}