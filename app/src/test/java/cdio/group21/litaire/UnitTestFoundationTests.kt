package cdio.group21.litaire

import cdio.group21.litaire.data.Block
import cdio.group21.litaire.data.Card
import cdio.group21.litaire.data.Move
import cdio.group21.litaire.viewmodels.solver.DUMMY_CARD
import cdio.group21.litaire.viewmodels.solver.Game
import cdio.group21.litaire.viewmodels.solver.GameLogic
import org.junit.Assert
import org.junit.Test

class UnitTestFoundationTests {

    private var foundation: ArrayList<Card> = ArrayList()
    private val blocks: ArrayList<Block> = ArrayList()
    private var waste = DUMMY_CARD.deepCopy()
    val lastMovesMap: HashMap<String, HashMap<String, Boolean>> = HashMap()
    val gameLogic = GameLogic()




    fun initializeBlocks() {
        for (i in 0..6) {
            blocks.add(Block())
        }
    }

    /*
    * Tests if different moves are possible
    * from blocks to the foundation piles
    * they are all found
    * */
    @Test
    fun findAllMovesToFoundation() {

        foundation.add(Card(9, 'd'))
        foundation.add(Card(5, 'h'))
        foundation.add(Card(1, 's'))

        initializeBlocks()

        //  Adding cards to the blocks
        blocks[0].cards.add(Card(5, 's'))
        blocks[0].cards.add(Card(3, 'h'))
        blocks[0].cards.add(Card(2, 'c'))


        blocks[1].cards.add(Card(4, 'h'))
        blocks[1].cards.add(Card(3, 'd'))
        blocks[1].cards.add(Card(10, 'd'))


        blocks[2].cards.add(Card(4, 'h'))
        blocks[2].cards.add(Card(3, 'c'))


        blocks[3].cards.add(Card(2, 'd'))
        blocks[3].cards.add(Card(2, 's'))


        blocks[4].cards.add(Card(1, 'c'))


        // Initializing the expected moves
        val move1 = Move(true, Card(10, 'd'), 1, 0)
        val move2 = Move(true, Card(2, 's'), 3, 2)
        val move3 = Move(true, Card(1, 'c'), 4, -1)




        var result = gameLogic.allPossibleMoves(foundation, blocks, waste, lastMovesMap)

        Assert.assertEquals(result.size, 3)

        Assert.assertEquals(result.contains(move1), true)
        Assert.assertEquals(result.contains(move2), true)
        Assert.assertEquals(result.contains(move3), true)

    }

    /*
    * Test if adding a card to foundation
    * after once finding all possible moves
    * it will find the new possible move to
    * foundation
    * */
    @Test
    fun findAfterAddingCardAllPossibleMovesToFoundation() {

        foundation.add(Card(9, 'd'))
        foundation.add(Card(5, 'h'))
        foundation.add(Card(1, 's'))

        initializeBlocks()

        //  Adding cards to the blocks
        blocks[0].cards.add(Card(5, 's'))
        blocks[0].cards.add(Card(3, 'h'))
        blocks[0].cards.add(Card(2, 'c'))


        blocks[1].cards.add(Card(4, 'h'))
        blocks[1].cards.add(Card(3, 'd'))
        blocks[1].cards.add(Card(10, 'd'))


        blocks[2].cards.add(Card(4, 'h'))
        blocks[2].cards.add(Card(3, 'c'))


        blocks[3].cards.add(Card(2, 'd'))
        blocks[3].cards.add(Card(2, 's'))


        blocks[4].cards.add(Card(1, 'c'))


        var result = gameLogic.allPossibleMoves(foundation, blocks, waste, lastMovesMap)
        Assert.assertEquals(result.size, 3)

        foundation.add(Card(4, 'c'))
        result = gameLogic.allPossibleMoves(foundation, blocks, waste, lastMovesMap)

        val move1 = Move(true, Card(10, 'd'), 1, 0)
        val move2 = Move(true, Card(2, 's'), 3, 2)



        Assert.assertEquals(result.contains(move1), true)
        Assert.assertEquals(result.contains(move2), true)
        Assert.assertEquals(result.size, 2)


    }

    /*
    * Tests if no possible move to foundation
    * is possible the correct value is returned
    * */
    @Test
    fun makeNotPossibleMoveToFoundation() {

        foundation.add(Card(9, 'd'))
        foundation.add(Card(5, 'h'))
        foundation.add(Card(1, 's'))

        initializeBlocks()

        //  Adding cards to the blocks
        blocks[0].cards.add(Card(5, 's'))
        blocks[0].cards.add(Card(3, 'h'))
        blocks[0].cards.add(Card(2, 'c'))


        blocks[1].cards.add(Card(4, 'h'))
        blocks[1].cards.add(Card(3, 'd'))
        blocks[1].cards.add(Card(10, 'd'))


        blocks[2].cards.add(Card(4, 'h'))
        blocks[2].cards.add(Card(3, 'c'))


        blocks[3].cards.add(Card(2, 'd'))
        blocks[3].cards.add(Card(2, 's'))


        blocks[4].cards.add(Card(1, 'c'))


        var result = gameLogic.allPossibleMoves(foundation, blocks, waste, lastMovesMap)

        // Initializing an unexpected move
        val move = Move(true, Card(3, 'c'), 2, 3)
        Assert.assertEquals(result.contains(move), false)

    }

    /*
    * Checks if a card is moved to foundation
    * it is moved to the correct foundation pile
    * and is removed from the block
    * */
    @Test
    fun checkIfAddedToFoundationAndIfPossibleToMoveFromBlock() {

        initializeBlocks()
        val card1 = Card(9, 'd')
        val card2 = Card(5, 'h')
        foundation.add(card1)
        foundation.add(card2)

        val card3 = Card(6, 'h')
        blocks[1].cards.add(card3)

        Assert.assertEquals(foundation[0] == card1, true)
        Assert.assertEquals(foundation[1] == card2, true)

        Assert.assertEquals(blocks[1].cards.last() == card3, true)



        var moves = gameLogic.allPossibleMoves(foundation, blocks, waste, lastMovesMap)
        val game = Game()

        moves.forEach {
            game.moveFromBlockToFoundation(it, foundation, blocks)
        }

        Assert.assertEquals(foundation[1] == card3, true)
        Assert.assertEquals(blocks[1].cards.isEmpty(), true)

    }

    /*
    * Checks if a card is moved to foundation
    * it is moved to the correct foundation pile
    * and is removed from the block
    * */
    @Test
    fun moveFromBlockToFoundation() {
        initializeBlocks()
        val detect1 = Card(9, 'd')
        val detect2 = Card(5, 'h')
        foundation.add(detect1)
        foundation.add(detect2)

        val detect4 = Card(5, 'c')
        blocks[1].cards.add(detect4)
        val detect3 = Card(6, 'h')
        blocks[1].cards.add(detect3)

        blocks[0].cards.add(Card(12, 'd'))

        Assert.assertEquals(foundation[0] == detect1, true)
        Assert.assertEquals(foundation[1] == detect2, true)

        Assert.assertEquals(blocks[1].cards.last() == detect3, true)

        val moves = gameLogic.allPossibleMoves(foundation, blocks, waste, lastMovesMap)
        val game = Game()

        Assert.assertEquals(moves.size, 1)

        moves.forEach {
            game.moveFromBlockToFoundation(it, foundation, blocks)
        }

        Assert.assertEquals(foundation[1] == detect3, true)
        Assert.assertEquals(blocks[1].cards.size, 1)
        Assert.assertEquals(blocks[1].cards.last(), detect4)
    }

    /*Test if evalBlockToFoundation works as intended*/
    @Test
    fun testEvalBlockToFoundation(){
        initializeBlocks()

        foundation.add(Card(1,'s'))

        val testCard = Card(2,'s')

        val result = gameLogic.evalBlockToFoundation(foundation[0], testCard)

        Assert.assertEquals(result, true)
    }

    /*Test if evalBlockToFoundation fails if card doesn't watch suit*/
    @Test
    fun testEvalBlockToFoundationFail(){
        initializeBlocks()

        foundation.add(Card(1,'s'))

        val testCard = Card(2,'c')

        val result = gameLogic.evalBlockToFoundation(foundation[0], testCard)

        Assert.assertEquals(result, false)
    }

    /*Test if evalBlockToFoundation works as intended*/
    @Test
    fun testEvalFoundationToBlock(){
        initializeBlocks()

        foundation.add(Card(3,'s'))

        blocks[0].cards.add(Card(4,'h'))

        val result = gameLogic.evalBlockToBlockAndWasteToBlock(blocks[0].cards.last(),foundation[0])
        val move = Move(false,foundation[0],0.toByte(),0.toByte())

        Assert.assertEquals(result, true)

        val game = Game()
        val result2 = game.moveFromFoundationToBlock(move,blocks,foundation,lastMovesMap)

        Assert.assertEquals(result2, true)
    }

    /*Test if evalBlockToFoundation works as intended*/
    @Test
    fun testEvalFoundationToBlockFail(){
        initializeBlocks()

        foundation.add(Card(2,'s'))

        blocks[0].cards.add(Card(4,'h'))

        val result = gameLogic.evalBlockToBlockAndWasteToBlock(blocks[0].cards.last(),foundation[0])
        val move = Move(false,foundation[0],0.toByte(),0.toByte())

        Assert.assertEquals(result, false)

        val game = Game()
        val result2 = game.moveFromFoundationToBlock(move,blocks,foundation,lastMovesMap)

        Assert.assertEquals(result2, false)
    }

/*    *//*Test if evalBlockToFoundation works as intended*//*
    @Test
    fun testPossibleMoveFoundationToBlock(){
        initializeBlocks()

        foundation.add(Card(3,'s'))

        blocks[0].cards.add(Card(4,'h'))

        val result = gameLogic.allPossibleMoves(foundation, blocks,waste,lastMovesMap)
        val move = Move(false, Card(3,'s'),0.toByte(),0.toByte())

        Assert.assertEquals(result.contains(move), true)
    }*/

/*    *//*Test if evalBlockToFoundation works as intended*//*
    @Test
    fun testPossibleMoveFoundationToBlockMoreCards(){
        initializeBlocks()

        foundation.add(Card(3,'s'))
        foundation.add(Card(5,'h'))

        blocks[0].cards.add(Card(5,'c'))
        blocks[0].cards.add(Card(4,'h'))

        blocks[1].cards.add(Card(5,'s'))

        blocks[2].cards.add(Card(6,'h'))

        val result = gameLogic.allPossibleMoves(foundation, blocks,waste,lastMovesMap)
        val move = Move(false, Card(3,'s'),0.toByte(),0.toByte())

        Assert.assertEquals(result.contains(move), true)
    }*/

    @Test
    fun winScenario() {
        initializeBlocks()
        foundation.add(Card(11,'s'))
        foundation.add(Card(12,'c'))
        foundation.add(Card(12,'h'))
        foundation.add(Card(12,'d'))

        blocks[0].cards.add(Card(13,'d'))

        blocks[1].cards.add(Card(13,'h'))
        blocks[1].cards.add(Card(12,'s'))

        blocks[2].cards.add(Card(13,'c'))

        blocks[3].cards.add(Card(13,'s'))

        val result = gameLogic.allPossibleMoves(foundation, blocks, waste, lastMovesMap)
        val move1 = Move(true, Card(13,'d'),0.toByte(),3.toByte())
        val move2 = Move(true, Card(12,'s'),1.toByte(),0.toByte())
        val move3 = Move(true, Card(13,'c'),2.toByte(),1.toByte())

        Assert.assertEquals(result.contains(move1),true)
        Assert.assertEquals(result.contains(move2),true)
        Assert.assertEquals(result.contains(move3),true)

        val game = Game()
        game.moveFromBlockToFoundation(move1,foundation,blocks)
        game.moveFromBlockToFoundation(move2,foundation,blocks)
        game.moveFromBlockToFoundation(move3,foundation,blocks)

        val result2 = gameLogic.allPossibleMoves(foundation, blocks, waste, lastMovesMap)
        val move4 = Move(true, Card(13,'h'),1.toByte(),2.toByte())
        val move5 = Move(true, Card(13,'s'),3.toByte(),0.toByte())

        Assert.assertEquals(result2.contains(move4),true)
        Assert.assertEquals(result2.contains(move5),true)

        game.moveFromBlockToFoundation(move4,foundation,blocks)
        game.moveFromBlockToFoundation(move5,foundation,blocks)

        Assert.assertEquals(foundation[0],Card(13,'s'))
        Assert.assertEquals(foundation[1],Card(13,'c'))
        Assert.assertEquals(foundation[2],Card(13,'h'))
        Assert.assertEquals(foundation[3],Card(13,'d'))
    }

}