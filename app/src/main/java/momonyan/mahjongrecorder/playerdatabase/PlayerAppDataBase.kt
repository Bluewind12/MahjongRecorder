package momonyan.mahjongrecorder.playerdatabase

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database (entities = [PlayerDB::class],version = 1)
abstract class PlayerAppDataBase:RoomDatabase(){

    abstract fun PlayerDataBaseDao():PlayerDataBaseDao

}