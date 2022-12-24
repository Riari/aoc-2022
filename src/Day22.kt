import kotlin.math.sqrt

fun main() {
    data class Vector2(var x: Int, var y: Int) {
        operator fun plus(other: Vector2): Vector2 {
            return Vector2(x + other.x, y + other.y)
        }

        operator fun minus(other: Vector2): Vector2 {
            return Vector2(x - other.x, y - other.y)
        }
    }

    // Action can be a movement or a rotation. For movements, the value is the number of spaces to move. For rotations, the value is -1 (left) or 1 (right).
    data class Action(val isRotation: Boolean, val value: Int)

    // Map is a container for the grid and actions. It also keeps track of simulation state.
    class Map(val grid: List<CharArray>, val actions: List<Action>) {
        var position = Vector2(0, 0)
        var facing = 0
        val facings = listOf('e', 's', 'w', 'n')
        val directions = mapOf(
            'e' to Vector2(1, 0),
            's' to Vector2(0, 1),
            'w' to Vector2(-1, 0),
            'n' to Vector2(0, -1),
        )

        // Cube mapping state
        var asCube = false
        var area = 0
        var sideLength = 0
        var width = 0; var height = 0
        var internalCorners = mutableListOf<Vector2>()

        init {
            reset()
        }

        fun reset() {
            position = Vector2(grid[0].takeWhile { it == ' ' }.count(), 0)
            facing = 0
        }

        fun enableCube() {
            asCube = true
            area = grid.sumOf { it.toString().trim().length }
            sideLength = sqrt((area / 6).toDouble()).toInt()
            width = grid.maxOf { it.size }
            height = grid.size
        }

        fun print() {
            for (line in grid) {
                println(line)
            }
        }

        // Steps through the list of actions and executes them.
        fun follow() {
            for (action in actions) {
                if (action.isRotation) {
                    facing = (facing + action.value).mod(4)
                    continue
                }

                move(directions[facings[facing]]!!, action.value)
            }
        }

        private fun move(direction: Vector2, distance: Int)
        {
            for (i in 0 until distance) {
                when (cell(position + direction)) {
                    '.' -> position += direction
                    '#' -> break
                    ' ', null -> {
                        // Out of bounds.
                        if (!asCube && !tryWrap(direction)) break
                        if (asCube && !tryWrapAsCube(direction)) break
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

        private fun tryWrapAsCube(direction: Vector2): Boolean {
            return false
        }

        private fun row(row: Int = position.y): CharArray? {
            return grid.getOrNull(row)
        }

        private fun cell(position: Vector2): Char? {
            return cell(position.x, position.y)
        }

        private fun cell(x: Int = position.x, y: Int = position.y): Char? {
            return row(y)?.getOrNull(x)
        }

        private fun isCellVoid(x: Int, y: Int): Int {
            if (cell(x, y) == ' ') return 1
            return 0
        }
    }

    fun processInput(input: List<String>): Map {
        // Last line contains actions
        val actions = mutableListOf<Action>()
        var action = ""
        for (char in input.last()) {
            if (char == 'L' || char == 'R') {
                actions.add(Action(false, action.toInt()))
                if (char == 'L') actions.add(Action(true, -1))
                else actions.add(Action(true, 1))
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
//    check(part2(testInput) == 5031)

    val input = processInput(readInput("Day22"))
    println(part1(input))
//    println(part2(input))
}
