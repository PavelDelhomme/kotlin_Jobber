package com.delhomme.jobber.Utils

import android.util.JsonReader
import android.util.Log
import com.delhomme.jobber.Model.Candidature
import com.delhomme.jobber.Model.Contact
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonWriter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.nio.charset.StandardCharsets

interface ApiService {
    @POST("sync/check_hashes")
    fun checkHashes(@Body hashList: List<HashModel>): Call<List<DataUpdateModel>>
}

interface SyncableData {
    fun getId(): String
    // Autres méthode communes a faire
}

val candidature : Candidature

class HashBasedSyncro {
}

data class HashModel(
    val id: String,
    val hash: String
)

data class DataUpdateModel(
    val id: String,
    val newData: SyncableData
)

fun sendHashesToServer() {
    val hashes = getAllData().map { HashModel(it.id, generateHash(it)) }
    apiService.checkHashes(hashes).enqueue(object: Callback<List<DataUpdateModel>> {
        override fun onResponse(
            call: Call<List<DataUpdateModel>>,
            response: Response<List<DataUpdateModel>>
        ) {
            updateLocalDatabase(response.body())
        }

        override fun onFailure(call: Call<List<DataUpdateModel>>, t: Throwable) {
            Log.e("Sync", "Error syncing : ${t.message}")
        }
    }
    )
}

fun generateHash(data: DataType): String {
    return Hashing.sha256().hashString(data.toString(), StandardCharsets.UTF_8).toString()
}

fun updateLocalDatabase(updates: List<DataUpdateModel>?) {
    updates?.forEach { update ->
        localDatabase.updateData(update.id, update.newData)
    }
}

class HashBasecSyncro {

}

class SyncableDataAdapterFactory : TypeAdapterFactory {
    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        if (!SyncableData::class.java.isAssignableFrom(type.rawType)) {
            return null
        }

        val delegate = gson.getDelegateAdapter(this, type)
        val elementAdapter = gson.getAdapter(JsonElement::class.java)

        return object : TypeAdapter<T>() {
            override fun write(out: JsonWriter?, value: T) {
                delegate.write(out, value)
            }

            override fun read(reader: JsonReader): T {
                val jsonElement = elementAdapter.read(reader)
                val jsonObject = jsonElement.asJsonObject
                val id = jsonObject.get("id").asString

                val specificTypeAdapter = when {
                    "Condition Candidature" -> gson.getDelegateAdapter(this, TypeToken.get(Candidature::class.java))
                    "Condition Contact" -> gson.getDelegateAdapter(this, TypeToken.get(Contact::class.java))
                    //ETC
                    else -> throw IllegalStateException("Type non géré")
                }
                return specificTypeAdapter.fromJsonTree(jsonElement)
            }
        }
    }
}

val gson = GsonBuilder()
    .registerTypeAdapterFactory(SyncableDataAdapterFactory())
    .create()

val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build()