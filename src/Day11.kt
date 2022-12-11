fun main() {
    data class Monkey(
        val operation: Char,
        val operand: Long?,
        val divisor: Long,
        val trueRecipient: Int,
        val falseRecipient: Int,
        var items: ArrayDeque<Long> = ArrayDeque(),
        var inspectionCount: Long = 0)

    // Worry level reduction idea taken from https://github.com/ClouddJR/advent-of-code-2022/blob/main/src/main/kotlin/com/clouddjr/advent2022/Day11.kt
    fun solve(input: List<String>, rounds: Int, processWorry: (Long, Long) -> Long): Long {
        val inputChunked = input.chunked(7)
        val monkeys = mutableListOf<Monkey>()
        val regex = Regex("\\d+")

        // Monkey setup
        for (notes in inputChunked) {
            val monkey = Monkey(
                if (notes[2].contains('+')) '+' else '*',
                regex.find(notes[2])?.value?.toLong(),
                regex.find(notes[3])!!.value.toLong(),
                regex.find(notes[4])!!.value.toInt(),
                regex.find(notes[5])!!.value.toInt(),
            )

            val items = regex.findAll(notes[1]).toList().map { it.value.toLong() }
            items.forEach { monkey.items.addFirst(it) }

            monkeys.add(monkey)
        }

        val modulus = monkeys.map { it.divisor }.reduce(Long::times)

        // Monkey business
        repeat (rounds) {
            for (monkey in monkeys) {
                val itemIterator = monkey.items.iterator()
                while (itemIterator.hasNext()) {
                    // Item value represents 'worry level'
                    var item = itemIterator.next()

                    // If operand is null, assume 'old', i.e. the level should be applied to itself
                    when (monkey.operation) {
                        '+' -> item += monkey.operand ?: item
                        '*' -> item *= monkey.operand ?: item
                    }

                    item = processWorry(item, modulus)

                    if (item % monkey.divisor == 0L) {
                        monkeys[monkey.trueRecipient].items.add(item)
                    } else {
                        monkeys[monkey.falseRecipient].items.add(item)
                    }

                    itemIterator.remove()
                    monkey.inspectionCount++
                }
            }
        }

        return monkeys.map { it.inspectionCount }.sortedDescending().take(2).reduce(Long::times)
    }

    fun part1(input: List<String>): Long {
        return solve(input, 20) { level, _ -> level / 3 }
    }

    fun part2(input: List<String>): Long {
        return solve(input, 10000) { level, modulus -> level % modulus }
    }

    val testInput = readInput("Day11_test")
    check(part1(testInput) == 10605.toLong())
    check(part2(testInput) == 2713310158)

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}
