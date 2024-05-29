package pl.nakodach.heroesofddd.recruitment

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import org.junit.jupiter.api.Test
import pl.nakodach.pl.nakodach.heroesofddd.recruitment.*
import pl.nakodach.pl.nakodach.heroesofddd.recruitment.DwellingCommand.RecruitCreature
import pl.nakodach.pl.nakodach.heroesofddd.recruitment.DwellingEvent.*
import pl.nakodach.pl.nakodach.heroesofddd.shared.kernel.Amount
import pl.nakodach.pl.nakodach.heroesofddd.shared.kernel.Cost
import pl.nakodach.pl.nakodach.heroesofddd.shared.kernel.CreatureId
import pl.nakodach.pl.nakodach.heroesofddd.shared.kernel.ResourceType.*
import java.util.UUID

class DwellingTest {

    private val angelId = CreatureId.of("Angel")
    private val archangelId = CreatureId.of("Archangel")
    private val costPerCreature = Cost.resources(GOLD to 3000, CRYSTAL to 1)
    private val dwellingId = DwellingId.of(UUID.randomUUID().toString())
    private val portalOfGlory = dwelling(angelId, costPerCreature)

    @Test
    fun `given empty Dwelling, when recruit creature, then nothing`() {
        // given
        val givenEvents = emptyList<DwellingEvent>()

        // when
        val whenCommand = RecruitCreature(dwellingId, angelId, Amount.of(1))

        // then
        val thenEvents = decide(givenEvents, whenCommand)
        assertThat(thenEvents).isEmpty()
    }

    @Test
    fun `given Dwelling with 1 creature, when recruit 1 creature, then recruited`() {
        // given
        val givenEvents = listOf(AvailableCreaturesChanged(dwellingId, angelId, Amount.of(1)))

        // when
        val whenCommand = RecruitCreature(dwellingId, angelId, Amount.of(1))

        // then
        val thenEvents = decide(givenEvents, whenCommand)
        val expectedRecruited = CreatureRecruited(
            dwellingId,
            angelId,
            Amount.of(1),
            Cost.resources(GOLD to 3000, CRYSTAL to 1)
        )
        assertThat(thenEvents).containsExactly(expectedRecruited)
    }

    @Test
    fun `given Dwelling with 4 creature, when recruit 3 creature, then recruited 3 and totalCost = costPerTroop x 3`() {
        // given
        val givenEvents = listOf(AvailableCreaturesChanged(dwellingId, angelId, Amount.of(4)))

        // when
        val whenCommand = RecruitCreature(dwellingId, angelId, Amount.of(3))

        // then
        val thenEvents = decide(givenEvents, whenCommand)
        val expectedRecruited = CreatureRecruited(
            dwellingId,
            angelId,
            Amount.of(3),
            Cost.resources(GOLD to 9000, CRYSTAL to 3)
        )
        assertThat(thenEvents).containsExactly(expectedRecruited)
    }

    @Test
    fun `given Dwelling with 2 creatures, when recruit 2 creatures, then recruited 2 and totalCost = costPerTroop x 2`() {
        // given
        val givenEvents = listOf(AvailableCreaturesChanged(dwellingId, angelId, Amount.of(2)))

        // when
        val whenCommand = RecruitCreature(dwellingId, angelId, Amount.of(2))

        // then
        val thenEvents = decide(givenEvents, whenCommand)
        val expectedRecruited = CreatureRecruited(
            dwellingId,
            angelId,
            Amount.of(2),
            Cost.resources(GOLD to 6000, CRYSTAL to 2)
        )
        assertThat(thenEvents).containsExactly(expectedRecruited)
    }


    @Test
    fun `given Dwelling with 1 creature, when recruit 2 creatures, then nothing`() {
        // given
        val givenEvents = listOf(AvailableCreaturesChanged(dwellingId, angelId, Amount.of(1)))

        // when
        val whenCommand = RecruitCreature(dwellingId, angelId, Amount.of(2))

        // then
        val thenEvents = decide(givenEvents, whenCommand)
        assertThat(thenEvents).isEmpty()
    }

    @Test
    fun `given Dwelling, when recruit creature not from this dwelling, then nothing`() {
        // given
        val givenEvents = listOf(AvailableCreaturesChanged(dwellingId, angelId, Amount.of(1)))

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
            AvailableCreaturesChanged(dwellingId, angelId, Amount.of(3)),
            CreatureRecruited(
                dwellingId,
                angelId,
                Amount.of(3),
                Cost.resources(GOLD to 9000, CRYSTAL to 3)
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
            AvailableCreaturesChanged(dwellingId, angelId, Amount.of(3)),
            CreatureRecruited(
                dwellingId,
                angelId,
                Amount.of(2),
                Cost.resources(GOLD to 6000, CRYSTAL to 2)
            ),
            CreatureRecruited(
                dwellingId,
                angelId,
                Amount.of(1),
                Cost.resources(GOLD to 3000, CRYSTAL to 1)
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
            AvailableCreaturesChanged(dwellingId, angelId, Amount.of(4)),
            CreatureRecruited(
                dwellingId,
                angelId,
                Amount.of(3),
                Cost.resources(GOLD to 9000, CRYSTAL to 3)
            ),
        )

        // when
        val whenCommand = RecruitCreature(dwellingId, angelId, Amount.of(1))

        // then
        val thenEvents = decide(givenEvents, whenCommand)
        val expectedEvent = CreatureRecruited(
            dwellingId,
            angelId,
            Amount.of(1),
            Cost.resources(GOLD to 3000, CRYSTAL to 1)
        )
        assertThat(thenEvents).containsExactly(expectedEvent)
    }

    // todo: increase available creatures

    private fun decide(
        givenEvents: Collection<DwellingEvent>,
        whenCommand: DwellingCommand
    ): List<DwellingEvent> =
        portalOfGlory.decide(whenCommand, stateFrom(givenEvents))

    private fun stateFrom(givenEvents: Collection<DwellingEvent>): Dwelling =
        givenEvents.fold(portalOfGlory.initialState) { state, event -> portalOfGlory.evolve(state, event) }
}


// jak event modeling
// najpierw given eventy - zakladamy co sie wydarzylo przed, potem wykonujemy jakas akcje, i na sam koniec then eventy, jaki bedzie wynik operacji,
// nie skupiamy sie na stanie - to szczegol implementacyjny, stan jest po to, zeby akcje zwracaly wlasciwy wynik, bazujac na poprzednich
// mowimy, ze mamy siedlisko, gdzie rekrutuje sie anioly, kosztule ono tyle. Pamietacie nazwe? Portal Chwaly.