package codes.andreirozov.procyclinginfo.data.repository

import codes.andreirozov.procyclinginfo.data.api.ApiHelper

class MemberRepository(private val apiHelper: ApiHelper) {

    suspend fun getMembers(category: String, year: String) = apiHelper.getMembers(category, year)
}