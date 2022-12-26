import java.util.*
import kotlin.math.sqrt

fun main() {
    data class Vector2(var x: Int, var y: Int) {
        operator fun plus(other: Vector2): Vector2 = Vector2(x + other.x, y + other.y)
        operator fun minus(other: Vector2): Vector2 = Vector2(x - other.x, y - other.y)
        operator fun times(other: Int): Vector2 = Vector2(x * other, y * other)

        override fun equals(other: Any?): Boolean {
            return other is Vector2
                    && x == other.x
                    && y == other.y
        }

        override fun hashCode(): Int = Objects.hash(x, y)
    }

    // Action can be a movement or a rotation. For movements, the value is the number of spaces to move. For rotations, the value is -1 (left) or 1 (right).
    data class Action(val isRotation: Boolean, val value: Int)

    // Map is a container for the grid and actions. It also keeps track of simulation state.
    class Map(val grid: List<CharArray>, val actions: List<Action>) {
        var width = 0; var height = 0
        var position = Vector2(0, 0)
        var facing = 0
        val facings = mapOf(
            'e' to Vector2(1, 0),
            's' to Vector2(0, 1),
            'w' to Vector2(-1, 0),
            'n' to Vector2(0, -1),
        )

        val path = mutableMapOf<Vector2, Char>()
        val portals = mutableMapOf<Vector2, Char>()
        val facingSymbols = listOf('>', 'v', '<', '^')

        // Cube mapping state
        var asCube = false
        var area = 0
        var sideLength = 0
        val innerCornerShapes = listOf(
            listOf(1, 1, 0, 1, 1, 1, 1, 1, 1),
            listOf(1, 1, 1, 1, 1, 1, 1, 1, 0),
            listOf(1, 1, 1, 1, 1, 1, 0, 1, 1),
            listOf(0, 1, 1, 1, 1, 1, 1, 1, 1),
        )
        val outerCornerShapes = listOf(
            listOf(0, 0, 0, 0, 1, 1, 0, 1, 1),
            listOf(0, 0, 0, 1, 1, 0, 1, 1, 0),
            listOf(1, 1, 0, 1, 1, 0, 0, 0, 0),
            listOf(0, 1, 1, 0, 1, 1, 0, 0, 0),
        )
        var innerCorners = mutableListOf<Vector2>()
        val edgeMap = mutableMapOf<Pair<Vector2, Int>, Pair<Vector2, Int>>()

        init {
            reset()
            width = grid.maxOf { it.size }
            height = grid.size
        }

        fun reset() {
            position = Vector2(grid[0].takeWhile { it == ' ' }.count(), 0)
            facing = 0
        }

        // Enables cube traversal and maps cube edge coordinates.
        // I borrowed part of the algorithm for this from https://gist.github.com/juj/1f09f475e01949233a2f206a0552425c,
        // but it has some issues with the way it traverses top/left edges internally the cube and bottom/right edges externally,
        // so I made some changes to rectify that.
        fun enableCube() {
            asCube = true
            area = grid.sumOf { it.filter { c -> c != ' ' }.size }
            sideLength = sqrt((area / 6).toDouble()).toInt()

            for (y in 0 until height) {
                for (x in 0 until width) {
                    if (isCellVoid(x, y)) continue
                    val vector = Vector2(x, y)
                    val map = positionToBitmap(vector)
                    if (getInnerCornerShape(map) >= 0) innerCorners.add(vector)
                }
            }

            mapCubeEdges()
            check(true)
        }

        private fun mapCubeEdges() {
            var charLower = 'a'
            var charUpper = 'A'
            for (corner in innerCorners) {
                var clockwise = corner
                var clockwiseHeading = getHeading(clockwise, true)!!
                var clockwiseFacing = getFacingFromIndex(clockwiseHeading)
                var antiClockwise = corner
                var antiClockwiseHeading = getHeading(antiClockwise, false)!!
                var antiClockwiseFacing = getFacingFromIndex(antiClockwiseHeading)

                while (true) {
                    val clockwiseAdvance = if (cell(clockwise + clockwiseFacing * sideLength) == ' ')
                        sideLength - 1 else sideLength
                    val antiClockwiseAdvance = if (cell(antiClockwise + antiClockwiseFacing * sideLength) == ' ')
                        sideLength - 1 else sideLength
                    val clockwiseEnd = clockwise + clockwiseFacing * clockwiseAdvance
                    val antiClockwiseEnd = antiClockwise + antiClockwiseFacing * antiClockwiseAdvance

                    for (n in 1 .. minOf(clockwiseAdvance, antiClockwiseAdvance)) {
                        val a = clockwise + clockwiseFacing * n
                        val b = antiClockwise + antiClockwiseFacing * n
                        val fromA = Pair(a, (clockwiseHeading - 1).mod(4))
                        val toB = Pair(b, (antiClockwiseHeading - 1).mod(4))
                        val fromB = Pair(b, (antiClockwiseHeading + 1).mod(4))
                        val toA = Pair(a, (clockwiseHeading + 1).mod(4))
                        edgeMap[fromA] = toB
                        edgeMap[fromB] = toA

                        portals[a] = if (n <= sideLength / 2) charLower else charUpper
                        portals[b] = if (n <= sideLength / 2) charLower else charUpper
                    }

                    charLower++
                    charUpper++

                    val newClockwiseHeading = getHeading(clockwiseEnd, true) ?: clockwiseHeading
                    val newAntiClockwiseHeading = getHeading(antiClockwiseEnd, false) ?: antiClockwiseHeading

                    if (newClockwiseHeading != clockwiseHeading && newAntiClockwiseHeading != antiClockwiseHeading) {
                        break
                    }

                    val newClockwiseFacing = getFacingFromIndex(newClockwiseHeading)
                    val newAntiClockwiseFacing = getFacingFromIndex(newAntiClockwiseHeading)

                    clockwise = if (newClockwiseHeading != clockwiseHeading) {
                        clockwiseEnd - newClockwiseFacing
                    } else {
                        clockwiseEnd
                    }

                    antiClockwise = if (newAntiClockwiseHeading != antiClockwiseHeading) {
                        antiClockwiseEnd - newAntiClockwiseFacing
                    } else {
                        antiClockwiseEnd
                    }

                    clockwiseHeading = newClockwiseHeading
                    clockwiseFacing = newClockwiseFacing
                    antiClockwiseHeading = newAntiClockwiseHeading
                    antiClockwiseFacing = newAntiClockwiseFacing
                }
            }
        }

        // Returns the heading according to corner shape (inner or outer) and direction (clockwise or anti-clockwise).
        // Null means the position is not on a corner.
        private fun getHeading(pos: Vector2, clockwise: Boolean): Int? {
            val map = positionToBitmap(pos)

            var shapeIndex = getInnerCornerShape(map)
            if (shapeIndex >= 0) return if (clockwise) shapeIndex else (shapeIndex - 1).mod(4)

            shapeIndex = getOuterCornerShape(map)
            if (shapeIndex >= 0) return if (clockwise) shapeIndex else (shapeIndex + 1).mod(4)

            return null
        }

        private fun getFacingFromIndex(index: Int): Vector2 = facings.values.toList()[index.mod(4)]

        fun print() {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val vector = Vector2(x, y)
                    print(portals.getOrDefault(vector, path.getOrDefault(vector, grid[y].getOrElse(x) { ' ' })))
                }
                print("\n")
            }

            print("\n\n")
        }

        fun follow() {
            for (action in actions) {
                if (action.isRotation) {
                    facing = (facing + action.value).mod(4)
                    continue
                }

                move(action.value)
            }
        }

        private fun move(distance: Int)
        {
            for (i in 0 until distance) {
                path[position] = facingSymbols[facing]
                val direction = getFacingFromIndex(facing)
                when (cell(position + direction)) {
                    '.' -> position += direction
                    '#' -> break
                    ' ' -> {
                        // Out of bounds.
                        if (!asCube && !tryWrap(direction)) break
                        if (asCube && !tryWrapAsCube()) break
                    }
                }
            }
        }

        private fun tryWrap(direction: Vector2): Boolean {
            var pos = position

            while (true) {
                if (direction.y != 0 && pos.x > grid[pos.y - direction.y].lastIndex) break
                if (cell(pos - direction) == ' ') break
                pos -= direction
                if (direction.x != 0 && (pos.x == 0 || pos.x == row()!!.lastIndex)) break
                if (direction.y != 0 && (pos.y == 0 || pos.y == grid.lastIndex)) break
            }

            if (cell(pos) == '#') return false

            position = pos
            return true
        }

        private fun tryWrapAsCube(): Boolean {
            val destination = edgeMap.getOrDefault(Pair(position, facing), null)
            check(destination != null)

            if (cell(destination.first) == '#') return false

            position = destination.first
            facing = destination.second

            return true
        }

        private fun row(row: Int = position.y): CharArray? = grid.getOrNull(row)
        private fun cell(position: Vector2): Char = cell(position.x, position.y)
        private fun cell(x: Int = position.x, y: Int = position.y): Char = row(y)?.getOrNull(x) ?: ' '
        private fun isCellVoid(x: Int, y: Int): Boolean = cell(x, y) == ' '

        // Returns a list representing the edge or corner shape at the given position.
        // 1 = filled, 0 = void.
        private fun positionToBitmap(pos: Vector2): List<Int> {
            val map = mutableListOf<Int>()
            for (y in pos.y - 1 .. pos.y + 1) {
                for (x in pos.x - 1 .. pos.x + 1) {
                    map.add(if (cell(x, y) == ' ') 0 else 1)
                }
            }

            return map
        }

        private fun getInnerCornerShape(shape: List<Int>): Int = getIndexOfShape(shape, innerCornerShapes)
        private fun getOuterCornerShape(shape: List<Int>): Int = getIndexOfShape(shape, outerCornerShapes)

        // Returns the index of the shape matching the given one, or -1 if there is no match.
        private fun getIndexOfShape(shape: List<Int>, shapes: List<List<Int>>): Int {
            return shapes.withIndex().firstOrNull { it.value == shape }?.index ?: -1
        }
    }

    fun processInput(input: List<String>): Map {
        // Last line contains actions
        val actions = mutableListOf<Action>()
        var action = ""
        for (char in input.last()) {
            if (char == 'L' || char == 'R') {
                actions.add(Action(false, action.toInt()))
                actions.add(Action(true, if (char == 'L') -1 else 1))
                action = ""
                continue
            }

            action += char
        }

        if (action.isNotBlank()) actions.add(Action(false, action.toInt()))

        return Map(input.takeWhile { it.isNotBlank() }.map { it.toCharArray() }, actions)
    }

    fun solve(map: Map, asCube: Boolean = false): Int {
        map.reset()
        if (asCube) map.enableCube()
        map.follow()
        return ((map.position.y + 1) * 1000) + ((map.position.x + 1) * 4) + map.facing
    }

    fun part1(map: Map): Int {
        return solve(map)
    }

    fun part2(map: Map): Int {
        return solve(map, true)
    }

    val testInput = processInput(readInput("Day22_test"))
    check(part1(testInput) == 6032)
    check(part2(testInput) == 5031)

    val input = processInput(readInput("Day22"))
    println(part1(input))
    println(part2(input))
}
