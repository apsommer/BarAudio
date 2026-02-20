package com.sommerengineering.baraudio.uitls

object RomanNumerals {

    private val numerals = arrayOf(
        "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X",
        "XI", "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XVIII", "XIX", "XX")

    private val spoken = arrayOf(
        "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
        "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen",  "seventeen",
        "eighteen", "nineteen", "twenty")

    fun toNumeral(index: Int): String =
        numerals.getOrElse(index) { (index + 1).toString() }

    fun toWord(token: String): String {
        val i = numerals.indexOf(token)
        return if (i >= 0) spoken[i] else token
    }
}