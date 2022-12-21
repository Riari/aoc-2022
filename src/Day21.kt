fun main() {
    class OperationMonkey(val leftId: String, val rightId: String, val operation: Char)

    fun processInput(input: List<String>): Pair<MutableMap<String, Long>, MutableMap<String, OperationMonkey>> {
        val yellers = mutableMapOf<String, Long>()
        val operators = mutableMapOf<String, OperationMonkey>()

        for (line in input) {
            val (id, value) = line.split(": ")
            val long = value.toLongOrNull()
            if (long != null) {
                yellers[id] = long
                continue
            }

            val parts = value.split(' ')
            operators[id] = OperationMonkey(parts[0], parts[2], parts[1].toCharArray()[0])
        }

        return Pair(yellers, operators)
    }

    fun solve(yellers: MutableMap<String, Long>, operators: MutableMap<String, OperationMonkey>, isPart2: Boolean = false, recurse: Boolean = false): Long {
        while (operators.isNotEmpty()) {
            val iterator = operators.iterator()
            while (iterator.hasNext()) {
                val monkey = iterator.next()
                val left = yellers.getOrDefault(monkey.value.leftId, null) ?: continue
                val right = yellers.getOrDefault(monkey.value.rightId, null) ?: continue

                // For part two, when we find the monkey using the value given by humn, start a recursive search for the correct value
                // TODO: Optimise this properly.
                if (isPart2) {
                    if (recurse) {
                        val isHumanLeft = monkey.value.leftId == "humn"
                        val isHumanRight = monkey.value.rightId == "humn"

                        if (isHumanLeft || isHumanRight) {
                            val yellersCopy = yellers.toMutableMap()
                            val thresholds = mutableListOf(4096L * 4096L, 2048L * 2048L, 1024L * 1024L, 512L * 512L, 256L * 256L)
                            var thresholdExceeded = false
                            var value = 0L
                            var result = 0L
                            do {
                                // There are some really dumb workarounds here to speed things up for actual input.
                                // Likely not to work with other people's input.

                                for (threshold in thresholds) {
                                    if (result < threshold) continue
                                    value += threshold / 2
                                    thresholdExceeded = true
                                    break
                                }

                                yellersCopy["humn"] = value
                                value += if (thresholdExceeded && result < 0) -1024L else 1

                                result = solve(yellersCopy.toMutableMap(), operators.toMutableMap(), true)
                            } while (result != 0L)

                            return value - 1
                        }
                    } else if (monkey.key == "root") {
                        return left - right
                    }
                }

                val result = when (monkey.value.operation) {
                    '+' -> left + right
                    '-' -> left - right
                    '*' -> left * right
                    '/' -> left / right
                    else -> 0L
                }

                yellers[monkey.key] = result
                iterator.remove()
            }
        }

        return yellers["root"]!!.toLong()
    }

    fun part1(monkeys: Pair<MutableMap<String, Long>, MutableMap<String, OperationMonkey>>): Long {
        return solve(monkeys.first.toMutableMap(), monkeys.second.toMutableMap())
    }

    fun part2(monkeys: Pair<MutableMap<String, Long>, MutableMap<String, OperationMonkey>>): Long {
        return solve(monkeys.first.toMutableMap(), monkeys.second.toMutableMap(), true, true)
    }

    val testInput = processInput(readInput("Day21_test"))
    check(part1(testInput) == 152L)
    check(part2(testInput) == 301L)

    val input = processInput(readInput("Day21"))
    println(part1(input))
    println(part2(input))
}
