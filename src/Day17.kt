fun main() {
    val tunnelWidth = 8

    data class Vector(var x: Long, var y: Long) {
        operator fun plus(other: Vector): Vector {
            return Vector(x + other.x, y + other.y)
        }
    }

    fun List<Vector>.has(vec: Vector): Boolean {
        return this.any { it.x == vec.x && it.y == vec.y }
    }

    val DIRECTION_LEFT = Vector(-1, 0)
    val DIRECTION_RIGHT = Vector(1, 0)
    val DIRECTION_DOWN = Vector(0, -1)

    class Rock(val shape: List<Vector>, val position: Vector = Vector(0, 0)) {
        val dimensions = Vector(0, 0)

        init {
            updateDimensions()
        }

        fun updateDimensions() {
            var maxY = 0L; var maxX = 0L
            for (vec in shape) {
                if (vec.x > maxX) maxX = vec.x
                if (vec.y > maxY) maxY = vec.y
            }

            dimensions.x = maxX + 1L
            dimensions.y = maxY + 1L
        }

        fun spawn(height: Long): Rock {
            return Rock(shape.toList(), Vector(3, height))
        }

        fun move(direction: Vector) {
            position.x += direction.x
            position.y += direction.y
        }

        fun getAbsolutePositions(): List<Vector> {
            return shape.map { it + position }
        }

        fun willCollideWithAny(rocks: List<Vector>, direction: Vector): Boolean {
            val movedShape = shape.map { Vector(position.x + it.x + direction.x, position.y + it.y + direction.y) }
            return rocks.any { movedShape.has(it) }
        }

        fun canGoLeft(rocks: List<Vector>): Boolean {
            if (willCollideWithAny(rocks, DIRECTION_LEFT)) return false
            return position.x > 1
        }

        fun canGoRight(rocks: List<Vector>): Boolean {
            if (willCollideWithAny(rocks, DIRECTION_RIGHT)) return false
            return (position.x + dimensions.x) < tunnelWidth
        }

        fun canGoDown(rocks: List<Vector>): Boolean {
            if (willCollideWithAny(rocks, DIRECTION_DOWN)) return false
            return position.y > 1
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
                if (it[y][x] == '#') shape.add(Vector(x.toLong(), y.toLong()))
            }
        }

        Rock(shape)
    }

    fun printTunnel(height: Int, settled: List<Rock>, current: Rock) {
        val tunnel = mutableListOf<MutableList<Char>>()
        for (y in 0..height + 5) {
            val row = mutableListOf<Char>()
            for (x in 0..tunnelWidth) {
                var char = '.'
                if (y == 0 && (x == 0 || x == tunnelWidth)) char = '+'
                else if (y == 0) char = '-'
                else if (x == 0 || x == tunnelWidth) char = '|'
                row.add(char)
            }
            tunnel.add(row)
        }

        for (rock in settled.union(listOf(current))) {
            for (vec in rock.shape) {
                tunnel[(rock.position.y + vec.y).toInt()][(rock.position.x + vec.x).toInt()] = '#'
            }
        }

        tunnel.reverse()

        for (row in tunnel) {
            for (char in row) {
                print(char)
            }
            print('\n')
        }
    }

    fun solve(input: List<String>, rounds: Int): Long {
        var rockIndex = 0; var jetIndex = 0
        var height = 1L
        val jets = input[0]
        val maxSettledRock = 200 // max number of positions to store for settled rocks
        val settledRock = mutableListOf<Vector>()

        println("\nSolving with $rounds rounds...")

        val begin = System.currentTimeMillis()
        val percentagesShown = mutableSetOf(0)

        repeat (rounds) {
            val rock = rocks[rockIndex++].spawn(height + 3)
            if (rockIndex == rocks.size) rockIndex = 0

            while (true) {
                val jet = jets[jetIndex++]
                if (jetIndex == jets.length) jetIndex = 0

                if (jet == '<' && rock.canGoLeft(settledRock)) {
                    rock.move(DIRECTION_LEFT)
                } else if (jet == '>' && rock.canGoRight(settledRock)) {
                    rock.move(DIRECTION_RIGHT)
                }

                if (!rock.canGoDown(settledRock)) break

                rock.move(DIRECTION_DOWN)
            }

            settledRock.addAll(0, rock.getAbsolutePositions())

            if (settledRock.size > maxSettledRock) {
                repeat (settledRock.size - maxSettledRock) {
                    settledRock.removeLast()
                }
            }

            if (rock.position.y + rock.dimensions.y > height) {
                height = rock.position.y + rock.dimensions.y
            }

            val completed = (((it + 1).toFloat() / rounds.toFloat()) * 100).toInt()
            if (!percentagesShown.contains(completed) && completed % 20 == 0) {
                val elapsed = System.currentTimeMillis() - begin
                println("$completed% complete (elapsed: $elapsed ms)")
                percentagesShown.add(completed)
            }
        }

        return height - 1
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
