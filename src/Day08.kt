fun main() {
    fun part1(input: List<String>): Int {
        val grid = input.map { it.toList() }
        val size = grid.size

        var visibleTrees = (size * 4) - 4

        for (x in 1 until size - 1) {
            for (y in 1 until size - 1) {
                val tree = grid[y][x]

                val north = grid.subList(0, y).map { it[x] }
                val east = grid[y].subList(x + 1, size)
                val south = grid.subList(y + 1, size).map { it[x] }
                val west = grid[y].subList(0, x)

                val blockedNorth = north.any { it >= tree }
                val blockedEast = east.any { it >= tree }
                val blockedSouth = south.any { it >= tree }
                val blockedWest = west.any { it >= tree }

                if (!blockedNorth || !blockedEast || !blockedSouth || !blockedWest) visibleTrees++
            }
        }

         return visibleTrees
    }

    fun part2(input: List<String>): Int {
        val grid = input.map { it.toList() }
        val size = grid.size

        var highestScore = 0

        fun countVisible(trees: List<Char>, height: Char): Int {
            var visible = 0

            for (tree in trees) {
                visible++
                if (tree >= height) break
            }

            return visible
        }

        for (x in 1 until size - 1) {
            for (y in 1 until size - 1) {
                val tree = grid[y][x]

                val north = grid.subList(0, y).map { it[x] }.reversed()
                val east = grid[y].subList(x + 1, size)
                val south = grid.subList(y + 1, size).map { it[x] }
                val west = grid[y].subList(0, x).reversed()

                val visibleNorth = countVisible(north, tree)
                val visibleEast = countVisible(east, tree)
                val visibleSouth = countVisible(south, tree)
                val visibleWest = countVisible(west, tree)

                val score = visibleNorth * visibleEast * visibleSouth * visibleWest

                if (score > highestScore) highestScore = score
            }
        }

        return highestScore
    }

    val testInput = readInput("Day08_test")
    check(part1(testInput) == 21)
    check(part2(testInput) == 8)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}
