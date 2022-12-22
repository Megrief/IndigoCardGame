package indigo

import kotlin.system.exitProcess


class Indigo {
    private var deck: ListIterator<FCard> = getDeck().listIterator()
    private val computer = Computer()
    private val human = Human()
    private val table = Table()
    private lateinit var lastWinner: Player

    companion object {
        private val cardTypes = arrayOf('♠', '♥', '♦', '♣')
        private val cardValues = arrayOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")

        fun getDeck(): List<FCard> {
            return buildList {
                for (type in cardTypes) {
                    for (value in cardValues) {
                        this.add(FCard(type, value))
                    }
                }
            }.shuffled()
        }
    }

    init {
        println("Indigo Card Game")
        val first = setFirst()
        table.putCardsOnTable(deck)
        computer.fillHand(deck)
        human.fillHand(deck)
        lastWinner = if (first) human else computer
        while (human.hand.isNotEmpty() && computer.hand.isNotEmpty()) {
            if (first) {
                moving(human)
                moving(computer)
            } else {
                moving(computer)
                moving(human)
            }
            if (human.hand.isEmpty() && deck.hasNext()) human.fillHand(deck)
            if (computer.hand.isEmpty() && deck.hasNext()) computer.fillHand(deck)
        }
        table.printState()
        lastWinner.takeCards(table.cardsOnTable)
        countTotalPoints(human, computer, first)
        exitProcess(0)
    }

    private fun moving(player: Player) {
        table.printState()
        if (player is Computer) {
            println(player.hand.joinToString(" "))
            player.upperCard = if (table.cardsOnTable.isNotEmpty()) table.cardsOnTable.last() else ECard()
        }
        val thrownCard = player.throwCard()
        if (table.wins(thrownCard)) {
            table.getCard(thrownCard)
            lastWinner = player
            player.takeCards(table.cardsOnTable)
            table.clearTable()
            println("$player wins cards")
            printCount()
        } else {
            table.getCard(thrownCard)
        }
    }

    private fun countTotalPoints(human: Player, computer: Player, first: Boolean) {
        var humanPoints = human.pocket.filter { it.price == 1 }.size
        var computerPoints = computer.pocket.filter { it.price == 1 }.size

        when {
            human.pocket.size > computer.pocket.size -> humanPoints += 3
            human.pocket.size < computer.pocket.size -> computerPoints += 3
            human.pocket.size == computer.pocket.size -> if (first) humanPoints += 3 else computerPoints += 3
        }
        printCount(humanPoints = humanPoints, computerPoints = computerPoints)
        println("Game Over")
    }

    private fun printCount(
        humanPoints: Int = human.getNumberOfPoints(),
        humanCards: Int = human.getNumberOfCards(),
        computerPoints: Int = computer.getNumberOfPoints(),
        computerCards: Int = computer.getNumberOfCards()
    ) {
        println(
            """
                Score: Player $humanPoints - Computer $computerPoints
                Cards: Player $humanCards - Computer $computerCards
            """.trimIndent()
        )
    }

    private fun setFirst(): Boolean {
        println("Play first?")
        return when (readln().lowercase()) {
            "yes" -> true
            "no" -> false
            else -> return setFirst()
        }
    }

}

class Table {
    val cardsOnTable = mutableListOf<FCard>()

    fun putCardsOnTable(deck: ListIterator<FCard>) {
        for (ind in 0..3) { cardsOnTable.add(deck.next()) }
        print("Initial cards on the table:")
        println(cardsOnTable.joinToString("") { " $it" })
    }

    fun printState() {
        println("")
        println(
            if (cardsOnTable.isNotEmpty()) {
                "${ cardsOnTable.size } cards on the table, and the top card is ${ cardsOnTable.last() }"
            } else "No cards on the table"
        )
    }

    fun wins(card: FCard): Boolean {
        return if (cardsOnTable.isNotEmpty()) {
            card.type == cardsOnTable.last().type || card.value == cardsOnTable.last().value
        } else false
    }
    fun clearTable() = cardsOnTable.clear()
    fun getCard(card: FCard) {
        cardsOnTable += card
    }
}