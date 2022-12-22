package indigo

import kotlin.system.exitProcess

abstract class Player : PlayerFuns

class Human : Player() {
    override val hand: MutableList<FCard> = mutableListOf()
    override val pocket: MutableList<FCard> = mutableListOf()

    override fun throwCard(): FCard {
        print("Cards in hand: ")
        println(
            hand.joinToString(" ") {
                "${hand.indexOf(it) + 1})" + it
            }
        )
        val chosen = chooseCard()
        hand.remove(chosen)
        return chosen
    }

    override fun chooseCard(): FCard {
        println("Choose a card to play (1-${ hand.size }):")
        val input = readln()
        return when {
            input.lowercase() == "exit" -> {
                println("Game Over")
                exitProcess(0)
            }
            input.toIntOrNull() in 1..hand.size -> hand[input.toInt() - 1]
            else -> chooseCard()
        }
    }

    override fun toString(): String = "Player"
}

class Computer : Player() {
    override val hand: MutableList<FCard> = mutableListOf()
    override val pocket: MutableList<FCard> = mutableListOf()
    lateinit var upperCard: Card

    override fun throwCard(): FCard {
        val cardToThrow = chooseCard()
        hand.remove(cardToThrow)
        println("Computer plays $cardToThrow")
        return cardToThrow
    }

    override fun chooseCard(): FCard {
        val list = if (isNotSuited()) hand else {
            hand.filter { (upperCard as FCard).type == it.type || (upperCard as FCard).value == it.value }
        }
        val sameType = cardsWithTheSame(list, "type")
        val sameValue = cardsWithTheSame(list, "value")
        return when {
            list.size == 1 -> list.first()
            sameType.size > 1 -> sameType.random()
            sameValue.size > 1 -> sameValue.random()
            else -> list.random()
        }
    }
    private fun cardsWithTheSame(list: List<FCard>, purpose: String): List<FCard> {
        val prop = if (purpose == "value") list.map { it.value } else list.map { it.type }
        return mutableListOf<FCard>().apply {
            for (elem in prop) {
                list.filter { if (purpose == "value") it.value == elem else it.type == elem }.let {
                    if (it.size > 1) this += it
                }
            }
        }
    }
    private fun isNotSuited(): Boolean {
        return upperCard is ECard ||
            hand.none { (upperCard as FCard).type == it.type || (upperCard as FCard).value == it.value }
    }

    override fun toString(): String = "Computer"
}

interface PlayerFuns {
    val hand: MutableList<FCard>
    val pocket: MutableList<FCard>

    fun throwCard() : FCard

    fun chooseCard(): FCard

    fun fillHand(deck: ListIterator<FCard>) {
        for (ind in 0..5) {
            hand += deck.next()
        }
    }

    fun takeCards(cards: MutableList<FCard>) {
        pocket.addAll(cards)
    }

    fun getNumberOfPoints(): Int = this.pocket.filter { it.price == 1 }.size

    fun getNumberOfCards(): Int = this.pocket.size
}