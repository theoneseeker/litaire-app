package cdio.group21.litaire.data

import Card
import Rank
import Suit
import android.graphics.RectF
import cdio.group21.litaire.API.Prediction

data class DetectionResult(val boundingBox: RectF, val card: Card, val confidence: Double) {
	fun toText(): String {
		return card.toString() + "," + confidence
	}

	fun deepCopy(
		boundingBox: RectF = this.boundingBox,
		percentage: Double = this.confidence,
		card: Card = this.card
	) = DetectionResult(boundingBox, card, confidence)

	companion object {
		fun fromPrediction(prediction: Prediction): DetectionResult {
			val category = prediction.class_
			val suit = Suit.fromChar(category.last())
			val rank = Rank.fromChar(category.replace("10", "T")[0])
			val card = Card(suit, rank)

			val width = prediction.width
			val height = prediction.height
			val xTop = prediction.x
			val yTop = prediction.y
			val xBottom = prediction.x + width
			val yBottom = prediction.y + height

			val boundingBox = RectF(xTop, yTop, xBottom, yBottom)

			return DetectionResult(boundingBox, card, prediction.confidence)
		}
	}
}

data class DetectionResult2(
	var card: Card
) {

	fun deepCopy(
		card: Card = this.card
	) = DetectionResult2(card)
}