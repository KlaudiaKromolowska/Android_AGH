package com.example.klaudia_kromolowska_sr_12_30

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.blongho.country_data.World
import java.util.*

/**
 * Autor: Klaudia Kromolowska, grupa sroda 12:30
 *
 * Wykonano szystkie punkty poza:
 *  - (pkt 3) obsługą obu tabel (dane są pobierane jedynie z tabeli A)
 *  - (pkt 4e) strzałki zielone/czerwone oznaczjące zmiany kursu walut w stosunku do poprzedniego dnia
 *
 * */
class MainActivity : AppCompatActivity() {
    var tableData: LinkedList<CurrencyDetails> = LinkedList<CurrencyDetails>()
    var isData:Boolean = false
    lateinit var tableButton:Button
    lateinit var goldButton: Button
    lateinit var exchangeButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tableButton = findViewById(R.id.choosingLists)
        //tableButton.setOnClickListener{addData { navigateToCurrenciesList() } }
        tableButton.setOnClickListener{navigateToCurrenciesList()}
        goldButton=findViewById(R.id.choosingGold)
        goldButton.setOnClickListener{navigateToGold()}
        exchangeButton=findViewById(R.id.choosingExchange)
        exchangeButton.setOnClickListener{navigateToExchange()}
        World.init(applicationContext)
    }

    private fun navigateToGold(){
        if (!verifyAvailableNetwork()) {
            Toast.makeText(this, "THERE IS SOME NETWORK ERROR",  Toast.LENGTH_LONG).show()
            return
        }

        val intent = Intent(this, GoldActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToExchange(){
        if (!verifyAvailableNetwork()) {
            Toast.makeText(this, "THERE IS SOME NETWORK ERROR",  Toast.LENGTH_LONG).show()
            return
        }
        val intent = Intent(this, ExchangeActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToCurrenciesList(){
        if (!verifyAvailableNetwork()) {
            Toast.makeText(this, "THERE IS SOME NETWORK ERROR",  Toast.LENGTH_LONG).show()
            return
        }
        val intent = Intent(this, ListOfCurrencies::class.java)
        startActivity(intent)
    }


    fun verifyAvailableNetwork(activity:AppCompatActivity = this):Boolean{
        val connectivityManager=activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo=connectivityManager.activeNetworkInfo
        return  networkInfo!=null && networkInfo.isConnected
    }

}