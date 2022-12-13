import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("input", "$name.txt")
    .readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * Flattens a list of pairs.
 */
fun <T> List<Pair<T, T>>.flatten(): MutableList<T> {
    val accumulator = mutableListOf<T>()
    this.forEach {
        accumulator.add(it.first)
        accumulator.add(it.second)
    }
    return accumulator
}