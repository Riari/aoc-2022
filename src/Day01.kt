fun main() {
    // Processes input into a list of total calories per elf in descending order
    fun processInput(input: List<String>): List<Int> {
        val elves = mutableListOf<Int>()
        var total = 0
        input.forEach {
            val calories = it.toIntOrNull()
            if (calories == null) {
                elves.add(total)
                total = 0
            } else {
                total += calories.toInt()
            }
        }

        elves.add(total)
        elves.sortDescending()

        return elves
    }

    fun part1(input: List<Int>): Int {
        return input[0]
    }

    fun part2(input: List<Int>): Int {
        return input.slice(0..2).sum()
    }

    val testInput = processInput(readInput("Day01_test"))
    check(part1(testInput) == 24000)
    check(part2(testInput) == 45000)

    val input = processInput(readInput("Day01"))
    println(part1(input))
    println(part2(input))
}
