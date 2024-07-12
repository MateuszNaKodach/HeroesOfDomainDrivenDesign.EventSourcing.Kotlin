package pl.nakodach.pl.nakodach.shared.buildingblocks.domain

interface IDecider<in C, S, E> {
    val decide: Decide<C, S, E>
    val evolve: Evolve<S, E>
    val initialState: S
}

data class Decider<in C, S, E>(
    override val decide: Decide<C, S, E>,
    override val evolve: Evolve<S, E>,
    override val initialState: S
) : IDecider<C, S, E>