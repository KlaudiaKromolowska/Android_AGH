package com.example.klaudia_kromolowska_sr_12_30

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import org.json.JSONArray

class ListOfCurrencies : AppCompatActivity() {

    internal lateinit var currenciesListRecyclerView: RecyclerView
    internal lateinit var adapter: CurrenciesListAdapter
    internal lateinit var data: Array <CurrencyDetails>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_of_currencies)
        currenciesListRecyclerView = findViewById(R.id.currenciesListRecyclerView)
        currenciesListRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CurrenciesListAdapter(emptyArray(), this)
        currenciesListRecyclerView.adapter = adapter
        DataHolder.prepare(applicationContext)
        makeRequest()
    }
    fun makeRequest(){
        val queue = DataHolder.queue
        val url = "http://api.nbp.pl/api/exchangerates/tables/A?format=json"

        val currencyListRequest = JsonArrayRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    println("Success!")
                    loadData(response)
                    adapter.dataSet=data
                    adapter.notifyDataSetChanged()
                },
                Response.ErrorListener{ response->
                })
        queue.add(currencyListRequest)
    }

    private fun loadData(response: JSONArray?){
        response?.let{
            val rates = response.getJSONObject(0).getJSONArray("rates")
            val ratesCount = rates.length()
            val tmpData = arrayOfNulls<CurrencyDetails>(ratesCount)
            for (i in 0 until ratesCount){
                val currencyCode = rates.getJSONObject(i).getString("code")
                val currencyRate = rates.getJSONObject(i).getDouble("mid")
                val flagID = DataHolder.getFlagForCurrency(currencyCode)
                val currencyObject = CurrencyDetails(currencyCode, currencyRate, flagID)  //tabela i increase
                tmpData[i] = currencyObject
            }
            this.data = tmpData as Array<CurrencyDetails>
        }
    }
}