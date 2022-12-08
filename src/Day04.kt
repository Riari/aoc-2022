import kotlin.math.abs

fun main() {
    // Processes input into lists of ints for each pair of assignments
    fun processInput(input: List<String>): List<Int> {
        val regex = Regex("[0-9]+")
        val assignments = mutableListOf<Int>()
        for (line in input) {
            assignments.addAll(regex.findAll(line).toList().map { it.value.toInt() })
        }

        return assignments
    }

    fun part1(assignments: List<Int>): Int {
        var fullyContained = 0
        for (ranges in assignments.chunked(4)) {
            if ((ranges[0] >= ranges[2] && ranges[1] <= ranges[3]) || (ranges[0] <= ranges[2] && ranges[1] >= ranges[3])) {
                fullyContained++
            }
        }

        return fullyContained
    }

    fun part2(assignments: List<Int>): Int {
        var overlaps = 0
        for (ranges in assignments.chunked(4)) {
            if (!(ranges[0] > ranges[3] || ranges[1] < ranges[2])) {
                overlaps++
            }
        }

        return overlaps
    }

    val testInput = processInput(readInput("Day04_test"))
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = processInput(readInput("Day04"))
    println(part1(input))
    println(part2(input))
}
