package com.example.klaudia_kromolowska_sr_12_30

interface CalcBrainInterface{
    public fun addNum(num: String)
    public fun addOperation(value:String)
    public fun lookForAnswer():Double
    fun removeLastOperation(){}
}