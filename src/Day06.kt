fun main() {
    fun solve(input: String, numDistinct: Int): Int {
        val windowed = input.windowed(numDistinct)
        for ((index, window) in windowed.withIndex()) {
            if (window.toList().distinct().size == numDistinct) {
                return index + numDistinct
            }
        }

        return 0
    }

    fun part1(input: List<String>): Int {
        return solve(input[0], 4)
    }

    fun part2(input: List<String>): Int {
        return solve(input[0], 14)
    }

    val testInput = readInput("Day06_test")
    check(part1(testInput) == 7)
    check(part2(testInput) == 19)

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))
}
