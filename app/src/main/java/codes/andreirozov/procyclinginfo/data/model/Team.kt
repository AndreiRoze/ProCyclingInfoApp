package codes.andreirozov.procyclinginfo.data.model

import com.google.gson.annotations.SerializedName

data class Team(
        @SerializedName("id") val id: Long,
        @SerializedName("team_code") val teamCode: String,
        @SerializedName("team_name") val teamName: String,
        @SerializedName("team_category") val teamCategory: String,
        @SerializedName("country") val country: String,
        @SerializedName("continent") val continent: String,
        @SerializedName("year") val year: String
)
