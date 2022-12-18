fun main() {
    data class Vector(var x: Int, var y: Int) {
        operator fun plus(other: Vector): Vector {
            return Vector(x + other.x, y + other.y)
        }
    }

    fun List<Vector>.has(vec: Vector): Boolean {
        return this.any { it.x == vec.x && it.y == vec.y }
    }

    val TUNNEL_WIDTH = 7
    val DIRECTION_LEFT = Vector(-1, 0)
    val DIRECTION_RIGHT = Vector(1, 0)
    val DIRECTION_DOWN = Vector(0, -1)

    class Rock(val shape: List<Vector>, val position: Vector = Vector(0, 0)) {
        val dimensions = Vector(0, 0)

        init {
            updateDimensions()
        }

        fun updateDimensions() {
            var maxY = 0; var maxX = 0
            for (vec in shape) {
                if (vec.x > maxX) maxX = vec.x
                if (vec.y > maxY) maxY = vec.y
            }

            dimensions.x = maxX + 1
            dimensions.y = maxY + 1
        }

        fun spawn(height: Int): Rock {
            return Rock(shape.toList(), Vector(2, height))
        }

        fun move(direction: Vector) {
            position.x += direction.x
            position.y += direction.y
        }

        fun getAbsolutePositions(): List<Vector> {
            return shape.map { it + position }
        }

        fun willCollideWithAny(window: Array<BooleanArray>, windowOffset: Int, direction: Vector): Boolean {
            val movedShape = shape.map { Vector(position.x + it.x + direction.x, position.y + it.y + direction.y - windowOffset) }
            return movedShape.any { window.getOrNull(it.y)?.getOrElse(it.x) { false } == true }
        }

        fun canGoLeft(window: Array<BooleanArray>, windowOffset: Int): Boolean {
            if (willCollideWithAny(window, windowOffset, DIRECTION_LEFT)) return false
            return position.x > 0
        }

        fun canGoRight(window: Array<BooleanArray>, windowOffset: Int): Boolean {
            if (willCollideWithAny(window, windowOffset, DIRECTION_RIGHT)) return false
            return (position.x + dimensions.x) < TUNNEL_WIDTH
        }

        fun canGoDown(window: Array<BooleanArray>, windowOffset: Int): Boolean {
            if (willCollideWithAny(window, windowOffset, DIRECTION_DOWN)) return false
            return position.y > 0
        }
    }

    val rocks = listOf(
        listOf("####"),
        listOf(".#.", "###", ".#."),
        listOf("###", "..#", "..#"),
        listOf("#", "#", "#", "#"),
        listOf("##", "##")
    ).map {
        val shape = mutableListOf<Vector>()
        for (y in it.indices) {
            for (x in it[y].indices) {
                if (it[y][x] == '#') shape.add(Vector(x, y))
            }
        }

        Rock(shape)
    }

    fun printWindow(window: Array<BooleanArray>, rock: Rock, withFloor: Boolean) {
        for (y in window.size - 1 downTo  0) {
            print("|")
            for ((x, pos) in window[y].withIndex()) {
                if (Vector(x, y) in rock.getAbsolutePositions()) print("@")
                else if (pos) print("#")
                else print(".")
            }
            print("|")
            print('\n')
        }

        if (withFloor) print("+-------+")
        print("\n\n")
    }

    fun solve(input: List<String>, rounds: Int): Long {
        var rockIndex = 0; var jetIndex = 0
        val jets = input[0]
        var height = 0
        var windowOffset = 0
        val window = Array(5000) { BooleanArray(7) { false } }

        println("\nSolving with $rounds rounds...")

        val begin = System.currentTimeMillis()
        val percentagesShown = mutableSetOf(0)

        repeat (rounds) { round ->
            val rock = rocks[rockIndex++].spawn(height + 3)
            if (rockIndex == rocks.size) rockIndex = 0

            while (true) {
//                printWindow(window, rock, windowOffset == 0)
                val jet = jets[jetIndex++]
                if (jetIndex == jets.length) jetIndex = 0

                if (jet == '<' && rock.canGoLeft(window, windowOffset)) {
                    rock.move(DIRECTION_LEFT)
                } else if (jet == '>' && rock.canGoRight(window, windowOffset)) {
                    rock.move(DIRECTION_RIGHT)
                }
//                printWindow(window, rock, windowOffset == 0)

                if (!rock.canGoDown(window, windowOffset)) break

                rock.move(DIRECTION_DOWN)
            }

            for (vec in rock.getAbsolutePositions()) {
                window[vec.y - windowOffset][vec.x] = true
            }

            if (rock.position.y + rock.dimensions.y > height) {
                height = rock.position.y + rock.dimensions.y
            }

            val absoluteWindowTop = windowOffset + window.size
            val availableSpace = absoluteWindowTop - height
            val spawnArea = rocks[rockIndex].dimensions.y + 3
            if (spawnArea > availableSpace) {
                val rowsToAdd = spawnArea - availableSpace
                repeat (rowsToAdd) {
                    window[it] = window[it + 1]
                }

                for (i in window.size - 1 downTo window.size - rowsToAdd - 1) {
                    window[i] = BooleanArray(7) { false }
                    windowOffset++
                }
            }

            val completed = (((round + 1).toFloat() / rounds.toFloat()) * 100).toInt()
            if (!percentagesShown.contains(completed) && completed % 20 == 0) {
                val elapsed = System.currentTimeMillis() - begin
                println("$completed% complete (elapsed: $elapsed ms)")
                percentagesShown.add(completed)
            }
        }

        return height.toLong()
    }

    fun part1(input: List<String>): Long {
        return solve(input, 2022)
    }

    fun part2(input: List<String>): Long {
//        return solve(input, 1_000_000_000_000L)
        return 0
    }

    val testInput = readInput("Day17_test")
    check(part1(testInput) == 3068L)
//    check(part2(testInput) == 1514285714288L)

    val input = readInput("Day17")
    println(part1(input))
//    println(part2(input))
}
