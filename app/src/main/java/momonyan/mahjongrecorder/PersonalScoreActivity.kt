package momonyan.mahjongrecorder

import android.arch.persistence.room.Room
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.personal_score_layout.*
import momonyan.mahjongrecorder.playerdatabase.PlayerAppDataBase
import momonyan.mahjongrecorder.playerdatabase.PlayerDB


class PersonalScoreActivity : AppCompatActivity() {
    private lateinit var playerAppDataBase: PlayerAppDataBase
    private lateinit var playerData: List<PlayerDB>
    private lateinit var playerNameList: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.personal_score_layout)

        playerAppDataBase =
            Room.databaseBuilder(this, PlayerAppDataBase::class.java, "MainPlayer.db")
                .build()

        //全体データ
        playerAppDataBase.playerDataBaseDao().getAll().observe(this, android.arch.lifecycle.Observer { data ->
            playerData = data!!
        })
        //名前リスト
        playerAppDataBase.playerDataBaseDao().getPlayerDistinct()
            .observe(this, android.arch.lifecycle.Observer { data ->
                playerNameList = data!!

                val dataAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, playerNameList)
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                personalSpinner.adapter = dataAdapter

            })
    }

}