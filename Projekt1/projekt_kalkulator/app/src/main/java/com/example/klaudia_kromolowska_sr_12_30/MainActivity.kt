/**
 * Autorka: Klaudia Kromolowska, sr 12:30
 * Wykonano wszystkie podpunkty poza:
 *  - 9. obsluga kolejnosci wykonywania dzialan
 *
 *
 *  UWAGI:
 *  - Aby obliczyc pierwiastek podaj najpierw jego stopien, potem symbol pierwiastka i na koncu liczbe pierwiastkowana
 *  - wyniki czesciowe wyswieltaja sie po nacisnieciu przycisku "=" (w przypadku, gdy ostatnia jest operacja kalkulator prosi o dopisanie liczby)
 *  - 3c - miedzy innymi wcisniecie przycisku operacji na poczatku
 **/
package com.example.klaudia_kromolowska_sr_12_30

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import kotlin.text.last as last

class MainActivity : AppCompatActivity() {
    // definiowanie przyciskow i dzialania
    internal lateinit var cleatPressedButton: Button
    internal lateinit var answerButton: Button
    internal lateinit var dotButton: Button
    internal lateinit var listOfBts: Array<Button>
    internal lateinit var listOfOps: Array<Button>
    internal lateinit var calculatorDisplay: TextView
    internal lateinit var calculatorDisplayAns: TextView
    private var calcInuptList = ""
        
    // na poczatek setter 
    set(value) {
        if (value == "") {
            this.calculatorDisplay.text=(0).toString()
        } else {
            this.calculatorDisplay.text=value
        }
        field = value
    }

    private val calc: CalcBrainInterface = CalcBrain(this@MainActivity)
    private var ifLastIsNumber = true
    private var ifLastIsDecimal = false
    private var ifLastIsOperation = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cleatPressedButton = findViewById(R.id.clear)
        dotButton = findViewById(R.id.buttonDot)
        answerButton = findViewById(R.id.buttonAnswer)
        calculatorDisplay = findViewById(R.id.textView4)
        calculatorDisplayAns = findViewById(R.id.textView)

        val listOfButtons = arrayOf(R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9)
        val listOfoperations = arrayOf(R.id.buttonAdd, R.id.buttonSub, R.id.buttonDiv, R.id.buttonMul, R.id.sqrt, R.id.proc)
        listOfBts = (listOfButtons.map { id -> findViewById(id) as Button }).toTypedArray()
        listOfOps = (listOfoperations.map { id -> findViewById(id) as Button }).toTypedArray()

        cleatPressedButton.setOnClickListener { clearPressed() }
        answerButton.setOnClickListener { lookForAnswer() }
        dotButton.setOnClickListener { dotButtonPressed() }
        listOfBts.forEach { button -> button.setOnClickListener { i -> buttonPressed(i as Button) } }
        listOfOps.forEach { button -> button.setOnClickListener { i -> operationPressed(i as Button) } }
    }

    private fun dotButtonPressed() {
        if (this.calculatorDisplay.text.toString() == "..." || this.calculatorDisplay.text.toString() == "0") {
            calcInuptList += "0."
        } else if (!ifLastIsDecimal) {
            calcInuptList += "."
        } else {
            this.calculatorDisplay.text = "ERROR"
        }
        ifLastIsDecimal = true
        ifLastIsNumber = true
    }

    private fun operationPressed(operation: Button) {
        if (this.calculatorDisplay.text.toString() == "...") {
            calcInuptList = "0"
            ifLastIsNumber = true
            ifLastIsOperation = false
            operationPressed(operation)
        } else if (!ifLastIsOperation && ifLastIsNumber) {
            calc.addOperation(operation.text.toString())
            ifLastIsNumber = false
            ifLastIsOperation = true
        } else {
            calc.removeLastOperation()
            calc.addOperation(operation.text.toString())
            ifLastIsNumber = false
        }
    }

    private fun buttonPressed(digit: Button) {
        if (ifLastIsNumber) {
            if (calcInuptList == "0") {
                calcInuptList = digit.text.toString()
            } else {
                calcInuptList += digit.text.toString()
            }
        } else {
            calc.addNum(calcInuptList)
            calcInuptList = digit.text.toString()
        }
        ifLastIsNumber = true
        ifLastIsOperation = false
    }

    private fun lookForAnswer() {
        if (calcInuptList.last() == '.') {
            calcInuptList += 0
        }
        if (!ifLastIsOperation) {
            calc.addNum(calcInuptList)
            val result = calc.lookForAnswer()
            calcInuptList = result.toString()
            this.calculatorDisplayAns.text = result.toString()
        } else {
            this.calculatorDisplay.text = "ADD SOME NUMBER"
        }

    }
    fun clearPressed() {
        this.calculatorDisplay.text="0"
        this.calculatorDisplayAns.text = "0"
        this.calcInuptList = "0"
        this.ifLastIsNumber = true
        this.ifLastIsDecimal = false
        this.ifLastIsOperation = false
    }
}