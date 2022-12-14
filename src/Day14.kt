fun main() {
    data class Tile(val x: Int, val y: Int)
    data class Slice(val tiles: Array<CharArray>, var deepestRock: Int = 0) {
        fun copyOf(): Slice {
            return Slice(tiles.map { it.copyOf() }.toTypedArray(), deepestRock)
        }
    }

    fun processInput(input: List<String>): Slice {
        val slice = Slice(Array(1000) { CharArray(1000) { '.' } })
        for (line in input) {
            val path = line.split(" -> ")
                .map { it.split(',') }
                .map { Tile(it[0].toInt(), it[1].toInt()) }
                .windowed(2, 1)

            for ((a, b) in path) {
                for (y in minOf(a.y, b.y)..maxOf(a.y, b.y)) {
                    for (x in minOf(a.x, b.x)..maxOf(a.x, b.x)) {
                        slice.tiles[y][x] = '#'
                    }
                }

                if (a.y > slice.deepestRock) slice.deepestRock = a.y
                else if (b.y > slice.deepestRock) slice.deepestRock = b.y
            }
        }

        return slice
    }

    // Simulates sand falling until it falls into the abyss (part 1) or blocks the source (part 2)
    fun simulate(slice: Slice): Int {
        var settled = 0
        outer@ while (true) {
            var x = 500; var y = 0
            if (slice.tiles[y][x] == 'o') break

            var hasSettled = false
            while (!hasSettled) {
                if (y > slice.deepestRock) break@outer

                if (slice.tiles.getOrNull(y + 1)?.get(x) == '.') {
                    y++
                } else if (slice.tiles.getOrNull(y + 1)?.getOrNull(x - 1) == '.') {
                    x--; y++
                } else if (slice.tiles.getOrNull(y + 1)?.getOrNull(x + 1) == '.') {
                    x++; y++
                } else {
                    hasSettled = true; settled++
                    slice.tiles[y][x] = 'o'
                }
            }
        }

        return settled
    }

    fun part1(slice: Slice): Int {
        return simulate(slice)
    }

    fun part2(slice: Slice): Int {
        // Add floor
        slice.deepestRock += 2
        for (x in 0 until slice.tiles[slice.deepestRock].size) {
            slice.tiles[slice.deepestRock][x] = '#'
        }

        return simulate(slice)
    }

    val testInput = processInput(readInput("Day14_test"))
    check(part1(testInput.copyOf()) == 24)
    check(part2(testInput) == 93)

    val input = processInput(readInput("Day14"))
    println(part1(input.copyOf()))
    println(part2(input))
}
