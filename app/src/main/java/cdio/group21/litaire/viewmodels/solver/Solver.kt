package cdio.group21.litaire.viewmodels.solver

import cdio.group21.litaire.data.Card
import cdio.group21.litaire.viewmodels.LandingPageViewModel

class Solver {

    companion object{
        val lastMoves: HashMap<String, HashMap<String, Boolean>> = HashMap()
    }

    //var waste = Card(0, 'k')
    //private var waste = Card(0, 'k')

    private var waste = DUMMY_CARD.deepCopy()

    private var foundations: ArrayList<Card> = ArrayList()
    private val blocks: ArrayList<ArrayList<Card>> = ArrayList()




    fun initt() {

        UtilSolver.simulateRandomCards(foundations, blocks, waste)
        val landingPageViewModel = LandingPageViewModel()
/*        landingPageViewModel.printFoundation2(foundations)
        landingPageViewModel.printBlock2(block)*/

        val ai = Ai()
        val game = Game()


        //val nextMove = ai.findBestMove(foundations, blocks)
/*
        landingPageViewModel.printFoundation2(foundations)
        landingPageViewModel.printWaste2(waste)
        landingPageViewModel.printBlock2(blocks)*/


        for (i in 0..200) {
            landingPageViewModel.printFoundation2(foundations)
            landingPageViewModel.printWaste2(waste)
            landingPageViewModel.printBlock2(blocks)


            val nextMove = ai.findBestMove(foundations, blocks, waste)

            if (nextMove != null) {
                if (nextMove.indexOfSourceBlock.toInt() == 8) {
                    println()
                }

                game.move_(nextMove, foundations, blocks, waste, lastMoves)

                if (waste == DUMMY_CARD) {
                    UtilSolver.cardDeck.removeLast()
                    waste = UtilSolver.cardDeck.last().deepCopy()
                }



            } else {

                //landingPageViewModel.printWaste2(waste)

                UtilSolver.cardDeck.shuffle()



                waste = UtilSolver.cardDeck.last().deepCopy()
                landingPageViewModel.printWaste2(waste)


                println("No more move available!")
               // break

            }
        }


        println("Cards left in the deck, deck size is: ${UtilSolver.cardDeck.size}")
        UtilSolver.cardDeck.forEach {
            print(" ${it.value}${it.suit}")
        }
        println()

/*        lastMoves.forEach {
            println("${it.key}:  ${it.value}")
        }*/

    }






}






/*    init {
        UtilSolver.simulateRandomCards(foundation, blocks)
        val landingPageViewModel = LandingPageViewModel()
        landingPageViewModel.printFoundation(foundation)
        landingPageViewModel.printBlocks(blocks)
*//*        Log.i(TAG, "print Blocks eval: ${evalBlock()}")
        Log.i(TAG, "print Foundation eval: ${evalFoundation()}")*//*


        val k = GameLogic.allPossibleMoves(foundation, blocks)

        k.forEach {
            Log.i(TAG, "print100: $it")
        }

    }*/
