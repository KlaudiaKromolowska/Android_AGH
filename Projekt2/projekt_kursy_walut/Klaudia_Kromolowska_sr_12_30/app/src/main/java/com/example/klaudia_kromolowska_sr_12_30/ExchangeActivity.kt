package com.example.klaudia_kromolowska_sr_12_30

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import org.json.JSONArray
import java.lang.NumberFormatException

class ExchangeActivity : AppCompatActivity() {
    internal lateinit var adapter: CurrenciesListAdapter
    lateinit var currencies: Array<CurrencyDetails>
    lateinit var currenciesCodes: Array<String>

    lateinit var choosingCurrencies: EditText
    lateinit var resultButton: Button
    lateinit var number: EditText
    lateinit var resultText: TextView
    lateinit var intoPLN: RadioButton
    lateinit var selectedCurrency: CurrencyDetails
    var toPLN: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currencies = emptyArray()
        adapter = CurrenciesListAdapter(emptyArray(), this)
        setContentView(R.layout.exchange)
        DataHolder.prepare(applicationContext)
        makeRequest()
        intoPLN = findViewById(R.id.intoPLN)
        choosingCurrencies = findViewById(R.id.currencyInput2)
        resultButton = findViewById(R.id.makeExchange)
        number = findViewById(R.id.currencyInput)
        resultText = findViewById(R.id.RESULT)

        resultButton.setOnClickListener { convertCurrency() }
        currenciesCodes = getCurrenciesCodes(currencies).toTypedArray()
    }


    private fun getCurrenciesCodes(currencies: Array<CurrencyDetails>): List<String> {
        return currencies.map { currency -> currency.currencyCode }.sorted()
    }

    private fun convertCurrency() {

        if(intoPLN.isChecked) toPLN=true else toPLN=false
        val result: String
        var currCode: String = ""
        var num: Double? = null
        selectedCurrency = CurrencyDetails("", 0.0,0)
        currCode = (choosingCurrencies.text.toString())
        currenciesCodes = getCurrenciesCodes(currencies).toTypedArray()

        if (currenciesCodes.contains(currCode)) {
            selectedCurrency = currencies.find { x -> x.currencyCode == currCode}!!
        }

        try {

            num = (number.text.toString()).toDoubleOrNull()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "INVALID NUMBER", Toast.LENGTH_SHORT).show()
        }
        if (selectedCurrency == CurrencyDetails("", 0.0,0)) {
            Toast.makeText(this, "INVALID CURRENCY", Toast.LENGTH_SHORT).show()
        }
        if (num == null) return Toast.makeText(this, "INVALID NUMBER", Toast.LENGTH_SHORT).show()
       if (toPLN) result = "${num * selectedCurrency.rate} PLN"
        else result = "${num / selectedCurrency.rate} ${selectedCurrency.currencyCode}"
        resultText.text = result

    }



    fun makeRequest(){
        val queue = DataHolder.queue
        val url = "http://api.nbp.pl/api/exchangerates/tables/A?format=json"

        val currencyListRequest = JsonArrayRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    println("Success!")
                    loadData(response)
                    adapter.dataSet=currencies
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
            this.currencies = tmpData as Array<CurrencyDetails>
        }
    }

}