package codes.andreirozov.procyclinginfo.data.model

import com.google.gson.annotations.SerializedName

data class Member(
        @SerializedName("id")
        val id: Long,
        @SerializedName("function")
        val function: String,
        @SerializedName("last_name")
        val LastName: String,
        @SerializedName("first_name")
        val firstName: String,
        @SerializedName("birth_date")
        val birthDate: String,
        @SerializedName("gender")
        val gender: String,
        @SerializedName("team_category")
        val teamCategory: String,
        @SerializedName("country")
        val country: String,
        @SerializedName("continent")
        val continent: String,
        @SerializedName("team_code")
        val teamCode: String,
        @SerializedName("team_name")
        val teamName: String,
        @SerializedName("uci_id")
        val uciId: String,
        @SerializedName("year")
        val year: String
)
