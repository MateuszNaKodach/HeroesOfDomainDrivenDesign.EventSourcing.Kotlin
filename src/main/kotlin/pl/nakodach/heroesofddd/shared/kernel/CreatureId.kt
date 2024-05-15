package pl.nakodach.pl.nakodach.heroesofddd.shared.kernel

data class CreatureId private constructor(val raw: String) {

    companion object {
        fun of(raw: String) = CreatureId(raw)
    }
}