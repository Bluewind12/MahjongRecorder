package momonyan.mahjongrecorder.playerdatabase

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface PlayerDataBaseDao {
    @Query("SELECT * FROM PlayerDB")
    fun getAll(): LiveData<List<PlayerDB>>

    @Query("SELECT * FROM PlayerDB WHERE id IN (:id)")
    fun getById(vararg id: Int): List<PlayerDB>


    //データ作成
    @Insert
    fun insert(playerDB: PlayerDB)


    // データモデルのクラスを引数に渡すことで、データの削除ができる。主キーでデータを検索して削除する場合。
    @Delete
    fun delete(id: Int)

    // 条件でDelete
    @Query("DELETE FROM PlayerDB WHERE id = :id")
    fun deleteId(id: Int)


/*
    // 編集
    @Query("UPDATE user SET memo = :changeMemo WHERE userId = :id ")
    fun editMemo(id: Int, changeMemo: String)
*/

}