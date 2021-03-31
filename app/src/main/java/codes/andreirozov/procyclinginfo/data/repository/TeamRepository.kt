package codes.andreirozov.procyclinginfo.data.repository

import codes.andreirozov.procyclinginfo.data.api.ApiHelper

class TeamRepository(private val apiHelper: ApiHelper) {

    suspend fun getTeams(category: String, year: String) = apiHelper.getTeams(category, year)
}