package momonyan.mahjongrecorder.pointdatabase

import androidx.room.Database
import androidx.room.RoomDatabase

@Database (entities = [PointDB::class],version = 1)
abstract class PointAppDataBase:RoomDatabase(){

    abstract fun pointDataBaseDao():PointDataBaseDao

}