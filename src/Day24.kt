import kotlin.math.abs

fun main() {
    data class Vector2(var x: Int, var y: Int) {
        operator fun plus(other: Vector2): Vector2 = Vector2(x + other.x, y + other.y)
        operator fun minus(other: Vector2): Vector2 = Vector2(x - other.x, y - other.y)
        override operator fun equals(other: Any?): Boolean = other is Vector2 && x == other.x && y == other.y
        fun clone(): Vector2 = Vector2(x, y)
    }

    data class Blizzard(var position: Vector2, var direction: Vector2) {
        fun clone(): Blizzard = Blizzard(position.clone(), direction.clone())
    }

    var initialBlizzards = listOf<Blizzard>()
    var blizzards = mutableListOf<Blizzard>()
    val directions = listOf(Vector2(1, 0), Vector2(0, 1), Vector2(0, -1), Vector2(-1, 0), Vector2(0, 0))
    var dimensions = Vector2(0, 0)
    val start = Vector2(1, 0)
    var end = Vector2(0, 0)

    fun processInput(input: List<String>) {
        blizzards.clear()
        dimensions = Vector2(input[0].length, input.size)
        end = Vector2(dimensions.x - 2, dimensions.y - 1)

        for ((y, row) in input.withIndex()) {
            if (y == 0 || y == input.lastIndex) continue

            for ((x, cell) in row.withIndex()) {
                if (x == 0 || x == row.lastIndex) continue

                if (cell == '.') continue
                val position = Vector2(x, y)
                val direction = Vector2(0, 0)
                when (cell) {
                    '^' -> direction.y--
                    '>' -> direction.x++
                    'v' -> direction.y++
                    '<' -> direction.x--
                }

                blizzards.add(Blizzard(position, direction))
            }
        }

        initialBlizzards = blizzards.map { it.copy() }
    }

    fun print(blizzards: List<Blizzard>, position: Vector2) {
        val map = MutableList(dimensions.y) { MutableList(dimensions.x) { '.' } }
        blizzards.forEach {
            val char = when (it.direction) {
                directions[0] -> '>'
                directions[1] -> 'v'
                directions[2] -> '^'
                directions[3] -> '<'
                else -> null
            }

            val x = it.position.x; val y = it.position.y

            if (map[y][x] != '.') {
                if (map[y][x] in listOf('^', '>', 'v', '<')) {
                    map[y][x] = '2'
                } else {
                    map[y][x] = (map[y][x].toString().toInt() + 1).toChar()
                }
            } else {
                map[y][x] = char!!
            }
        }

        for (y in 0 until dimensions.y) {
            for (x in 0 until dimensions.x) {
                if (y in 1 until dimensions.y - 1 && x in 1 until dimensions.x - 1) continue
                if ((start.x == x && start.y == y) || (end.x == x && end.y == y)) continue
                map[y][x] = '#'
            }
        }

        map[position.y][position.x] = 'E'

        for (row in map) {
            for (c in row) print(c)
            print('\n')
        }

        print("\n\n")
    }

    fun solve(startAt: Vector2, goal: Vector2): Int {
        val horizontalRange = 1 until dimensions.x - 1
        val verticalRange = 1 until dimensions.y - 1
        var minutes = 0
        var branches = hashSetOf(startAt)
        while (true) {
            minutes++

            for (i in blizzards.indices) {
                val destination = blizzards[i].position + blizzards[i].direction
                if (destination.x < 1) destination.x = horizontalRange.last
                else if (destination.x > horizontalRange.last) destination.x = 1
                else if (destination.y < 1) destination.y = verticalRange.last
                else if (destination.y > verticalRange.last) destination.y = 1
                blizzards[i].position = destination
            }

            val newBranches = HashSet<Vector2>()
            for (pos in branches) {
                for (direction in directions) {
                    val destination = pos + direction
                    if ((destination == goal || destination == startAt || (destination.x in horizontalRange && destination.y in verticalRange))
                        && blizzards.none { it.position == destination }) {
                        newBranches.add(destination)
                    }
                }
            }

            if (newBranches.size == 0) continue
            if (newBranches.any { it == goal }) return minutes

            branches = newBranches
        }
    }

    fun part1(): Int {
        return solve(start, end)
    }

    fun part2(): Int {
        blizzards = initialBlizzards.map { it.copy() }.toMutableList()
        val a = solve(start, end)
        val b = solve(end, start)
        val c = solve(start, end)
        return a + b + c
    }

    processInput(readInput("Day24_test"))
    check(part1() == 18)
    check(part2() == 54)

    println("Checks passed")

    processInput(readInput("Day24"))
    println(part1())
    println(part2())
}
