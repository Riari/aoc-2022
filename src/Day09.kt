import kotlin.math.abs

fun main() {
    data class Point(var x: Int, var y: Int)

    fun solve(input: List<String>, knots: Int): Int {
        // Starting position is the "centre", with plenty of padding to avoid going into negative coordinates.
        // Positive X goes right, positive Y goes up.
        val rope = mutableListOf<Point>()
        repeat(knots) {
            rope.add(Point(1000, 1000))
        }

        val head = rope[0]
        val tail = rope[rope.size - 1]
        val visited = mutableSetOf<String>()
        visited.add(tail.toString())

        for (move in input) {
            val parts = move.split(" ")
            val direction = parts[0]
            val spaces = parts[1].toInt()

            repeat (spaces) {
                when (direction) {
                    "U" -> head.y++
                    "R" -> head.x++
                    "D" -> head.y--
                    "L" -> head.x--
                }

                for (i in 1 until rope.size) {
                    val lead = rope[i - 1]
                    val knot = rope[i]

                    if (lead.x != knot.x && lead.y != knot.y && (abs(lead.x - knot.x) + abs(lead.y - knot.y)) > 2) {
                        if (lead.x > knot.x) {
                            knot.x++
                        } else if (lead.x < knot.x) {
                            knot.x--
                        }

                        if (lead.y > knot.y) {
                            knot.y++
                        } else if (lead.y < knot.y) {
                            knot.y--
                        }

                        if (i == rope.size - 1) visited.add(knot.toString())
                    }

                    if (lead.x - knot.x > 1) {
                        repeat((lead.x - knot.x) - 1) {
                            knot.x++
                            if (i == rope.size - 1) visited.add(knot.toString())
                        }
                    } else if (knot.x - lead.x > 1) {
                        repeat((knot.x - lead.x) - 1) {
                            knot.x--
                            if (i == rope.size - 1) visited.add(knot.toString())
                        }
                    } else if (lead.y - knot.y > 1) {
                        repeat((lead.y - knot.y) - 1) {
                            knot.y++
                            if (i == rope.size - 1) visited.add(knot.toString())
                        }
                    } else if (knot.y - lead.y > 1) {
                        repeat((knot.y - lead.y) - 1) {
                            knot.y--
                            if (i == rope.size - 1) visited.add(knot.toString())
                        }
                    }
                }
            }
        }

        return visited.size
    }

    fun part1(input: List<String>): Int {
        return solve(input, 2)
    }

    fun part2(input: List<String>): Int {
        return solve(input, 10)
    }

    val testInput = readInput("Day09_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 1)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}
