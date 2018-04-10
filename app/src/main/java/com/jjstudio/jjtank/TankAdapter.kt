package com.jjstudio.jjtank

/**
 * Created by Charlie Jiang on 2/03/2018.
 */
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.tank_item_layout.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class TankAdapter(val tankList: ArrayList<Tank>, val listener: RecyclerViewClickListener) : RecyclerView.Adapter<TankAdapter.ViewHolder>(), AnkoLogger {


    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.tankTitle?.text = tankList[position].title
        holder?.tankStatus?.text = tankList[position].title
        holder?.connectButton?.setOnClickListener(holder)

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.tank_item_layout, parent, false)
        return ViewHolder(v, listener).listen { pos, type ->
            run {
                val tank = tankList[pos]
                info("click on")
            }
        }
    }


    override fun getItemCount(): Int {
        return tankList.size
    }

    class ViewHolder(itemView: View, listener: RecyclerViewClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val tankTitle = itemView.tankTitle
        val tankStatus = itemView.tankStatus
        val connectButton = itemView.connectButton
        val mListener: RecyclerViewClickListener = listener

        override fun onClick(view: View?) {
            mListener.onClick(view, adapterPosition)
//        info(tank.title)
        }


    }


    fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(getAdapterPosition(), getItemViewType())
        }
        return this
    }

}

interface RecyclerViewClickListener {
    fun onClick(view: View?, position: Int)
}
