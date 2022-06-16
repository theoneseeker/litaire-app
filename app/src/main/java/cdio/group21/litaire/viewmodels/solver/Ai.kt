package cdio.group21.litaire.viewmodels.solver

import cdio.group21.litaire.data.*
import cdio.group21.litaire.viewmodels.solver.UtilSolver.Companion.mapDeepCopy

val FACE_DOWN_CARD_VALUE = -8
val CARDS_NOT_IN_TABLEAU_BUILD = - 5
val CARDS_TO_FOUNDATION = 5


class Ai {

    val ga = Game()
    val gameLogic = GameLogic()
    fun findBestMove(
        foundations: ArrayList<Card>,
        blocks: ArrayList<Block>,
        waste: Card,
        lastMoves: HashMap<String, HashMap<String, Boolean>>
    ): Move? {
        val depth = 8
/*        val initialState = GameSate(ga.evalFoundation(foundations), 0)
        var bestState = GameSate(ga.evalFoundation(foundations), 0)*/

        val heu1 = heuristicOne(blocks, foundations)
        val heu2 = heuristicTwo(blocks, foundations, waste)
        val initialState = GameSate( heu1, heu2, 0)
        var bestState = GameSate(heu1, heu2, 0)
        var bestMove: Move? = null
        val availableMoves = gameLogic.allPossibleMoves(foundations, blocks, waste, lastMoves)



        var isGameInLastEnd = false
        val retVal1 = heuristicFaceDown(blocks)
        if (retVal1 >= 6* FACE_DOWN_CARD_VALUE) {
            isGameInLastEnd = true
        }


        if (!isGameInLastEnd) {
            if (availableMoves.size == 1) {
                if (availableMoves[0].indexOfSourceBlock == INDEX_OF_SOURCE_BLOCK_FROM_WASTE) {
                    return availableMoves[0]
                }
            }
        }


        availableMoves.forEach {currMove ->

            val foundationsCopy = ArrayList( foundations.map { detectR -> detectR.deepCopy()})
            val blocksCopy = ArrayList(blocks.map { b -> b.deepCopy() })
            val wasteCopy = waste.copy()
            val leafValue: ArrayList<GameSate> = ArrayList()
            val mapCopy = mapDeepCopy(lastMoves)


            val newMoves = ga.move_(currMove, foundationsCopy, blocksCopy, wasteCopy, mapCopy)
            if (!newMoves) {
                return@forEach
            }

            algorithm(blocksCopy, foundationsCopy, wasteCopy, leafValue, mapCopy, depth-1)
            if(leafValue.isEmpty()){ return@forEach }

            if (isGameInLastEnd) {
                leafValue.sortBy { gs -> gs.heuristicTwoVal }

                val newSate = leafValue.last()
                if (newSate.heuristicTwoVal > bestState.heuristicTwoVal) {
                    bestMove = currMove
                    bestState = newSate

                } else if (newSate.heuristicTwoVal == bestState.heuristicTwoVal) {
                    if (newSate.length > bestState.length) {
                        bestMove = currMove
                        bestState = newSate
                    }
                }


            } else {
                leafValue.sortBy { gs -> gs.heuristicOneVal }
                val newSate = leafValue.last()
                if (newSate.heuristicOneVal > bestState.heuristicOneVal) {
                    bestMove = currMove
                    bestState = newSate

                } else if (newSate.heuristicOneVal == bestState.heuristicOneVal) {
                    if (newSate.length < bestState.length){
                        bestMove = currMove
                        bestState = bestState
                    }
                }
            }



        }

        bestState.heuristicOneVal = bestState.heuristicOneVal - initialState.heuristicOneVal
        bestState.heuristicTwoVal = bestState.heuristicTwoVal - initialState.heuristicTwoVal

        bestState.length = depth - bestState.length
        println( "The next move is: $bestMove, $bestState")

        return bestMove
    }


    private fun algorithm(
        currBlocks: ArrayList<Block>,
        currFoundations: ArrayList<Card>,
        currWaste: Card,
        leafValues: ArrayList<GameSate>,
        lastMovesMap: HashMap<String, HashMap<String, Boolean>>,
        depth: Int
    ) {

        if(depth < 1) {
            setGameState(currBlocks, currFoundations, currWaste, leafValues, depth)
            return
        }

        val newPossibleMoves = gameLogic.allPossibleMoves(currFoundations, currBlocks, currWaste, lastMovesMap)

        if(newPossibleMoves.isEmpty()) {
            setGameState(currBlocks, currFoundations, currWaste, leafValues, depth)
            return
        }

        newPossibleMoves.forEach { move ->
            val blocksCopy = ArrayList(currBlocks.map { b -> b.deepCopy() })
            val foundationsCopy = ArrayList( currFoundations.map { detectR -> detectR.deepCopy()})
            val wasteCopy = currWaste.copy()
            //val mapCopy = HashMap(lastMovesMap)
            val mapCopy = mapDeepCopy(lastMovesMap)

            ga.move_(move, foundationsCopy, blocksCopy, wasteCopy,  mapCopy)
            algorithm(blocksCopy, foundationsCopy, wasteCopy, leafValues, mapCopy, depth-1)

        }

    }



    private fun setGameState(
        blocks: ArrayList<Block>,
        foundations: ArrayList<Card>,
        waste: Card,
        leafValues: ArrayList<GameSate>,
        length: Int
    ) {

        val gameSate = GameSate(heuristicOne(blocks, foundations), heuristicTwo(blocks, foundations, waste), length)
        leafValues.add(gameSate)

    }


    fun heuristicOne(
        blocks: ArrayList<Block>,
        foundations: ArrayList<Card>
    ): Int {
        return heuristicFaceDown(blocks) +
                heuristicFoundations(foundations) +
                heuristicCardsNotInBuild(blocks, CARDS_NOT_IN_TABLEAU_BUILD)
    }



     fun heuristicFoundations(
        foundations: ArrayList<Card>
    ): Int {

        var total = 0
        foundations.forEach { f ->
            var lastCardVal = f.value
            while (lastCardVal > 0) {
               total += 5 - (lastCardVal - 1)
                lastCardVal--
            }
        }
        return total
    }


    fun heuristicFaceDown(
        blocks: ArrayList<Block>,
    ): Int {

        /**
         * TODO
         */

        var total = 0
        blocks.forEach {
            total += it.hiddenCards * FACE_DOWN_CARD_VALUE
        }
        return total
    }


    fun heuristicCardsNotInBuild(
        blocks: ArrayList<Block>,
        value: Int
    ): Int {
        var total = 0
        blocks.forEach {
            if (it.cards.isNotEmpty()) {
                val cards = GameLogic().checkBlock(it)
                if (cards == null) {
                    total += it.cards.size * value
                } else {
                    val k = cards.size - cards.size
                    total += k * value
                }
            }
        }
        return total
    }



//////////////////////////////


/*    fun heuristicTwo(
        blocks: ArrayList<Block>,
        foundations: ArrayList<Card>,
        waste: Card
    ): Int {

        return heuristicFaceDown(blocks) +
         heuristicFoundationsTwo(foundations) +
                isWasteAbleToMove(blocks, foundations, waste) +
                heuristicCardsNotInBuild(blocks,-2)
    }*/


    fun heuristicTwo(
        blocks: ArrayList<Block>,
        foundations: ArrayList<Card>,
        waste: Card
    ): Int {

        return heuristicFoundationsTwo(foundations)
    }



    fun heuristicFoundationsTwo(
        foundations: ArrayList<Card>
    ): Int {
        var total = 0
        foundations.forEach { f ->
            total += 5 * f.value
        }
        return total
    }


    fun isWasteAbleToMove(
        blocks: ArrayList<Block>,
        foundations: ArrayList<Card>,
        waste: Card
    ): Int {
        var total = waste.value.toInt()

        blocks.forEach { b ->
            b.cards.forEach{ c ->
                total++
            }
        }

        foundations.forEach {
            total += it.value
        }
        return total
    }


}
