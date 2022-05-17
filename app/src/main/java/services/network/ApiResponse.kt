package services.network

import java.lang.Exception

sealed class ApiResponse<T>(
    data: T? = null,
    exception: Exception? = null,
) {
    data class Success<T>(val data: T) : ApiResponse<T>(
        data = data,
        exception = null
    )
    data class Error<T>(val exception: Exception) : ApiResponse<T>(
        data = null,
        exception = exception
    )
}
