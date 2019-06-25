package momonyan.mahjongrecorder

import android.arch.persistence.room.Room
import android.graphics.Point
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import kotlinx.android.synthetic.main.match_score_layout.*
import momonyan.mahjongrecorder.datalist.ItemAdapter
import momonyan.mahjongrecorder.datalist.ItemDataClass
import momonyan.mahjongrecorder.playerdatabase.PlayerAppDataBase
import momonyan.mahjongrecorder.pointdatabase.PointAppDataBase

class MatchScoreActivity : AppCompatActivity() {
    private var mDataList: ArrayList<ItemDataClass> = ArrayList()
    private lateinit var pointAppDataBase: PointAppDataBase
    private lateinit var playerAppDataBase: PlayerAppDataBase

    private lateinit var spinnerList: ArrayList<Spinner>
    private lateinit var playerNameTextViewList: ArrayList<TextView>
    private lateinit var playerRank1TextViewList: ArrayList<TextView>
    private lateinit var playerRank2TextViewList: ArrayList<TextView>
    private lateinit var playerRank3TextViewList: ArrayList<TextView>
    private lateinit var playerRank4TextViewList: ArrayList<TextView>

    private var nameList = mutableListOf("", "", "", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.match_score_layout)

        //DB取得
        pointAppDataBase =
            Room.databaseBuilder(this, PointAppDataBase::class.java, "MainPoint.db")
                .build()
        playerAppDataBase =
            Room.databaseBuilder(this, PlayerAppDataBase::class.java, "MainPlayer.db")
                .build()

        //取得
        spinnerList = arrayListOf(playerSelectSpinner, playerSelectSpinner2, playerSelectSpinner3, playerSelectSpinner4)
        playerNameTextViewList =
            arrayListOf(playerNameTextView, playerNameTextView2, playerNameTextView3, playerNameTextView4)
        playerRank1TextViewList =
            arrayListOf(playerRank1TextView, playerRank1TextView2, playerRank1TextView3, playerRank1TextView4)
        playerRank2TextViewList =
            arrayListOf(playerRank2TextView, playerRank2TextView2, playerRank2TextView3, playerRank2TextView4)
        playerRank3TextViewList =
            arrayListOf(playerRank3TextView, playerRank3TextView2, playerRank3TextView3, playerRank3TextView4)
        playerRank4TextViewList =
            arrayListOf(playerRank4TextView, playerRank4TextView2, playerRank4TextView3, playerRank4TextView4)

        //DBデータ取得
        playerAppDataBase.playerDataBaseDao().getPlayerDistinct()
            .observe(this, android.arch.lifecycle.Observer { data ->
                val mutableList = mutableListOf("プレイヤーを選択してください")
                mutableList += data!!
                val dataAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mutableList)
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerList.forEach {
                    it.adapter = dataAdapter
                }
            })

        //Listenerセット
        for (i in 0 until 4) {
            spinnerList[i].onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    dataSet(spinnerList[i].selectedItem.toString(), i)
                }

            }

        }
    }

    //データ出し
    private fun dataSet(name: String, pos: Int) {
        nameList[pos] = name
        if (name != "プレイヤーを選択してください") {
            playerNameTextViewList[pos].text = name
        } else {
            playerNameTextViewList[pos].text = "選択無し"
        }
        if (nameList.filter { it != "プレイヤーを選択してください" }.distinct().size == 4) {

            val wm = getSystemService(WINDOW_SERVICE)
            if (wm is WindowManager) {
                val disp = wm.defaultDisplay
                val size = Point()
                disp.getSize(size)
                matchRecyclerView.layoutParams.height = size.x * 3 / 5
            }
            //DBからの取得
            pointAppDataBase.pointDataBaseDao().getMatchData(nameList[0], nameList[1], nameList[2], nameList[3])
                .observe(this, android.arch.lifecycle.Observer { data ->
                    mDataList = ArrayList()
                    for (i in 0 until data!!.size) {
                        val nameList: MutableList<String> =
                            mutableListOf(
                                data[i].name1,
                                data[i].name2,
                                data[i].name3,
                                data[i].name4
                            )
                        val pointList: MutableList<Int> =
                            mutableListOf(
                                data[i].point1 * 100,
                                data[i].point2 * 100,
                                data[i].point3 * 100,
                                data[i].point4 * 100
                            )
                        val resultList: MutableList<Double> =
                            mutableListOf(
                                (pointList[0] - 300) / 10.0 + 50,
                                (pointList[1] - 300) / 10.0 + 10,
                                (pointList[2] - 300) / 10.0 - 10,
                                (pointList[3] - 300) / 10.0 - 30
                            )
                        mDataList.add(
                            ItemDataClass(
                                data[i].id,
                                nameList,
                                pointList,
                                resultList,
                                data[i].date
                            )
                        )
                    }
                    val adapter = ItemAdapter(mDataList, null)
                    matchRecyclerView.adapter = adapter
                    matchRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

                    for (j in 0 until 4) {
                        val countList = mutableListOf(0, 0, 0, 0)
                        nameList[j]
                        val matchCount = data.size.toDouble()
                        data.forEach {
                            if (it.name1 == nameList[j]) {
                                countList[0]++
                            }
                            if (it.name2 == nameList[j]) {
                                countList[1]++
                            }
                            if (it.name3 == nameList[j]) {
                                countList[2]++
                            }
                            if (it.name4 == nameList[j]) {
                                countList[3]++
                            }
                        }
                        playerRank1TextViewList[j].text =
                            String.format("%d回：\n%.0f%%", countList[0], (countList[0] / matchCount) * 100)
                        playerRank2TextViewList[j].text =
                            String.format("%d回：\n%.0f%%", countList[1], (countList[1] / matchCount) * 100)
                        playerRank3TextViewList[j].text =
                            String.format("%d回：\n%.0f%%", countList[2], (countList[2] / matchCount) * 100)
                        playerRank4TextViewList[j].text =
                            String.format("%d回：\n%.0f%%", countList[3], (countList[3] / matchCount) * 100)
                    }
                })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.def_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuBack -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}