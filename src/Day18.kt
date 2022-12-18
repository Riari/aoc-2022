import kotlin.math.abs

fun main() {
    data class MinMax(var min: Int = Int.MAX_VALUE, var max: Int = Int.MIN_VALUE): Iterable<Int> {
        fun update(value: Int) {
            min = minOf(min, value)
            max = maxOf(max, value)
        }

        override fun iterator(): Iterator<Int> {
            return (min..max).iterator()
        }
    }

    data class Vector(val x: Int, val y: Int, val z: Int) {
        fun isAdjacentTo(other: Vector): Boolean {
            val onX = y == other.y && z == other.z && abs(x - other.x) == 1
            val onY = x == other.x && z == other.z && abs(y - other.y) == 1
            val onZ = x == other.x && y == other.y && abs(z - other.z) == 1
            return onX || onY || onZ
        }
    }

    fun List<Vector>.has(x: Int, y: Int, z: Int): Boolean {
        return this.any { it.x == x && it.y == y && it.z == z }
    }

    fun processInput(input: List<String>): List<Vector> {
        return input.map { it.split(',').map { c -> c.toInt() } }
            .map { Vector(it[0], it[1], it[2]) }
    }

    fun solve(grid: List<Vector>, withoutInterior: Boolean = false): Int {
        var totalFaces = grid.size * 6
        val xRange = MinMax(); val yRange = MinMax(); val zRange = MinMax()

        for (position in grid) {
            for (other in grid) {
                if (position == other) continue
                if (position.isAdjacentTo(other)) totalFaces--
            }

            if (withoutInterior) {
                xRange.update(position.x)
                yRange.update(position.y)
                zRange.update(position.z)
            }
        }

        if (!withoutInterior) return totalFaces

        val airPocketPositions = mutableListOf<Vector>()
        for (x in xRange) {
            for (y in yRange) {
                for (z in zRange) {
                    if (grid.has(x, y, z)) continue

                    val blocked = listOf(
                        (x .. xRange.max).any { grid.has(it, y, z) },     // Right
                        (x downTo xRange.min).any { grid.has(it, y, z) }, // Left
                        (y .. yRange.max).any { grid.has(x, it, z) },     // Forward
                        (y downTo yRange.min).any { grid.has(x, it, z) }, // Backward
                        (z .. zRange.max).any { grid.has(x, y, it) },     // Above
                        (z downTo zRange.min).any { grid.has(x, y, it) }  // Below
                    )

                    if (blocked.all { it }) airPocketPositions.add(Vector(x, y, z))
                }
            }
        }

        for (position in airPocketPositions) {
            for (block in grid) {
                if (position.isAdjacentTo(block)) totalFaces--
            }
        }

        return totalFaces
    }

    fun part1(grid: List<Vector>): Int {
        return solve(grid)
    }

    fun part2(grid: List<Vector>): Int {
        return solve(grid, true)
    }

    val testInput = processInput(readInput("Day18_test"))
    check(part1(testInput) == 64)
    check(part2(testInput) == 58)

    val input = processInput(readInput("Day18"))
    println(part1(input))
    println(part2(input))
}
