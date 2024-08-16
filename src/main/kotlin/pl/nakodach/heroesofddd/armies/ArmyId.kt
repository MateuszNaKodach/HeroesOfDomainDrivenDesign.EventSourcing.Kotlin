package pl.nakodach.pl.nakodach.heroesofddd.armies

data class ArmyId private constructor(val raw: String) {

    companion object {
        fun of(raw: String) = ArmyId(raw)
    }
}