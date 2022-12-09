import kotlin.collections.ArrayDeque

fun main() {
    data class Procedure(val move: Int, val from: Int, val to: Int)

    fun parseInitialState(input: List<String>): List<ArrayDeque<Char>> {
        val state = mutableListOf<ArrayDeque<Char>>()
        val regex = Regex("[A-Z]")

        for (line in input) {
            if (line == "") break

            regex.findAll(line).forEach {
                val stack = ((it.range.first - 1) / 4) // get zero-based stack index
                while (state.size <= stack) state.add(ArrayDeque()) // grow state as needed
                state[stack].addFirst(it.value.toCharArray()[0]) // add crate to bottom of stack
            }
        }

        return state
    }

    fun parseProcedures(input: List<String>): List<Procedure> {
        val procedures = mutableListOf<Procedure>()
        for (line in input) {
            if (!line.startsWith("move")) continue
            val regex = Regex("[0-9]+")
            val proc = regex.findAll(line).toList().map { it.value.toInt() }
            procedures.add(Procedure(proc[0], proc[1] - 1, proc[2] - 1))
        }

        return procedures
    }

    fun solve(state: List<ArrayDeque<Char>>, procedures: List<Procedure>, canMoveMultipleAtOnce: Boolean): String {
        for (procedure in procedures) {
            val cratesToMove = mutableListOf<Char>()
            repeat(procedure.move) {
                val crate = state[procedure.from].removeLast()
                cratesToMove.add(crate)
            }

            if (canMoveMultipleAtOnce) cratesToMove.reverse()

            for (crate in cratesToMove) {
                state[procedure.to].add(crate)
            }
        }

        var message = ""
        for (stack in state) {
            message += stack.removeLast()
        }

        return message
    }

    fun part1(state: List<ArrayDeque<Char>>, procedures: List<Procedure>): String {
        return solve(state, procedures, false)
    }

    fun part2(state: List<ArrayDeque<Char>>, procedures: List<Procedure>): String {
        return solve(state, procedures, true)
    }

    // TODO: find a way of deep-copying a list of ArrayDeque instead of re-parsing the input each time

    val testInput = readInput("Day05_test")
    val testProcedures = parseProcedures(testInput)
    check(part1(parseInitialState(testInput), testProcedures) == "CMZ")
    check(part2(parseInitialState(testInput), testProcedures) == "MCD")

    val input = readInput("Day05")
    val procedures = parseProcedures(input)
    println(part1(parseInitialState(input), procedures))
    println(part2(parseInitialState(input), procedures))
}
