package momonyan.mahjongrecorder.pointdatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class PointDB {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    //Player1
    @ColumnInfo(name = "Name1")
    var name1: String = ""
    @ColumnInfo(name = "Point1")
    var point1: Int = 0

    //Player2
    @ColumnInfo(name = "Name2")
    var name2: String = ""
    @ColumnInfo(name = "Point2")
    var point2: Int = 0

    //Player3
    @ColumnInfo(name = "Name3")
    var name3: String = ""
    @ColumnInfo(name = "Point3")
    var point3: Int = 0

    //Player4
    @ColumnInfo(name = "Name4")
    var name4: String = ""
    @ColumnInfo(name = "Point4")
    var point4: Int = 0

    //Data
    @ColumnInfo(name= "Date")
    var date :String = ""

}
