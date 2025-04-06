package com.reguerta.domain.enums

enum class ContainerType(val value: String) {
    PACKET("Paquete"),
    JAR("Bote"),
    MESH("Malla"),
    CARAFE("Garrafa"),
    PIECE("Pieza"),
    BOX("Caja"),
    BOTTLE("Botella"),
    BUNCH("Manojo"),
    CAN("Lata"),
    BULK("A granel"),
    COMMIT("Compromiso"),
    RESIGN("Renuncia"),
    COMMIT_MANGOES("CompMangos"),
    COMMIT_AVOCADOS("CompAguacates");

    fun nameAbr(): String {
        return when (this) {
            COMMIT -> "Ecocesta"
            COMMIT_MANGOES -> "C.Mangos"
            COMMIT_AVOCADOS -> "C.Aguacat"
            else -> this.value
        }
    }

    companion object {
        fun sharedContainers() = listOf(
            PACKET, JAR, MESH, CARAFE, PIECE, BOX, BOTTLE, BUNCH, CAN, BULK
        )

        fun forMainProducers() = sharedContainers() + listOf(COMMIT, RESIGN)

        fun forTropicalProducer() = listOf(COMMIT_MANGOES, COMMIT_AVOCADOS)
    }
}
