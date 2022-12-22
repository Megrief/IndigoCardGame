package indigo

open class Card

class ECard : Card()

class FCard(
    val type: Char,
    val value: String
) : Card() {
    val price = if (value in values) 1 else 0

    companion object {
        val values = arrayOf("10", "J", "Q", "K", "A")
    }

    override fun toString(): String = value + type
}
