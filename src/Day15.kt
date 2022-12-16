import kotlin.math.abs
import kotlin.math.absoluteValue

// Lots of inspiration for both parts taken from here: https://nickymeuleman.netlify.app/garden/aoc2022-day15
fun main() {
    data class Position(val x: Long, val y: Long) {
        fun manhattanDistance(to: Position): Long {
            return abs(x - to.x) + abs(y - to.y)
        }
    }

    data class Sensor(val position: Position, val beacon: Position) {
        fun findCoverageAlongY(y: Long): LongRange? {
            val radius = position.manhattanDistance(beacon)
            val offset = radius - (position.y - y).absoluteValue

            if (offset < 0) return null
            return (position.x - offset)..(position.x + offset)
        }
    }

    fun processInput(input: List<String>): List<Sensor> {
        val sensors = mutableListOf<Sensor>()
        val indices = listOf(2, 3, 8, 9)
        val regex = Regex("-?\\d+")
        for (line in input) {
            val parts = line.split(' ').withIndex()
                .filter { indices.contains(it.index) }
                .map { regex.find(it.value)!!.value.toLong() }

            sensors.add(Sensor(
                Position(parts[0], parts[1]),
                Position(parts[2], parts[3])
            ))
        }

        return sensors
    }

    fun findCoveredRangesAlongY(sensors: List<Sensor>, y: Long): List<LongRange> {
        val ranges: List<LongRange> = sensors
            .mapNotNull { it.findCoverageAlongY(y) }
            .sortedBy { it.first }

        val mergedRanges = mutableListOf(ranges[0])
        for (i in 1 until ranges.size) {
            val range = ranges[i]
            val lastMerged = mergedRanges.last()

            // Attempt to merge this range with the previous if they overlap, otherwise just add the range to the list
            if (!(range.first > lastMerged.last || range.last < lastMerged.first)) {
                if (range.last > lastMerged.last) {
                    mergedRanges[mergedRanges.lastIndex] = lastMerged.first..range.last
                }
            } else {
                mergedRanges.add(range)
            }
        }

        return mergedRanges
    }

    fun part1(sensors: List<Sensor>, atY: Long): Long {
        val covered = findCoveredRangesAlongY(sensors, atY).sumOf { it.count() }.toLong()
        val beacons = sensors.filter { it.beacon.y == atY }.map { it.beacon.x }.distinct().size.toLong()

        return covered - beacons
    }

    fun part2(sensors: List<Sensor>, max: Long): Long {
        val result = (0..max)
            .map { Pair(it, findCoveredRangesAlongY(sensors, it)) }
            .find { it.second.size > 1 }!!
        val y = result.first
        val x = result.second.first().last + 1

        return x * 4_000_000 + y
    }

    val testInput = processInput(readInput("Day15_test"))
    check(part1(testInput.toList(), 10L) == 26L)
    check(part2(testInput, 20L) == 56000011L)

    val input = processInput(readInput("Day15"))
    println(part1(input, 2000000L))
    println(part2(input, 4_000_000L))
}
