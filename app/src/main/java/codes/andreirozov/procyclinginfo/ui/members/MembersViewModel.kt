package codes.andreirozov.procyclinginfo.ui.members

import androidx.lifecycle.*
import codes.andreirozov.procyclinginfo.data.repository.MemberRepository
import codes.andreirozov.procyclinginfo.utils.Resource
import kotlinx.coroutines.Dispatchers

class MembersViewModel(
    private val memberRepository: MemberRepository,
    private val raceType: String
) : ViewModel() {

    private val year: MutableLiveData<String> = MutableLiveData()

    val members = year.switchMap {
        liveData(Dispatchers.IO) {

            emit(Resource.loading(null))

            try {

                val data = memberRepository.getMembers(raceType, it)
                if (data.isSuccessful) {
                    emit(Resource.success(data))
                } else emit(Resource.serverError(data))

            } catch (exception: Exception) {
                emit(Resource.error(null, exception.message ?: "Error occurred."))
            }
        }
    }

    fun setYear(year: String) {
        this.year.value = year
    }
}