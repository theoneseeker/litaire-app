package cdio.group21.litaire.data

data class GameSate(
/*    var foundations: Int,
    var emptyBlock: Int,*/
	var heuristicOneVal: Int,
	var heuristicTwoVal: Int,
	var length: UInt,
	var afterFirstMove: GameSate? = null
)
