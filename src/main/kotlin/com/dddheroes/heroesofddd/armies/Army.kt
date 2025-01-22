import com.dddheroes.heroesofddd.armies.ArmyId
import com.dddheroes.heroesofddd.recruitment.DwellingId
import com.dddheroes.heroesofddd.shared.kernel.Amount
import com.dddheroes.heroesofddd.shared.kernel.CreatureId

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
