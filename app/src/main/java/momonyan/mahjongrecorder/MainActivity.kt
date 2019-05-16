package momonyan.mahjongrecorder

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import android.widget.SearchView
import kotlinx.android.synthetic.main.activity_main.*
import momonyan.mahjongrecorder.datalist.ItemAdapter
import momonyan.mahjongrecorder.datalist.ItemDataClass
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    private var mDataList: ArrayList<ItemDataClass> = ArrayList()

    private lateinit var adapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //検索
        searchView.setIconifiedByDefault(false)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?): Boolean {
                // 検索キーが押下された
                Log.d("Search", "submit text: $text")
                return false
            }

            override fun onQueryTextChange(text: String?): Boolean {
                // テキストが変更された
                Log.d("Search", "change text: $text")
                searchResulut(text!!)
                return false
            }

        })
        searchView.queryHint = "検索文字を入力して下さい。"

        for (i in 0 until 10) {
            val pointList: MutableList<Int> =
                mutableListOf(
                    (Random.nextInt(600) - 50) * 100,
                    (Random.nextInt(450) - 100) * 100,
                    (Random.nextInt(200) - 100) * 100,
                    (Random.nextInt(100) - 100) * 100
                )
            pointList.sortDescending()
            val resultList: MutableList<Double> =
                mutableListOf(
                    (pointList[0] - 30000) / 1000.0,
                    (pointList[1] - 30000) / 1000.0,
                    (pointList[2] - 30000) / 1000.0,
                    (pointList[3] - 30000) / 1000.0
                )
            mDataList.add(
                ItemDataClass(
                    mutableListOf("A", "B", "C", "D"),
                    pointList,
                    resultList
                )
            )
        }

        adapter = ItemAdapter(mDataList)
        dataRecyclerView.adapter = adapter
        dataRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuPlayerData -> {
                //TODO プレイヤー個人の成績を出力するページへ（グラフとかあるといいかもしれぬ）
                Log.d("Menu",item.itemId.toString())
                return true
            }
            R.id.menuResult -> {
                //TODO 存在するプレイヤーの成績を簡易出力
                Log.d("Menu",item.itemId.toString())
                return true
            }
            R.id.menuPrivacy -> {
                //TODO プライバシーポリシーに飛ばす
                Log.d("Menu",item.itemId.toString())
                return true
            }
            R.id.menuReview -> {
                //TODO レビューへ飛ばす
                Log.d("Menu",item.itemId.toString())
                return true
            }
        }
        return true
    }

    fun searchResulut(text: String) {
        val adapter = adapter
        if (text != "") {
            adapter.mValue = ArrayList(mDataList.filter { it.dName.contains(text) })
        } else {
            adapter.mValue = mDataList
        }
        adapter.notifyDataSetChanged()
    }

}

