package momonyan.mahjongrecorder.playerdatalist

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.card_list.view.*

class CardListHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
    //名前
    val vNameTextViews: TextView = view.nameCardListTextView

    //順位
    val vRankTextViews: ArrayList<TextView> = arrayListOf(
        view.rankCardListTextView,
        view.rankCardListTextView2,
        view.rankCardListTextView3,
        view.rankCardListTextView4
    )

    //点数
    val vPointTextViews: TextView = view.pointCardListTextView
    //集計
    val vResultTextViews: TextView = view.resultCardListTextView

}