package cdio.group21.litaire.viewmodels.solver

import cdio.group21.litaire.data.Card
val DUMMY_CARD = Card(-2, 'k')
val INDEX_OF_SOURCE_BLOCK_FROM_FOUNDATION: Byte = 8

class UtilSolver {
    companion object {


        val cardDeck : ArrayList<Card> = ArrayList()
        val suits: Array<Char> = arrayOf('s', 'h', 'd', 'c')
        val values: Array<Byte> = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13)



        fun simulateRandomCards(
            foundation: ArrayList<Card>,
            blocks: ArrayList<ArrayList<Card>>,
            waste: Card
        ) {

/*            foundation.add(Card(9, "d"))
            foundation.add(Card(5, "h"))
            foundation.add(Card(1, "s"))
            foundation.add(Card(4, "c"))*/





            for (i in 0..6) {
                blocks.add(ArrayList())
            }


            for (suit in suits) {

                for (value in values) {
                    val card = Card(value, suit)
                    cardDeck.add(card)
                }
            }

            cardDeck.shuffle()
            cardDeck.shuffle()
            cardDeck.shuffle()



            for (i in 0..6) {

                var k = i +1
                while (k > 0) {
                    blocks[i].add(cardDeck.last())
                    cardDeck.removeLast()
                    k--
                }

            }


            waste.value = cardDeck.last().value
            waste.suit = cardDeck.last().suit

        }


        fun solvableCardDeck(
            foundation: ArrayList<Card>,
            blocks: ArrayList<ArrayList<Card>>,
            waste: Card
        ) {

            for (i in 0..6) {
                blocks.add(ArrayList())
            }


            for (suit in suits) {

                for (value in values) {
                    val card = Card(value, suit)
                    cardDeck.add(card)
                }
            }

            val i = 0
            cardDeck.forEach {
                println("$i: ${it.value}${it.suit}")
            }






        }





    }
}