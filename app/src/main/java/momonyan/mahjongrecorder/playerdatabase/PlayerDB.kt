package momonyan.mahjongrecorder.playerdatabase

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class PlayerDB {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    //Player1
    @ColumnInfo(name = "Name")
    var name1: String = ""

    @ColumnInfo(name = "Point")
    var point4: Int = 0

    //Data
    @ColumnInfo(name= "Date")
    var date :String = ""

}
