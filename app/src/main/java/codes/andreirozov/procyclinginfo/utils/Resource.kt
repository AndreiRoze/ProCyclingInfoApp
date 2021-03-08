package codes.andreirozov.procyclinginfo.utils

class Resource<out T>(val status: Status, val data: T?, val message: String?) {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING,
        SERVER_ERROR
    }

    companion object {
        fun <T> success(data: T): Resource<T> {
            return Resource(Status.SUCCESS, data, "Success")
        }

        fun <T> error(data: T? = null, message: String): Resource<T> {
            return Resource(Status.ERROR, data, message)
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(Status.LOADING, data, "Loading")
        }

        fun <T> serverError(data: T): Resource<T> {
            return Resource(Status.SERVER_ERROR, data, "Success request, but wrong response(code not 200-300)")
        }
    }
}