package cdio.group21.litaire

import Card
import Rank
import Suit
import cdio.group21.litaire.data.Block
import cdio.group21.litaire.data.Move
import cdio.group21.litaire.viewmodels.solver.DUMMY_CARD
import cdio.group21.litaire.viewmodels.solver.Game
import cdio.group21.litaire.viewmodels.solver.GameLogic
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test

class UnitTestBlockTest {

	private var foundation: MutableList<Card> = mutableListOf()
	private val blocks: MutableList<Block> = mutableListOf()
	private var waste = DUMMY_CARD.deepCopy()

	val lastMovesMap: HashMap<String, HashMap<String, Boolean>> = HashMap()
	val gameLogic = GameLogic()


	fun initializeBlocks() {
		for (i in 0..6) {
			blocks.add(Block())
		}
	}

	/*
	* Tests if given two cards in different blocks
	*  if possible move of one card to the other
	* is found
	* */
	@Test
	fun findMoveCardToAnotherBlock() {

		initializeBlocks()

		blocks[0].cards.add(Card(Suit.SPADE, Rank.FIVE))
		blocks[3].cards.add(Card(Suit.HEART, Rank.SIX))


		val returnVal: MutableList<Move> = mutableListOf()

		for (indexBlock in blocks.indices) {
			val item = blocks[indexBlock]

			if (item.cards.isNullOrEmpty()) {
				continue
			}
			gameLogic.possibleMovesFromBlockToBlock(
				item,
				blocks,
				indexBlock,
				returnVal,
				lastMovesMap
			)


		}

		val expMove1 = Move(false, Card(Suit.SPADE, Rank.FIVE), 0, 3)

		Assert.assertEquals(returnVal.size, 1)
		Assert.assertEquals(returnVal[0] == expMove1, true)

	}

	/*
		* Tests if a possible move between two blocks
		* is found and if moved it is moved correctly
		* and the previous block only contains the not-moved
		* cards
		* */
	@Test
	fun moveOneCardToBlock() {
		initializeBlocks()

		val detect1 = Card(Suit.DIAMOND, Rank.QUEEN)
		val detect2 = Card(Suit.SPADE, Rank.FIVE)

		blocks[0].cards.add(detect1)
		blocks[0].cards.add(detect2)


		val detect4 = Card(Suit.CLUB, Rank.FIVE)
		val detect3 = Card(Suit.HEART, Rank.SIX)

		blocks[2].cards.add(detect4)
		blocks[2].cards.add(detect3)

		Assert.assertEquals(blocks[2].cards.size, 2)
		Assert.assertEquals(blocks[0].cards.size, 2)


		val moves = gameLogic.allPossibleMoves(foundation, blocks, waste, lastMovesMap)
		Assert.assertEquals(moves.size, 1)
		Assert.assertEquals(moves[0], Move(false, Card(Suit.SPADE, Rank.FIVE), 0, 2))


		val game = Game.emptyGame()

		moves.forEach {
			Game.moveFromBlockToBlock(game, it, blocks, lastMovesMap)
		}

		Assert.assertEquals(blocks[2].cards.last(), detect2)
		Assert.assertEquals(blocks[2].cards.size, 3)

		Assert.assertEquals(blocks[0].cards.size, 1)
		Assert.assertEquals(blocks[0].cards.last(), detect1)

	}

	/*
	* Tests if no possible moves are found
	* between blocks the correct value
	* is returned
	* */
	@Test
	fun noPossibleMoveBetweenBlocks() {
		initializeBlocks()

		val detect1 = Card(Suit.DIAMOND, Rank.QUEEN)
		val detect2 = Card(Suit.SPADE, Rank.FIVE)
		val detect5 = Card(Suit.SPADE, Rank.TEN)


		blocks[0].cards.add(detect1)
		blocks[0].cards.add(detect2)
		blocks[0].cards.add(detect5)


		val detect4 = Card(Suit.CLUB, Rank.FIVE)
		val detect3 = Card(Suit.HEART, Rank.SIX)

		blocks[2].cards.add(detect4)
		blocks[2].cards.add(detect3)


		Assert.assertEquals(blocks[0].cards.size, 3)
		Assert.assertEquals(blocks[2].cards.size, 2)


		val moves = gameLogic.allPossibleMoves(foundation, blocks, waste, lastMovesMap)
		Assert.assertEquals(moves.size, 0)

	}

	/*
	* Tests if a whole block can be moved
	* it is done so correctly
	* */
	@Test
	fun moveAllCardsToAnotherBlock() {
		initializeBlocks()

		val detect2 = Card(Suit.SPADE, Rank.FIVE)
		blocks[0].cards.add(detect2)

		val detect4 = Card(Suit.CLUB, Rank.FIVE)
		val detect3 = Card(Suit.HEART, Rank.SIX)

		blocks[2].cards.add(detect4)
		blocks[2].cards.add(detect3)

		Assert.assertEquals(blocks[0].cards.size, 1)
		Assert.assertEquals(blocks[2].cards.size, 2)


		val moves = gameLogic.allPossibleMoves(foundation, blocks, waste, lastMovesMap)
		Assert.assertEquals(moves.size, 1)
		Assert.assertEquals(moves[0], Move(false, Card(Suit.SPADE, Rank.FIVE), 0, 2))


		val game = Game.emptyGame()

		Game.moveFromBlockToBlock(game, moves[0], blocks, lastMovesMap)

		Assert.assertEquals(blocks[2].cards.last(), detect2)
		Assert.assertEquals(blocks[2].cards.size, 3)
		Assert.assertEquals(blocks[0].cards.size, 0)

	}

	/*
	* Tester at man ikke flytter kongen til en ny tom block
	* */
	@Test
	fun moveBlockToBlock5() {
		initializeBlocks()

		val detect2 = Card(Suit.SPADE, Rank.KING)
		blocks[6].cards.add(detect2)

		val detect4 = Card(Suit.CLUB, Rank.FIVE)
		val detect3 = Card(Suit.HEART, Rank.SIX)

		blocks[2].cards.add(detect4)
		blocks[2].cards.add(detect3)

		Assert.assertEquals(blocks[6].cards.size, 1)
		Assert.assertEquals(blocks[2].cards.size, 2)

		val moves = gameLogic.allPossibleMoves(foundation, blocks, waste, lastMovesMap)
		Assert.assertEquals(moves.size, 0)
	}

	/*
	* Tests if no possible moves
	* the correct value is returned
	* */
	@Test
	fun noPossibleMovesFromBlockToBlock() {

		initializeBlocks()


		blocks[0].cards.add(Card(Suit.SPADE, Rank.FIVE))
		blocks[0].cards.add(Card(Suit.HEART, Rank.THREE))
		blocks[0].cards.add(Card(Suit.CLUB, Rank.TWO))

		blocks[1].cards.add(Card(Suit.HEART, Rank.FOUR))
		blocks[1].cards.add(Card(Suit.DIAMOND, Rank.THREE))
		blocks[1].cards.add(Card(Suit.DIAMOND, Rank.TEN))

		blocks[2].cards.add(Card(Suit.HEART, Rank.JACK))
		blocks[2].cards.add(Card(Suit.SPADE, Rank.THREE))

		blocks[3].cards.add(Card(Suit.DIAMOND, Rank.TWO))
		blocks[3].cards.add(Card(Suit.SPADE, Rank.TWO))
		blocks[3].cards.add(Card(Suit.HEART, Rank.SIX))

		blocks[4].cards.add(Card(Suit.SPADE, Rank.SEVEN))
		blocks[4].cards.add(Card(Suit.CLUB, Rank.QUEEN))
		blocks[4].cards.add(Card(Suit.HEART, Rank.QUEEN))

		blocks[5].cards.add(Card(Suit.CLUB, Rank.THREE))

		var returnVal: MutableList<Move> = mutableListOf()

		for (indexBlock in blocks.indices) {
			val itemBlock = blocks[indexBlock]

			if (itemBlock.cards.isNullOrEmpty()) {
				continue
			}
			gameLogic.hasChecked = false

			gameLogic.possibleMovesFromBlockToBlock(
				itemBlock,
				blocks,
				indexBlock,
				returnVal,
				lastMovesMap
			)

			Assert.assertEquals(returnVal.size, 0)
		}
	}

	/*
	* Tests if the correct block is added to move function
	* when a move is possible
	* */
	@Test
	fun possibleMovesFromBlockToBlock() {

		initializeBlocks()

		blocks[0].cards.add(Card(Suit.SPADE, Rank.FIVE))
		blocks[0].cards.add(Card(Suit.HEART, Rank.THREE))
		blocks[0].cards.add(Card(Suit.CLUB, Rank.TWO))

		blocks[1].cards.add(Card(Suit.HEART, Rank.FOUR))
		blocks[1].cards.add(Card(Suit.DIAMOND, Rank.THREE))
		blocks[1].cards.add(Card(Suit.DIAMOND, Rank.TEN))

		blocks[2].cards.add(Card(Suit.HEART, Rank.JACK))
		blocks[2].cards.add(Card(Suit.SPADE, Rank.THREE))

		blocks[3].cards.add(Card(Suit.DIAMOND, Rank.TWO))
		blocks[3].cards.add(Card(Suit.SPADE, Rank.TWO))
		blocks[3].cards.add(Card(Suit.HEART, Rank.SIX))

		blocks[4].cards.add(Card(Suit.SPADE, Rank.SEVEN))
		blocks[4].cards.add(Card(Suit.CLUB, Rank.QUEEN))
		blocks[4].cards.add(Card(Suit.HEART, Rank.QUEEN))


		blocks[5].cards.add(Card(Suit.CLUB, Rank.THREE))

		blocks[6].cards.add(Card(Suit.CLUB, Rank.FIVE))
		blocks[6].cards.add(Card(Suit.SPADE, Rank.FOUR))

		var returnVal: MutableList<Move> = mutableListOf()

		for (indexBlock in blocks.indices) {
			val itemBlock = blocks[indexBlock]

			if (itemBlock.cards.isNullOrEmpty()) {
				continue
			}
			gameLogic.hasChecked = false

			val result = gameLogic.possibleMovesFromBlockToBlock(
				itemBlock,
				blocks,
				indexBlock,
				returnVal,
				lastMovesMap
			)

		}

		val move1 = Move(false, Card(Suit.HEART, Rank.THREE), 0, 6)

		Assert.assertEquals(returnVal.size, 1)
		Assert.assertEquals(returnVal[0], move1)
	}

	/*
	* Tests if the correct blocks are added to move function
	* when multiple moves are possible
	* */
	@Test
	fun multiplePossibleMovesFromBlockToBlock() {

		initializeBlocks()

		blocks[0].cards.add(Card(Suit.SPADE, Rank.FIVE))
		blocks[0].cards.add(Card(Suit.HEART, Rank.THREE))
		blocks[0].cards.add(Card(Suit.CLUB, Rank.TWO))

		blocks[1].cards.add(Card(Suit.HEART, Rank.FOUR))
		blocks[1].cards.add(Card(Suit.DIAMOND, Rank.THREE))
		blocks[1].cards.add(Card(Suit.DIAMOND, Rank.TEN))

		blocks[2].cards.add(Card(Suit.HEART, Rank.JACK))
		blocks[2].cards.add(Card(Suit.SPADE, Rank.THREE))

		blocks[3].cards.add(Card(Suit.DIAMOND, Rank.TWO))
		blocks[3].cards.add(Card(Suit.SPADE, Rank.TWO))
		blocks[3].cards.add(Card(Suit.HEART, Rank.SIX))

		blocks[4].cards.add(Card(Suit.SPADE, Rank.SEVEN))
		blocks[4].cards.add(Card(Suit.CLUB, Rank.QUEEN))
		blocks[4].cards.add(Card(Suit.HEART, Rank.QUEEN))

		blocks[5].cards.add(Card(Suit.CLUB, Rank.THREE))
		blocks[5].cards.add(Card(Suit.CLUB, Rank.JACK))

		blocks[6].cards.add(Card(Suit.CLUB, Rank.FIVE))
		blocks[6].cards.add(Card(Suit.SPADE, Rank.FOUR))

		var returnVal: MutableList<Move> = mutableListOf()

		for (indexBlock in blocks.indices) {
			val itemBlock = blocks[indexBlock]

			if (itemBlock.cards.isNullOrEmpty()) {
				continue
			}
			gameLogic.hasChecked = false

			val result = gameLogic.possibleMovesFromBlockToBlock(
				itemBlock,
				blocks,
				indexBlock,
				returnVal,
				lastMovesMap
			)


		}

		val move1 = Move(false, Card(Suit.HEART, Rank.THREE), 0, 6)
		val move2 = Move(false, Card(Suit.DIAMOND, Rank.TEN), 1, 5)
		val move3 = Move(false, Card(Suit.CLUB, Rank.JACK), 5, 4)

		Assert.assertEquals(returnVal.size, 3)
		Assert.assertEquals(returnVal.contains(move1), true)
		Assert.assertEquals(returnVal.contains(move2), true)
		Assert.assertEquals(returnVal.contains(move3), true)
	}

	/*
	* Tests is multiple parts of a block
	* can be moved all are added to the move array */
	@Test
	fun multiplePartsOfBlockCanBeMoved() {

		initializeBlocks()

		blocks[0].cards.add(Card(Suit.SPADE, Rank.FOUR))
		blocks[0].cards.add(Card(Suit.HEART, Rank.THREE))
		blocks[0].cards.add(Card(Suit.CLUB, Rank.TWO))

		blocks[1].cards.add(Card(Suit.HEART, Rank.FOUR))
		blocks[1].cards.add(Card(Suit.CLUB, Rank.THREE))
		blocks[1].cards.add(Card(Suit.DIAMOND, Rank.TEN))

		blocks[2].cards.add(Card(Suit.HEART, Rank.JACK))
		blocks[2].cards.add(Card(Suit.SPADE, Rank.THREE))
		blocks[2].cards.add(Card(Suit.CLUB, Rank.FOUR))

		blocks[3].cards.add(Card(Suit.DIAMOND, Rank.TWO))
		blocks[3].cards.add(Card(Suit.SPADE, Rank.ACE))
		blocks[3].cards.add(Card(Suit.HEART, Rank.SIX))

		blocks[4].cards.add(Card(Suit.SPADE, Rank.SEVEN))
		blocks[4].cards.add(Card(Suit.CLUB, Rank.QUEEN))
		blocks[4].cards.add(Card(Suit.HEART, Rank.JACK))

		blocks[5].cards.add(Card(Suit.CLUB, Rank.THREE))
		blocks[5].cards.add(Card(Suit.CLUB, Rank.JACK))

		blocks[6].cards.add(Card(Suit.DIAMOND, Rank.FIVE))

		val game = Game.emptyGame()
		var returnVal = Game.gameLogic.allPossibleMoves(foundation, blocks, waste, lastMovesMap)


		val move1 = Move(false, Card(Suit.SPADE, Rank.FOUR), 0, 6)
		val move2 = Move(false, Card(Suit.DIAMOND, Rank.TEN), 1, 5)
		val move3 = Move(false, Card(Suit.CLUB, Rank.FOUR), 2, 6)

		Assert.assertEquals(returnVal.size, 3)
		Assert.assertEquals(returnVal.contains(move1), true)
		Assert.assertEquals(returnVal.contains(move2), true)
	}

	/*
	* Tests if the correct blocks are added to move function
	* when multiple moves are possible
	* */
	@Test
	fun multiplePossibleMovesFromBlockToBlock2() {

		initializeBlocks()
		blocks[0].cards.add(Card(Suit.DIAMOND, Rank.FOUR))
		blocks[0].cards.add(Card(Suit.HEART, Rank.THREE))
		blocks[0].cards.add(Card(Suit.CLUB, Rank.TWO))

		blocks[1].cards.add(Card(Suit.HEART, Rank.FOUR))
		blocks[1].cards.add(Card(Suit.DIAMOND, Rank.THREE))
		blocks[1].cards.add(Card(Suit.DIAMOND, Rank.TEN))

		blocks[2].cards.add(Card(Suit.HEART, Rank.JACK))
		blocks[2].cards.add(Card(Suit.SPADE, Rank.THREE))

		blocks[3].cards.add(Card(Suit.DIAMOND, Rank.TWO))
		blocks[3].cards.add(Card(Suit.SPADE, Rank.TWO))
		blocks[3].cards.add(Card(Suit.HEART, Rank.SIX))

		blocks[4].cards.add(Card(Suit.SPADE, Rank.SEVEN))
		blocks[4].cards.add(Card(Suit.CLUB, Rank.QUEEN))
		blocks[4].cards.add(Card(Suit.HEART, Rank.QUEEN))

		blocks[5].cards.add(Card(Suit.CLUB, Rank.THREE))
		blocks[5].cards.add(Card(Suit.CLUB, Rank.JACK))

		blocks[6].cards.add(Card(Suit.DIAMOND, Rank.FIVE))
		blocks[6].cards.add(Card(Suit.SPADE, Rank.FOUR))


		var returnVal: MutableList<Move> = mutableListOf()

		for (indexBlock in blocks.indices) {
			val itemBlock = blocks[indexBlock]

			if (itemBlock.cards.isNullOrEmpty()) {
				continue
			}
			gameLogic.hasChecked = false

			val result = gameLogic.possibleMovesFromBlockToBlock(
				itemBlock,
				blocks,
				indexBlock,
				returnVal,
				lastMovesMap
			)

		}

		val move1 = Move(false, Card(Suit.HEART, Rank.THREE), 0, 6)
		val move2 = Move(false, Card(Suit.DIAMOND, Rank.TEN), 1, 5)
		val move3 = Move(false, Card(Suit.CLUB, Rank.JACK), 5, 4)

		Assert.assertEquals(returnVal.size, 3)
		Assert.assertEquals(returnVal.contains(move1), true)
		Assert.assertEquals(returnVal.contains(move2), true)
		Assert.assertEquals(returnVal.contains(move3), true)
	}

	/*
	* Tests if first card in a block is a King
	* it will be moved to a possible free block
	* */
	@Test
	fun findMoveKingToEmptyBlock() {
		initializeBlocks()

		blocks[0].cards.add(Card(Suit.SPADE, Rank.FIVE))
		blocks[0].cards.add(Card(Suit.HEART, Rank.THREE))
		blocks[0].cards.add(Card(Suit.CLUB, Rank.TWO))



		blocks[1].cards.add(Card(Suit.HEART, Rank.FOUR))
		blocks[1].cards.add(Card(Suit.DIAMOND, Rank.THREE))
		blocks[1].cards.add(Card(Suit.DIAMOND, Rank.TEN))



		blocks[2].cards.add(Card(Suit.HEART, Rank.JACK))
		blocks[2].cards.add(Card(Suit.SPADE, Rank.THREE))


		blocks[3].cards.add(Card(Suit.DIAMOND, Rank.TWO))
		blocks[3].cards.add(Card(Suit.SPADE, Rank.TWO))
		blocks[3].cards.add(Card(Suit.HEART, Rank.SIX))


		blocks[4].cards.add(Card(Suit.SPADE, Rank.SEVEN))
		blocks[4].cards.add(Card(Suit.CLUB, Rank.QUEEN))
		blocks[4].cards.add(Card(Suit.HEART, Rank.KING))



		blocks[5].cards.add(Card(Suit.CLUB, Rank.THREE))


		var returnVal: MutableList<Move> = mutableListOf()

		for (indexBlock in blocks.indices) {
			val itemBlock = blocks[indexBlock]

			if (itemBlock.cards.isNullOrEmpty()) {
				continue
			}
			gameLogic.hasChecked = false

			gameLogic.possibleMovesFromBlockToBlock(
				itemBlock,
				blocks,
				indexBlock,
				returnVal,
				lastMovesMap
			)

		}


		assertEquals(returnVal.size, 1)


		val expMove1 = Move(false, Card(Suit.HEART, Rank.KING), 4, 6)
		Assert.assertEquals(returnVal[0], expMove1)

	}

	/*
	* Tests if multiple first cards in a block is a King
	* it will be moved to a possible free block
	* */
	@Test
	fun findMoveMultipleKingsToEmptyBlock() {
		initializeBlocks()

		blocks[0].cards.add(Card(Suit.SPADE, Rank.FIVE))
		blocks[0].cards.add(Card(Suit.HEART, Rank.THREE))
		blocks[0].cards.add(Card(Suit.CLUB, Rank.KING))



		blocks[1].cards.add(Card(Suit.HEART, Rank.FOUR))
		blocks[1].cards.add(Card(Suit.DIAMOND, Rank.THREE))
		blocks[1].cards.add(Card(Suit.DIAMOND, Rank.TEN))



		blocks[2].cards.add(Card(Suit.HEART, Rank.JACK))
		blocks[2].cards.add(Card(Suit.SPADE, Rank.THREE))


		blocks[3].cards.add(Card(Suit.DIAMOND, Rank.TWO))
		blocks[3].cards.add(Card(Suit.SPADE, Rank.TWO))
		blocks[3].cards.add(Card(Suit.HEART, Rank.SIX))


		blocks[4].cards.add(Card(Suit.SPADE, Rank.SEVEN))
		blocks[4].cards.add(Card(Suit.CLUB, Rank.QUEEN))
		blocks[4].cards.add(Card(Suit.HEART, Rank.KING))


		var returnVal: MutableList<Move> = mutableListOf()

		for (indexBlock in blocks.indices) {
			val itemBlock = blocks[indexBlock]

			if (itemBlock.cards.isNullOrEmpty()) {
				continue
			}
			gameLogic.hasChecked = false

			gameLogic.possibleMovesFromBlockToBlock(
				itemBlock,
				blocks,
				indexBlock,
				returnVal,
				lastMovesMap
			)

		}


		assertEquals(returnVal.size, 2)


		val expMove1 = Move(false, Card(Suit.CLUB, Rank.KING), 0, 5)
		val expMove2 = Move(false, Card(Suit.HEART, Rank.KING), 4, 5)
		Assert.assertEquals(returnVal.contains(expMove1), true)
		Assert.assertEquals(returnVal.contains(expMove2), true)
	}


	/*Test if evalBlockToBlockAndWasteToBlock works as intended*/
	@Test
	fun testEvalBlockToBlockAndWasteToBlock() {
		initializeBlocks()

		val testBlockLastCard = Card(Suit.SPADE, Rank.FIVE)
		val testCard = Card(Suit.HEART, Rank.SIX)

		val result = gameLogic.evalBlockToBlockAndWasteToBlock(testCard, testBlockLastCard)

		Assert.assertEquals(result, true)
	}

	/* Tests if evalBlockToBlockAndWasteToBlock works as intended if meant to fail*/
	@Test
	fun testEvalBlockToBlockAndWasteToBlockFail() {
		initializeBlocks()

		val testBlockLastCard = Card(Suit.SPADE, Rank.KING)
		val testCard = Card(Suit.HEART, Rank.SIX)

		val result = gameLogic.evalBlockToBlockAndWasteToBlock(testCard, testBlockLastCard)

		Assert.assertEquals(result, false)
	}

	/*Test possibleMovesFromBlockToBlock if it works as expected*/
	@Test
	fun testPossibleMovesFromBlockToBlock() {
		initializeBlocks()
		blocks[0].cards.add(Card(Suit.HEART, Rank.FOUR))
		blocks[0].cards.add(Card(Suit.CLUB, Rank.THREE))

		blocks[1].cards.add(Card(Suit.DIAMOND, Rank.SIX))
		blocks[1].cards.add(Card(Suit.SPADE, Rank.FIVE))

		val returnVal: MutableList<Move> = mutableListOf()
		gameLogic.possibleMovesFromBlockToBlock(blocks[0], blocks, 0, returnVal, lastMovesMap)
		val moveTest = Move(false, Card(Suit.HEART, Rank.FOUR), 0, 1)

		Assert.assertEquals(returnVal.contains(moveTest), true)
	}

	/*Test possibleMovesFromBlockToBlock if it fails as expected*/
	@Test
	fun testPossibleMovesFromBlockToBlockFail() {
		initializeBlocks()
		blocks[0].cards.add(Card(Suit.HEART, Rank.FOUR))
		blocks[0].cards.add(Card(Suit.CLUB, Rank.ACE))

		blocks[1].cards.add(Card(Suit.DIAMOND, Rank.SIX))
		blocks[1].cards.add(Card(Suit.SPADE, Rank.FIVE))

		val returnVal: MutableList<Move> = mutableListOf()
		gameLogic.possibleMovesFromBlockToBlock(blocks[0], blocks, 0, returnVal, lastMovesMap)

		Assert.assertEquals(returnVal.size, 0)
	}
}