package pl.nakodach.heroesofddd.recruitment

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import org.junit.jupiter.api.Test
import pl.nakodach.pl.nakodach.heroesofddd.recruitment.*
import pl.nakodach.pl.nakodach.heroesofddd.recruitment.DwellingCommand.*
import pl.nakodach.pl.nakodach.heroesofddd.recruitment.DwellingEvent.*
import pl.nakodach.pl.nakodach.heroesofddd.shared.kernel.Amount
import pl.nakodach.pl.nakodach.heroesofddd.shared.kernel.Cost.Companion.resources
import pl.nakodach.pl.nakodach.heroesofddd.shared.kernel.CreatureId
import pl.nakodach.pl.nakodach.heroesofddd.shared.kernel.ResourceType.CRYSTAL
import pl.nakodach.pl.nakodach.heroesofddd.shared.kernel.ResourceType.GOLD
import java.util.*

class DwellingTest {

    private val angelId = CreatureId.of("Angel")
    private val archangelId = CreatureId.of("Archangel")
    private val dwellingId = DwellingId.of(UUID.randomUUID().toString())
    private val angelCostPerTroop = resources(GOLD to 3000, CRYSTAL to 1)
    private val dwelling = dwelling()

    @Test
    fun `given not built Dwelling, when build, then built`() {
        // given
        val givenEvents = emptyList<DwellingEvent>()

        // when
        val whenCommand = BuildDwelling(dwellingId, angelId, angelCostPerTroop)

        // then
        val thenEvents = decide(givenEvents, whenCommand)
        assertThat(thenEvents).containsExactly(DwellingBuilt(dwellingId, angelId, angelCostPerTroop))
    }

    @Test
    fun `given built Dwelling, when build, then nothing`() {
        // given
        val givenEvents = listOf(
            DwellingBuilt(dwellingId, angelId, angelCostPerTroop)
        )

        // when
        val whenCommand = BuildDwelling(dwellingId, angelId, angelCostPerTroop)

        // then
        val thenEvents = decide(givenEvents, whenCommand)
        assertThat(thenEvents).isEmpty()
    }

    @Test
    fun `given not built Dwelling, when recruit creature, then nothing`() {
        // given
        val givenEvents = emptyList<DwellingEvent>()

        // when
        val whenCommand = RecruitCreature(dwellingId, angelId, Amount.of(1))

        // then
        val thenEvents = decide(givenEvents, whenCommand)
        assertThat(thenEvents).isEmpty()
    }

    @Test
    fun `given built, but empty Dwelling, when recruit creature, then nothing`() {
        // given
        val givenEvents = listOf(
            DwellingBuilt(dwellingId, angelId, angelCostPerTroop)
        )

        // when
        val whenCommand = RecruitCreature(dwellingId, angelId, Amount.of(1))

        // then
        val thenEvents = decide(givenEvents, whenCommand)
        assertThat(thenEvents).isEmpty()
    }


    @Test
    fun `given Dwelling with 1 creature, when recruit 1 creature, then recruited`() {
        // given
        val givenEvents = listOf(
            DwellingBuilt(dwellingId, angelId, angelCostPerTroop),
            AvailableCreaturesChanged(dwellingId, angelId, Amount.of(1))
        )

        // when
        val whenCommand = RecruitCreature(dwellingId, angelId, Amount.of(1))

        // then
        val thenEvents = decide(givenEvents, whenCommand)
        val expectedRecruited = CreatureRecruited(
            dwellingId, angelId, Amount.of(1), resources(GOLD to 3000, CRYSTAL to 1)
        )
        assertThat(thenEvents).containsExactly(expectedRecruited)
    }

    @Test
    fun `given Dwelling with 4 creature, when recruit 3 creature, then recruited 3 and totalCost = costPerTroop x 3`() {
        // given
        val givenEvents = listOf(
            DwellingBuilt(dwellingId, angelId, angelCostPerTroop),
            AvailableCreaturesChanged(dwellingId, angelId, Amount.of(4))
        )

        // when
        val whenCommand = RecruitCreature(dwellingId, angelId, Amount.of(3))

        // then
        val thenEvents = decide(givenEvents, whenCommand)
        val expectedRecruited = CreatureRecruited(
            dwellingId, angelId, Amount.of(3), resources(GOLD to 9000, CRYSTAL to 3)
        )
        assertThat(thenEvents).containsExactly(expectedRecruited)
    }

    @Test
    fun `given Dwelling with 2 creatures, when recruit 2 creatures, then recruited 2 and totalCost = costPerTroop x 2`() {
        // given
        val givenEvents = listOf(
            DwellingBuilt(dwellingId, angelId, angelCostPerTroop),
            AvailableCreaturesChanged(dwellingId, angelId, changedTo = Amount.of(2))
        )

        // when
        val whenCommand = RecruitCreature(dwellingId, angelId, recruit = Amount.of(2))

        // then
        val thenEvents = decide(givenEvents, whenCommand)
        val expectedRecruited = CreatureRecruited(
            dwellingId, angelId, recruited = Amount.of(2), totalCost = resources(GOLD to 6000, CRYSTAL to 2)
        )
        assertThat(thenEvents).containsExactly(expectedRecruited)
    }


    @Test
    fun `given Dwelling with 1 creature, when recruit 2 creatures, then nothing`() {
        // given
        val givenEvents = listOf(
            DwellingBuilt(dwellingId, angelId, angelCostPerTroop),
            AvailableCreaturesChanged(dwellingId, angelId, Amount.of(1))
        )

        // when
        val whenCommand = RecruitCreature(dwellingId, angelId, Amount.of(2))

        // then
        val thenEvents = decide(givenEvents, whenCommand)
        assertThat(thenEvents).isEmpty()
    }

    @Test
    fun `given Dwelling, when recruit creature not from this dwelling, then nothing`() {
        // given
        val givenEvents = listOf(
            DwellingBuilt(dwellingId, angelId, angelCostPerTroop),
            AvailableCreaturesChanged(dwellingId, angelId, Amount.of(1))
        )

        // when
        val whenCommand = RecruitCreature(dwellingId, archangelId, Amount.of(1))

        // then
        val thenEvents = decide(givenEvents, whenCommand)
        assertThat(thenEvents).isEmpty()
    }

    @Test
    fun `given Dwelling with recruited all available creatures at once, when recruit creature, then nothing`() {
        // given
        val givenEvents = listOf(
            DwellingBuilt(dwellingId, angelId, angelCostPerTroop),
            AvailableCreaturesChanged(dwellingId, angelId, Amount.of(3)),
            CreatureRecruited(
                dwellingId, angelId, Amount.of(3), resources(GOLD to 9000, CRYSTAL to 3)
            ),
        )

        // when
        val whenCommand = RecruitCreature(dwellingId, archangelId, Amount.of(1))

        // then
        val thenEvents = decide(givenEvents, whenCommand)
        assertThat(thenEvents).isEmpty()
    }

    @Test
    fun `given Dwelling with recruited all available creatures, when recruit creature, then nothing`() {
        // given
        val givenEvents = listOf(
            DwellingBuilt(dwellingId, angelId, angelCostPerTroop),
            AvailableCreaturesChanged(dwellingId, angelId, Amount.of(3)),
            CreatureRecruited(
                dwellingId, angelId, Amount.of(2), resources(GOLD to 6000, CRYSTAL to 2)
            ),
            CreatureRecruited(
                dwellingId, angelId, Amount.of(1), resources(GOLD to 3000, CRYSTAL to 1)
            ),
        )

        // when
        val whenCommand = RecruitCreature(dwellingId, archangelId, Amount.of(1))

        // then
        val thenEvents = decide(givenEvents, whenCommand)
        assertThat(thenEvents).isEmpty()
    }

    @Test
    fun `given Dwelling with recruited some available creatures and 1 left, when recruit 1 creature, then recruited`() {
        // given
        val givenEvents = listOf(
            DwellingBuilt(dwellingId, angelId, angelCostPerTroop),
            AvailableCreaturesChanged(dwellingId, angelId, changedTo = Amount.of(4)),
            CreatureRecruited(
                dwellingId, angelId, recruited = Amount.of(3), totalCost = resources(GOLD to 9000, CRYSTAL to 3)
            ),
        )

        // when
        val whenCommand = RecruitCreature(dwellingId, angelId, Amount.of(1))

        // then
        val thenEvents = decide(givenEvents, whenCommand)
        val expectedEvent = CreatureRecruited(
            dwellingId, angelId, recruited = Amount.of(1), totalCost = resources(GOLD to 3000, CRYSTAL to 1)
        )
        assertThat(thenEvents).containsExactly(expectedEvent)
    }

    // todo: increase available creatures

    private fun decide(
        givenEvents: Collection<DwellingEvent>, whenCommand: DwellingCommand
    ): List<DwellingEvent> = dwelling.decide(whenCommand, stateFrom(givenEvents))

    private fun stateFrom(givenEvents: Collection<DwellingEvent>): Dwelling =
        givenEvents.fold(dwelling.initialState) { state, event -> dwelling.evolve(state, event) }
}