package org.example.com.nyeinchan.kotlin.game

import kotlin.math.max
import kotlin.math.min

interface Damageable {
    var health: Int
    fun takeDamage(amount: Int)
}

interface Combatant : Damageable {
    val name: String
    val attackPower: Int
    fun attack(target: Damageable)
}

interface Defender {
    var stamina: Int
    val defensePower: Int
    fun defend(attackPower: Int): Int
}

interface Healer {
    var mana: Int
    val healingPower: Int
    fun heal()
}

abstract class Character(
    override val name: String,
    override var health: Int,
    override val attackPower: Int
) : Combatant {

    override fun takeDamage(amount: Int) {
        val oldHealth = health
        health = max(0, health - amount)
        val damageTaken = oldHealth - health

        println("\n[$name]: 💢 Takes $damageTaken damage! ❤️ HP: $health")

        if (health == 0) {
            println("[$name]: 💀 Has been defeated! ⚰️")
        }
    }

    override fun attack(target: Damageable) {
        if (health > 0) {
            println("\n[$name]: ⚔️ Attacks ${target.javaClass.simpleName}! (-$attackPower HP) 💥")
            target.takeDamage(attackPower)
        } else {
            println("\n[$name]: ☠️ Is dead and cannot attack! ⚔️")
        }
    }
}

class Warrior(
    name: String,
    health: Int = 100,
    attackPower: Int = 20,
    override var stamina: Int = 100,
    override val defensePower: Int = 10
) : Character(name, health, attackPower), Defender {

    override fun attack(target: Damageable) {
        if (stamina <= 0) {
            println("\n[$name]: 💨 Too tired to attack! 💤")
        } else {
            stamina -= 1
            println("\n[$name]: ⚔️🛡️ Swings a mighty sword at the enemy! (-$attackPower HP) 💥")
            target.takeDamage(attackPower)
        }
    }

    override fun defend(attackPower: Int): Int {
        if (stamina <= 0) {
            println("\n[$name]: 💨 Too exhausted to defend! 😵‍💫")
            return attackPower
        }

        val reducedAttackPower = max(0, attackPower - defensePower)
        stamina -= 1

        println("\n[$name]: 🛡️ Raises a shield and blocks attack of $attackPower damage! 💢")
        println("💥 Damage taken: $reducedAttackPower. ⚡ Stamina remaining: $stamina")

        return reducedAttackPower
    }

    override fun takeDamage(amount: Int) {
        super.takeDamage(defend(amount))
    }
}

class Sorcerer(
    name: String,
    health: Int = 100,
    attackPower: Int = 25,
    override var mana: Int = 100,
    override val healingPower: Int = 15
) : Character(name, health, attackPower), Healer {

    override fun attack(target: Damageable) {
        if (mana <= 0) {
            println("\n[$name]: 💨 Out of mana and cannot attack! 🧙‍♂️❌")
        } else {
            heal()  // Auto-heals when attacking
            mana -= 1
            println("\n[$name]: 🔥🌀 Casts a fireball at the enemy! (-$attackPower HP) 🌪️")
            println("✨ Mana remaining: $mana 🧪")
            target.takeDamage(attackPower)
        }
    }

    override fun heal() {
        if (mana <= 0) {
            println("\n[$name]: 💨 Out of mana and cannot heal! 🧙‍♂️❌")
        } else if (health >= 100) {
            println("\n[$name]: 💚 Already at full health, no need to heal. 🌿")
        } else {
            mana -= 1
            val oldHealth = health
            health = min(100, health + healingPower)
            val healedAmount = health - oldHealth

            println("\n[$name]: 💖✨ Heals self (+$healedAmount HP) 🌟")
            println("❤️ HP: $health | 🔋 Mana remaining: $mana")
        }
    }
}

fun main() {
    val warrior = Warrior("Leon")
    val sorcerer = Sorcerer("Merlin")

    warrior.attack(sorcerer)
    sorcerer.attack(warrior)
    warrior.takeDamage(30)
    sorcerer.heal()
}
