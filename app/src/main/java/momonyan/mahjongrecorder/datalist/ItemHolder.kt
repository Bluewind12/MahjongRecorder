package momonyan.mahjongrecorder.datalist

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.card_main.view.*

class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
    val vNames: ArrayList<TextView> =
        arrayListOf(view.nameTextView1, view.nameTextView2, view.nameTextView3, view.nameTextView4)

    val vPoint: ArrayList<TextView> =
        arrayListOf(view.pointTextView1, view.pointTextView2, view.pointTextView3, view.pointTextView4)

    val vResult: ArrayList<TextView> = arrayListOf(
        view.resultPointTextView1,
        view.resultPointTextView2,
        view.resultPointTextView3,
        view.resultPointTextView4
    )

}