package momonyan.mahjongrecorder

import android.arch.persistence.room.Room
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import kotlinx.android.synthetic.main.match_score_layout.*
import momonyan.mahjongrecorder.datalist.ItemDataClass
import momonyan.mahjongrecorder.playerdatabase.PlayerAppDataBase
import momonyan.mahjongrecorder.pointdatabase.PointAppDataBase

class MatchScoreActivity : AppCompatActivity() {
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
        playerNameTextViewList[pos].text = name
        if (nameList.filter { it != "プレイヤーを選択してください" }.distinct().size == 4) {
            //TODO 回数とかの取得、表示。

        }
    }
}