
import WeatherResponse
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 *  note take care of suspended functions as you will use it inside courotine
 *  in the view Model
 */
@Dao
interface WeatherDao {
    @Query("SELECT * FROM currentWeather")
    suspend fun getCurrentWeather(): WeatherResponse

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(c_weather: WeatherResponse):Long

    @Delete
    suspend fun deleteCurrentWeather(c_weather: WeatherResponse): Int
}
