package com.delhomme.jobber.Api.Repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

abstract class BaseDataRepository<T>(val context: Context, private val sharedPrefsKey: String) {
    protected var items: MutableList<T>? = null
    private val gson = Gson()

    init {
        items = loadItems().toMutableList()
    }

    fun getItems(): List<T> {
        return items ?: emptyList()
    }

    fun saveItem(item: T) {
        val mutableItems = items ?: mutableListOf()
        updateOrAddItem(mutableItems, item)
        items = mutableItems
        saveItemsToPrefs(items!!)
    }

    fun loadItems(): List<T> {
        val jsonString = context.getSharedPreferences("JobberPrefs", Context.MODE_PRIVATE).getString(sharedPrefsKey, null)
        return if (jsonString != null) {
            val type = object : TypeToken<List<T>>() {}.type
            gson.fromJson(jsonString, type)
        } else {
            emptyList()
        }
    }

    protected fun saveItemsToPrefs(items: List<T>) {
        val jsonString = gson.toJson(items)
        context.getSharedPreferences("JobberPrefs", Context.MODE_PRIVATE).edit().putString(sharedPrefsKey, jsonString).apply()
    }

    abstract fun updateOrAddItem(mutableItems: MutableList<T>, item: T)
    fun findByCondition(predicate: (T) -> Boolean): List<T> {
        return items?.filter(predicate) ?: emptyList()
    }

    fun <R> loadRelatedItemsById(fieldAccessor: (T) -> R?, id: R): List<T> {
        return items?.filter { fieldAccessor(it) == id } ?: emptyList()
    }

    fun deleteItem(predicate: (T) -> Boolean) {
        items?.let { itemList ->
            val itemToRemove = itemList.firstOrNull(predicate)
            itemToRemove?.let {
                itemList.remove(it)
                saveItemsToPrefs(itemList)
            }
        }
    }
    fun <R> loadItemsWhereCollectionContains(fieldAccessor: (T) -> Collection<R>, value: R): List<T> {
        return items?.filter { value in fieldAccessor(it) } ?: emptyList()
    }
    // TODO normalement cette méthode fonctionne si une Collection est passé ou simplement un objet unique
    fun <R> loadRelatedItemsById2(fieldAccessor: (T) -> Any?, id: R): List<T> {
        return items?.filter { item ->
            val fieldValue = fieldAccessor(item)
            when (fieldValue) {
                is Collection<*> -> id in fieldValue
                else -> id == fieldValue
            }
        } ?: emptyList()
    }
    fun <R> getItemsGroupedBy(fieldSelector: (T) -> R): Map<R, List<T>> {
        return items?.groupBy(fieldSelector) ?: emptyMap()
    }

    // Generic method to generate HTML for graphs
    fun generateHtmlForGraph(title: String, typeChart: String, data: Map<String, Int>): String {
        val labels = data.keys.joinToString(",")
        val values = data.values.joinToString(",")
        return """
            <html>
            <head>
                <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
            </head>
            <body>
                <h1>$title</h1>
                <canvas id="myChart" width="400" height="400"></canvas>
                <script>
                    var ctx = document.getElementById('myChart').getContext('2d');
                    var myChart = new Chart(ctx, {
                        type: '$typeChart',
                        data: {
                            labels: [$labels],
                            datasets: [{
                                label: '# of Votes',
                                data: [$values],
                                backgroundColor: 'rgba(255, 99, 132, 0.2)',
                                borderColor: 'rgba(255, 99, 132, 1)',
                                borderWidth: 1
                            }]
                        },
                        options: {
                            scales: {
                                y: {
                                    beginAtZero: true
                                }
                            }
                        }
                    });
                </script>
            </body>
            </html>
        """
    }

    fun getLast7DaysData(dayOffset: Int, dateExtractor: (T) -> Date): Map<String, Int> {
        val format = SimpleDateFormat("yyyy-MM-dd")
        val endDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, dayOffset) }
        val startDate = endDate.clone() as Calendar
        startDate.add(Calendar.DAY_OF_YEAR, -6)

        val counts = mutableMapOf<String, Int>()

        for (i in 0..6) {
            val dateString = format.format(startDate.time)
            counts[dateString] = items?.count {
                val itemDate = format.format(dateExtractor(it))
                itemDate == dateString
            } ?: 0
            startDate.add(Calendar.DAY_OF_YEAR, 1)
        }

        return counts
    }
}
