import kotlinx.coroutines.*
import kotlin.math.ceil
import kotlin.math.min
import kotlin.system.measureTimeMillis

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
    )

    data class State(
        val timeLimit: Int,
        var timer: Int = 0,
        var robots: Resources = Resources(1, 0, 0, 0),
        var materials: Resources = Resources(0, 0, 0, 0)
    ) {
        fun tick(minutes: Int): State {
            val time = min(timeLimit - timer, minutes)

            return this.copy(
                materials = materials.copy(
                    ore = materials.ore + robots.ore * time,
                    clay = materials.clay + robots.clay * time,
                    obsidian = materials.obsidian + robots.obsidian * time,
                    geode = materials.geode + robots.geode * time
                ),
                timer = timer + time
            )
        }
    }

    // Some ideas taken from https://github.com/ckainz11/AdventOfCode2022/blob/main/src/main/kotlin/days/day19/Day19.kt
    class Simulator(
        val blueprint: Blueprint,
        var maxGeodeOutput: Int = 0
    ) {
        val maxCosts = Resources(
            maxOf(blueprint.oreRobotCost, blueprint.clayRobotCost, blueprint.obsidianRobotCost.first),
            blueprint.obsidianRobotCost.second,
            blueprint.geodeRobotCost.second
        )

        fun run(state: State): Int {
            // Return the blueprint's maximum geode output when the timer ends
            if (state.timer >= state.timeLimit) {
                maxGeodeOutput = maxOf(maxGeodeOutput, state.materials.geode)
                return state.materials.geode
            }

            // Bail if it's impossible to improve the output (using every remaining minute to build a geode robot)
            if (state.materials.geode + (0 until state.timeLimit - state.timer).sumOf { it + state.robots.geode } < maxGeodeOutput) {
                return 0
            }

            // Find the best branch
            return maxOf(
                if (state.robots.obsidian > 0)
                    run(buildGeodeRobot(state))
                else 0,
                if (state.robots.clay > 0 && state.robots.obsidian < maxCosts.obsidian)
                    run(buildObsidianRobot(state))
                else 0,
                if (state.robots.ore > 0 && state.robots.clay < maxCosts.clay)
                    run(buildClayRobot(state))
                else 0,
                if (state.robots.ore in 1 until maxCosts.ore)
                    run(buildOreRobot(state))
                else 0
            )
        }

        fun buildOreRobot(state: State): State {
            val requiredOre = maxOf(blueprint.oreRobotCost - state.materials.ore, 0).toFloat()

            return state.tick(
                if (requiredOre > 0) ceil(requiredOre / state.robots.ore).toInt() + 1
                else 1
            ).let {
                it.copy(
                    materials = it.materials.copy(ore = it.materials.ore - blueprint.oreRobotCost),
                    robots = it.robots.copy(ore = it.robots.ore + 1)
                )
            }
        }

        fun buildClayRobot(state: State): State {
            val requiredOre = maxOf(blueprint.clayRobotCost - state.materials.ore, 0).toFloat()

            return state.tick(
                if (requiredOre > 0) ceil(requiredOre / state.robots.ore).toInt() + 1
                else 1
            ).let {
                it.copy(
                    materials = it.materials.copy(ore = it.materials.ore - blueprint.clayRobotCost),
                    robots = it.robots.copy(clay = it.robots.clay + 1)
                )
            }
        }

        fun buildObsidianRobot(state: State): State {
            val requiredOre = maxOf(blueprint.obsidianRobotCost.first - state.materials.ore, 0).toFloat()
            val requiredClay = maxOf(blueprint.obsidianRobotCost.second - state.materials.clay, 0).toFloat()

            return state.tick(
                if (requiredOre > 0 || requiredClay > 0) {
                    maxOf(
                        ceil(requiredOre / state.robots.ore),
                        ceil(requiredClay / state.robots.clay)
                    ).toInt() + 1
                }
                else 1
            ).let {
                it.copy(
                    materials = it.materials.copy(
                        ore = it.materials.ore - blueprint.obsidianRobotCost.first,
                        clay = it.materials.clay - blueprint.obsidianRobotCost.second
                    ),
                    robots = it.robots.copy(obsidian = it.robots.obsidian + 1)
                )
            }
        }

        fun buildGeodeRobot(state: State): State {
            val requiredOre = maxOf(blueprint.geodeRobotCost.first - state.materials.ore, 0).toFloat()
            val requiredObsidian = maxOf(blueprint.geodeRobotCost.second - state.materials.obsidian, 0).toFloat()

            return state.tick(
                if (requiredOre > 0 || requiredObsidian > 0) {
                    maxOf(
                        ceil(requiredOre / state.robots.ore),
                        ceil(requiredObsidian / state.robots.obsidian)
                    ).toInt() + 1
                }
                else 1
            ).let {
                it.copy(
                    materials = it.materials.copy(
                        ore = it.materials.ore - blueprint.geodeRobotCost.first,
                        obsidian = it.materials.obsidian - blueprint.geodeRobotCost.second
                    ),
                    robots = it.robots.copy(geode = it.robots.geode + 1)
                )
            }
        }
    }

    fun processInput(input: List<String>): List<Blueprint> {
        val blueprints = mutableListOf<Blueprint>()
        val regex = Regex("\\d+")
        for (line in input) {
            val num = regex.findAll(line).map { it.value.toInt() }.toList()
            blueprints.add(Blueprint(num[0], num[1], num[2], Pair(num[3], num[4]), Pair(num[5], num[6])))
        }

        return blueprints
    }

    fun solve(blueprint: Blueprint, state: State): Int {
        return Simulator(blueprint).run(state)
    }

    // TODO: Ditch coroutines for both parts as they're probably not speeding things up in this scenario

    fun part1(blueprints: List<Blueprint>): Int = runBlocking {
        var result: Int
        val timeTaken = measureTimeMillis {
            result = blueprints
                .map { async { solve(it, State(24)) } }
                .withIndex().sumOf { (it.index + 1) * it.value.await() }
        }

        println("Part 1 completed in $timeTaken ms")

        return@runBlocking result
    }

    fun part2(blueprints: List<Blueprint>): Int = runBlocking {
        var result: Int
        val timeTaken = measureTimeMillis {
            result = blueprints.take(3)
                .map { async { solve(it, State(32)) } }
                .map { it.await() }
                .reduce(Int::times)
        }

        println("Part 2 completed in $timeTaken ms")

        return@runBlocking result
    }

    val testInput = processInput(readInput("Day19_test"))
    check(part1(testInput) == 33)
    check(part2(testInput) == 3472)

    val input = processInput(readInput("Day19"))
    println(part1(input))
    println(part2(input))
}
