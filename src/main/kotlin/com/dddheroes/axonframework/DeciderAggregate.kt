package com.dddheroes.axonframework

import com.dddheroes.shared.buildingblocks.domain.IDecider
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle

/**
 * Abstract and generic aggregate
 */
abstract class DeciderAggregate<C, S, E> {
    // Main decision-making component
    abstract val decider: IDecider<C, S, E>

    // The state of this aggregate
    abstract var state: S

    // An aggregate identifier
    abstract fun E.aggregateIdentifier(): String

    @AggregateIdentifier
    private var id: String? = null

    protected fun handleCommand(command: C) {
        decider.decide(command, state).forEach {
            AggregateLifecycle.apply(it)
        }
    }

    @EventSourcingHandler
    fun onEvent(event: E) {
        id = event.aggregateIdentifier()
        state = decider.evolve(state, event)
    }
}