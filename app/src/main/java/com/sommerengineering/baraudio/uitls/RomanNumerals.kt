package com.sommerengineering.baraudio.uitls

object RomanNumerals {

    private val numerals = arrayOf(
        "I","II","III","IV","V","VI","VII","VIII","IX","X",
        "XI","XII","XIII","XIV","XV","XVI","XVII","XVIII","XIX","XX")

    fun toInt(token: String): Int {
        val i = numerals.indexOf(token)
        return if (i >= 0) i + 1 else 0
    }

    fun toNumeral(index: Int): String =
        numerals.getOrElse(index) { (index + 1).toString() }
}