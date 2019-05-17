package momonyan.mahjongrecorder

import android.arch.persistence.room.Room
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import com.facebook.stetho.Stetho
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.data_input_layout.view.*
import momonyan.mahjongrecorder.datalist.ItemAdapter
import momonyan.mahjongrecorder.datalist.ItemDataClass
import momonyan.mahjongrecorder.playerdatabase.PlayerAppDataBase
import momonyan.mahjongrecorder.playerdatabase.PlayerDB
import momonyan.mahjongrecorder.pointdatabase.PointAppDataBase
import momonyan.mahjongrecorder.pointdatabase.PointDB
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    private var mDataList: ArrayList<ItemDataClass> = ArrayList()

    private lateinit var adapter: ItemAdapter
    private lateinit var dialogView: View

    private lateinit var pointAppDataBase: PointAppDataBase
    private lateinit var playerAppDataBase: PlayerAppDataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //DataBase
        pointAppDataBase =
            Room.databaseBuilder(this, PointAppDataBase::class.java, "MainPoint.db")
                .build()

        playerAppDataBase =
            Room.databaseBuilder(this, PlayerAppDataBase::class.java, "MainPlayer.db")
                .build()

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
                searchResult(text!!)
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

            // 見るためよう（あとで消す）
            Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                    .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                    .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                    .build()
            )
        }

        adapter = ItemAdapter(mDataList)
        dataRecyclerView.adapter = adapter
        dataRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val names = arrayListOf("もも", "TEST", "蒼風")
        val adapter = ArrayAdapter<String>(
            this, android.R.layout.simple_dropdown_item_1line, names
        )

        floatingActionButton.setOnClickListener {
            dialogView = layoutInflater.inflate(R.layout.data_input_layout, null)

            dialogView.nameEditText1.setAdapter<ArrayAdapter<String>>(adapter)
            dialogView.nameEditText2.setAdapter<ArrayAdapter<String>>(adapter)
            dialogView.nameEditText3.setAdapter<ArrayAdapter<String>>(adapter)
            dialogView.nameEditText4.setAdapter<ArrayAdapter<String>>(adapter)

            val alert = AlertDialog.Builder(this)
                .setTitle("登録")

            dialogView.nameEditText1

            setEditEvent(dialogView.resultTextView1, dialogView.pointEditText1, 50)
            setEditEvent(dialogView.resultTextView2, dialogView.pointEditText2, 10)
            setEditEvent(dialogView.resultTextView3, dialogView.pointEditText3, -10)
            setEditEvent(dialogView.resultTextView4, dialogView.pointEditText4, -30)

            alert.setView(dialogView)
                .setPositiveButton("OK") { _, _ ->
                    val df = SimpleDateFormat("yyyy/MM/dd HH:mm")
                    val date = Date()

                    //DB入れ子
                    val pointDataBaseHolder = PointDB()
                    val playerDataBaseHolder =
                        arrayListOf(PlayerDB(), PlayerDB(), PlayerDB(), PlayerDB())

                    //取得
                    val playerName = arrayListOf(
                        dialogView.nameEditText1.text.toString(),
                        dialogView.nameEditText2.text.toString(),
                        dialogView.nameEditText3.text.toString(),
                        dialogView.nameEditText4.text.toString()
                    )

                    //点数
                    val points = arrayListOf(
                        dialogView.pointEditText1.text.toString().toInt(),
                        dialogView.pointEditText2.text.toString().toInt(),
                        dialogView.pointEditText3.text.toString().toInt(),
                        dialogView.pointEditText4.text.toString().toInt()
                    )

                    pointDataBaseHolder.name1 = playerName[0]
                    pointDataBaseHolder.point1 = points[0]
                    pointDataBaseHolder.name2 = playerName[1]
                    pointDataBaseHolder.point2 = points[1]
                    pointDataBaseHolder.name3 = playerName[2]
                    pointDataBaseHolder.point3 = points[2]
                    pointDataBaseHolder.name4 = playerName[3]
                    pointDataBaseHolder.point4 = points[3]
                    pointDataBaseHolder.date = df.format(date)

                    Completable.fromAction { pointAppDataBase.pointDataBaseDao().insert(pointDataBaseHolder) }
                        .subscribeOn(Schedulers.io())
                        .subscribe()
                    for (i in 0 until 4) {
                        playerDataBaseHolder[i].name = playerName[i]
                        playerDataBaseHolder[i].point = points[i]
                        playerDataBaseHolder[i].rank = i + 1
                        playerDataBaseHolder[i].date = df.format(date)
                        Completable.fromAction { playerAppDataBase.playerDataBaseDao().insert(playerDataBaseHolder[i]) }
                            .subscribeOn(Schedulers.io())
                            .subscribe()
                    }

                    //Log
                    Log.e("LogData", "------------------------------")
                    Log.d("LogData", "$playerName")
                    Log.e("LogData", "---")
                    Log.d("LogData", "$points")
                    Log.e("LogData", "---")
                    Log.d("LogData", df.format(date))
                    Log.e("LogData", "------------------------------")
                }

                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuPlayerData -> {
                //TODO プレイヤー個人の成績を出力するページへ（グラフとかあるといいかもしれぬ）
                Log.d("Menu", item.itemId.toString())
                return true
            }
            R.id.menuResult -> {
                //TODO 存在するプレイヤーの成績を簡易出力
                Log.d("Menu", item.itemId.toString())
                return true
            }
            R.id.menuPrivacy -> {
                //TODO プライバシーポリシーに飛ばす
                Log.d("Menu", item.itemId.toString())
                return true
            }
            R.id.menuReview -> {
                //TODO レビューへ飛ばす
                Log.d("Menu", item.itemId.toString())
                return true
            }
        }
        return true
    }

    fun searchResult(text: String) {
        val adapter = adapter
        if (text != "") {
            adapter.mValue = ArrayList(mDataList.filter { it.dName.contains(text) })
        } else {
            adapter.mValue = mDataList
        }
        adapter.notifyDataSetChanged()
    }

    private fun setEditEvent(vText: TextView, editText: EditText, point: Int) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("TEXT", p0.toString())
                val res = if (p0.toString() != "") {
                    (p0.toString().toDouble() - 300) / 10.0 + point
                } else {
                    point * 1.0
                }
                if (res >= 0) {
                    vText.setTextColor(Color.BLACK)
                    vText.text = String.format("  %.1f", res)
                } else {
                    vText.setTextColor(Color.RED)
                    vText.text = String.format("▲ %.1f", res)
                }
                setSunResults()

            }
        })
    }

    fun setSunResults() {
        var sum = 0.0

        if (dialogView.pointEditText1.text.toString() != "") {
            sum += dialogView.pointEditText1.text.toString().toDouble()
        }
        if (dialogView.pointEditText2.text.toString() != "") {
            sum += dialogView.pointEditText2.text.toString().toDouble()
        }
        if (dialogView.pointEditText3.text.toString() != "") {
            sum += dialogView.pointEditText3.text.toString().toDouble()
        }
        if (dialogView.pointEditText4.text.toString() != "") {
            sum += dialogView.pointEditText4.text.toString().toDouble()
        }
        dialogView.sumTextView.text = String.format("計：%.0f点", sum * 100)
    }


}

