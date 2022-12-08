fun main() {
   fun part1(input: List<String>): Int {
      var sum = 0

      for (rucksack in input) {
         val firstCompartment = mutableSetOf<Char>()
         val secondCompartment = mutableSetOf<Char>()

         for ((index, item) in rucksack.withIndex()) {
            if (index < rucksack.length / 2) {
               firstCompartment.add(item)
            } else {
               secondCompartment.add(item)
            }
         }

         // Find the intersection of the two compartment sets
         val intersection = firstCompartment.intersect(secondCompartment)

         // Iterate over each item in the intersection and add its priority to the sum
         for (item in intersection) {
            sum += if (item.isLowerCase()) item - 'a' + 1 else item - 'A' + 27
         }
      }

      return sum
   }

   fun part2(rucksacks: List<String>): Int {
      return 0
   }

   val testInput = readInput("Day03_test")
   check(part1(testInput) == 157)
   check(part2(testInput) == 0)

   val input = readInput("Day03")
   println(part1(input))
   println(part2(input))
}
