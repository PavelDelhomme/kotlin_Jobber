package com.delhomme.jobber.Api.DjangoApi

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.delhomme.jobber.Activity.SignUser.LoginActivity
import com.delhomme.jobber.Api.LocalApi.LocalStorageManager
import com.delhomme.jobber.Api.Repository.*
import com.delhomme.jobber.*
import com.delhomme.jobber.Appel.model.Appel
import com.delhomme.jobber.Calendrier.Evenement
import com.delhomme.jobber.Candidature.model.Candidature
import com.delhomme.jobber.Contact.model.Contact
import com.delhomme.jobber.Entreprise.model.Entreprise
import com.delhomme.jobber.Entretien.model.Entretien
import com.delhomme.jobber.Model.*
import com.delhomme.jobber.Relance.model.Relance
import com.delhomme.jobber.SignUser.User
import com.delhomme.jobber.Utils.*
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.hash.Hashing
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import java.nio.charset.StandardCharsets

interface ApiService {
    @POST("sync/check_hashes")
    fun checkHashes(@Body hashList: List<HashModel>): Call<List<DataUpdateModel>>

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
        val localData = getAllData()
        val hashes = localData.map { HashModel(it.getId(), generateHash(it)) }

        apiService.checkHashes(hashes).enqueue(object : Callback<List<DataUpdateModel>> {
            override fun onResponse(call: Call<List<DataUpdateModel>>, response: Response<List<DataUpdateModel>>) {
                if (response.isSuccessful) {
                    val updates = response.body()
                    updates?.let {
                        updateLocalDatabase(it)
                    }
                    syncAllData()
                } else if (response.code() == 401) {
                    refreshToken { isSuccess ->
                        if (isSuccess) {
                            syncData()
                        } else {
                            redirectToLogin()
                        }
                    }
                } else {
                    Log.e("DataSyncManager", "Failed to check hashes: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<DataUpdateModel>>, t: Throwable) {
                Log.e("DataSyncManager", "Error checking hashes: ${t.message}")
            }
        })
    }

    private fun syncAllData() {
        val candidatures = fetchLocalCandidatures()
        val entreprises = fetchLocalEntreprises()
        val contacts = fetchLocalContacts()
        val appels = fetchLocalAppels()
        val relances = fetchLocalRelances()
        val evenements = fetchLocalEvenements()
        val users = fetchLocalUsers()
        val entretiens = fetchLocalEntretiens()

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
                    } else {
                        redirectToLogin()
                    }
                }
            } else if (response.isSuccessful) {
                Log.d("DataSyncManager", "Data synchronized successfully.")
                Toast.makeText(context, "Données synchronisées avec succès", Toast.LENGTH_SHORT).show()
            } else {
                Log.e("DataSyncManager", "Failed to sync data: ${response.errorBody()?.string()}")
                Toast.makeText(context, "Echec de la synchronisation : ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
            }
        }

        override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
            Log.e("DataSyncManager", "Error syncing data: ${t.message}")
            Toast.makeText(context, "Erreur réseaux: ${t.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun refreshToken(onComplete: (Boolean) -> Unit) {
        val refreshToken = LocalStorageManager.getRefreshToken()
        if (refreshToken != null) {
            RetrofitClient.createService(TokenService::class.java).refreshToken(mapOf("refresh" to refreshToken))
                .enqueue(object : Callback<TokenResponse> {
                    override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                LocalStorageManager.saveJWT(it.accessToken)
                                onComplete(true)
                            }
                        } else {
                            onComplete(false)
                        }
                    }

                    override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                        onComplete(false)
                    }
                })
        } else {
            onComplete(false)
        }
    }

    private fun redirectToLogin() {
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

    private fun fetchLocalCandidatures(): List<Candidature> { return CandidatureDataRepository(context).getItems() }
    private fun fetchLocalEntreprises(): List<Entreprise> { return EntrepriseDataRepository(context).getItems() }
    private fun fetchLocalAppels(): List<Appel> { return AppelDataRepository(context).getItems() }
    private fun fetchLocalContacts(): List<Contact> { return ContactDataRepository(context).getItems() }
    private fun fetchLocalEvenements(): List<Evenement> { return EvenementDataRepository(context).getItems() }
    private fun fetchLocalRelances(): List<Relance> { return RelanceDataRepository(context).getItems() }
    private fun fetchLocalUsers(): List<User> { return UserRepository(context).getItems() }
    private fun fetchLocalEntretiens(): List<Entretien> { return EntretienDataRepository(context).getItems() }

    private fun getAllData(): List<SyncableData> {
        return fetchLocalCandidatures() + fetchLocalEntreprises() + fetchLocalAppels() + fetchLocalContacts() + fetchLocalEvenements() + fetchLocalRelances() + fetchLocalUsers() + fetchLocalEntretiens()
    }

    private fun generateHash(data: SyncableData): String {
        return Hashing.sha256().hashString(data.toString(), StandardCharsets.UTF_8).toString()
    }

    private fun updateLocalDatabase(updates: List<DataUpdateModel>?) {
        updates?.forEach { update ->
            when (update.newData) {
                is Candidature -> CandidatureDataRepository(context).saveItem(update.newData as Candidature)
                is Entreprise -> EntrepriseDataRepository(context).saveItem(update.newData as Entreprise)
                is Appel -> AppelDataRepository(context).saveItem(update.newData as Appel)
                is Contact -> ContactDataRepository(context).saveItem(update.newData as Contact)
                is Evenement -> EvenementDataRepository(context).saveItem(update.newData as Evenement)
                is Relance -> RelanceDataRepository(context).saveItem(update.newData as Relance)
                is User -> UserRepository(context).saveItem(update.newData as User)
                is Entretien -> EntretienDataRepository(context).saveItem(update.newData as Entretien)
            }
        }
    }
}
