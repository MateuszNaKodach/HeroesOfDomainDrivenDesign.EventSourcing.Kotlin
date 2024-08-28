package pl.nakodach.pl.nakodach.heroesofddd.recruitment

import ArmyCommand

fun whenRecruitedThenAddToArmy(event: DwellingEvent) =
    when (event) {
        is DwellingEvent.CreatureRecruited -> listOf(
            ArmyCommand.AddCreatures(event.armyId, event.creatureId, event.recruited)
        )
        else -> emptyList()
    }