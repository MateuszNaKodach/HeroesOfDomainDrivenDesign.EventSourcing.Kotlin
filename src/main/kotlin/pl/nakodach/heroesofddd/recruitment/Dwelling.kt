package pl.nakodach.pl.nakodach.heroesofddd.recruitment

import pl.nakodach.pl.nakodach.heroesofddd.recruitment.Dwelling.*
import pl.nakodach.pl.nakodach.heroesofddd.recruitment.DwellingCommand.*
import pl.nakodach.pl.nakodach.heroesofddd.recruitment.DwellingEvent.*
import pl.nakodach.pl.nakodach.heroesofddd.shared.kernel.Amount
import pl.nakodach.pl.nakodach.heroesofddd.shared.kernel.Cost
import pl.nakodach.pl.nakodach.heroesofddd.shared.kernel.CreatureId
import pl.nakodach.pl.nakodach.shared.buildingblocks.domain.Decider
import pl.nakodach.pl.nakodach.shared.buildingblocks.domain.IDecider

sealed interface DwellingCommand {
    val dwellingId: DwellingId

    data class BuildDwelling(
        override val dwellingId: DwellingId,
        val creatureId: CreatureId,
        val costPerTroop: Cost
    ) : DwellingCommand

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

    data class DwellingBuilt(
        override val dwellingId: DwellingId,
        val creatureId: CreatureId,
        val costPerTroop: Cost
    ) : DwellingEvent

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

sealed interface Dwelling {
    data object NotBuilt : Dwelling;
    data class Built(val creatureId: CreatureId, val costPerTroop: Cost, val availableCreatures: Amount) : Dwelling;
}

fun dwelling(): IDecider<DwellingCommand, Dwelling, DwellingEvent> = Decider(
    decide = ::decide,
    evolve = { state, event ->
        when (state) {
            NotBuilt -> when (event) {
                is DwellingBuilt -> Built(event.creatureId, event.costPerTroop, Amount.zero())
                else -> state
            }

            is Built -> when (event) {
                is CreatureRecruited -> state.copy(availableCreatures = state.availableCreatures - event.recruited)
                is AvailableCreaturesChanged -> state.copy(availableCreatures = event.changedTo)
                else -> state
            }
        }
    },
    initialState = NotBuilt
)

private fun decide(command: DwellingCommand, state: Dwelling): List<DwellingEvent> =
    when (state) {
        NotBuilt -> when (command) {
            is BuildDwelling -> listOf(
                DwellingBuilt(
                    command.dwellingId,
                    command.creatureId,
                    command.costPerTroop
                )
            )

            else -> emptyList()
        }

        is Built -> when (command) {
            is RecruitCreature -> {
                if (state.creatureId != command.creatureId || command.recruit > state.availableCreatures)
                    emptyList()
                else
                    listOf(
                        CreatureRecruited(
                            command.dwellingId,
                            command.creatureId,
                            command.recruit,
                            state.costPerTroop * command.recruit
                        )
                    )
            }

            is IncreaseAvailableCreatures -> listOf(
                AvailableCreaturesChanged(
                    command.dwellingId,
                    command.creatureId,
                    command.available
                )
            )

            else -> emptyList()
        }
    }

//todo; event built musi byc, mozna state wproadzic, budynek wybudowany, nie wybudowany
//zmienic nazwy jak na ui, costPerTroop itp.