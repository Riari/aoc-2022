private abstract class Packet {
    fun compareTo(other: Packet): Int {
        if (this is IntPacket && other is IntPacket) return this.compareTo(other)
        if (this is IntPacket && other is ListPacket) return this.compareTo(other)
        if (this is ListPacket && other is ListPacket) return this.compareTo(other)
        if (this is ListPacket && other is IntPacket) return this.compareTo(other)
        return 0
    }
}

private class IntPacket(val value: Int) : Packet() {
    fun toListPacket(): ListPacket {
        return ListPacket(mutableListOf(this))
    }

    fun compareTo(other: ListPacket): Int {
        return toListPacket().compareTo(other)
    }

    fun compareTo(other: IntPacket): Int {
        return other.value.compareTo(this.value)
    }

    override fun toString(): String {
        return "$value"
    }
}

private class ListPacket(val value: MutableList<Packet> = mutableListOf()) : Packet() {
    fun compareTo(other: ListPacket): Int {
        for (i in 0 until maxOf(value.size, other.value.size)) {
            val left = value.getOrNull(i)
            val right = other.value.getOrNull(i)
            if (left == null) return 1
            if (right == null) return -1
            val result = left.compareTo(right)
            if (result != 0) return result
        }

        return 0
    }

    fun compareTo(other: IntPacket): Int {
        return compareTo(other.toListPacket())
    }

    override fun toString(): String {
        var string = "["
        for ((index, packet) in value.withIndex()) {
            string += packet.toString()
            if (index < value.size - 1) string += ","
        }

        return "$string]"
    }
}

fun main() {
    fun String.findClosingBracket(opensAt: Int): Int {
        var endsAt: Int = opensAt
        var counter = 1
        while (counter > 0) {
            val c: Char = this[++endsAt]
            if (c == '[') counter++
            else if (c == ']') counter--
        }

        return endsAt
    }

    fun parse(string: String): Packet {
        val packet = ListPacket()
        var i = 0
        while (i < string.length) {
            when (string[i]) {
                '[' -> {
                    val endsAt = string.findClosingBracket(i)
                    val nested = string.substring(i + 1 until endsAt)
                    packet.value.add(parse(nested))
                    i = endsAt
                    continue
                }
                ',', ']' -> {}
                else -> {
                    var number: String = string[i].toString()
                    if (i + 1 < string.length && string[i + 1].isDigit()) number += string[++i]
                    packet.value.add(IntPacket(number.toInt()))
                }
            }

            i++
        }

        return packet
    }

    fun processInput(input: List<String>): List<Pair<Packet, Packet>> {
        val packets = mutableListOf<Pair<Packet, Packet>>()

        input.chunked(3).forEach {
            packets.add(Pair(
                parse(it[0].substring(1 until it[0].length)),
                parse(it[1].substring(1 until it[1].length))
            ))
        }

        return packets
    }

    fun part1(packets: List<Pair<Packet, Packet>>): Int {
        return packets.withIndex().filter { it.value.first.compareTo(it.value.second) == 1 }.sumOf { it.index + 1 }
    }

    fun part2(packets: List<Pair<Packet, Packet>>): Int {
        val flattened: MutableList<Packet> = packets.flatten()
        val dividers = listOf(parse("[[2]]"), parse("[[6]]"))
        dividers.forEach { flattened.add(it) }
        val sorted = flattened.sortedWith { left, right -> right.compareTo(left) }

        return sorted.withIndex().filter { dividers.contains(it.value) }.map { it.index + 1 }.reduce { acc, i -> acc * i }
    }

    val testInput = processInput(readInput("Day13_test"))
    check(part1(testInput) == 13)
    check(part2(testInput) == 140)

    val input = processInput(readInput("Day13"))
    println(part1(input))
    println(part2(input))
}
