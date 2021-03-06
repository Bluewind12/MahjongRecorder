package momonyan.mahjongrecorder.pointdatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PointDataBaseDao {
    @Query("SELECT * FROM PointDB")
    fun getAll(): LiveData<List<PointDB>>


    @Query("SELECT * FROM PointDB WHERE( name1 = :name OR name2 = :name OR name3 = :name OR name4 = :name) AND( name1 = :name2 OR name2 = :name2 OR name3 = :name2 OR name4 = :name2)AND( name1 = :name3 OR name2 = :name3 OR name3 = :name3 OR name4 = :name3)AND( name1 = :name4 OR name2 = :name4 OR name3 = :name4 OR name4 = :name4)")
    fun getMatchData(name: String,name2: String,name3: String,name4: String): LiveData<List<PointDB>>


    @Query("SELECT * FROM PointDB WHERE( name1 = :name OR name2 = :name OR name3 = :name OR name4 = :name) AND( name1 = :name2 OR name2 = :name2 OR name3 = :name2 OR name4 = :name2)AND( name1 = :name3 OR name2 = :name3 OR name3 = :name3 OR name4 = :name3)")
    fun getAnyMatchData(name: String,name2: String,name3:String): LiveData<List<PointDB>>

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