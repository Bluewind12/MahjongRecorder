package momonyan.mahjongrecorder.playerdatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class PlayerDB {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    //Player
    @ColumnInfo(name = "Name")
    var name: String = ""

    @ColumnInfo(name = "Point")
    var point: Int = 0

    @ColumnInfo(name = "Rank")
    var rank: Int = 0
    //Data
    @ColumnInfo(name= "Date")
    var date :String = ""

}
