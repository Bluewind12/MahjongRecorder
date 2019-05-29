package momonyan.mahjongrecorder

import android.arch.persistence.room.Room
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_cardlist.*
import momonyan.mahjongrecorder.playerdatabase.PlayerAppDataBase
import momonyan.mahjongrecorder.playerdatalist.CardListAdapter
import momonyan.mahjongrecorder.playerdatalist.CardListDataClass

class CardListActivity : AppCompatActivity() {
    private var mDataList: ArrayList<CardListDataClass> = ArrayList()
    private lateinit var adapter: CardListAdapter

    private lateinit var playerAppDataBase: PlayerAppDataBase
    private lateinit var pointStand: ArrayList<Int>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cardlist)

        pointStand = arrayListOf(50, 10, -10, -30)

        playerAppDataBase =
            Room.databaseBuilder(this, PlayerAppDataBase::class.java, "MainPlayer.db")
                .build()

        playerAppDataBase.playerDataBaseDao().getPlayerDistinct()
            .observe(this, android.arch.lifecycle.Observer { data ->
                mDataList = ArrayList()
                for (i in 0 until data!!.size) {
                    var matchCount = 0
                    var pointSum = 0
                    var resultSum = 0.0
                    val rankList = mutableListOf(0, 0, 0, 0)

                    playerAppDataBase.playerDataBaseDao().getPersonalData(data[i])
                        .observe(this, android.arch.lifecycle.Observer { playerData ->
                            matchCount = playerData!!.size
                            for (j in 0 until matchCount) {
                                pointSum += playerData[j].point * 100
                                resultSum += playerData[j].point / 10 + pointStand[playerData[j].rank - 1]
                                rankList[playerData[j].rank - 1]++
                            }
                            mDataList.add(
                                CardListDataClass(
                                    data[i],
                                    String.format("点数\n合計 %d\n平均 %.2f\n", pointSum, pointSum / matchCount.toDouble()),
                                    String.format("集計\n合計 %.1f\n平均 %.2f", resultSum, resultSum / matchCount.toDouble()),
                                    rankList,
                                    matchCount
                                )
                            )

                            adapter = CardListAdapter(mDataList)
                            cardListRecyclerView.adapter = adapter
                            cardListRecyclerView.layoutManager =
                                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                        })
                }
            })
    }


}