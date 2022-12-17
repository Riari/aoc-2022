fun main() {
    data class Valve(val id: String, val rate: Int, val edges: List<String>)
    data class State(val valves: Map<String, Valve>, val shortestPaths: MutableMap<String, MutableMap<String, Int>>, var score: Int = 0)

    fun processInput(input: List<String>): State {
        val list = mutableListOf<Valve>()
        val regexValves = Regex("[A-Z]{2}")
        val regexFlow = Regex("\\d+")
        for (line in input) {
            val valves = regexValves.findAll(line).map { it.value }.toList()
            val flow = regexFlow.find(line)!!.value.toInt()
            list.add(Valve(valves[0], flow, valves.subList(1, valves.lastIndex + 1)))
        }

        // The Floyd-Warshall algorithm seems to be a perfect fit for this day, but it's totally new to me.
        // I ended up borrowing the implementation from here: https://github.com/ckainz11/AdventOfCode2022/blob/main/src/main/kotlin/days/day16/Day16.kt
        val shortestPaths = list.associate { it.id to it.edges.associateWith { 1 }.toMutableMap() }.toMutableMap()
        for (k in shortestPaths.keys) {
            for (i in shortestPaths.keys) {
                for (j in shortestPaths.keys) {
                    val ik = shortestPaths[i]?.get(k) ?: 9999
                    val kj = shortestPaths[k]?.get(j) ?: 9999
                    val ij = shortestPaths[i]?.get(j) ?: 9999
                    if (ik + kj < ij) shortestPaths[i]?.set(j, ik + kj)
                }
            }
        }

        val map = list.associateBy { it.id }
        shortestPaths.values.forEach { it.keys.removeIf { valve -> map[valve]?.rate == 0 } }

        return State(map, shortestPaths)
    }

    // Solve using recursive DFS
    fun solve(state: State, timeLimit: Int = 30, withHelp: Boolean = false, opened: Set<String> = setOf(), score: Int = 0, currentValve: String = "AA", timer: Int = 0) {
        state.score = maxOf(state.score, score)
        for ((valve, distance) in state.shortestPaths[currentValve]!!) {
            val ticks = timer + distance + 1

            if (opened.contains(valve) || ticks >= timeLimit) continue

            solve(
                state,
                timeLimit,
                withHelp,
                opened.union(listOf(valve)),
                score + (timeLimit - ticks) * state.valves[valve]!!.rate,
                valve,
                ticks)
        }

        if (withHelp) solve(state, timeLimit, false, opened, score)
    }

    fun part1(state: State): Int {
        solve(state)
        return state.score
    }

    fun part2(state: State): Int {
        state.score = 0
        solve(state, 26, true)
        return state.score
    }

    val testInput = processInput(readInput("Day16_test"))
    check(part1(testInput) == 1651)
    check(part2(testInput) == 1707)

    val input = processInput(readInput("Day16"))
    println(part1(input))
    println(part2(input))
}
