package pl.nakodach.pl.nakodach.heroesofddd.recruitment

data class DwellingId private constructor(val raw: String) {

    companion object {
        fun of(raw: String) = DwellingId(raw)
    }
}