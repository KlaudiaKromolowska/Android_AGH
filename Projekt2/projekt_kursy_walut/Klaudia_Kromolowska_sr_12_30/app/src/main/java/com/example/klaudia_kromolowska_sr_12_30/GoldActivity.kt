package com.example.klaudia_kromolowska_sr_12_30

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import org.json.JSONArray
import org.json.JSONObject
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
class GoldActivity : AppCompatActivity() {
    var queue: RequestQueue? = null

    private var goldRatesWeekly: LinkedList<Gold> = LinkedList()
    private var goldRatesMonthly: LinkedList<Gold> = LinkedList()
    lateinit var todayView: TextView
    lateinit var yesterdayView: TextView
    lateinit var chartWeeklyGold: LineChart
    lateinit var chartMonthlyGold: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gold)
        queue = Volley.newRequestQueue(applicationContext)
        todayView = findViewById(R.id.todayRate)
        yesterdayView = findViewById(R.id.yesterdayRate)
        chartWeeklyGold = findViewById<View>(R.id.weeklyGoldRates) as LineChart
        chartMonthlyGold = findViewById<View>(R.id.monthlyGoldRates) as LineChart

        getHistoricRatesWeekly()
        getHistoricRatesMonthly()
    }


    private fun getHistoricRatesWeekly() {
        val url = "http://api.nbp.pl/api/cenyzlota/last/7?format=json"
        val request = JsonArrayRequest(
                Request.Method.GET, url, null,
                Response.Listener { response ->
                    loadDataWeekly(response)
                },
                Response.ErrorListener { repsonse ->
                })
        queue?.add(request)
    }

    private fun loadDataWeekly(response: JSONArray?) {
        response?.let {
            val ratesCount = response.length()
            val tmpData = arrayOfNulls<Gold>(ratesCount)

            for (i in 0 until response.length()) {
                val rate = response.getJSONObject(i)
                val date = rate.getString("data")
                val price = rate.getString("cena")
                tmpData[i] = Gold(i, price.toDouble())
            }
            goldRatesWeekly.addAll(tmpData as Array<Gold>)
        }
        setupChartWeekly()
    }

    private fun getHistoricRatesMonthly() {
        val url = "http://api.nbp.pl/api/cenyzlota/last/30?format=json"
        val request = JsonArrayRequest(
                Request.Method.GET, url, null,
                Response.Listener { response ->
                    loadDataMonthly(response)
                },
                Response.ErrorListener { repsonse ->
                })
        queue?.add(request)
    }

    private fun loadDataMonthly(response: JSONArray?) {
        response?.let {
            val ratesCount = response.length()
            val tmpData = arrayOfNulls<Gold>(ratesCount)

            for (i in 0 until response.length()) {
                val rate = response.getJSONObject(i)
                val date = rate.getString("data")
                val price = rate.getString("cena")
                tmpData[i] = Gold(i, price.toDouble())
            }
            goldRatesMonthly.addAll(tmpData as Array<Gold>)
        }
        setupChartMonthly()
    }

    private fun setupChartWeekly() {
        prepareDataForChart(chartWeeklyGold, goldRatesWeekly.toTypedArray(), 7) // (chart30, goldRatesWeekly.toTypedArray(), 30);
    }

    private fun setupChartMonthly() {
        yesterdayView.text = "Yesterday exchange rate: ${goldRatesMonthly[goldRatesMonthly.size - 2].price} PLN"
        todayView.text = "Today exchange rate: ${goldRatesMonthly.last().price} PLN"
        prepareDataForChart(chartWeeklyGold, goldRatesWeekly.toTypedArray(), 7) // (chart30, goldRatesWeekly.toTypedArray(), 30);
        prepareDataForChart(chartMonthlyGold, goldRatesMonthly.toTypedArray(), 30) // (chart30, goldRatesWeekly.toTypedArray(), 30);
    }

    private fun prepareDataForChart(chart: LineChart, data: Array<Gold>, days: Int) {
        val axisX = IntRange(0, days).toList()
        val entries = LinkedList<Entry>()
        data.forEachIndexed { index, dailyRate ->
            val entry = Entry(axisX[index].toFloat(), dailyRate.price.toFloat())
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
        chart.axisLeft.apply {
            axisMaximum = 220.0f
            axisMinimum = 205.0f
            setDrawGridLines(false)
        }
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = data.size.toFloat()
        xAxis.textColor = Color.BLACK
        val sortedRates = data.sortedByDescending { it.price }
        yAxis.textSize = 12f
        yAxis.textColor = Color.BLACK
        chart.invalidate()
    }
}