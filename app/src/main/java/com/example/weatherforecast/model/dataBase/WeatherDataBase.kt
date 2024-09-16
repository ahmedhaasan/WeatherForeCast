
import WeatherResponse
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context


@Database(entities = [WeatherResponse::class], version = 1)
abstract class WeatherDataBase : RoomDatabase() {
    abstract fun getWeatherDao(): WeatherDao

    companion object {
        @Volatile
        private var instance: WeatherDataBase? = null

        fun getInstance(context: Context): WeatherDataBase {
            return instance ?: synchronized(this) {
                val tempInstance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDataBase::class.java,
                    "CurrentWeather"
                ).build()
                instance = tempInstance
                tempInstance
            }
        }
    }
}
