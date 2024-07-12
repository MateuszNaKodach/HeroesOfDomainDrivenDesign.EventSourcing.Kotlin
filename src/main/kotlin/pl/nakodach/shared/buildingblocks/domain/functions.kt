package pl.nakodach.pl.nakodach.shared.buildingblocks.domain

typealias Decide<C, S, E> = (command: C, state: S) -> List<E>
typealias Evolve<S, E> = (state: S, event: E) -> S
typealias React<E, C> = (event: E) -> C
