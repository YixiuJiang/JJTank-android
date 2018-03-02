package com.jjstudio.jjtank

/**
 * Created by Charlie Jiang on 2/03/2018.
 */
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class TankAdapter(val tankList: ArrayList<Tank>): RecyclerView.Adapter<TankAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.tankTitle?.text = tankList[position].title
        holder?.tankStatus?.text = tankList[position].title

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.tank_item_layout, parent, false)
        return ViewHolder(v);
    }

    override fun getItemCount(): Int {
        return tankList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tankTitle = itemView.findViewById<TextView>(R.id.tankTitle)
        val tankStatus = itemView.findViewById<TextView>(R.id.tankStatus)

    }

}