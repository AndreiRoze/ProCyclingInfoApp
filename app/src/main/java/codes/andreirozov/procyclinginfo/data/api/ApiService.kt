package codes.andreirozov.procyclinginfo.data.api

import codes.andreirozov.procyclinginfo.data.model.Member
import codes.andreirozov.procyclinginfo.data.model.Team
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("members/{category}/{year}")
    suspend fun getMembers(
        @Path("category") category: String,
        @Path("year") year: String
    ): Response<List<Member>>

    @GET("teams/{category}/{year}")
    suspend fun getTeams(
        @Path("category") category: String,
        @Path("year") year: String
    ): Response<List<Team>>

}