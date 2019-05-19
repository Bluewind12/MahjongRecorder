package momonyan.mahjongrecorder.datalist

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import momonyan.mahjongrecorder.MainActivity
import momonyan.mahjongrecorder.R

class ItemAdapter(var mValue: ArrayList<ItemDataClass>, private val activity: MainActivity?) :
    RecyclerView.Adapter<ItemHolder>() {
    //作成
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ItemHolder {
        val hView = LayoutInflater.from(p0.context).inflate(R.layout.card_main, p0, false)
        return ItemHolder(hView)
    }

    //内部
    override fun onBindViewHolder(holder: ItemHolder, p1: Int) {
        val item = mValue[p1]
        Log.e("Check", "--------------------------")
        Log.d("Check", "$p1:${item.dName} - ${item.dPoint},${item.dResult}")
        Log.e("Check", "--------------------------")

        for (i in 0 until 4) {
            holder.vNames[i].text = item.dName[i]
            holder.vPoint[i].text = "${item.dPoint[i]}点"
            if (item.dPoint[i] > 0) {
                holder.vPoint[i].setTextColor(Color.BLACK)
            } else {
                holder.vPoint[i].setTextColor(Color.RED)
            }
            if (item.dResult[i] >= 0) {
                holder.vResult[i].setTextColor(Color.BLACK)
                holder.vResult[i].text = "${item.dResult[i]}"

            } else {
                holder.vResult[i].setTextColor(Color.RED)
                holder.vResult[i].text = "▲${item.dResult[i] * -1}"
            }
        }
        if (activity != null) {
            holder.vCardView.setOnClickListener {
                activity.createChangeDialog(item.id, item.dName, item.dPoint, item.dDate)
            }
        }
    }

    //カウント
    override fun getItemCount(): Int {
        return mValue.size
    }

}



