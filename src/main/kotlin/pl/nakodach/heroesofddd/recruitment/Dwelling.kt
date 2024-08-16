package pl.nakodach.pl.nakodach.heroesofddd.recruitment

import pl.nakodach.pl.nakodach.heroesofddd.armies.ArmyId
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
        val recruit: Amount,
        val armyId: ArmyId // to: ArmyId
    ) : DwellingCommand

    data class IncreaseAvailableCreatures(
        override val dwellingId: DwellingId,
        val creatureId: CreatureId,
        val increaseBy: Amount
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
        val totalCost: Cost,
        val armyId: ArmyId
    ) : DwellingEvent

    data class AvailableCreaturesChanged(
        override val dwellingId: DwellingId,
        val creatureId: CreatureId,
        val changedTo: Amount
    ) : DwellingEvent
}

sealed interface Dwelling {
    data object NotBuilt : Dwelling;
    data class Built(
        val dwellingId: DwellingId,
        val creatureId: CreatureId,
        val costPerTroop: Cost,
        val availableCreatures: Amount
    ) : Dwelling;
}

fun dwelling(): IDecider<DwellingCommand, Dwelling, DwellingEvent> = Decider(
    decide = ::decide,
    evolve = { state, event ->
        when (state) {
            NotBuilt -> when (event) {
                is DwellingBuilt -> Built(event.dwellingId, event.creatureId, event.costPerTroop, Amount.zero())
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
            is BuildDwelling -> whenCommand(command)
            else -> emptyList()
        }

        is Built -> when (command) {
            is RecruitCreature -> whenCommand(state, command)
            is IncreaseAvailableCreatures -> whenCommand(state, command)
            else -> emptyList()
        }
    }

private fun whenCommand(command: BuildDwelling) =
    listOf(
        DwellingBuilt(
            command.dwellingId,
            command.creatureId,
            command.costPerTroop
        )
    )

private fun whenCommand(
    state: Built,
    command: RecruitCreature
) = if (state.creatureId != command.creatureId || command.recruit > state.availableCreatures)
    emptyList()
else
    listOf(
        CreatureRecruited(
            command.dwellingId,
            command.creatureId,
            command.recruit,
            state.costPerTroop * command.recruit,
            command.armyId
        )
    )

private fun whenCommand(
    state: Built,
    command: IncreaseAvailableCreatures
) = listOf(
    AvailableCreaturesChanged(
        state.dwellingId,
        state.creatureId,
        command.increaseBy
    )
)

//todo; event built musi byc, mozna state wproadzic, budynek wybudowany, nie wybudowany
//zmienic nazwy jak na ui, costPerTroop itp.


/*
require "test_helper"

# Recruitment = ProductCatalog
#
# module Recruitment
#   BuildDwelling = ProductCatalog::RegisterProduct
# end

# summary

# 1. constant assignment is enough for simple module replacement and for event lookup
# 2. but when we really want to see different events in db we need to change event_type probably
# 3. this leads us to one potential solution, separate domain event from infra event (as fidel did in payments_sample repo)
# 4. possible next step is to move ProductCatalog or Fulflillment to this approach
# 5. side effect is that ProductCatalog will not dependend on Infra gem






module Recruitment
  BuildDwelling = ProductCatalog::RegisterProduct
  IncreaseAvailableCreatures = Inventory::Supply

  CreatureRecruited = Ordering::OrderPlaced
end

module Ordering
  class OrderPlaced
    def event_type
      "Recruitment::CreatureRecruited"
    end
  end
end


class RecruitingHeroesTest < RealRESIntegrationTestCase


  def test_availability_updates
    angel_id = SecureRandom.uuid
    order_id = SecureRandom.uuid
    build_dwelling(angel_id, "Angel", 100)
    run_command(Recruitment::IncreaseAvailableCreatures.new(product_id: angel_id, quantity: 1))
    run_command(Ordering::AddItemToBasket.new(order_id: order_id, product_id: angel_id))
    run_command(Ordering::SubmitOrder.new(order_id: order_id))

    pp event_store.read.to_a.map(&:event_type)
    assert_order_placed(order_id)
  end

  private

  def assert_order_placed(order_id)
    assert_equal(1, event_store.read.to_a.select{|event| event.event_type == "Recruitment::CreatureRecruited"}.count)
  end

  def event_store
    Rails.configuration.event_store
  end

  def build_dwelling(product_id, name, cost)
    run_command(
      Recruitment::BuildDwelling.new(
        product_id: product_id,
        )
    )
    run_command(
      ProductCatalog::NameProduct.new(
        product_id: product_id,
        name: name
      )
    )
    run_command(Pricing::SetPrice.new(product_id: product_id, price: cost))
  end
end
 */


/*
require "test_helper"

# Recruitment = ProductCatalog
#
# module Recruitment
#   BuildDwelling = ProductCatalog::RegisterProduct
# end

# summary

# 1. constant assignment is enough for simple module replacement and for event lookup
# 2. but when we really want to see different events in db we need to change event_type probably
# 3. this leads us to one potential solution, separate domain event from infra event (as fidel did in payments_sample repo)
# 4. possible next step is to move ProductCatalog or Fullfilment to this approach
# 5. side effect is that ProductCatalog will not dependend on Infra gem
# 6. another direction is not to use the same events, but translate events from generic domain to specific domain






module Recruitment
  BuildDwelling = ProductCatalog::RegisterProduct
  IncreaseAvailableCreatures = Inventory::Supply

  CreatureRecruited = Ordering::OrderPlaced
end

module Recruitment
  class CreatureRecruited
    def event_type
      "Recruitment::CreatureRecruited"
    end
  end

end

event_store.subscribe(event_store.publish(Recruitment::CreatureRecruited), to: [Ordering::OrderPlaced])


module Ordering
  class OrderPlaced
    def event_type
      "Recruitment::CreatureRecruited"
    end
  end
end


class RecruitingHeroesTest < RealRESIntegrationTestCase


  def test_availability_updates
    angel_id = SecureRandom.uuid
    order_id = SecureRandom.uuid
    build_dwelling(angel_id, "Angel", 100)
    run_command(Recruitment::IncreaseAvailableCreatures.new(product_id: angel_id, quantity: 1))
    run_command(Ordering::AddItemToBasket.new(order_id: order_id, product_id: angel_id))
    run_command(Ordering::SubmitOrder.new(order_id: order_id))

    pp event_store.read.to_a.map(&:event_type)
    assert_order_placed(order_id)
  end

  private

  def assert_order_placed(order_id)
    assert_equal(1, event_store.read.to_a.select{|event| event.event_type == "Recruitment::CreatureRecruited"}.count)
  end

  def event_store
    Rails.configuration.event_store
  end

  def build_dwelling(product_id, name, cost)
    run_command(
      Recruitment::BuildDwelling.new(
        product_id: product_id,
        )
    )
    run_command(
      ProductCatalog::NameProduct.new(
        product_id: product_id,
        name: name
      )
    )
    run_command(Pricing::SetPrice.new(product_id: product_id, price: cost))
  end
end
 */