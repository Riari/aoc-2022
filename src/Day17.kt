fun main() {
    data class Vector(var x: Int, var y: Int) {
        operator fun plus(other: Vector): Vector {
            return Vector(x + other.x, y + other.y)
        }
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
    }

    class Window(size: Int, var offset: Int = 0) {
        var view: Array<BooleanArray> = Array(size) { BooleanArray(7) { false } }

        fun rockWillCollideWithAny(rock: Rock, direction: Vector): Boolean {
            val movedShape = rock.shape.map { Vector(rock.position.x + it.x + direction.x, rock.position.y + it.y + direction.y - offset) }
            return movedShape.any { view.getOrNull(it.y)?.getOrElse(it.x) { false } == true }
        }

        fun canRockGoLeft(rock: Rock): Boolean {
            if (rockWillCollideWithAny(rock, DIRECTION_LEFT)) return false
            return rock.position.x > 0
        }

        fun canRockGoRight(rock: Rock): Boolean {
            if (rockWillCollideWithAny(rock, DIRECTION_RIGHT)) return false
            return (rock.position.x + rock.dimensions.x) < view[0].size
        }

        fun canRockGoDown(rock: Rock): Boolean {
            if (rockWillCollideWithAny(rock, DIRECTION_DOWN)) return false
            return rock.position.y > 0
        }

        fun add(rock: Rock) {
            for (vec in rock.getAbsolutePositions()) {
                view[vec.y - offset][vec.x] = true
            }
        }

        fun getDepthProfile(height: Int): List<Int> {
            // TODO: Update this to produce more of a "depth map" (relative to the current total height).
            val profile = MutableList(7) { 0 }
            for (y in height - offset downTo 0) {
                for ((x, value) in view[y].withIndex()) {
                    if (value && profile[x] < y + 1) profile[x] = y + 1
                }

                if (profile.none { it == 0 }) return profile
            }

            return profile
        }

        fun slideIfNeeded(height: Int, spawnArea: Int) {
            val absoluteWindowTop = offset + view.size
            val availableSpace = absoluteWindowTop - height
            if (spawnArea > availableSpace) {
                val rowsToAdd = spawnArea - availableSpace
                repeat (rowsToAdd) {
                    view[it] = view[it + 1]
                }

                for (i in view.size - 1 downTo view.size - rowsToAdd - 1) {
                    view[i] = BooleanArray(7) { false }
                    offset++
                }
            }
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

    fun solve(input: List<String>, isPartTwo: Boolean = false): Long {
        val jets = input[0]
        val seenJets = mutableSetOf<Int>()
        var height = 0
        val seenHeights = mutableListOf<Int>()
        val window = if (isPartTwo) Window(6000) else Window(5000)
        val rounds = if (isPartTwo) 3500 else 2022
        val partTwoRounds = 1_000_000_000_000L

        var rockIndex = 0; var jetIndex = 0
        var roundAtCycleStart = -1
        var jetIndexAtCycleStart = -1

        println("\nSolving with $rounds rounds...")

        val begin = System.currentTimeMillis()
        val percentagesShown = mutableSetOf(0)

        repeat (rounds) { round ->
            val rock = rocks[rockIndex++].spawn(height + 3)
            if (rockIndex == rocks.size) rockIndex = 0

            window.slideIfNeeded(height, rock.dimensions.y + 3)

            while (true) {
                val jet = jets[jetIndex++]
                if (jetIndex == jets.length) jetIndex = 0

                if (jet == '<' && window.canRockGoLeft(rock)) rock.move(DIRECTION_LEFT)
                else if (jet == '>' && window.canRockGoRight(rock)) rock.move(DIRECTION_RIGHT)

                if (window.canRockGoDown(rock)) rock.move(DIRECTION_DOWN)
                else break
            }

            window.add(rock)

            if (rock.position.y + rock.dimensions.y > height) {
                height = rock.position.y + rock.dimensions.y
            }

            if (isPartTwo && rockIndex == 0) {
                if (jetIndexAtCycleStart == -1 && !seenJets.add(jetIndex)) {
                    roundAtCycleStart = round
                    jetIndexAtCycleStart = jetIndex
                } else if (jetIndex == jetIndexAtCycleStart) {
                    val heightDelta = seenHeights.last() - seenHeights.first()
                    val roundsDelta = round - roundAtCycleStart
                    val roundsRemaining = partTwoRounds - roundAtCycleStart
                    val cyclesRemaining = roundsRemaining / roundsDelta
                    val finalHeight = (cyclesRemaining * heightDelta) + seenHeights[(roundsRemaining % roundsDelta).toInt()]
                    return finalHeight - 1
                }
            }

            if (jetIndexAtCycleStart > -1) {
                seenHeights.add(height)
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
        return solve(input)
    }

    fun part2(input: List<String>): Long {
        return solve(input, true)
    }

    val testInput = readInput("Day17_test")
    check(part1(testInput) == 3068L)
    check(part2(testInput) == 1514285714288L)

    val input = readInput("Day17")
    println(part1(input))
    println(part2(input))
}
