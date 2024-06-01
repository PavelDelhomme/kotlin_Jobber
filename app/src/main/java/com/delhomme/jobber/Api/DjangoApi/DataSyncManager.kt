package com.delhomme.jobber.Api.DjangoApi

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.delhomme.jobber.Activity.SignUser.LoginActivity
import com.delhomme.jobber.Api.LocalApi.LocalStorageManager
import com.delhomme.jobber.Api.Repository.AppelDataRepository
import com.delhomme.jobber.Api.Repository.CandidatureDataRepository
import com.delhomme.jobber.Api.Repository.ContactDataRepository
import com.delhomme.jobber.Api.Repository.EntrepriseDataRepository
import com.delhomme.jobber.Api.Repository.EntretienDataRepository
import com.delhomme.jobber.Api.Repository.EvenementDataRepository
import com.delhomme.jobber.Api.Repository.RelanceDataRepository
import com.delhomme.jobber.Api.Repository.UserRepository
import com.delhomme.jobber.Model.Appel
import com.delhomme.jobber.Model.Candidature
import com.delhomme.jobber.Model.Contact
import com.delhomme.jobber.Model.Entreprise
import com.delhomme.jobber.Model.Entretien
import com.delhomme.jobber.Model.Evenement
import com.delhomme.jobber.Model.Relance
import com.delhomme.jobber.Model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

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
    fun syncData() {
        val candidatures = fetchLocalCandidatures()
        Log.d("DataSyncManager", "DataSyncedManager : syncData() : candidatures : $candidatures")
        val entreprises = fetchLocalEntreprises()
        Log.d("DataSyncManager", "DataSyncedManager : syncData() : entreprises : $entreprises")
        val contacts = fetchLocalContacts()
        Log.d("DataSyncManager", "DataSyncedManager : syncData() : contacts: $contacts")
        val appels = fetchLocalAppels()
        Log.d("DataSyncManager", "DataSyncedManager : syncData() : appels : $appels")
        val relances = fetchLocalRelances()
        Log.d("DataSyncManager", "DataSyncedManager : syncData() : relances : $relances")
        val evenements = fetchLocalEvenements()
        Log.d("DataSyncManager", "DataSyncedManager : syncData() : evenements : $evenements")
        val users = fetchLocalUsers()
        Log.d("DataSyncManager", "DataSyncedManager : syncData() : users : $users")
        val entretiens = fetchLocalEntretiens()
        Log.d("DataSyncManager", "DataSyncedManager : syncData() : entretiens : $entretiens")

        apiService.syncCandidatures(candidatures).enqueue(handleApiResponse())
        apiService.syncEntreprises(entreprises).enqueue(handleApiResponse())
        apiService.syncContacts(contacts).enqueue(handleApiResponse())
        apiService.syncAppels(appels).enqueue(handleApiResponse())
        apiService.syncRelances(relances).enqueue(handleApiResponse())
        apiService.syncEntretiens(entretiens).enqueue(handleApiResponse())
        apiService.syncEvenements(evenements).enqueue(handleApiResponse())
        apiService.syncUsers(users).enqueue(handleApiResponse())
    }

    private fun handleApiResponse(): Callback<ApiResponse> = object : Callback<ApiResponse> {
        override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {

            if (!response.isSuccessful && response.code() == 401) {
                refreshToken { isSuccess ->
                    if (isSuccess) {
                        syncData()
                        Toast.makeText(context, "Données synchronisées avec succès", Toast.LENGTH_SHORT).show()
                        Log.d("DataSyncManager", "DataSyncManager : données synchronisé avec succes.")
                    } else {
                        redirectToLogin()
                    }
                }
            } else if (response.code() == 401) {
                // Si le token est expirée essayer de le rafraîchir
                refreshToken { isSuccess ->
                    if (isSuccess) {

                        syncData()
                    } else {
                        redirectToLogin()
                    }
                }
            } else {
                Log.d("DataSyncManager", "DataSyncManager : echec de la synchronisation : ${response.body().toString()}")
                // Gérer les autres type d'errerus
                Toast.makeText(context, "Echec de la synchronisation : ${response.errorBody().toString()}", Toast.LENGTH_LONG).show()
            }
        }

        override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
            Toast.makeText(context, "Erreur réseaux: ${t.message}", Toast.LENGTH_LONG).show()
            Log.e("DataSyncManager", "Erreur onFailure into handleApiResponse : ${t.message}")
            Log.e("DataSyncManager", "Erreur réseaux: ${t.message}")
        }
    }
    private fun refreshToken(onComplete: (Boolean) -> Unit) {
        val refreshToken = LocalStorageManager.getRefreshToken()
        Log.d("DataSyncManager", "refreshToken : $refreshToken")
        if (refreshToken != null) {
            Log.d("DataSyncManager", "refreshToken != null call refreshToken so")
            RetrofitClient.createService(TokenService::class.java).refreshToken(mapOf("refresh" to refreshToken))
                .enqueue(object: Callback<TokenResponse> {
                    override fun onResponse(
                        call: Call<TokenResponse>,
                        response: Response<TokenResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                Log.d("DataSyncManager", "refrehToken : it.accessToken saved : ${it.accessToken}")
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

    private fun fetchLocalCandidatures(): List<Candidature> {return CandidatureDataRepository(context).getItems()}
    private fun fetchLocalEntreprises(): List<Entreprise> {return EntrepriseDataRepository(context).getItems()}
    private fun fetchLocalAppels(): List<Appel> {return AppelDataRepository(context).getItems()}
    private fun fetchLocalContacts(): List<Contact> {return ContactDataRepository(context).getItems()}
    private fun fetchLocalEvenements(): List<Evenement> {return EvenementDataRepository(context).getItems()}
    private fun fetchLocalRelances(): List<Relance> {return RelanceDataRepository(context).getItems()}
    private fun fetchLocalUsers(): List<User> {return UserRepository(context).getItems()}
    private fun fetchLocalEntretiens(): List<Entretien> {return EntretienDataRepository(context).getItems()}
}