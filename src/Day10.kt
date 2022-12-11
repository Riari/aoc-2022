fun main() {
    fun processCycles(input: List<String>): List<Int> {
        val cycles = mutableListOf<Int>()
        var x = 1
        for (instruction in input) {
            val parts = instruction.split(" ")

            when (parts[0]) {
                "noop" -> cycles.add(x)
                "addx" -> {
                    cycles.add(x)
                    cycles.add(x)
                    x += parts[1].toInt()
                }
            }
        }

        return cycles
    }

    fun part1(input: List<String>): Int {
        val cycles = processCycles(input)
        val signalStrengths = mutableListOf<Int>()
        for (i in cycles.indices) {
            if (intArrayOf(19, 59, 99, 139, 179, 219).contains(i)) {
                signalStrengths.add((i + 1) * cycles[i])
            }
        }

        return signalStrengths.sum()
    }

    fun part2(input: List<String>): String {
        val cycles = processCycles(input)
        var result = ""
        for (i in cycles.indices) {
            result += if (((i) % 40) in (cycles[i] - 1)..(cycles[i] + 1)) {
                "\uD83C\uDF81"
            } else {
                "\uD83C\uDF84"
            }

            if (((i + 1) % 40) == 0) {
                result += "\n"
            }
        }

        return result
    }

    val testInput = readInput("Day10_test")
    check(part1(testInput) == 13140)
    check(part2(testInput) == """🎁🎁🎄🎄🎁🎁🎄🎄🎁🎁🎄🎄🎁🎁🎄🎄🎁🎁🎄🎄🎁🎁🎄🎄🎁🎁🎄🎄🎁🎁🎄🎄🎁🎁🎄🎄🎁🎁🎄🎄
🎁🎁🎁🎄🎄🎄🎁🎁🎁🎄🎄🎄🎁🎁🎁🎄🎄🎄🎁🎁🎁🎄🎄🎄🎁🎁🎁🎄🎄🎄🎁🎁🎁🎄🎄🎄🎁🎁🎁🎄
🎁🎁🎁🎁🎄🎄🎄🎄🎁🎁🎁🎁🎄🎄🎄🎄🎁🎁🎁🎁🎄🎄🎄🎄🎁🎁🎁🎁🎄🎄🎄🎄🎁🎁🎁🎁🎄🎄🎄🎄
🎁🎁🎁🎁🎁🎄🎄🎄🎄🎄🎁🎁🎁🎁🎁🎄🎄🎄🎄🎄🎁🎁🎁🎁🎁🎄🎄🎄🎄🎄🎁🎁🎁🎁🎁🎄🎄🎄🎄🎄
🎁🎁🎁🎁🎁🎁🎄🎄🎄🎄🎄🎄🎁🎁🎁🎁🎁🎁🎄🎄🎄🎄🎄🎄🎁🎁🎁🎁🎁🎁🎄🎄🎄🎄🎄🎄🎁🎁🎁🎁
🎁🎁🎁🎁🎁🎁🎁🎄🎄🎄🎄🎄🎄🎄🎁🎁🎁🎁🎁🎁🎁🎄🎄🎄🎄🎄🎄🎄🎁🎁🎁🎁🎁🎁🎁🎄🎄🎄🎄🎄
""")

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}
