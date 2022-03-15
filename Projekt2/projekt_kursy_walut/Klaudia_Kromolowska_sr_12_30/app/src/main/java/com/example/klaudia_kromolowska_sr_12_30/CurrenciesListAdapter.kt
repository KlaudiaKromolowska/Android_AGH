package com.example.klaudia_kromolowska_sr_12_30

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CurrenciesListAdapter(var dataSet: Array<CurrencyDetails>, val context : Context): RecyclerView.Adapter<CurrenciesListAdapter.ViewHolder>() {
    class ViewHolder(view: View) :RecyclerView.ViewHolder(view){
        val currencyCodeTextView: TextView
        val rateTextView: TextView
        val flagView: ImageView
        init {
            currencyCodeTextView = view.findViewById(R.id.currencyCodeTextView)
            rateTextView = view.findViewById(R.id.currencyRateTextView)
            flagView = view.findViewById(R.id.flagView)

        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.currency_row, viewGroup, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val currency = dataSet[position]
        viewHolder.currencyCodeTextView.text = currency.currencyCode
        viewHolder.rateTextView.text = currency.rate.toString()
        viewHolder.flagView.setImageResource(currency.flag)
        viewHolder.itemView.setOnClickListener({goToDetails(currency.currencyCode)})
    }

    private fun goToDetails(currencyCode: String) {
        val intent = Intent(context, HistoricRatesActivity::class.java).apply{
            putExtra("currencyCode", currencyCode)
        }
        context.startActivity(intent)

    }


    override fun getItemCount() = dataSet.size
}
