package pl.nakodach.pl.nakodach.heroesofddd.recruitment

import pl.nakodach.pl.nakodach.heroesofddd.shared.kernel.Amount
import pl.nakodach.pl.nakodach.heroesofddd.shared.kernel.Cost
import pl.nakodach.pl.nakodach.heroesofddd.shared.kernel.CreatureId
import pl.nakodach.pl.nakodach.shared.buildingblocks.domain.Decider
import pl.nakodach.pl.nakodach.shared.buildingblocks.domain.IDecider

sealed interface DwellingCommand {
    val dwellingId: DwellingId

    data class RecruitCreature(
        override val dwellingId: DwellingId,
        val creatureId: CreatureId,
        val recruit: Amount
    ) : DwellingCommand

    data class IncreaseAvailableCreatures(
        override val dwellingId: DwellingId,
        val creatureId: CreatureId,
        val available: Amount
    ) : DwellingCommand
}

sealed interface DwellingEvent {
    val dwellingId: DwellingId

    data class CreatureRecruited(
        override val dwellingId: DwellingId,
        val creatureId: CreatureId,
        val recruited: Amount,
        val totalCost: Cost
    ) : DwellingEvent

    data class AvailableCreaturesChanged(
        override val dwellingId: DwellingId,
        val creatureId: CreatureId,
        val changedTo: Amount
    ) : DwellingEvent
}

data class Dwelling(val creatureId: CreatureId, val costPerTroop: Cost, val availableCreatures: Amount)

fun dwelling(creatureId: CreatureId, costPerTroop: Cost): IDecider<DwellingCommand, Dwelling, DwellingEvent> = Decider(
    decide = ::decide,
    evolve = { state, event ->
        when (event) {
            is DwellingEvent.CreatureRecruited -> state.copy(availableCreatures = state.availableCreatures - event.recruited)
            is DwellingEvent.AvailableCreaturesChanged -> state.copy(availableCreatures = event.changedTo)
        }
    },
    initialState = Dwelling(creatureId, costPerTroop, Amount.zero())
)

private fun decide(command: DwellingCommand, state: Dwelling): List<DwellingEvent> =
    when (command) {
        is DwellingCommand.RecruitCreature -> {
            if (state.creatureId != command.creatureId || command.recruit > state.availableCreatures)
                emptyList()
            else
                listOf(
                    DwellingEvent.CreatureRecruited(
                        command.dwellingId,
                        command.creatureId,
                        command.recruit,
                        state.costPerTroop * command.recruit
                    )
                )
        }

        is DwellingCommand.IncreaseAvailableCreatures -> listOf(
            DwellingEvent.AvailableCreaturesChanged(
                command.dwellingId,
                command.creatureId,
                command.available
            )
        )
    }

//todo; event built musi byc, mozna state wproadzic, budynek wybudowany, nie wybudowany
//zmienic nazwy jak na ui, costPerTroop itp.