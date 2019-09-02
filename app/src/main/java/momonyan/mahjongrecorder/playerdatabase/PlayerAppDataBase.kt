package momonyan.mahjongrecorder.playerdatabase

import androidx.room.Database
import androidx.room.RoomDatabase

@Database (entities = [PlayerDB::class],version = 1)
abstract class PlayerAppDataBase:RoomDatabase(){

    abstract fun playerDataBaseDao():PlayerDataBaseDao

}