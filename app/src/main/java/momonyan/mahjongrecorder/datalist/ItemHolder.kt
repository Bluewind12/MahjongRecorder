package momonyan.mahjongrecorder.datalist

import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.card_main.view.*

class ItemHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
    val vNameTextViews: ArrayList<TextView> =
        arrayListOf(view.nameTextView1, view.nameTextView2, view.nameTextView3, view.nameTextView4)

    val vPointTextViews: ArrayList<TextView> =
        arrayListOf(view.pointTextView1, view.pointTextView2, view.pointTextView3, view.pointTextView4)

    val vResultTextViews: ArrayList<TextView> = arrayListOf(
        view.resultPointTextView1,
        view.resultPointTextView2,
        view.resultPointTextView3,
        view.resultPointTextView4
    )
    val vDateTextViews: TextView = view.dateTextView
    val vCardView: androidx.cardview.widget.CardView = view.cardView

}