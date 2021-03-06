package momonyan.mahjongrecorder.playerdatalist

import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import momonyan.mahjongrecorder.R

class CardListAdapter(var mValue: ArrayList<CardListDataClass>) :
    androidx.recyclerview.widget.RecyclerView.Adapter<CardListHolder>() {
    //作成
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CardListHolder {
        val hView = LayoutInflater.from(p0.context).inflate(R.layout.card_list, p0, false)
        return CardListHolder(hView)
    }

    //内部
    override fun onBindViewHolder(holder: CardListHolder, p1: Int) {
        val item = mValue[p1]
        //名前など
        holder.vNameTextViews.text = item.dName
        holder.vNameTextViews.paintFlags =
            holder.vNameTextViews.paintFlags or Paint.UNDERLINE_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG

        holder.vPointTextViews.text = item.dPoint
        holder.vResultTextViews.text = item.dResult

        for(i in 0 until 4) {
            holder.vRankTextViews[i].text =
                String.format("%d位　%d回/\t%.0f%%", i+1, item.dRank[i], (item.dRank[i] / item.dCount.toDouble()) * 100)
        }
    }

    //カウント
    override fun getItemCount(): Int {
        return mValue.size
    }

}