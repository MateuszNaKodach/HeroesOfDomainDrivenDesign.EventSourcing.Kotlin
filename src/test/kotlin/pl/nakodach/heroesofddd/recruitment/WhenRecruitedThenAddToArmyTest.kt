package pl.nakodach.heroesofddd.recruitment

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import org.junit.jupiter.api.Test
import pl.nakodach.pl.nakodach.heroesofddd.armies.ArmyId
import pl.nakodach.pl.nakodach.heroesofddd.recruitment.*
import pl.nakodach.pl.nakodach.heroesofddd.recruitment.DwellingEvent.*
import pl.nakodach.pl.nakodach.heroesofddd.shared.kernel.Amount
import pl.nakodach.pl.nakodach.heroesofddd.shared.kernel.Cost.Companion.resources
import pl.nakodach.pl.nakodach.heroesofddd.shared.kernel.CreatureId
import pl.nakodach.pl.nakodach.heroesofddd.shared.kernel.ResourceType.GOLD
import pl.nakodach.pl.nakodach.heroesofddd.shared.kernel.ResourceType.CRYSTAL
import java.util.*

class WhenRecruitedThenAddToArmyTest {

    private val angelId = CreatureId.of("Angel")
    private val dwellingId = DwellingId.of(UUID.randomUUID().toString())
    private val armyId = ArmyId.of(UUID.randomUUID().toString())

    @Test
    fun ` when CreatureRecruited, then AddCreatures command`() {
        // when
        val creatureRecruited = CreatureRecruited(
            dwellingId = dwellingId,
            creatureId = angelId,
            recruited = Amount.of(3),
            totalCost = resources(GOLD to 9000, CRYSTAL to 3),
            armyId = armyId
        )
        val commands = whenRecruitedThenAddToArmy(creatureRecruited)

        // then
        val expectedCommand = ArmyCommand.AddCreatures(armyId, angelId, Amount.of(3))
        assertThat(commands).containsExactly(expectedCommand)
    }

    @Test
    fun `when non-CreatureRecruited event, then nothing`() {
        // when
        val dwellingBuilt = DwellingBuilt(
            dwellingId = dwellingId,
            creatureId = angelId,
            costPerTroop = resources(GOLD to 3000, CRYSTAL to 1)
        )
        val commands = whenRecruitedThenAddToArmy(dwellingBuilt)

        // then
        assertThat(commands).isEmpty()
    }
}