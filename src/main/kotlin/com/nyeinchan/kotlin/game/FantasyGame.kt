package org.example.com.nyeinchan.kotlin.game

import kotlin.math.max
import kotlin.math.min

enum class CharacterLevel(val points: Int)
{
    LEVEL_1(10),
    LEVEL_2(20),
    LEVEL_3(30),
    LEVEL_4(40),
    LEVEL_5(50),
    LEVEL_6(60),
    LEVEL_7(70),
    LEVEL_8(80),
    LEVEL_9(90),
    LEVEL_10(100),
}

// interfaces
interface Recoverable
{
    fun beforeRounds()
    fun afterRound()
}

interface Damageable
{
    var health: Int
    fun takeDamage(damage: Int)
}

interface Combatant: Damageable
{
    val name: String
    val attackPower: Int
    fun attack(target: Damageable)
}

interface Defender
{
    var stamina: Int
    val defensePower: Int
    fun defend(attackPower: Int): Int
}

interface Healer
{
    val mana: Int
    val healingPower: Int
    fun heal()
}


// abstract
abstract class Character (
    override val name: String,
    override var health: Int,
    override var attackPower: Int
) : Combatant, Recoverable
{
    abstract val level: CharacterLevel

    override fun takeDamage(amount: Int)
    {
        val oldHealth = health
        health = max(0, health - amount)
        val damageTaken = oldHealth - health

        println("[$name]: 💢 Takes $damageTaken damage! ❤️ HP: $health")

        if (health == 0) {
            println("[$name]: 💀 Has been defeated! ⚰️")
        }
    }

    override fun attack(target: Damageable)
    {
        if (health > 0) {
            println("[$name]: ⚔️ Attacks with $attackPower power!")
            target.takeDamage(attackPower)
        } else {
            println("[$name]: ☠️ Is dead and cannot attack!")
        }
    }

    override fun beforeRounds() {
        TODO("Not yet implemented")
    }

    override fun afterRound() {
        TODO("Not yet implemented")
    }
}


// concrete
class Warrior(
    override val level: CharacterLevel,
    name: String,
    health: Int = 100,
    attackPower: Int = 20,
    override var stamina: Int = 100,
    override val defensePower: Int = 10
): Character(name, health, attackPower), Defender
{
    init {
        require(health + attackPower + stamina <= level.points) {
            "Warrior $name exceeds allowed level points: ${level.points}"
        }
    }

    override fun attack(target: Damageable) {
        if (stamina <= 0) {
            println("[$name]: 💨 Too tired to attack! 💤")
        } else {
            stamina -= 1
            println("[$name]: ⚔️🛡️ Swings a mighty sword! (-$attackPower HP)")
            target.takeDamage(attackPower)
        }
    }

    override fun defend(attackPower: Int): Int {
        if (stamina <= 0) {
            println("[$name]: 💨 Too exhausted to defend!")
            return attackPower
        }
        val reducedAttack = max(0, attackPower - defensePower)
        stamina -= 1
        println("[$name]: 🛡️ Blocks some damage! (Def:$defensePower) Incoming:$attackPower -> $reducedAttack")
        return reducedAttack
    }

    override fun takeDamage(amount: Int) {
        super.takeDamage(defend(amount))
    }

    override fun beforeRounds() {
        stamina += 1
        println("[$name]: (+1 Stamina before match) Stamina now: $stamina")
    }

    override fun afterRound() {
    }
}

class Sorcerer(
    override val level: CharacterLevel,
    name: String,
    health: Int = 100,
    attackPower: Int = 25,
    override var mana: Int = 100,
    override val healingPower: Int = 15
) : Character(name, health, attackPower), Healer
{
    init {
        require(health + attackPower + mana <= level.points) {
            "Sorcerer $name exceeds allowed level points: ${level.points}"
        }
    }

    override fun attack(target: Damageable) {
        if (mana <= 0) {
            println("[$name]: 💨 Out of mana and cannot attack!")
        } else {
            heal()
            mana -= 1
            println("[$name]: 🔥🌀 Casts a fireball! (-$attackPower HP)")
            println("[$name]: Mana remaining: $mana")
            target.takeDamage(attackPower)
        }
    }

    override fun heal() {
        if (mana <= 0) {
            println("[$name]: 💨 Out of mana and cannot heal!")
        } else if (health >= 100) {
            println("[$name]: 💚 Already at full health!")
        } else {
            mana -= 1
            val oldHealth = health
            health = min(100, health + healingPower)
            val healedAmount = health - oldHealth
            println("[$name]: 💖✨ Heals self (+$healedAmount HP). HP now: $health")
        }
    }

    override fun beforeRounds() {
        mana += 1
        println("[$name]: (+1 Mana before match) Mana now: $mana")
    }

    override fun afterRound() {
    }
}

class Match(
    val rounds: Int,
    val challenger: Character,
    val opponent: Character,
)
{
    fun fight(): Character?
    {
        repeat(rounds) { roundIndex ->
            if (roundIndex > 0) {
                challenger.beforeRounds()
                opponent.beforeRounds()
            }

            println("\n===== ROUND ${roundIndex + 1} =====")

            // Challenger attacks
            challenger.attack(opponent)
            if (opponent.health <= 0) {
                challenger.afterRound()
                opponent.afterRound()
                return challenger
            }

            // Opponent attacks
            opponent.attack(challenger)
            if (challenger.health <= 0) {
                challenger.afterRound()
                opponent.afterRound()
                return opponent
            }

            // End of round
            challenger.afterRound()
            opponent.afterRound()
        }

        println("No clear winner after $rounds rounds!")
        return null
    }
}

fun main() {
    val warrior = Warrior(
        level = CharacterLevel.LEVEL_2,
        name = "Leon",
        health = 5,
        attackPower = 3,
        stamina = 2
    )

    val sorcerer = Sorcerer(
        level = CharacterLevel.LEVEL_2,
        name = "Merlin",
        health = 4,
        attackPower = 4,
        mana = 2
    )

    val match = Match(rounds = 5, challenger = warrior, opponent = sorcerer)
    val winner = match.fight()

    println("Winner: ${winner?.name ?: "No winner"}")
}