package com.dddheroes.heroesofddd.recruitment

import com.dddheroes.axonextension.AbstractAggregate
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.spring.stereotype.Aggregate

@Aggregate(commandTargetResolver = "metaDataCommandTargetResolver")
class DwellingAggregate : AbstractAggregate<DwellingCommand, Dwelling, DwellingEvent> {

    final override val decider = dwelling()
    override fun DwellingEvent.aggregateIdentifier() = this.dwellingId.raw
    override var state: Dwelling = decider.initialState

    private constructor()

    @CommandHandler
    constructor(command: DwellingCommand) {
        handleCommand(command)
    }

    @CommandHandler
    fun handle(command: DwellingCommand.BuildDwelling) = handleCommand(command)

    @CommandHandler
    fun handle(command: DwellingCommand.RecruitCreature) = handleCommand(command)

    @CommandHandler
    fun handle(command: DwellingCommand.IncreaseAvailableCreatures) = handleCommand(command)
}
