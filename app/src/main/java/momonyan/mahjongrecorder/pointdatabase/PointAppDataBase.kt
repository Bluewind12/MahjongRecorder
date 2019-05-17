package momonyan.mahjongrecorder.pointdatabase

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database (entities = [PointDB::class],version = 1)
abstract class PointAppDataBase:RoomDatabase(){

    abstract fun PointDataBaseDao():PointDataBaseDao

}