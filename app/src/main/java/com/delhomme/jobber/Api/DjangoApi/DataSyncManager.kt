package com.delhomme.jobber.Api.DjangoApi

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.delhomme.jobber.Activity.SignUser.LoginActivity
import com.delhomme.jobber.Api.LocalApi.LocalStorageManager
import com.delhomme.jobber.Api.Repository.*
import com.delhomme.jobber.Model.*
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

interface ApiService {
    @POST("candidatures/sync/")
    fun syncCandidatures(@Body candidatures: List<Candidature>): Call<ApiResponse>
    @POST("entreprises/sync/")
    fun syncEntreprises(@Body entreprises: List<Entreprise>): Call<ApiResponse>
    @POST("relances/sync/")
    fun syncRelances(@Body relances: List<Relance>): Call<ApiResponse>

    @POST("entretiens/sync/")
    fun syncEntretiens(@Body entretiens: List<Entretien>): Call<ApiResponse>

    @POST("users/sync/")
    fun syncUsers(@Body users: List<User>): Call<ApiResponse>

    @POST("evenements/sync/")
    fun syncEvenements(@Body evenements: List<Evenement>): Call<ApiResponse>

    @POST("contacts/sync/")
    fun syncContacts(@Body contacts: List<Contact>): Call<ApiResponse>

    @POST("appels/sync/")
    fun syncAppels(@Body appels: List<Appel>): Call<ApiResponse>
}

class DataSyncManager(private val apiService: ApiService, private val context: Context) {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun syncData() {
        coroutineScope.launch {
            try {
                syncCandidatures()
                syncEntreprises()
                syncContacts()
                syncAppels()
                syncRelances()
                syncEvenements()
                syncUsers()
                syncEntretiens()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Données synchronisées avec succès", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("DataSyncManager", "Erreur lors de la synchronisation : ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erreur lors de la synchronisation : ${e.message}", Toast.LENGTH_LONG).show()
                }
                redirectToLogin()
            }
        }
    }

    private suspend fun syncCandidatures() {
        val candidatures = fetchLocalCandidatures()
        Log.d("DataSyncManager", "syncCandidatures : $candidatures")
        handleApiResponse(apiService.syncCandidatures(candidatures))
    }

    private suspend fun syncEntreprises() {
        val entreprises = fetchLocalEntreprises()
        Log.d("DataSyncManager", "syncEntreprises : $entreprises")
        handleApiResponse(apiService.syncEntreprises(entreprises))
    }

    private suspend fun syncContacts() {
        val contacts = fetchLocalContacts()
        Log.d("DataSyncManager", "syncContacts : $contacts")
        handleApiResponse(apiService.syncContacts(contacts))
    }

    private suspend fun syncAppels() {
        val appels = fetchLocalAppels()
        Log.d("DataSyncManager", "syncAppels : $appels")
        handleApiResponse(apiService.syncAppels(appels))
    }

    private suspend fun syncRelances() {
        val relances = fetchLocalRelances()
        Log.d("DataSyncManager", "syncRelances : $relances")
        handleApiResponse(apiService.syncRelances(relances))
    }

    private suspend fun syncEvenements() {
        val evenements = fetchLocalEvenements()
        Log.d("DataSyncManager", "syncEvenements : $evenements")
        handleApiResponse(apiService.syncEvenements(evenements))
    }

    private suspend fun syncUsers() {
        val users = fetchLocalUsers()
        Log.d("DataSyncManager", "syncUsers : $users")
        handleApiResponse(apiService.syncUsers(users))
    }

    private suspend fun syncEntretiens() {
        val entretiens = fetchLocalEntretiens()
        Log.d("DataSyncManager", "syncEntretiens : $entretiens")
        handleApiResponse(apiService.syncEntretiens(entretiens))
    }

    private fun fetchLocalCandidatures(): List<Candidature> {
        return CandidatureDataRepository(context).getItems()
    }

    private fun fetchLocalEntreprises(): List<Entreprise> {
        return EntrepriseDataRepository(context).getItems()
    }

    private fun fetchLocalAppels(): List<Appel> {
        return AppelDataRepository(context).getItems()
    }

    private fun fetchLocalContacts(): List<Contact> {
        return ContactDataRepository(context).getItems()
    }

    private fun fetchLocalEvenements(): List<Evenement> {
        return EvenementDataRepository(context).getItems()
    }

    private fun fetchLocalRelances(): List<Relance> {
        return RelanceDataRepository(context).getItems()
    }

    private fun fetchLocalUsers(): List<User> {
        return UserRepository(context).getItems()
    }

    private fun fetchLocalEntretiens(): List<Entretien> {
        return EntretienDataRepository(context).getItems()
    }
    private suspend fun <T> handleApiResponse(call: Call<T>) {
        return suspendCoroutine { continuation ->
            call.enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (response.isSuccessful) {
                        Log.d("DataSyncManager", "Response successful: ${response.body()}")
                        continuation.resume(Unit)
                    } else {
                        if (response.code() == 401) {
                            refreshToken { isSuccess ->
                                if (isSuccess) {
                                    continuation.resume(Unit)
                                } else {
                                    continuation.resumeWithException(RuntimeException("Token expired"))
                                }
                            }
                        } else {
                            val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                            Log.e("DataSyncManager", "Error response: $errorMessage")
                            continuation.resumeWithException(RuntimeException("Error: ${response.message()}, $errorMessage"))
                        }
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    Log.e("DataSyncManager", "API call failure: ${t.message}")
                    continuation.resumeWithException(t)
                }
            })
        }
    }


    private fun refreshToken(onComplete: (Boolean) -> Unit) {
        val refreshToken = LocalStorageManager.getRefreshToken()
        Log.d("DataSyncManager", "refreshToken : $refreshToken")
        if (refreshToken != null) {
            Log.d("DataSyncManager", "refreshToken != null call refreshToken so")
            RetrofitClient.createService(TokenService::class.java).refreshToken(mapOf("refresh" to refreshToken))
                .enqueue(object : Callback<TokenResponse> {
                    override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                Log.d("DataSyncManager", "refreshToken : it.accessToken saved : ${it.accessToken}")
                                LocalStorageManager.saveJWT(it.accessToken)
                                onComplete(true)
                            }
                        } else {
                            Log.d("DataSyncManager", "refreshToken : onComplete false...")
                            onComplete(false)
                        }
                    }

                    override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                        Log.d("DataSyncManager", "refreshToken : onFailure : ${t.message}")
                        onComplete(false)
                    }
                })
        } else {
            onComplete(false)
            Log.d("DataSyncManager", "refreshToken : refreshToken is null $refreshToken")
        }
    }

    private fun redirectToLogin() {
        Log.d("DataSyncManager", "redirectToLogin called")
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }
}
