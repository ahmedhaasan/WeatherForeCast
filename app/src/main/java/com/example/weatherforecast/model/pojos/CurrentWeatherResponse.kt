import com.google.gson.annotations.SerializedName

data class Coord(
    val lon: Double,
    val lat: Double
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Main(
    val temp: Double,
    val feelsLike: Double,
   val tempMin: Double,
   val tempMax: Double,
    val pressure :Int,
    val humidity:Int,
   val seaLevel: Int? = null,
    val grndLevel: Int? = null
)

data class Wind(
    val speed: Double,
    val deg: Int? = null,
    val gust: Double? = null
)

data class Clouds(
    val all: Int
)

data class Sys(
    val type: Int,
    val id: Int,
    val country: String,
    val sunrise: Int,
    val sunset: Int
)
