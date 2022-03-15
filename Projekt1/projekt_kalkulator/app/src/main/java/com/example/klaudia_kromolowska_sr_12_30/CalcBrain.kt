package com.example.klaudia_kromolowska_sr_12_30

import android.content.Context
import java.lang.Error
import kotlin.math.pow

class CalcBrain(var context: Context) : CalcBrainInterface {
    var listOfNums = mutableListOf<Double>()
    var listOfOps = mutableListOf<String>()
    override fun addNum(num: String) {
        listOfNums.add(0, num.toDouble())
    }

    override fun addOperation(value: String) {
        listOfOps.add(0, value)
    }

    @ExperimentalStdlibApi
    override fun lookForAnswer(): Double {
        while (listOfNums.isEmpty() != true && listOfOps.isEmpty() != true) {
            listOfNums.add(eval(listOfNums.removeLast(), listOfNums.removeLast(), listOfOps.removeLast()))
        }
        return listOfNums.removeLast()
    }

    private fun eval(a: Double, b: Double, o: String): Double {
        when (o) {
            context.getString(R.string.add) -> return a + b
            context.getString(R.string.sub) -> return a - b
            context.getString(R.string.mul) -> return a * b
            context.getString(R.string.div) -> return a / b
            context.getString(R.string.proc) -> return a.rem(b)
            context.getString(R.string.sqrt) -> return b.pow(1 / a)
        }
        throw Error()
    }
}
