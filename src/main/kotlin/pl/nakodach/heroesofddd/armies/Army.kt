import pl.nakodach.pl.nakodach.heroesofddd.armies.ArmyId
import pl.nakodach.pl.nakodach.heroesofddd.recruitment.DwellingId
import pl.nakodach.pl.nakodach.heroesofddd.shared.kernel.Amount
import pl.nakodach.pl.nakodach.heroesofddd.shared.kernel.CreatureId

sealed interface ArmyCommand {
    val armyId: ArmyId

    data class AddCreatures(
        override val armyId: ArmyId,
        val creatureId: CreatureId,
        val units: Amount,
    ) : ArmyCommand

}


sealed interface ArmyEvent {
    val dwellingId: DwellingId
}
