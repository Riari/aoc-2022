fun main() {
    class Node(var name: String, var isFile: Boolean = false, var size: Int = 0) {
        var parent: Node? = null
        var children = mutableListOf<Node>()

        fun add(node: Node) {
            if (isFile) return
            children.add(node)
            node.parent = this
        }

        fun find(childName: String): Node? {
            children.forEach {
                if (it.name == childName) return it
            }

            return null
        }

        override fun toString(): String {
            var s = "$name ("
            s += if (isFile) "file" else "dir"
            s += ", size=$size)\n"

            if (children.isNotEmpty()) {
                children.forEach {
                    s += " - $it"
                }
            }

            return s
        }
    }

    fun parseToTree(input: List<String>): Node {
        var node = Node("/")
        for (line in input) {
            val parts = line.split(" ")

            if (parts[0] == "$") {
                if (parts[1] == "cd") {
                    val path = parts[2]

                    if (path == "..") {
                        node = node.parent!!
                    } else if (node.name != path) {
                        node = node.find(path)!!
                    }
                }
            } else {
                if (parts[0] == "dir") {
                    // Encountered directory
                    node.add(Node(parts[1]))
                } else {
                    // Encountered file
                    val size = parts[0].toInt()

                    var parent = node.parent
                    while (parent != null) {
                        parent.size += size
                        parent = parent.parent
                    }

                    node.size += size
                    node.add(Node(parts[1], true, size))
                }
            }
        }

        // Traverse back up to the root node
        while (node.parent != null) {
            node = node.parent!!
        }

        return node
    }

    fun sumDirectories(node: Node, maxSize: Int): Int {
        var size = 0
        for (child in node.children) {
            if (child.isFile) continue

            if (child.size <= maxSize) {
                size += child.size
            }

            size += sumDirectories(child, maxSize)
        }

        return size
    }

    fun findSmallestDirSizeAbove(node: Node, size: Int): Int {
        var smallest = node.size
        for (child in node.children) {
            if (child.isFile) continue

            val innerSmallest = findSmallestDirSizeAbove(child, size)

            if (innerSmallest in size until smallest) {
                smallest = innerSmallest
            }
        }

        return smallest
    }

    fun part1(node: Node): Int {
        return sumDirectories(node, 100000)
    }

    fun part2(node: Node): Int {
        val totalCapacity = 70000000
        val required = 30000000
        val available = totalCapacity - node.size
        val toFree = required - available

        return findSmallestDirSizeAbove(node, toFree)
    }

    val testInput = parseToTree(readInput("Day07_test"))
    check(part1(testInput) == 95437)
    check(part2(testInput) == 24933642)

    val input = parseToTree(readInput("Day07"))
    println(part1(input))
    println(part2(input))
}
