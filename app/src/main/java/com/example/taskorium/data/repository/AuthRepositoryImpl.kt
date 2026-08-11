package com.example.taskorium.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.taskorium.core.util.Constants
import com.example.taskorium.core.util.NetworkResult
import com.example.taskorium.core.session.SessionManager
import com.example.taskorium.core.util.requestClasses.AuthRequest
import com.example.taskorium.data.local.dao.CategoryDao
import com.example.taskorium.data.local.dao.TaskoriumDao
import com.example.taskorium.data.remote.AuthApiService
import com.example.taskorium.data.remote.dto.RefreshTokenRequest
import com.example.taskorium.data.remote.dto.SupabaseAuthResponse
import com.example.taskorium.data.repository.mappers.toDomain
import com.example.taskorium.domain.model.User
import com.example.taskorium.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthApiService,
    private val dataStore: DataStore<Preferences>,
    private val sessionManager: SessionManager,
    private val taskDB: TaskoriumDao,
    private val catDB: CategoryDao
    ): AuthRepository {
    override suspend fun login(
        email: String, password: String
    ): NetworkResult<User> {
        try {
            val response = authService.login(AuthRequest(email = email, password = password))
            if (response.isSuccessful){
                val user = response.body()?.user
                val accessToken = response.body()?.accessToken
                val refreshToken = response.body()?.refreshToken
                return if (user != null && accessToken != null && refreshToken != null){
                    Log.d("api res",response.body().toString())
                    sessionManager.token = accessToken
                    sessionManager.refreshToken = refreshToken
                    dataStore.edit {preferences ->
                        preferences[Constants.TOKEN_KEY] = accessToken
                        preferences[Constants.REFRESH_TOKEN_KEY] = refreshToken
                    }
                    NetworkResult.Success(user.toDomain())
                }else{
                    NetworkResult.Error("المستخدم غير موجود")
                }
            }else{
                return when (response.code()) {
                    400 -> {
                        val error = response.errorBody()?.string()

                        if (error?.contains("invalid_credentials") == true) {
                            NetworkResult.Error("البريد الإلكتروني أو كلمة المرور غير صحيحة")
                        } else {
                            NetworkResult.Error("طلب غير صالح")
                        }
                    }

                    else -> NetworkResult.Error("حدث خطأ غير متوقع")
                }
//                return when (response.code()) {
//                    401 -> NetworkResult.Error("البريد الإلكتروني أو كلمة المرور غير صحيحة")
//                    400 -> NetworkResult.Error("صيغة البريد الإلكتروني غير صالحة")
//                    else -> NetworkResult.Error("حدث خطأ في السيرفر: ${response.message()}")
//                }
            }

        }catch (_: IOException){
            return NetworkResult.Error("فشل الاتصال، يرجى التحقق من شبكة الإنترنت")
        }catch (e: Exception){
            return NetworkResult.Error("حدث خطأ غير متوقع: ${e.localizedMessage}")
        }
    }

    override suspend fun register(
        email: String,
        password: String
    ): NetworkResult<User> {
        try {
            val response = authService.register(AuthRequest(email = email, password = password))
            if (response.isSuccessful){
                val user = response.body()?.user
                val accessToken = response.body()?.accessToken
                val refreshToken = response.body()?.refreshToken

                return if (user != null && accessToken != null && refreshToken != null){
                    sessionManager.token = accessToken
                    sessionManager.refreshToken = refreshToken
                        dataStore.edit {preferences ->
                            preferences[Constants.TOKEN_KEY] = accessToken
                            preferences[Constants.REFRESH_TOKEN_KEY] = refreshToken
                    }
                    NetworkResult.Success(user.toDomain())

                }else{
                    NetworkResult.Error("حدث خطأ اثناء تسجيل الحساب")
                }
            }else{
                return when (response.code()) {
                    409 -> NetworkResult.Error("هذا البريد الإلكتروني مسجل بالفعل")
                    400 -> NetworkResult.Error("كلمة المرور ضعيفة")
                    else -> NetworkResult.Error("فشل إنشاء الحساب: ${response.message()}")
                }
            }
        }catch (e: IOException){
            return NetworkResult.Error("لا يوجد اتصال بالإنترنت، يرجى المحاولة لاحقاً")
        } catch (e: Exception) {
            return NetworkResult.Error("حدث خطأ غير متوقع: ${e.localizedMessage}")
        }
    }

    override suspend fun refreshSession(refreshToken: String): String? {
        try {
            val response = authService.refresh(RefreshTokenRequest(refreshToken))

            return if (response.isSuccessful){
                val accessToken = response.body()?.accessToken
                val newRefreshToken = response.body()?.refreshToken
                 if (accessToken != null && newRefreshToken != null) {
                    sessionManager.token = accessToken
                    sessionManager.refreshToken = newRefreshToken
                    dataStore.edit { preferences ->
                        preferences[Constants.TOKEN_KEY] = accessToken
                        preferences[Constants.REFRESH_TOKEN_KEY] = newRefreshToken
                    }
                     accessToken
                } else  null
            } else null
        }catch (_: Exception){
            return null
        }


    }

    override fun getAuthToken(): Flow<String?> {
        return dataStore.data.map {preferences ->
            preferences[Constants.TOKEN_KEY]
//            return listOf(, preferences[Constants.REFRESH_TOKEN_KEY])
        }.flowOn(Dispatchers.IO)
    }

    override fun getRefreshToken(): Flow<String?> {
        return dataStore.data.map {preferences ->
            preferences[Constants.REFRESH_TOKEN_KEY]
//            return listOf(, preferences[Constants.REFRESH_TOKEN_KEY])
        }.flowOn(Dispatchers.IO)    }

    override suspend fun logout() {
        sessionManager.token = null
        sessionManager.refreshToken = null
        dataStore.edit {preferences ->
            preferences.remove(Constants.TOKEN_KEY)
            preferences.remove(Constants.REFRESH_TOKEN_KEY)
        }
        taskDB.clearTasks()
        catDB.clearCategories()
    }

}