package cdio.group21.litaire.data

import Card
import Rank
import Suit
import android.util.Log
import cdio.group21.litaire.utils.MutableMemoryList
import cdio.group21.litaire.utils.extensions.copyOf
import cdio.group21.litaire.utils.extensions.mutableCopyOf
import cdio.group21.litaire.utils.mutableMemoryListOf

data class CardAndContainer(val card: Card, val pile: MutableList<Card>)

data class Solitaire(
	val tableau: List<MutableList<Card>>,
	val foundations: List<MutableList<Card>>,
	val stock: MutableList<Card>,
	val talon: MutableList<Card>,

	) {
	private var cardObjectToReveal: Card? = null

	fun revealCard(newCard: Card): Result<Unit> {
		val cardObjectToReveal = this.cardObjectToReveal ?:
			return Result.failure(IllegalStateException("Error: No card to reveal!"))
		val result = replaceCardObject(cardObjectToReveal, newCard).getOrElse { return Result.failure(it) }
		this.cardObjectToReveal = null
		return Result.success(result)
	}

	fun isWon(): Boolean {
		return foundations.all { it.size == 13 }
	}

	fun replaceCardObject(cardObjectToReveal: Card, value: Card): Result<Unit> {
		val talonIndex = talon.indexOfFirst { card -> card === cardObjectToReveal }
		if (talonIndex != -1) {
			talon[talonIndex] = value
			return Result.success(Unit)
		}

		tableau.forEach { cards ->
			val index = cards.indexOfFirst { card -> card === cardObjectToReveal }
			if (index == -1) return@forEach
			cards[index] = value
			return Result.success(Unit)
		}
		foundations.forEach { foundation ->
			val index = foundation.indexOfFirst { card -> card === cardObjectToReveal }
			if (index == -1) return@forEach
			foundation[index] = value
			return Result.success(Unit)
		}

		return Result.failure(IllegalArgumentException("Error: $cardObjectToReveal is not in the game!"))
	}

	fun findCardFromString(cardString: String): Card? {

		val targetCard =
			Card(rank = Rank.fromChar(cardString[0]), suit = Suit.fromChar(cardString[1]))

		var resultCard: Card? = null
		tableau.forEach { col ->
			val foundCard = col.find { card ->
				return@find card.toString() == targetCard.toString()
			}
			if (foundCard != null) {
				resultCard = foundCard
				return@forEach
			}
		}
		return resultCard
	}

	/**
	 * Finds a card in the tableu that is the same kind. This is to prevent having lots of copies around.
	 */
	private fun findEqualCard(targetCard: Card): Result<CardAndContainer> {
		tableau.forEach { col ->
			val foundCard = col.find { card ->
				card == targetCard
			}
			if (foundCard != null) {
				return Result.success(CardAndContainer(foundCard, col))
			}
		}
		foundations.forEach {foundation ->
			foundation.find { card -> card == targetCard }?.let {
				return Result.success(CardAndContainer(it, talon))
			}
		}
		talon.find { card -> card == targetCard }?.let {
			return Result.success(CardAndContainer(it, talon))
		}
		stock.find { card -> card == targetCard }?.let {
			return Result.success(CardAndContainer(it, stock))
		}
		return Result.failure(IllegalArgumentException("Error: ${targetCard} not found!"))
	}

	/**
	 * Removes a card from its pile and returns both.
	 */
	private fun removeCard(card: Card): Result<CardAndContainer> {
		val cardAndContainer = findEqualCard(card).getOrElse { return Result.failure(it) }
		if (!cardAndContainer.pile.remove(card)) return Result.failure(IllegalArgumentException("Error: ${card} could not be removed!"))
		return Result.success(cardAndContainer)
	}

	fun copy(): Solitaire {
		val solitaire = Solitaire(
			tableau = tableau.map { it.mutableCopyOf() },
			foundations = foundations.map { it.mutableCopyOf() },
			stock = stock.mutableCopyOf(),
			talon = talon.mutableCopyOf()
		)
		solitaire.cardObjectToReveal = this.cardObjectToReveal
		return solitaire
	}

	/**
	 * Adds a card the the specified block in the tableau
	 * @param card The card to add
	 * @param blockIndex The index of the block to add the card to
	 */
	private fun addCardToTableau(card: Card, blockIndex: Int) {
		val toBlock = tableau[blockIndex]
		toBlock.add(card)
	}

	fun validTableau(weirdMoveIndex: UInt): Boolean {
		if (weirdMoveIndex < 7u) return true
		return false
	}

	fun weirdMoveIndexToFoundation(weirdMoveIndex: UInt): Result<MutableList<Card>> {
		return when (weirdMoveIndex) {
			0u -> getFoundationFromSuit(Suit.CLUB)
			1u -> getFoundationFromSuit(Suit.DIAMOND)
			2u -> getFoundationFromSuit(Suit.HEART)
			3u -> getFoundationFromSuit(Suit.SPADE)
			else -> return Result.failure(IllegalArgumentException("Error: Invalid WeirdMove index!"))
		}
	}

	fun weirdMoveIndexToPile(
		weirdMoveIndex: UInt,
		foundation: Boolean = false
	): Result<MutableList<Card>> {
		if (foundation) {
			return weirdMoveIndexToFoundation(weirdMoveIndex)
		}

		when (weirdMoveIndex) {
			8u -> return Result.success(talon)
			7u -> return Result.success(stock)
		}

		if (!validTableau(weirdMoveIndex))
			return Result.failure(IllegalArgumentException("Error: Invalid WeirdMove index!"))
		return Result.success(tableau[weirdMoveIndex.toInt()])
	}

	fun performMove(move: Move?): Result<Card?> {
		val card =
			if (move == null)
				drawCards().getOrElse { return Result.failure(it) }
			else if (move.isMoveToFoundation)
				performMoveToFoundation(move).getOrElse { return Result.failure(it) }
			else
				performMoveToTableau(move).getOrElse { return Result.failure(it) }
		cardObjectToReveal = card
		return Result.success(card)
	}

	private fun drawCards(): Result<Card?> {
		if (stock.size < 3) {
			stock.addAll(talon)
			talon.clear()
		}
		if (stock.size < 3) return Result.failure(IllegalArgumentException("Error: Stock is empty!"))

		for (i in 0 until 3) {
			talon.add(stock.removeFirst())
		}

		val revealedCard = talon.lastOrNull() ?: return Result.success(null)
		if (revealedCard.isUnknown()) return Result.success(revealedCard);
		return Result.success(null)
	}

	private fun performMoveToFoundation(move: Move): Result<Card?> {
		val foundation =
			getFoundationFromSuit(move.card.suit).getOrElse { return Result.failure(it) }
		val cardAndContainer = removeCard(move.card).getOrElse { return Result.failure(it) }
		foundation.add(cardAndContainer.card)

		val revealedCard = cardAndContainer.pile.lastOrNull() ?: return Result.success(null)
		if (!revealedCard.isUnknown()) return Result.success(null);
		return Result.success(revealedCard)
	}

	private fun performMoveToTableau(move: Move): Result<Card?> {
		if (validTableau(move.indexOfSourceBlock.toUInt()))
			return moveBetweenTableau(move)
		val cardAndContainer = removeCard(move.card).getOrElse { return Result.failure(it) }
		val destinationPile = weirdMoveIndexToPile(move.indexOfDestination.toUInt()).getOrElse {
			return Result.failure(it)
		}
		destinationPile.add(cardAndContainer.card)
		val revealedCard = cardAndContainer.pile.lastOrNull() ?: return Result.success(null)
		if (!revealedCard.isUnknown()) return Result.success(null);
		return Result.success(revealedCard)
	}

	private fun moveBetweenTableau(move: Move): Result<Card?> {
		val source = findEqualCard(move.card).getOrElse { return Result.failure(it) }
		val destination = weirdMoveIndexToPile(move.indexOfDestination.toUInt()).getOrElse {
			return Result.failure(it)
		}
		// This is inefficient, but it works.
		val tempStorage = mutableListOf<Card>()
		do {
			val card = source.pile.removeLast()
			tempStorage.add(card)
		} while (card != move.card && source.pile.isNotEmpty())
		destination.addAll(tempStorage.reversed())

		val revealedCard = source.pile.lastOrNull() ?: return Result.success(null)
		if (!revealedCard.isUnknown()) return Result.success(null);
		return Result.success(revealedCard)
	}


	/**
	 * Removes a card from a block and adds it to a fitting foundation
	 * @param card The card to move
	 * @param shouldPop Should the card be removed from tableau?
	 */
	fun moveCardToFoundation(card: Card, shouldPop: Boolean = true): Boolean {

		if (shouldPop) {
			val poppedCard = removeCard(card).getOrElse { return false }.card
			val foundation = getFoundationFromSuit(poppedCard.suit).getOrElse { return false }
			foundation.add(poppedCard)
			return true
		}
		val foundation = getFoundationFromSuit(card.suit).getOrElse { return false }
		foundation.add(card)
		return true
	}

	/**
	 * Move a card and cards under it from a block to another block in the tableau
	 * @param fromIndex The index of the block to move from
	 * @param toIndex The index of the block to move to
	 * @param card The top card to move
	 */
	fun moveCardsInTableau(card: Card, fromIndex: Int, toIndex: Int) {
		val fromBlock = tableau[fromIndex]
		val toBlock = tableau[toIndex]
		val cardIndex = fromBlock.indexOfFirst { it.toString() == card.toString() }
		val lastFromBlockIndex = fromBlock.lastIndex
		val cardsToMove = fromBlock.subList(cardIndex, lastFromBlockIndex + 1)
		toBlock.addAll(cardsToMove)
		fromBlock.removeAll(cardsToMove)
	}

	/**
	 * Move a card from the talon (waste) to either the foundation or a block in the tableau
	 * @param blockIndex The block in the tableau to move the card to
	 * @param toFoundation Should the card be moved to foundation?
	 */
	fun moveCardFromTalon(toFoundation: Boolean = false, blockIndex: Int = 0) {
		if (talon.isEmpty()) return
		val card = talon.last()
		if (toFoundation) moveCardToFoundation(card) else addCardToTableau(card, blockIndex)
	}

	private fun getFoundationFromSuit(suit: Suit): Result<MutableList<Card>> {
		when (suit) {
			Suit.CLUB -> return Result.success(foundations[0])
			Suit.DIAMOND -> return Result.success(foundations[1])
			Suit.HEART -> return Result.success(foundations[2])
			Suit.SPADE -> return Result.success(foundations[3])
			Suit.UNKNOWN -> return Result.failure(IllegalArgumentException("Error: Unknown suit"))
		}
	}

	companion object {
		fun fromInitialCards(knownCards: List<Card>): Solitaire {
			if (knownCards.size != 7) {
				throw IllegalArgumentException("Error: Expected 7 cards!")
			}

			val tableau = List(7) { mutableListOf<Card>() }
			for (i in 0..6) {
				for (j in 0 until i) {
					tableau[i].add(Card(Suit.UNKNOWN, Rank.UNKNOWN))
				}
				tableau[i].add(knownCards[i])
			}
			val foundations = List(4) { mutableListOf<Card>() }
			val stock = mutableMemoryListOf<Card>()
			for (i in 0..24) {
				stock.add(Card(Suit.UNKNOWN, Rank.UNKNOWN))
			}
			val talon = mutableListOf<Card>()
			return Solitaire(tableau, foundations, stock, talon)
		}

		val EMPTY_GAME: Solitaire = emptyGame()
		private fun emptyGame(): Solitaire {
			val tableau = List(7) { mutableListOf<Card>() }
			val foundations = List(4) { mutableListOf<Card>() }
			val stock = mutableMemoryListOf<Card>()
			val talon = mutableListOf<Card>()
			return Solitaire(tableau, foundations, stock, talon)
		}
	}

}