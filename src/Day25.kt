fun main() {
    val base = 5
    val digits = "012=-"

    fun snafuToDec(snafu: String): Long {
        return snafu.fold(0) { carry, digit ->
            (carry * base) + when(digit) {
                '=' -> -2
                '-' -> -1
                else -> digit.digitToInt()
            }
        }
    }

    fun decToSnafu(dec: Long): String {
        var snafu = ""
        var value = dec
        while (value != 0L) {
            snafu = digits[value.mod(base)] + snafu
            value = (value + 2).floorDiv(base)
        }

        return snafu
    }

    fun part1(input: List<String>): String = decToSnafu(input.fold(0) { a, b -> a + snafuToDec(b) })

    val testInput = readInput("Day25_test")
    check(part1(testInput) == "2=-1=0")

    val input = readInput("Day25")
    println(part1(input))
}
