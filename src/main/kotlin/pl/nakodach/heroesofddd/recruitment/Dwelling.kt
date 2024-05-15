package pl.nakodach.pl.nakodach.heroesofddd.recruitment

import pl.nakodach.pl.nakodach.heroesofddd.shared.kernel.Amount
import pl.nakodach.pl.nakodach.heroesofddd.shared.kernel.Cost
import pl.nakodach.pl.nakodach.heroesofddd.shared.kernel.CreatureId
import pl.nakodach.pl.nakodach.shared.buildingblocks.domain.Decider
import pl.nakodach.pl.nakodach.shared.buildingblocks.domain.IDecider

//interface Dwelling {
//    fun canRecruit(creatureId: CreatureId, availableTroops: Amount): Boolean
//}
//
//data class CreatureDwelling(val creatureId: CreatureId, val costPerTroop: Cost, val availableTroops: Amount) : Dwelling {
//    override fun canRecruit(creatureId: CreatureId, availableTroops: Amount): Boolean {
//        return this.creatureId == creatureId && this.availableTroops >= availableTroops
//    }
//
//    fun upgradedWith(upgradedCreatureId: CreatureId, costPerTroop: Cost, availableTroops: Amount): CreatureDwelling {
//        return CreatureDwelling(upgradedCreatureId, costPerTroop, availableTroops)
//    }
//
//}
//
//data class MultiCreatureDwelling(val dwellings: List<CreatureDwelling>) : Dwelling {
//    override fun canRecruit(creatureId: CreatureId, availableTroops: Amount): Boolean {
//        return dwellings.any { it.canRecruit(creatureId, availableTroops) }
//    }
//
//    fun upgradedWith(upgradedCreatureId: CreatureId, costPerTroop: Cost, availableTroops: Amount): MultiCreatureDwelling {
//        return MultiCreatureDwelling(dwellings.map { it.upgradedWith(upgradedCreatureId, costPerTroop, availableTroops) })
//    }
//
//}


sealed class DwellingCommand {
    data class RecruitCreature(val creatureId: CreatureId, val amount: Amount) : DwellingCommand()
    data class IncreaseAvailableTroops(val creatureId: CreatureId, val amount: Amount) : DwellingCommand()
}

sealed class DwellingEvent {
    data class CreatureRecruited(val creatureId: CreatureId, val amount: Amount, val totalCost: Cost) : DwellingEvent()
    data class AvailableTroopsChanged(val creatureId: CreatureId, val changedTo: Amount) : DwellingEvent()
}

data class Dwelling(val creatureId: CreatureId, val costPerTroop: Cost, val availableTroops: Amount)

fun dwelling(creatureId: CreatureId, costPerTroop: Cost): IDecider<DwellingCommand, Dwelling, DwellingEvent> = Decider(
    decide = ::decide,
    evolve = { state, event ->
        when (event) {
            is DwellingEvent.CreatureRecruited -> state.copy(availableTroops = state.availableTroops - event.amount)
            is DwellingEvent.AvailableTroopsChanged -> state.copy(availableTroops = event.changedTo)
        }
    },
    initialState = Dwelling(creatureId, costPerTroop, Amount.zero())
)

private fun decide(command: DwellingCommand, state: Dwelling): List<DwellingEvent> =
    when (command) {
        is DwellingCommand.RecruitCreature -> {
            if (state.creatureId != command.creatureId || command.amount > state.availableTroops)
                emptyList()
            else
                listOf(
                    DwellingEvent.CreatureRecruited(
                        command.creatureId,
                        command.amount,
                        state.costPerTroop * command.amount.raw
                    )
                )
        }

        is DwellingCommand.IncreaseAvailableTroops -> listOf(
            DwellingEvent.AvailableTroopsChanged(
                command.creatureId,
                command.amount
            )
        )
    }