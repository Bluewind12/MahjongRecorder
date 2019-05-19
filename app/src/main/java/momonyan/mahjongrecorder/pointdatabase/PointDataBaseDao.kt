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


    // 編集
    @Query("UPDATE PointDB SET Name1 = :name1 ,Point1 = :point1,Name2 = :name2 ,Point2 = :point2,Name3 = :name3 ,Point3 = :point3,Name4 = :name4 ,Point4 = :point4 WHERE id = :id ")
    fun updateData(
        id: Int,
        name1: String,
        point1: Int,
        name2: String,
        point2: Int,
        name3: String,
        point3: Int,
        name4: String,
        point4: Int
    )

}