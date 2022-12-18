import kotlin.math.abs

fun main() {
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
        var minX = 100; var maxX = 0
        var minY = 100; var maxY = 0
        var minZ = 100; var maxZ = 0

        for (position in grid) {
            for (other in grid) {
                if (position == other) continue
                if (position.isAdjacentTo(other)) totalFaces--
            }

            if (withoutInterior) {
                if (position.x < minX) minX = position.x
                else if (position.x > maxX) maxX = position.x
                if (position.y < minY) minY = position.y
                else if (position.y > maxY) maxY = position.y
                if (position.z < minZ) minZ = position.z
                else if (position.z > maxZ) maxZ = position.z
            }
        }

        if (!withoutInterior) return totalFaces

        val airPocketPositions = mutableListOf<Vector>()
        for (x in minX..maxX) {
            for (y in minY..maxY) {
                for (z in minZ..maxZ) {
                    if (grid.has(x, y, z)) continue

                    val blocked = listOf(
                        (x downTo minX).any { grid.has(it, y, z) }, // Left
                        (x .. maxX).any { grid.has(it, y, z) },     // Right
                        (y .. maxY).any { grid.has(x, it, z) },     // Forward
                        (y downTo minY).any { grid.has(x, it, z) }, // Backward
                        (z .. maxZ).any { grid.has(x, y, it) },     // Above
                        (z downTo minZ).any { grid.has(x, y, it) }  // Below
                    )

                    if (blocked.all { it }) {
                        airPocketPositions.add(Vector(x, y, z))
                    }
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
