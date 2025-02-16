package org.example.com.nyeinchan.kotlin.lesson06

val jediNames = listOf("Luke Skywalker", "Yoda", "Obi-Wan Kenobi", "Mace Windu", "Qui-Gon Jinn")
val sithNames = listOf("Darth Vader", "Emperor Palpatine", "Darth Maul", "Kylo Ren", "Count Dooku")
val rebelNames = listOf("Leia Organa", "Han Solo", "Chewbacca", "C3PO", "R2D2")
val imperialNames = listOf("Stormtrooper", "Imperial Officer", "Imperial Guard", "Death Trooper", "TIE Fighter Pilot")

enum class Fraction {
    JEDI, SITH, REBEL, IMPERIAL
}

data class StarWarsCharacter (
    val name: String,
    val fraction: Fraction
)

fun String.toStarWarsCharacter(fraction: Fraction): StarWarsCharacter {
    return StarWarsCharacter(this, fraction)
}

fun createPair(lightSide: List<StarWarsCharacter>, darkSide: List<StarWarsCharacter>)
    :List<Pair<StarWarsCharacter, StarWarsCharacter>>
{
    return lightSide.zip(darkSide)
}

fun simulateRound(pairedCharacter: List<Pair<StarWarsCharacter, StarWarsCharacter>>)
    :List<Pair<StarWarsCharacter, Int>>
{
    return pairedCharacter.flatMap { (light, dark) ->
        val characterSet = setOfNotNull(light, dark)
        val winner = characterSet.random()
        characterSet.map {
            it to if (it == winner) 1 else 0
        }
    }
}

fun List<Pair<StarWarsCharacter, StarWarsCharacter>>.matchWithFlatResults(
    rounds: Int = 3
)
    :List<Pair<StarWarsCharacter, Int>>
{
    return this.flatMap { battlePair ->
        (1..rounds).flatMap {
            simulateRound(listOf(battlePair))
        }
    }
}

fun printList(list: List<Pair<StarWarsCharacter, StarWarsCharacter>>) {
    list.forEach { (light, dark) ->
        println("${light.name} vs ${dark.name}")
    }
    println()
}

fun printResults(roundResults: List<Pair<StarWarsCharacter, Int>>) {
    println("-- Character Results --")
    roundResults.forEach { (character, result) ->
        println("${character.name} - $result")
    }
    println()
}

fun printFractionResults(roundResults: List<Pair<Fraction, Int>>) {
    println("-- Fraction Results --")
    roundResults.forEach { (fraction, result) ->
        println("$fraction - $result")
    }
    println()
}


fun main()
{
    val jediCharacters = jediNames.map {it.toStarWarsCharacter(Fraction.JEDI)}
    val sithCharacters = sithNames.map {it.toStarWarsCharacter(Fraction.SITH)}
    val rebelCharacters = rebelNames.map {it.toStarWarsCharacter(Fraction.REBEL)}
    val imperialCharacters = imperialNames.map {it.toStarWarsCharacter(Fraction.IMPERIAL)}

    val characters = jediCharacters + sithCharacters + rebelCharacters + imperialCharacters

    // filtering characters
    val lightSide = characters.filter { it -> it.fraction == Fraction.JEDI || it.fraction == Fraction.REBEL }
    val darkSide = characters.filterNot { it -> it.fraction == Fraction.JEDI || it.fraction == Fraction.REBEL }

    val pairedSides = createPair(lightSide, darkSide)
    printList(pairedSides)

    val simulatedRounds = pairedSides.matchWithFlatResults(3)
    printResults(simulatedRounds)


    // group and sort
    // group and sort the results by character
    val groupedResults = simulatedRounds
        .groupBy { it.first }
        .mapValues { (_, results) -> results.sumOf { it.second } }
        .toList()
        .sortedByDescending { it.second }
    printResults(groupedResults)

    // group and sort teh results by fraction
    val groupedFraction = simulatedRounds
        .groupBy { it.first.fraction }
        .mapValues { (_, results) -> results.sumOf { it.second } }
        .toList()
        .sortedByDescending { it.second }
    printFractionResults(groupedFraction)


    // other transformations functions
    val (starWarCharacters, scores) = simulatedRounds.unzip()
    println("Characters: $starWarCharacters")
    println("Scores: $scores")

    val funPairs = characters.zipWithNext()
    printList(funPairs)
}
