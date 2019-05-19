package momonyan.mahjongrecorder.pointdatabase

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface PointDataBaseDao {
    @Query("SELECT * FROM PointDB")
    fun getAll(): LiveData<List<PointDB>>

    @Query("SELECT * FROM PointDB WHERE id IN (:id)")
    fun getById(vararg id: Int): List<PointDB>

    @Query("SELECT * FROM PointDB WHERE name1 = :name OR name2 = :name OR name3 = :name OR name4 = :name ")
    fun getByName(name: String): LiveData<List<PointDB>>


    //データ作成
    @Insert
    fun insert(pointDB: PointDB)

    // 条件でDelete
    @Query("DELETE FROM PointDB WHERE id = :id")
    fun deleteId(id: Int)



/*
    // 編集
    @Query("UPDATE user SET memo = :changeMemo WHERE userId = :id ")
    fun editMemo(id: Int, changeMemo: String)
*/

}