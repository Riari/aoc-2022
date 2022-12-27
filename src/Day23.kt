fun main() {
    data class Vector2(var x: Int, var y: Int) {
        operator fun plus(other: Vector2): Vector2 = Vector2(x + other.x, y + other.y)
        operator fun minus(other: Vector2): Vector2 = Vector2(x - other.x, y - other.y)
    }

    var paddingSize = 0
    val directions = listOf(
        listOf(Vector2(0, -1), Vector2(1, -1), Vector2(-1, -1)),
        listOf(Vector2(0, 1), Vector2(1, 1), Vector2(-1, 1)),
        listOf(Vector2(-1, 0), Vector2(-1, -1), Vector2(-1, 1)),
        listOf(Vector2(1, 0), Vector2(1, -1), Vector2(1, 1)),
    )

    fun processInput(input: List<String>): List<BooleanArray> {
        paddingSize = input.size * 2
        val output = mutableListOf<BooleanArray>()
        val paddingRow = BooleanArray(paddingSize * 2 + input.size)
        val paddingSide = BooleanArray(paddingSize)
        repeat (paddingSize) { output.add(paddingRow.clone()) }
        input.forEach {
            val middle = it.map { c -> c == '#' }.toBooleanArray()
            output.add(paddingSide.clone() + middle + paddingSide.clone())
        }
        repeat (paddingSize) { output.add(paddingRow.clone()) }

        return output
    }

    fun getSmallestArea(map: List<BooleanArray>): List<List<Boolean>> {
        val start = Vector2(Int.MAX_VALUE, map.indexOfFirst { it.contains(true) })
        val end = Vector2(0, map.indexOfLast { it.contains(true) })

        for (row in map) {
            val firstElf = row.indexOfFirst { it }
            val lastElf = row.indexOfLast { it }
            if (firstElf > -1) start.x = minOf(start.x, firstElf)
            if (lastElf > -1) end.x = maxOf(end.x, lastElf)
        }

        return map.subList(start.y, end.y + 1).map { it.slice(start.x .. end.x) }
    }

    fun print(area: List<List<Boolean>>) {
        for (row in area) {
            for (isElf in row) {
                print(if (isElf) '#' else '.')
            }
            print('\n')
        }

        print("\n\n")
    }

    fun solve(input: List<BooleanArray>, maxRounds: Int): Pair<List<List<Boolean>>, Int> {
        val map = input.map { it.clone() }
        var startDirection = 0
        val moves = mutableMapOf<Vector2, MutableList<Vector2>>()

        var round = 0
        while (round < maxRounds) {
            // Calculate moves
            var elvesMoved = 0
            for ((y, row) in map.withIndex()) {
                for ((x, isElf) in row.withIndex()) {
                    if (!isElf) continue
                    val position = Vector2(x, y)
                    val movesForElf = mutableListOf<Vector2>()
                    for (i in startDirection until startDirection + directions.size) {
                        val index = i % 4
                        val direction = directions[index]
                        val destinations = direction.map { position + it }
                        if (destinations.none { map[it.y][it.x] }) {
                            movesForElf.add(destinations[0])
                        }
                    }

                    if (movesForElf.size in 1 until 4) {
                        if (!moves.containsKey(movesForElf[0])) moves[movesForElf[0]] = mutableListOf()
                        moves[movesForElf[0]]!!.add(position)
                        elvesMoved++
                    }
                }
            }

            if (elvesMoved == 0) break

            // Execute moves for elves who won't clash with any others
            for (move in moves) {
                if (move.value.size > 1) continue
                val destination = move.key
                val position = move.value[0]
                map[position.y][position.x] = false
                map[destination.y][destination.x] = true
            }

            moves.clear()

            // Start with the next direction for the next round
            startDirection = (startDirection + 1) % 4

            round++
        }

        return Pair(getSmallestArea(map), round + 1)
    }

    fun part1(input: List<BooleanArray>): Int {
        val result = solve(input, 10)
        return result.first.sumOf { it.count { isElf -> !isElf } }
    }

    fun part2(input: List<BooleanArray>): Int {
        val result = solve(input, 1000)
        return result.second
    }

    val testInput = processInput(readInput("Day23_test"))
    check(part1(testInput) == 110)
    check(part2(testInput) == 20)

    val input = processInput(readInput("Day23"))
    println(part1(input))
    println(part2(input))
}
