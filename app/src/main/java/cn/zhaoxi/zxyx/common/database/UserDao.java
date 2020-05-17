package cn.zhaoxi.zxyx.common.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import cn.zhaoxi.zxyx.data.entity.User;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(User user);

    @Delete
    void delete(User user);

    @Query("DELETE FROM User")
    void deleteAll();

    @Query("SELECT * FROM User")
    LiveData<List<User>> getAllUser();

    @Query("SELECT * FROM User LIMIT :count")
    LiveData<List<User>> getUser(int count);

    @Query("SELECT * FROM User WHERE userId = :userId LIMIT :count")
    List<User> getUser(long userId, int count);

    @Update
    int update(User user);
}
