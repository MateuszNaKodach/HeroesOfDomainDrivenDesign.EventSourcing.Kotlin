package pl.nakodach.pl.nakodach.heroesofddd.shared.kernel

data class Cost(private val resources: Map<ResourceType, Amount>)

data class Amount(val raw: Int) {

    companion object {
        fun of(raw: Int) {
            require(raw >= 0) { "Amount cannot be negative" }
            Amount(raw)
        }

        fun zero() = Amount(0)

    }

    operator fun compareTo(o: Amount): Int = raw.compareTo(o.raw)
    operator fun plus(o: Amount): Amount = Amount(raw + o.raw)
    operator fun minus(o: Amount): Amount = Amount(raw - o.raw)
}