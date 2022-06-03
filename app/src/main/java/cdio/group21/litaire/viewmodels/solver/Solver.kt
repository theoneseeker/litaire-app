package cdio.group21.litaire.viewmodels.solver

import android.content.ContentValues.TAG
import android.graphics.RectF
import android.nfc.Tag
import android.util.Log
import cdio.group21.litaire.data.DetectionResult
import cdio.group21.litaire.data.SortedResult
import cdio.group21.litaire.viewmodels.LandingPageViewModel

class Solver {

    var waste = null
    var foundation: ArrayList<DetectionResult> = ArrayList()
    var tableaus: ArrayList<SortedResult> = ArrayList()


    init {
        simulateRandomCards()
        val landingPageViewModel = LandingPageViewModel()
        landingPageViewModel.printFoundation(foundation)
        landingPageViewModel.printTableaus(tableaus)
        Log.i(TAG, "print Tableau eval: ${evalTableau()}")
        Log.i(TAG, "print Foundation eval: ${evalFoundation()}")

    }



/* This function evaluates the foundation piles always wanting to move cards to the foundation pile*/
    fun evalFoundation(): Int{
    var sum = 0

    foundation.forEach {
        sum += it.text[0].toString().toInt()
        }
    return sum
    }
/* This function evaluates the tableau should find the optimal step which would be chronologically build the piles*/
    fun evalTableau():Int {

        var size = 0

        tableaus.forEach {

            if (it.block.size > size) {
                size = it.block.size
            }

        }
      return size
    }


    fun simulateRandomCards() {

        foundation.add(DetectionResult(RectF(), "9d"))
        foundation.add(DetectionResult(RectF(), "5h"))
        foundation.add(DetectionResult(RectF(), "1s"))
        foundation.add(DetectionResult(RectF(), "4c"))

        val suits = arrayOf("s", "h", "d", "c")
        val values = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)


        for (i in 0..6) {
            val k = SortedResult(0f, 0f, ArrayList())
            tableaus.add(k)
        }


        for (suit in suits) {

            for (value in values) {

                val randomInt = (0..6).random()
                val card = value.toString() + suit

                tableaus[randomInt].block.add(DetectionResult(RectF(), card))

            }
        }
    }

}