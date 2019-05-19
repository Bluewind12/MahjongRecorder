package momonyan.mahjongrecorder

import android.arch.persistence.room.Room
import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.personal_score_layout.*
import momonyan.mahjongrecorder.datalist.ItemAdapter
import momonyan.mahjongrecorder.datalist.ItemDataClass
import momonyan.mahjongrecorder.playerdatabase.PlayerAppDataBase
import momonyan.mahjongrecorder.pointdatabase.PointAppDataBase


class PersonalScoreActivity : AppCompatActivity() {
    private var mDataList: ArrayList<ItemDataClass> = ArrayList()
    private lateinit var pointAppDataBase: PointAppDataBase

    private lateinit var playerAppDataBase: PlayerAppDataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.personal_score_layout)

        pointAppDataBase =
            Room.databaseBuilder(this, PointAppDataBase::class.java, "MainPoint.db")
                .build()

        playerAppDataBase =
            Room.databaseBuilder(this, PlayerAppDataBase::class.java, "MainPlayer.db")
                .build()


        //名前リスト
        playerAppDataBase.playerDataBaseDao().getPlayerDistinct()
            .observe(this, android.arch.lifecycle.Observer { data ->
                val mutableList = mutableListOf("プレイヤーを選択してください")
                mutableList += data!!
                val dataAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mutableList)
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                personalSpinner.adapter = dataAdapter

            })

        personalSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                dataSet(personalSpinner.selectedItem.toString())
            }

        }
    }

    fun dataSet(name: String) {
        if (name != "" && name != "プレイヤーを選択してください") {
            playerAppDataBase.playerDataBaseDao().getPoint(name).observe(this, android.arch.lifecycle.Observer { data ->
                Log.d("?????", "koko")
                val maxPoint = data!!.max()
                val minPoint = data.min()
                val sumPoint = data.sum()
                val avePoint = data.average()

                pointSumTextView.text = String.format("%d 点", sumPoint)
                pointAveTextView.text = String.format("%.2f点", avePoint)
                pointMaxTextView.text = String.format("%d点", maxPoint)
                pointMinTextView.text = String.format("%d点", minPoint)

                setChart(data)
            })

            playerAppDataBase.playerDataBaseDao().getRank(name).observe(this, android.arch.lifecycle.Observer { data ->
                val countRank1 = data!!.count { it == 1 }
                val countRank2 = data.count { it == 2 }
                val countRank3 = data.count { it == 3 }
                val countRank4 = data.count { it == 4 }
                val allCount = data.count()

                rank1CountTextView.text = String.format("%d 回 / %d 回", countRank1, allCount)
                rank2CountTextView.text = String.format("%d 回 / %d 回", countRank2, allCount)
                rank3CountTextView.text = String.format("%d 回 / %d 回", countRank3, allCount)
                rank4CountTextView.text = String.format("%d 回 / %d 回", countRank4, allCount)

                rank1PercentTextView.text = String.format("%.0f %%", countRank1 / allCount.toDouble() * 100)
                rank2PercentTextView.text = String.format("%.0f %%", countRank2 / allCount.toDouble() * 100)
                rank3PercentTextView.text = String.format("%.0f %%", countRank3 / allCount.toDouble() * 100)
                rank4PercentTextView.text = String.format("%.0f %%", countRank4 / allCount.toDouble() * 100)

            })
            //DBからの取得
            pointAppDataBase.pointDataBaseDao().getByName(name).observe(this, android.arch.lifecycle.Observer { data ->
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
                personRecyclerView.adapter = adapter
                personRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            })


        }
    }

    private fun setChart(data: Array<Int>) {
        val mChart = lineChart

        // Grid背景色
        mChart.setDrawGridBackground(true)

        // no description text
        mChart.description.isEnabled = true

        // Grid縦軸を破線
        val xAxis = mChart.xAxis
        xAxis.enableGridDashedLine(10f, 10f, 0f)
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        val leftAxis = mChart.axisLeft
        // Y軸最大最小設定
        //leftAxis.setAxisMaximum(150f)
        //leftAxis.setAxisMinimum(0f)
        // Grid横軸を破線
        leftAxis.enableGridDashedLine(10f, 10f, 0f)
        leftAxis.setDrawZeroLine(true)

        // 右側の目盛り
        mChart.axisRight.isEnabled = false

        // add data
        val values = ArrayList<Entry>()

        for (i in data.indices) {
            values.add(Entry(i.toFloat(), data[i].toFloat(), null, null))
        }

        val set1: LineDataSet

        if (mChart.data != null && mChart.data.dataSetCount > 0) {

            set1 = mChart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            mChart.data.notifyDataChanged()
            mChart.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(values, "点数")

            set1.setDrawIcons(false)
            set1.color = Color.BLUE
            set1.setCircleColor(Color.BLACK)
            set1.lineWidth = 1f
            set1.circleRadius = 3f
            set1.setDrawCircleHole(false)
            set1.valueTextSize = 0f
            set1.setDrawFilled(true)
            set1.formLineWidth = 1f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set1.formSize = 15f

            set1.fillColor = Color.CYAN

            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the datasets

            // create a data object with the datasets
            val lineData = LineData(dataSets)

            // set data
            mChart.data = lineData

            mChart.animateX(2500)
        }

    }
}