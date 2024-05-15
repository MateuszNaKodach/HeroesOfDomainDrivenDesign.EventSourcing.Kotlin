package pl.nakodach.heroesofddd.recruitment

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import org.junit.jupiter.api.Test
import pl.nakodach.pl.nakodach.heroesofddd.recruitment.Dwelling
import pl.nakodach.pl.nakodach.heroesofddd.recruitment.DwellingCommand
import pl.nakodach.pl.nakodach.heroesofddd.recruitment.DwellingCommand.RecruitCreature
import pl.nakodach.pl.nakodach.heroesofddd.recruitment.DwellingEvent
import pl.nakodach.pl.nakodach.heroesofddd.recruitment.DwellingEvent.*
import pl.nakodach.pl.nakodach.heroesofddd.recruitment.dwelling
import pl.nakodach.pl.nakodach.heroesofddd.shared.kernel.Amount
import pl.nakodach.pl.nakodach.heroesofddd.shared.kernel.Cost
import pl.nakodach.pl.nakodach.heroesofddd.shared.kernel.CreatureId
import pl.nakodach.pl.nakodach.heroesofddd.shared.kernel.ResourceType

class DwellingTest {

    private val angelId = CreatureId.of("Angel")
    private val archangelId = CreatureId.of("Archangel")
    private val costPerTroop = Cost.resources(ResourceType.GOLD to 3000, ResourceType.CRYSTAL to 1)
    private val portalOfGlory = dwelling(angelId, costPerTroop)

    @Test
    fun `given empty Dwelling, when recruit creature, then nothing`() {
        // given
        val givenEvents = emptyList<DwellingEvent>()

        // when
        val whenCommand = RecruitCreature(angelId, Amount.of(1))

        // then
        val thenEvents = decide(givenEvents, whenCommand)
        assertThat(thenEvents).isEmpty()
    }

    @Test
    fun `given Dwelling with 1 troop, when recruit 1 troop, then recruited`() {
        // given
        val givenEvents = listOf(AvailableTroopsChanged(angelId, Amount.of(1)))

        // when
        val whenCommand = RecruitCreature(angelId, Amount.of(1))

        // then
        val thenEvents = decide(givenEvents, whenCommand)
        val expectedRecruited = CreatureRecruited(
            angelId,
            Amount.of(1),
            Cost.resources(ResourceType.GOLD to 3000, ResourceType.CRYSTAL to 1)
        )
        assertThat(thenEvents).containsExactly(expectedRecruited)
    }

    @Test
    fun `given Dwelling with 4 troop, when recruit 3 troop, then recruited 2 and totalCost = costPerTroop x 3`() {
        // given
        val givenEvents = listOf(AvailableTroopsChanged(angelId, Amount.of(4)))

        // when
        val whenCommand = RecruitCreature(angelId, Amount.of(3))

        // then
        val thenEvents = decide(givenEvents, whenCommand)
        val expectedRecruited = CreatureRecruited(
            angelId,
            Amount.of(3),
            Cost.resources(ResourceType.GOLD to 9000, ResourceType.CRYSTAL to 3)
        )
        assertThat(thenEvents).containsExactly(expectedRecruited)
    }

    @Test
    fun `given Dwelling with 1 troop, when recruit 2 troops, then nothing`() {
        // given
        val givenEvents = listOf(AvailableTroopsChanged(angelId, Amount.of(1)))

        // when
        val whenCommand = RecruitCreature(angelId, Amount.of(2))

        // then
        val thenEvents = decide(givenEvents, whenCommand)
        assertThat(thenEvents).isEmpty()
    }

    @Test
    fun `given Dwelling, when recruit troop not from this dwelling, then nothing`() {
        // given
        val givenEvents = listOf(AvailableTroopsChanged(angelId, Amount.of(1)))

        // when
        val whenCommand = RecruitCreature(archangelId, Amount.of(1))

        // then
        val thenEvents = decide(givenEvents, whenCommand)
        assertThat(thenEvents).isEmpty()
    }

    private fun decide(
        givenEvents: Collection<DwellingEvent>,
        whenCommand: DwellingCommand
    ): List<DwellingEvent> =
        portalOfGlory.decide(whenCommand, stateFrom(givenEvents))

    private fun stateFrom(givenEvents: Collection<DwellingEvent>): Dwelling =
        givenEvents.fold(portalOfGlory.initialState) { state, event -> portalOfGlory.evolve(state, event) }
}