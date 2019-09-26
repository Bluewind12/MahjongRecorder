package momonyan.mahjongrecorder

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
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


class MainActivity : AppCompatActivity() {
    private var mDataList: ArrayList<ItemDataClass> = ArrayList()

    private lateinit var adapter: ItemAdapter
    private lateinit var dialogView: View

    private lateinit var pointAppDataBase: PointAppDataBase
    private lateinit var playerAppDataBase: PlayerAppDataBase

    private lateinit var alert: AlertDialog

    private lateinit var nameEditTexts: ArrayList<AutoCompleteTextView>
    private lateinit var pointEditTexts: ArrayList<EditText>
    private lateinit var resultTextViews: ArrayList<TextView>

    private var pointData = arrayListOf(50, 10, -10, -30)

    private var pointCheckFrag = false

    private lateinit var mInterstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        MobileAds.initialize(this) {}

        val adRequest = AdRequest.Builder().build()
        adMain.loadAd(adRequest)

        setSupportActionBar(tool_bar)
        val actionBarDrawerToggle =
            object : ActionBarDrawerToggle(this, drawer_layout, tool_bar, R.string.drawer_open, R.string.drawer_close) {
                override fun onDrawerStateChanged(newState: Int) {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE)
                    if (imm is InputMethodManager) {
                        imm.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                    }
                }
            }
        drawer_layout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        //
        val data = getSharedPreferences("DataSave", Context.MODE_PRIVATE)


        //ドロワー
        nav_view.getHeaderView(0).findViewById<TextView>(R.id.versionTextView).text = getString(R.string.version_names,getVersionName(this))
        val navMenu = nav_view.menu
        navMenu.findItem(R.id.menuDataList).icon.setColorFilter(
            ContextCompat.getColor(this, R.color.iconColor),
            PorterDuff.Mode.SRC_IN
        )
        navMenu.findItem(R.id.menuPlayerData).icon.setColorFilter(
            ContextCompat.getColor(this, R.color.iconColor),
            PorterDuff.Mode.SRC_IN
        )
        navMenu.findItem(R.id.menuResult).icon.setColorFilter(
            ContextCompat.getColor(this, R.color.iconColor),
            PorterDuff.Mode.SRC_IN
        )
        navMenu.findItem(R.id.setting5_10).icon.setColorFilter(
            ContextCompat.getColor(this, R.color.iconColor),
            PorterDuff.Mode.SRC_IN
        )
        navMenu.findItem(R.id.setting10_20).icon.setColorFilter(
            ContextCompat.getColor(this, R.color.iconColor),
            PorterDuff.Mode.SRC_IN
        )
        navMenu.findItem(R.id.setting10_30).icon.setColorFilter(
            ContextCompat.getColor(this, R.color.iconColor),
            PorterDuff.Mode.SRC_IN
        )
        navMenu.findItem(R.id.setting20_30).icon.setColorFilter(
            ContextCompat.getColor(this, R.color.iconColor),
            PorterDuff.Mode.SRC_IN
        )
        navMenu.findItem(R.id.menuAppMJ).icon.setColorFilter(
            ContextCompat.getColor(this, R.color.colorPrimaryDark),
            PorterDuff.Mode.SRC_IN
        )
        navMenu.findItem(R.id.menuHp).icon.setColorFilter(
            ContextCompat.getColor(this, R.color.sakura),
            PorterDuff.Mode.SRC_IN
        )
        nav_view.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuDataList -> {
                    val intent = Intent(this, CardListActivity::class.java)
                    startActivity(intent)
                }
                R.id.menuPlayerData -> {
                    val intent = Intent(this, PersonalScoreActivity::class.java)
                    startActivity(intent)
                }
                R.id.menuResult -> {
                    val intent = Intent(this, MatchScoreActivity::class.java)
                    startActivity(intent)
                }
                R.id.menuPrivacy -> {
                    val url = Uri.parse(getString(R.string.privacy_url))
                    val intent = Intent(Intent.ACTION_VIEW, url)
                    startActivity(intent)
                }
                R.id.menuHp -> {
                    val url = Uri.parse(getString(R.string.home_url))
                    val intent = Intent(Intent.ACTION_VIEW, url)
                    startActivity(intent)
                }
                R.id.menuReview -> {
                    val url = Uri.parse(getString(R.string.review_url))
                    val intent = Intent(Intent.ACTION_VIEW, url)
                    startActivity(intent)
                }
                R.id.menuAppMJ -> {
                    val packageName = "momonyan.mahjongg_tools"
                    val className = "momonyan.mahjongg_tools.MainActivity"
                    intent.setClassName(packageName, className)
                    try {
                        startActivity(intent)
                    } catch (e: Exception) {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
                    }
                }
                R.id.setting5_10 -> {
                    val editor = data.edit()
                    editor.putString("PointResult", "5-10")
                    editor.apply()
                    pointData = arrayListOf(30, 5, -5, -10)
                    navMenu.findItem(R.id.setting5_10).icon = getDrawable(R.drawable.icon_check)
                    navMenu.findItem(R.id.setting10_20).icon = getDrawable(R.drawable.icon_space)
                    navMenu.findItem(R.id.setting10_30).icon = getDrawable(R.drawable.icon_space)
                    navMenu.findItem(R.id.setting20_30).icon = getDrawable(R.drawable.icon_space)
                }
                R.id.setting10_20 -> {
                    val editor = data.edit()
                    editor.putString("PointResult", "10-20")
                    editor.apply()
                    pointData = arrayListOf(40, 10, -10, -20)

                    navMenu.findItem(R.id.setting5_10).icon = getDrawable(R.drawable.icon_space)
                    navMenu.findItem(R.id.setting10_20).icon = getDrawable(R.drawable.icon_check)
                    navMenu.findItem(R.id.setting10_30).icon = getDrawable(R.drawable.icon_space)
                    navMenu.findItem(R.id.setting20_30).icon = getDrawable(R.drawable.icon_space)
                }
                R.id.setting10_30 -> {
                    val editor = data.edit()
                    editor.putString("PointResult", "10-30")
                    editor.apply()
                    pointData = arrayListOf(50, 10, -10, -30)

                    navMenu.findItem(R.id.setting5_10).icon = getDrawable(R.drawable.icon_space)
                    navMenu.findItem(R.id.setting10_20).icon = getDrawable(R.drawable.icon_space)
                    navMenu.findItem(R.id.setting10_30).icon = getDrawable(R.drawable.icon_check)
                    navMenu.findItem(R.id.setting20_30).icon = getDrawable(R.drawable.icon_space)
                }
                R.id.setting20_30 -> {
                    val editor = data.edit()
                    editor.putString("PointResult", "20-30")
                    editor.apply()
                    pointData = arrayListOf(50, 20, -20, -30)

                    navMenu.findItem(R.id.setting5_10).icon = getDrawable(R.drawable.icon_space)
                    navMenu.findItem(R.id.setting10_20).icon = getDrawable(R.drawable.icon_space)
                    navMenu.findItem(R.id.setting10_30).icon = getDrawable(R.drawable.icon_space)
                    navMenu.findItem(R.id.setting20_30).icon = getDrawable(R.drawable.icon_check)
                }
                else -> {
                }
            }
            false
        }


        //AD(インタースティシャル)
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = getString(R.string.ad_pop)
        mInterstitialAd.loadAd(AdRequest.Builder().build())

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
                if (text != "") {
                    searchResult(text!!)
                }
                return false
            }

        })
        searchView.queryHint = "検索したい名前を入力してください。"

        //DBからの取得
        pointAppDataBase.pointDataBaseDao().getAll().observe(this, androidx.lifecycle.Observer { data ->
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
                        (pointList[0] - 30000) / 1000.0 + pointData[0],
                        (pointList[1] - 30000) / 1000.0 + pointData[1],
                        (pointList[2] - 30000) / 1000.0 + pointData[2],
                        (pointList[3] - 30000) / 1000.0 + pointData[3]
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
            adapter = ItemAdapter(mDataList, this)
            dataRecyclerView.adapter = adapter
            dataRecyclerView.layoutManager = LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
            )
        })

        //データ追加（Fab）
        floatingActionButton.setOnClickListener {
            createDialog()
        }
    }


    private fun createDialog() {
        dialogView = layoutInflater.inflate(R.layout.data_input_layout, null)

        dialogView.dialogTitleTextView.text = "登録"
        dialogView.shareImageButton.visibility = View.INVISIBLE
        nameEditTexts = arrayListOf(
            dialogView.nameEditText1,
            dialogView.nameEditText2,
            dialogView.nameEditText3,
            dialogView.nameEditText4
        )
        pointEditTexts = arrayListOf(
            dialogView.pointEditText1,
            dialogView.pointEditText2,
            dialogView.pointEditText3,
            dialogView.pointEditText4
        )
        resultTextViews = arrayListOf(
            dialogView.resultTextView1,
            dialogView.resultTextView2,
            dialogView.resultTextView3,
            dialogView.resultTextView4
        )
        dialogView.sumTextView.text = String.format("計：0点\n(残り100000点)")

        playerAppDataBase.playerDataBaseDao().getPlayerDistinct().observe(
            this,
            androidx.lifecycle.Observer { t ->
                val adapter = ArrayAdapter<String>(
                    this, android.R.layout.simple_dropdown_item_1line, t!!
                )
                nameEditTexts.forEach {
                    it.setAdapter<ArrayAdapter<String>>(adapter)
                }
            })


        for (i in 0 until 4) {
            setEditEvent(resultTextViews[i], pointEditTexts[i], pointData[i])
            setNameEditEvent(nameEditTexts[i])
        }

        val alertBuilder = AlertDialog.Builder(this)
        alert = alertBuilder
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("OK") { _, _ ->

                val df = SimpleDateFormat("yyyy/MM/dd HH:mm")
                val date = Date()

                //DB入れ子
                val pointDataBaseHolder = PointDB()
                val playerDataBaseHolder =
                    arrayListOf(PlayerDB(), PlayerDB(), PlayerDB(), PlayerDB())

                //取得
                val playerName = arrayListOf(
                    nameEditTexts[0].text.toString(),
                    nameEditTexts[1].text.toString(),
                    nameEditTexts[2].text.toString(),
                    nameEditTexts[3].text.toString()
                )

                //点数
                val points = arrayListOf(
                    pointEditTexts[0].text.toString().toInt(),
                    pointEditTexts[1].text.toString().toInt(),
                    pointEditTexts[2].text.toString().toInt(),
                    pointEditTexts[3].text.toString().toInt()
                )


                pointDataBaseHolder.name1 = playerName[0]
                pointDataBaseHolder.name2 = playerName[1]
                pointDataBaseHolder.name3 = playerName[2]
                pointDataBaseHolder.name4 = playerName[3]
                pointDataBaseHolder.point1 = points[0]
                pointDataBaseHolder.point2 = points[1]
                pointDataBaseHolder.point3 = points[2]
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
            }
            .setOnDismissListener {
                if (mInterstitialAd.isLoaded) {
                    mInterstitialAd.show()
                }
            }
            .create()

        alert.show()

        alert.getButton(Dialog.BUTTON_POSITIVE).isEnabled = false
    }

    fun createChangeDialog(id: Int, names: MutableList<String>, point: MutableList<Int>, date: String) {
        dialogView = layoutInflater.inflate(R.layout.data_input_layout, null)
        dialogView.dialogTitleTextView.text = "変更"

        nameEditTexts = arrayListOf(
            dialogView.nameEditText1,
            dialogView.nameEditText2,
            dialogView.nameEditText3,
            dialogView.nameEditText4
        )
        pointEditTexts = arrayListOf(
            dialogView.pointEditText1,
            dialogView.pointEditText2,
            dialogView.pointEditText3,
            dialogView.pointEditText4
        )
        resultTextViews = arrayListOf(
            dialogView.resultTextView1,
            dialogView.resultTextView2,
            dialogView.resultTextView3,
            dialogView.resultTextView4
        )

        //シェアボタン
        dialogView.shareImageButton.visibility = View.VISIBLE
        dialogView.shareImageButton.setOnClickListener {
            var shareStringData = ""
            for (i in 0 until 4) {
                shareStringData += getString(
                    R.string.share_format_string_data,
                    i+1,
                    nameEditTexts[i].text,
                    pointEditTexts[i].text
                )
            }
            shareStringData += "\n" + date
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, shareStringData)
            startActivity(intent)
        }

        playerAppDataBase.playerDataBaseDao().getPlayerDistinct().observe(
            this,
            androidx.lifecycle.Observer { t ->
                val adapter = ArrayAdapter(
                    this, android.R.layout.simple_dropdown_item_1line, t!!
                )
                dialogView.nameEditText1.setAdapter(adapter)
                dialogView.nameEditText2.setAdapter(adapter)
                dialogView.nameEditText3.setAdapter(adapter)
                dialogView.nameEditText4.setAdapter(adapter)
            })
        for (i in 0 until 4) {
            nameEditTexts[i].setText(names[i], TextView.BufferType.NORMAL)
            pointEditTexts[i].setText((point[i] / 100).toString(), TextView.BufferType.NORMAL)
            setResultText(resultTextViews[i], (point[i] - 30000) / 1000.0 + pointData[i])
            setEditEvent(resultTextViews[i], pointEditTexts[i], pointData[i])
            setNameEditEvent(nameEditTexts[i])
        }

        val alertBuilder = AlertDialog.Builder(this)
        alert = alertBuilder
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("OK") { _, _ ->
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



                Completable.fromAction {
                    pointAppDataBase.pointDataBaseDao().updateData(
                        id,
                        playerName[0],
                        points[0],
                        playerName[1],
                        points[1],
                        playerName[2],
                        points[2],
                        playerName[3],
                        points[3]
                    )
                }
                    .subscribeOn(Schedulers.io())
                    .subscribe()
                for (i in 0 until 4) {
                    Completable.fromAction {
                        playerAppDataBase.playerDataBaseDao().updateData(names[i], playerName[i], points[i])
                    }
                        .subscribeOn(Schedulers.io())
                        .subscribe()
                }

                if (mInterstitialAd.isLoaded && Random().nextInt(100) >= 50) {
                    mInterstitialAd.show()
                }
            }
            .setNeutralButton("データ削除") { _, _ ->
                AlertDialog.Builder(this)
                    .setTitle("削除します")
                    .setPositiveButton("OK") { _, _ ->
                        //削除
                        Completable.fromAction { pointAppDataBase.pointDataBaseDao().deleteId(id) }
                            .subscribeOn(Schedulers.io())
                            .subscribe()
                        //削除
                        for (i in 0 until 4) {
                            Log.d("ZZZ", names[i] + date + point[i])
                            Completable.fromAction {
                                playerAppDataBase.playerDataBaseDao().deletePlayer(names[i], date, point[i] / 100)
                            }
                                .subscribeOn(Schedulers.io())
                                .subscribe()
                        }

                        if (mInterstitialAd.isLoaded && Random().nextInt(100) >= 80) {
                            mInterstitialAd.show()
                        }
                    }
                    .setNegativeButton("Cancel") { _, _ ->
                        if (mInterstitialAd.isLoaded && Random().nextInt(100) >= 80) {
                            mInterstitialAd.show()
                        }
                    }
                    .show()

            }
            .setOnDismissListener {
            }
            .create()

        alert.show()

        alert.getButton(Dialog.BUTTON_POSITIVE).isEnabled = false
    }

    fun searchResult(text: String) {
        val adapter = adapter
        if (text != "") {
            adapter.mValue = ArrayList(mDataList.filter {
                it.dName[0].contains(text) || it.dName[1].contains(text) || it.dName[2].contains(text) || it.dName[3].contains(
                    text
                )
            })
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
                val res = if (p0.toString() != "" && p0.toString() != "-") {
                    Log.d("TAGAA", p0.toString())
                    (p0.toString().toDouble() - 300) / 10.0 + point
                } else {
                    point * 1.0
                }
                if (res >= 0) {
                    vText.setTextColor(Color.BLACK)
                    vText.text = String.format("  %.1f", res)
                } else {
                    vText.setTextColor(Color.RED)
                    vText.text = String.format("▲ %.1f", res * -1)
                }
                setSunResults()
                inputCheck()
            }
        })
    }

    private fun setNameEditEvent(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                inputCheck()
            }
        })
    }

    fun setSunResults() {
        var sum = 0.0

        pointEditTexts.forEach {
            if (it.text.toString() != "" && it.text.toString() != "-") {
                sum += it.text.toString().toDouble()
            }
        }
        dialogView.sumTextView.text = String.format("計：%.0f点\n(残り%.0f点)", sum * 100, 100000 - (sum * 100))
        Log.d("SUM", sum.toString())
        pointCheckFrag = sum == 1000.0
    }

    fun inputCheck() {
        alert.getButton(Dialog.BUTTON_POSITIVE).isEnabled =
            dialogView.pointEditText1.text.toString() != "" &&
                    dialogView.pointEditText2.text.toString() != "" &&
                    dialogView.pointEditText3.text.toString() != "" &&
                    dialogView.pointEditText4.text.toString() != "" &&
                    dialogView.pointEditText1.text.toString() != "-" &&
                    dialogView.pointEditText2.text.toString() != "-" &&
                    dialogView.pointEditText3.text.toString() != "-" &&
                    dialogView.pointEditText4.text.toString() != "-" &&
                    dialogView.nameEditText1.text.toString() != "" &&
                    dialogView.nameEditText2.text.toString() != "" &&
                    dialogView.nameEditText3.text.toString() != "" &&
                    dialogView.nameEditText4.text.toString() != "" &&
                    pointCheckFrag

    }

    private fun setResultText(textView: TextView, res: Double) {
        Log.d("RES", res.toString())
        if (res >= 0) {
            textView.setTextColor(Color.BLACK)
            textView.text = String.format("  %.1f", res)
        } else {
            textView.setTextColor(Color.RED)
            textView.text = String.format("▲ %.1f", res * -1)
        }
    }

    /**
     * バージョン名を取得する
     *
     * @param context
     * @return
     */
    private fun getVersionName(context: Context): String {
        val pm = context.packageManager
        var versionName = ""
        try {
            val packageInfo = pm.getPackageInfo(context.packageName, 0)
            versionName = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return versionName
    }
}

