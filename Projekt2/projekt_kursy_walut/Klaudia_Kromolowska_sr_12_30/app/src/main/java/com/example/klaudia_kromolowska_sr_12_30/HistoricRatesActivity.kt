package com.example.klaudia_kromolowska_sr_12_30

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.JsonObjectRequest
import com.github.mikephil.charting.charts.LineChart
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.Collections.max

class HistoricRatesActivity : AppCompatActivity() {

    var queue: RequestQueue? = null


    lateinit var todayView: TextView
    lateinit var yesterdayView: TextView
    lateinit var chartWeekly: LineChart
    lateinit var chartMonthly: LineChart
    lateinit var currencyCode: String
    private lateinit var dataWeekly: Array<Pair<String, Double>>
    private lateinit var dataMonthly: Array<Pair<String,Double>>
    private lateinit var table: String
    private lateinit var code: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historic_rates)
        dataMonthly= emptyArray()
        dataWeekly= emptyArray()
        queue = Volley.newRequestQueue(applicationContext)
        todayView = findViewById(R.id.todayRate)
        yesterdayView = findViewById(R.id.yesterdayRate)
        chartWeekly = findViewById<View>(R.id.weeklyRates) as LineChart
        chartMonthly = findViewById<View>(R.id.monthlyRates) as LineChart

        currencyCode = intent.getStringExtra("currencyCode")!!

        getHistoricRatesWeekly()
       getHistoricRatesMonthly()
    }


    private fun getHistoricRatesWeekly() {
        val url = "http://api.nbp.pl/api/exchangerates/rates/A/$currencyCode/last/7"
        val request = JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener { response ->
                    loadDataWeekly(response)
                },
                Response.ErrorListener { repsonse ->
                })
        queue?.add(request)
    }

    private fun loadDataWeekly(response: JSONObject?) {
        response?.let{
            val rates = response.getJSONArray("rates")
            val ratesCount = rates.length()
            val tmpData = arrayOfNulls<Pair<String,Double>>(ratesCount)
            for (i in 0 until ratesCount){
                val date = rates.getJSONObject(i).getString("effectiveDate")
                val rate = rates.getJSONObject(i).getDouble("mid")
                tmpData[i] = Pair(date,rate)
            }
            this.dataWeekly = tmpData as Array<Pair<String,Double>>
        }
        setupChartWeekly()
    }

        private fun getHistoricRatesMonthly() {
            val url = "http://api.nbp.pl/api/exchangerates/rates/A/$currencyCode/last/30"
            val request = JsonObjectRequest(
                    Request.Method.GET, url, null,
                    Response.Listener { response ->
                      loadDataMonthly(response)
                    },
                    Response.ErrorListener { repsonse ->
                    })
            queue?.add(request)
        }

           private fun loadDataMonthly(response: JSONObject?) {
               dataMonthly = emptyArray()
               response?.let{
                   val rates = response.getJSONArray("rates")
                   val ratesCount = rates.length()
                   val tmpData = arrayOfNulls<Pair<String,Double>>(ratesCount)
                   for (i in 0 until ratesCount){
                       val date = rates.getJSONObject(i).getString("effectiveDate")
                       val rate = rates.getJSONObject(i).getDouble("mid")
                       tmpData[i] = Pair(date,rate)
                   }
                   dataMonthly = tmpData as Array<Pair<String,Double>>
               }
              setupChartMonthly()
           }

    private fun setupChartWeekly(){
        prepareDataForChart(chartWeekly, dataWeekly, 7) // (chart30, goldRatesWeekly.toTypedArray(), 30);
    }
    private fun setupChartMonthly() {
        yesterdayView.text = "Yesterday exchange rate: ${dataMonthly[dataMonthly.size - 2].second} PLN"
        todayView.text = "Today exchange rate: ${dataMonthly.last().second} PLN"
        prepareDataForChart(chartMonthly, dataMonthly, 30) // (chart30, goldRatesWeekly.toTypedArray(), 30);
    }

    private fun prepareDataForChart(chart: LineChart, data: Array<Pair<String,Double>>, days: Int) {
        val axisX = IntRange(0, days).toList()
        val entries = LinkedList<Entry>()
        data.forEachIndexed { index, dailyRate ->
            val entry = Entry(axisX[index].toFloat(), dailyRate.second.toFloat())
            entries.add(entry)
        }
        val dataSet: LineDataSet = LineDataSet(entries, "Exchange rates")
        val lineData: LineData = LineData(dataSet)
        dataSet.color = Color.BLACK
        dataSet.valueTextSize = 15f
        chart.data = lineData

        val xAxis = chart.xAxis
        val yAxis = chart.axisLeft
        chart.data.setDrawValues(false)
        chart.axisRight.isEnabled = false
        chart.xAxis.labelRotationAngle = 0f
        chart.axisRight.isEnabled = false

        chart.setTouchEnabled(true)
        chart.setPinchZoom(false)
        chart.description.text = "Days"
        chart.animateX(1800, com.github.mikephil.charting.animation.Easing.EaseInExpo)

        chart.apply {
            isDragEnabled = false
            setTouchEnabled(true)
            setScaleEnabled(true)
            setDrawGridBackground(false)
            setPinchZoom(false)
        }
        xAxis.apply {
            setAvoidFirstLastClipping(true)
            setDrawGridLines(false)
            isEnabled = false
        }
        val sortedRates = data.sortedByDescending { it.second }
        chart.axisLeft.apply {
            axisMaximum = sortedRates[0].second.toFloat()
            axisMinimum = sortedRates.last().second.toFloat()
            setDrawGridLines(false)
        }
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = data.size.toFloat()
        xAxis.textColor = Color.BLACK

        yAxis.textSize = 12f
        yAxis.textColor = Color.BLACK
        chart.invalidate()
    }
}

