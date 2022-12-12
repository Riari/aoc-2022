fun main() {
    data class Cell(var value: Char, val edges: MutableList<Int> = mutableListOf())
    data class CellToVisit(val index: Int, val distance: Int)
    data class Heightmap(var startIndex: Int, var endIndex: Int, val cells: MutableList<Cell> = mutableListOf())

    fun buildHeightmap(input: List<String>): Heightmap {
        val map = Heightmap(0, 0)
        val width = input[0].length
        val height = input.size

        var i = 0
        for (y in input.indices) {
            for (x in 0 until width) {
                var value = input[y][x]

                when (value) {
                    'S' -> {
                        map.startIndex = i
                        value = 'a'
                    }
                    'E' -> {
                        map.endIndex = i
                        value = 'z'
                    }
                }

                val edges = mutableListOf<Int>()
                if (x > 0) edges.add(i - 1)
                if (x < width - 1) edges.add(i + 1)
                if (y > 0) edges.add(i - width)
                if (y < height - 1) edges.add(i + width)
                map.cells.add(Cell(value, edges))

                i++
            }
        }

        return map
    }

    fun solve(map: Heightmap, startAt: Int): Int {
        val visited = BooleanArray(map.cells.size) { false }
        val queue: MutableList<CellToVisit> = mutableListOf(CellToVisit(startAt, 0))

        while (queue.isNotEmpty()) {
            val cell = queue.removeAt(0)

            if (cell.index == map.endIndex) return cell.distance
            if (visited[cell.index]) continue

            map.cells[cell.index].edges.filter { map.cells[it].value <= map.cells[cell.index].value + 1 }.forEach {
                queue.add(CellToVisit(it, cell.distance + 1))
            }

            visited[cell.index] = true
        }

        return Int.MAX_VALUE
    }

    fun part1(map: Heightmap): Int {
        return solve(map, map.startIndex)
    }

    fun part2(map: Heightmap): Int {
        var fewestSteps = Int.MAX_VALUE
        for ((i, cell) in map.cells.withIndex()) {
            if (cell.value != 'a') continue
            val steps = solve(map, i)
            if (steps < fewestSteps) fewestSteps = steps
        }

        return fewestSteps
    }

    val testInput = buildHeightmap(readInput("Day12_test"))
    check(part1(testInput) == 31)
    check(part2(testInput) == 29)

    val input = buildHeightmap(readInput("Day12"))
    println(part1(input))
    println(part2(input))
}
