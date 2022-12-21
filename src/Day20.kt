fun main() {
    val magicIndices = listOf(1000, 2000, 3000)

    fun solve(input: List<String>, key: Long = 1, iterations: Int = 1): Long {
        val sequence = input.map { it.toInt() * key }.withIndex().toMutableList()

        repeat (iterations) {
            for (originalIndex in sequence.indices) {
                val index = sequence.indexOfFirst { it.index == originalIndex }
                val number = sequence.removeAt(index)
                sequence.add((index + number.value).mod(sequence.size), number)
            }
        }

        val zeroIndex = sequence.indexOfFirst { it.value == 0L }
        return magicIndices.sumOf { sequence[(zeroIndex + it) % sequence.size].value }
    }

    fun part1(input: List<String>): Long {
        return solve(input)
    }

    fun part2(input: List<String>): Long {
        return solve(input, 811589153, 10)
    }

    val testInput = readInput("Day20_test")
    check(part1(testInput) == 3L)
    check(part2(testInput) == 1623178306L)

    val input = readInput("Day20")
    println(part1(input))
    println(part2(input))
}
