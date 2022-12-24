fun main() {
    data class Vector2(var x: Int, var y: Int)

    // Action can be a movement or a rotation. For movements, the value is the number of spaces to move. For rotations, the value is -1 (left) or 1 (right).
    data class Action(val isRotation: Boolean, val value: Int)

    // Map is a container for the grid and actions. It also keeps track of simulation state.
    class Map(val grid: List<CharArray>, val actions: List<Action>) {
        var position = Vector2(0, 0)
        var facing = 0
        val directions = listOf('e', 's', 'w', 'n')

        init {
            reset()
        }

        fun reset() {
            position = Vector2(grid[0].takeWhile { it == ' ' }.count(), 0)
            facing = 0
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

                when (directions[facing]) {
                    'n' -> moveVertical(action.value, -1)
                    'e' -> moveHorizontal(action.value, 1)
                    's' -> moveVertical(action.value, 1)
                    'w' -> moveHorizontal(action.value, -1)
                }
            }
        }

        private fun moveVertical(spaces: Int, direction: Int)
        {
            for (i in 0 until spaces) {
                when (cell(Vector2(0, direction))) {
                    '.' -> position.y += direction
                    '#' -> break
                    ' ', null -> {
                        // Out of bounds.
                        var y = position.y

                        while (true) {
                            if (position.x > grid[y - direction].lastIndex) break
                            if (grid[y - direction][position.x] == ' ') break
                            y -= direction
                            if (y == 0 || y == grid.lastIndex) break
                        }

                        if (grid[y][position.x] == '#') break

                        position.y = y
                    }
                }
            }
        }

        private fun moveHorizontal(spaces: Int, direction: Int)
        {
            for (i in 0 until spaces) {
                when (cell(Vector2(direction, 0))) {
                    '.' -> position.x += direction
                    '#' -> break
                    ' ', null -> {
                        // Out of bounds.
                        var x = position.x

                        while (true) {
                            if (row()!![x - direction] == ' ') break
                            x -= direction
                            if (x == 0 || x == row()!!.lastIndex) break
                        }

                        if (grid[position.y][x] == '#') break

                        position.x = x
                    }
                }
            }
        }

        // Gets the current (or specified offset) row.
        private fun row(offset: Int = 0): CharArray? {
            return grid.getOrNull(position.y + offset)
        }

        // Gets the current (or specified offset) cell.
        private fun cell(offset: Vector2 = Vector2(0, 0)): Char? {
            return row(offset.y)?.getOrNull(position.x + offset.x)
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

    fun part1(map: Map): Int {
        map.follow()
        map.print()
        val result = ((map.position.y + 1) * 1000) + ((map.position.x + 1) * 4) + map.facing
        return result
    }

    fun part2(map: Map): Int {
        return 0
    }

    val testInput = processInput(readInput("Day22_test"))
    check(part1(testInput) == 6032)
    check(part2(testInput) == 0)

    val input = processInput(readInput("Day22"))
    println(part1(input))
    println(part2(input))
}