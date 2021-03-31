package codes.andreirozov.procyclinginfo.data.api

class ApiHelper(private val apiService: ApiService) {

    suspend fun getMembers(category: String, year: String) = apiService.getMembers(category, year)

    suspend fun getTeams(category: String, year: String) = apiService.getTeams(category, year)
}