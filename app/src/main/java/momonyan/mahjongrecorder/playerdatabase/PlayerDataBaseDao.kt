package momonyan.mahjongrecorder.playerdatabase

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface PlayerDataBaseDao {
    @Query("SELECT * FROM PlayerDB")
    fun getAll(): LiveData<List<PlayerDB>>

    @Query("SELECT * FROM PlayerDB WHERE id IN (:id)")
    fun getById(vararg id: Int): List<PlayerDB>

    @Query("SELECT DISTINCT Name FROM PlayerDB")
    fun getPlayerDistinct(): LiveData<Array<String>>

    //データ作成
    @Insert
    fun insert(playerDB: PlayerDB)



    // 条件でDelete
    @Query("DELETE FROM PlayerDB WHERE id = :id")
    fun deleteId(id: Int)


/*
    // 編集
    @Query("UPDATE user SET memo = :changeMemo WHERE userId = :id ")
    fun editMemo(id: Int, changeMemo: String)
*/

}