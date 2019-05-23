package momonyan.mahjongrecorder

import android.arch.persistence.room.Room
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Point
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.WindowManager
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

    private lateinit var pointCriterion: ArrayList<Int>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.personal_score_layout)

        //
        pointCriterion = arrayListOf(50, 10, -10, -30)

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
                mutableList += data!!.sortedArray()
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
            //グラフのサイズの動的変更
            val wm = getSystemService(WINDOW_SERVICE)
            if (wm is WindowManager) {
                val disp = wm.defaultDisplay
                val size = Point()
                disp.getSize(size)
                lineChart.layoutParams.height = size.x * 2 / 3
                lineChart2.layoutParams.height = size.x * 2 / 3
            }

            playerAppDataBase.playerDataBaseDao().getPersonalData(name)
                .observe(this, android.arch.lifecycle.Observer { data ->
                    val pointMutableList = mutableListOf<Float>()
                    data!!.forEach {
                        pointMutableList.add((it.point * 100 - 30000) / 1000.0f + pointCriterion[it.rank - 1])
                    }
                    setResultChart(pointMutableList)
                    val sum = pointMutableList.sum()
                    val ave = pointMutableList.average()
                    if (sum >= 0) {
                        resultSumText.text = String.format("%.1f", sum)
                        resultSumText.setTextColor(Color.BLACK)
                    } else {
                        resultSumText.text = String.format("▲ %.1f", -sum)
                        resultSumText.setTextColor(Color.RED)
                    }

                    if (ave >= 0) {
                        resultAveTextView.text = String.format("%.3f", ave)
                        resultAveTextView.setTextColor(Color.BLACK)
                    } else {
                        resultAveTextView.text = String.format("▲ %.3f", -ave)
                        resultAveTextView.setTextColor(Color.RED)
                    }
                })

            playerAppDataBase.playerDataBaseDao().getPoint(name).observe(this, android.arch.lifecycle.Observer { data ->
                val maxPoint = data!!.max()!! * 100
                val minPoint = data.min()!! * 100
                val sumPoint = data.sum() * 100
                val avePoint = data.average() * 100

                pointSumTextView.text = String.format("%d 点", sumPoint)
                pointAveTextView.text = String.format("%.2f点", avePoint)
                pointMaxTextView.text = String.format("%d点", maxPoint)
                pointMinTextView.text = String.format("%d点", minPoint)

                setPointChart(data)
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
                            (pointList[0] - 30000) / 1000.0 + 50,
                            (pointList[1] - 30000) / 1000.0 + 10,
                            (pointList[2] - 30000) / 1000.0 - 10,
                            (pointList[3] - 30000) / 1000.0 - 30
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
                mDataList.reverse()
                val adapter = ItemAdapter(mDataList, null)
                personRecyclerView.adapter = adapter
                personRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            })


        }
    }

    private fun setPointChart(basicData: Array<Int>) {
        val data = mutableListOf<Float>()
        var sumData = 0.0f
        for (i in 0 until basicData.size) {
            sumData += basicData[i]
            data.add(sumData / (i + 1))
            Log.d("Data", "$i - $sumData")
        }

        Log.d("DataRes", "$data")
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
        val values2 = ArrayList<Entry>()

        for (i in data.indices) {
            values.add(Entry(i.toFloat(), data[i] * 100, null, null))
            values2.add(Entry(i.toFloat(), basicData[i].toFloat() * 100, null, null))
        }

        val set1: LineDataSet
        val set2: LineDataSet

        if (mChart.data != null && mChart.data.dataSetCount > 0) {
            set1 = mChart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            set2 = mChart.data.getDataSetByIndex(0) as LineDataSet
            set2.values = values
            mChart.data.notifyDataChanged()
            mChart.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(values, "点数平均")
            set1.setDrawIcons(false)
            set1.color = Color.BLUE
            set1.setCircleColor(Color.BLUE)
            set1.lineWidth = 3f
            set1.circleRadius = 5f
            set1.setDrawCircleHole(false)
            set1.valueTextSize = 0f
            set1.setDrawFilled(true)
            set1.formLineWidth = 1f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set1.formSize = 15f
            set1.fillColor = Color.argb(0, 255, 255, 255)

            set2 = LineDataSet(values2, "取得点数")
            set2.setDrawIcons(false)
            set2.color = Color.RED
            set2.setCircleColor(Color.RED)
            set2.lineWidth = 3f
            set2.circleRadius = 5f
            set2.setDrawCircleHole(false)
            set2.valueTextSize = 0f
            set2.setDrawFilled(true)
            set2.formLineWidth = 1f
            set2.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set2.formSize = 15f
            set2.fillColor = Color.argb(0, 255,255,255)


            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the datasets
            dataSets.add(set2) // add the datasets

            // create a data object with the datasets
            val lineData = LineData(dataSets)

            // set data
            mChart.data = lineData

            mChart.animateX(2500)
        }
    }

    private fun setResultChart(basicData: MutableList<Float>) {
        val data = mutableListOf<Float>()
        var sumData = 0.0f
        for (i in 0 until basicData.size) {
            sumData += basicData[i]
            data.add(sumData)
        }
        val mChart = lineChart2

        // Grid背景色
        mChart.setDrawGridBackground(true)

        // no description text
        mChart.description.isEnabled = true

        // Grid縦軸を破線
        val xAxis = mChart.xAxis
        xAxis.enableGridDashedLine(10f, 10f, 0f)
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        val leftAxis = mChart.axisLeft
        // Grid横軸を破線
        leftAxis.enableGridDashedLine(10f, 10f, 0f)
        leftAxis.setDrawZeroLine(true)

        // 右側の目盛り
        mChart.axisRight.isEnabled = false

        // add data
        val values = ArrayList<Entry>()
        val values2 = ArrayList<Entry>()

        for (i in data.indices) {
            values.add(Entry(i.toFloat(), data[i], null, null))
            values2.add(Entry(i.toFloat(), basicData[i], null, null))
        }

        val set1: LineDataSet
        val set2: LineDataSet

        if (mChart.data != null && mChart.data.dataSetCount > 0) {
            set1 = mChart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            set2 = mChart.data.getDataSetByIndex(0) as LineDataSet
            set2.values = values
            mChart.data.notifyDataChanged()
            mChart.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(values, "累積")
            set1.setDrawIcons(false)
            set1.color = Color.BLUE
            set1.setCircleColor(Color.BLUE)
            set1.lineWidth = 3f
            set1.circleRadius = 5f
            set1.setDrawCircleHole(false)
            set1.valueTextSize = 0f
            set1.setDrawFilled(true)
            set1.formLineWidth = 1f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set1.formSize = 15f
            set1.fillColor = Color.argb(0, 255, 255, 255)

            set2 = LineDataSet(values2, "取得")
            set2.setDrawIcons(false)
            set2.color = Color.RED
            set2.setCircleColor(Color.RED)
            set2.lineWidth = 3f
            set2.circleRadius = 5f
            set2.setDrawCircleHole(false)
            set2.valueTextSize = 0f
            set2.setDrawFilled(true)
            set2.formLineWidth = 1f
            set2.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set2.formSize = 15f
            set2.fillColor = Color.argb(0, 255, 255, 255)

            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the datasets
            dataSets.add(set2) // add the datasets

            // create a data object with the datasets
            val lineData = LineData(dataSets)

            // set data
            mChart.data = lineData

            mChart.animateX(2500)
        }
    }
}