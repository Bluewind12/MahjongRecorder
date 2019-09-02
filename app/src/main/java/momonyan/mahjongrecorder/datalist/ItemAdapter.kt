package momonyan.mahjongrecorder.datalist

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import momonyan.mahjongrecorder.MainActivity
import momonyan.mahjongrecorder.R

class ItemAdapter(var mValue: ArrayList<ItemDataClass>, private val activity: MainActivity?) :
    androidx.recyclerview.widget.RecyclerView.Adapter<ItemHolder>() {
    //作成
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ItemHolder {
        val hView = LayoutInflater.from(p0.context).inflate(R.layout.card_main, p0, false)
        return ItemHolder(hView)
    }

    //内部
    override fun onBindViewHolder(holder: ItemHolder, p1: Int) {
        val item = mValue[p1]
        //点数など
        for (i in 0 until 4) {
            holder.vNameTextViews[i].text = item.dName[i]
            holder.vPointTextViews[i].text = String.format("%d点", item.dPoint[i])
            if (item.dPoint[i] > 0) {
                holder.vPointTextViews[i].setTextColor(Color.BLACK)
            } else {
                holder.vPointTextViews[i].setTextColor(Color.RED)
            }
            if (item.dResult[i] >= 0) {
                holder.vResultTextViews[i].setTextColor(Color.BLACK)
                holder.vResultTextViews[i].text = String.format("%.2f", item.dResult[i])

            } else {
                holder.vResultTextViews[i].setTextColor(Color.RED)
                holder.vResultTextViews[i].text = String.format("▲%.2f", item.dResult[i] * -1)
            }
        }
        holder.vDateTextViews.text = item.dDate
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