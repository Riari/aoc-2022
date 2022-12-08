#!/usr/bin/env python3
import os, sys

srcPath = os.path.join(os.getcwd(), 'src')
inputPath = os.path.join(os.getcwd(), 'input')

day = sys.argv[1] if len(sys.argv) > 1 else input("Enter day: ")
day = day.zfill(2)
filename = 'Day' + day
srcFile = filename + '.kt'

if os.path.exists(os.path.join(srcPath, srcFile)):
    exit(f'{filename} already exists')

if not os.path.exists(srcPath):
    os.mkdir(srcPath)

if not os.path.exists(inputPath):
    os.mkdir(inputPath)

f = open(os.path.join(srcPath, srcFile), 'w')
f.write('''fun main() {{
    fun part1(input: List<String>): Int {{
        return 0
    }}

    fun part2(input: List<String>): Int {{
        return 0
    }}

    val testInput = readInput("Day{n}_test")
    check(part1(testInput) == 0)
    check(part2(testInput) == 0)

    val input = readInput("Day{n}")
    println(part1(input))
    println(part2(input))
}}
'''.format(n=day))
f.close()

for file in [filename + '.txt', filename + '_test.txt']:
    open(os.path.join(inputPath, file), 'w').close()

exit(f'{filename} files created')