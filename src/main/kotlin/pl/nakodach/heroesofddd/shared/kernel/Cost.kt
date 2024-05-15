package pl.nakodach.pl.nakodach.heroesofddd.shared.kernel

data class Cost(private val resources: Map<ResourceType, Amount>) {
    companion object {
        fun resources(vararg resources: Pair<ResourceType, Int>): Cost = Cost(resources.associate {
            it.first to Amount(
                it.second
            )
        })
    }

    operator fun times(multiplier: Int): Cost = Cost(resources.mapValues { Amount(it.value.raw * multiplier) })
}

data class Amount(val raw: Int) {

    companion object {
        fun of(raw: Int): Amount {
            require(raw >= 0) { "Amount cannot be negative" }
            return Amount(raw)
        }

        fun zero() = Amount(0)

    }

    operator fun compareTo(o: Amount): Int = raw.compareTo(o.raw)
    operator fun plus(o: Amount): Amount = Amount(raw + o.raw)
    operator fun minus(o: Amount): Amount = Amount(raw - o.raw)
}