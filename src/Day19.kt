import kotlinx.coroutines.*

fun main() {
    data class Blueprint(
        val id: Int,
        val oreRobotCost: Int,
        val clayRobotCost: Int,
        val obsidianRobotCost: Pair<Int, Int>,
        val geodeRobotCost: Pair<Int, Int>
    )

    data class Resources(
        var ore: Int = 0,
        var clay: Int = 0,
        var obsidian: Int = 0,
        var geode: Int = 0
    ) {
        fun clone(): Resources {
            return Resources(ore, clay, obsidian, geode)
        }
    }

    data class State(
        var buildNext: Int = 0,
        var robots: Resources = Resources(1, 0, 0, 0),
        var materials: Resources = Resources(0, 0, 0, 0)
    ) {
        fun branch(newBuildNext: Int): State {
            return State(newBuildNext, robots.clone(), materials.clone())
        }
    }

    fun processInput(input: List<String>): List<Blueprint> {
        val blueprints = mutableListOf<Blueprint>()
        val regex = Regex("\\d+")
        for (line in input) {
            val num = regex.findAll(line).map { it.value.toInt() }.toList()
            blueprints.add(Blueprint(num[0], num[1], num[2], Pair(num[3], num[4]), Pair(num[5], num[6])))
        }

        return blueprints;
    }

    fun solve(blueprint: Blueprint, state: State, minutes: Int): Int {
        var timer = minutes
        val buildsInProgress = Resources()
        var maxQualitySeen = 0
        var i = 0
        while (timer >= 0) {
            // Check quality so far
            val quality = blueprint.id * state.materials.geode
            if (quality > maxQualitySeen) maxQualitySeen = quality

            // Finish robots being built
            repeat (buildsInProgress.ore) { state.robots.ore++ }; buildsInProgress.ore = 0
            repeat (buildsInProgress.clay) { state.robots.clay++ }; buildsInProgress.clay = 0
            repeat (buildsInProgress.obsidian) { state.robots.obsidian++ }; buildsInProgress.obsidian = 0
            repeat (buildsInProgress.geode) { state.robots.geode++ }; buildsInProgress.geode = 0

            // Process build queue
            when (state.buildNext % 4) {
                0 -> { // Ore robot
                    if (state.materials.ore >= blueprint.oreRobotCost) {
                        state.materials.ore -= blueprint.oreRobotCost
                        buildsInProgress.ore++
                        state.buildNext++
                    }
                }
                1 -> { // Clay robot
                    if (state.materials.ore >= blueprint.clayRobotCost) {
                        state.materials.ore -= blueprint.clayRobotCost
                        buildsInProgress.clay++
                        state.buildNext++
                    }
                }
                2 -> { // Obsidian robot
                    if (state.materials.ore >= blueprint.obsidianRobotCost.first && state.materials.clay >= blueprint.obsidianRobotCost.second) {
                        state.materials.ore -= blueprint.obsidianRobotCost.first
                        state.materials.clay -= blueprint.obsidianRobotCost.second
                        buildsInProgress.obsidian++
                        state.buildNext++
                    }
                }
                3 -> { // Geode robot
                    if (state.materials.ore >= blueprint.geodeRobotCost.first && state.materials.obsidian >= blueprint.geodeRobotCost.second) {
                        state.materials.ore -= blueprint.geodeRobotCost.first
                        state.materials.obsidian -= blueprint.geodeRobotCost.second
                        buildsInProgress.geode++
                        state.buildNext++
                    }
                }
            }

            // Gathering
            repeat (state.robots.ore) { state.materials.ore++ }
            repeat (state.robots.clay) { state.materials.clay++ }
            repeat (state.robots.obsidian) { state.materials.obsidian++ }
            repeat (state.robots.geode) { state.materials.geode++ }

            timer--
            i++
        }

        return maxQualitySeen
    }

    fun part1(blueprints: List<Blueprint>): Int = runBlocking {
        val coroutines = mutableListOf<Deferred<Int>>()
        for (blueprint in blueprints) {
            coroutines.add(async { solve(blueprint, State(), 24) })
        }

        val results = mutableListOf<Int>()
        for (coroutine in coroutines) {
            results.add(coroutine.await())
        }

        return@runBlocking results.sum()
    }

    fun part2(blueprints: List<Blueprint>): Int {
        return 0
    }

    val testInput = processInput(readInput("Day19_test"))
    check(part1(testInput) == 33)
    check(part2(testInput) == 0)

    val input = processInput(readInput("Day19"))
    println(part1(input))
    println(part2(input))
}
