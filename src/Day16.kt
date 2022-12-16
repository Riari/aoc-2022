fun main() {
    data class Valve(val rate: Int, val valves: List<String>)

    fun processInput(input: List<String>): Map<String, Valve> {
        val map = mutableMapOf<String, Valve>()
        val regexValves = Regex("[A-Z]{2}")
        val regexFlow = Regex("\\d+")
        for (line in input) {
            val valves = regexValves.findAll(line).map { it.value }.toList()
            val flow = regexFlow.find(line)!!.value.toInt()
            map[valves[0]] = Valve(flow, valves.subList(1, valves.lastIndex))
        }

        return map
    }

    fun part1(map: Map<String, Valve>): Int {
        return 0
    }

    fun part2(map: Map<String, Valve>): Int {
        return 0
    }

    val testInput = processInput(readInput("Day16_test"))
    check(part1(testInput) == 0)
    check(part2(testInput) == 0)

    val input = processInput(readInput("Day16"))
    println(part1(input))
    println(part2(input))
}
