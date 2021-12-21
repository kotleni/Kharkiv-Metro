package unicon.metro.kharkiv

import android.graphics.Color
import unicon.metro.kharkiv.types.Point
import unicon.metro.kharkiv.types.Vector
import unicon.metro.kharkiv.types.elements.BaseElement
import unicon.metro.kharkiv.types.elements.BranchElement
import unicon.metro.kharkiv.types.elements.TransElement

fun makeMapData() : ArrayList<BaseElement> {
    val mapData = ArrayList<BaseElement>()

    // зеленая ветка
    mapData.add(
        BranchElement(listOf(
        Point(
            Vector(173, 34), R.string.name_peremoga,
            R.string.desc_peremoga
        ),
        Point(
            Vector(190, 51), R.string.name_olecsiivka,
            R.string.desc_olecsiivka
        ),
        Point(
            Vector(209, 81), R.string.name_23serpnya,
            R.string.desc_23serpnya
        ),
        Point(
            Vector(209, 104), R.string.name_botanichniysad,
            R.string.desc_botanichniysad
        ),
        Point(
            Vector(209, 127), R.string.name_naukova,
            R.string.desc_naukova
        ),
        Point(
            Vector(209, 151), R.string.name_dergprom,
            R.string.desc_dergprom
        ),
        Point(
            Vector(255, 208), R.string.name_arh_beketova,
            R.string.desc_arh_beketova
        ),
        Point(
            Vector(278, 231), R.string.name_zah_ukr,
            R.string.desc_zah_ukr
        ),
        Point(
            Vector(302, 267), R.string.name_metrobud,
            R.string.desc_metrobud
        )
    ), Color.parseColor("#53ad63"))
    )

    // красная ветка
    mapData.add(
        BranchElement(listOf(
        Point(
            Vector(22, 279), R.string.name_holodnagora,
            R.string.desc_holodnagora
        ),
        Point(
            Vector(46, 256), R.string.name_pivdvokzal,
            R.string.desc_pivdvokzal
        ),
        Point(
            Vector(69, 233), R.string.name_centrarinok,
            R.string.desc_centrarinok
        ),
        Point(
            Vector(151, 208), R.string.name_maydankonst,
            R.string.desc_maydankonst
        ),
        Point(
            Vector(197, 243), R.string.name_prospect_gagarnina,
            R.string.desc_prospect_gagarnina
        ),
        Point(
            Vector(290, 278), R.string.name_sportivna,
            R.string.desc_sportivna
        ),
        Point(
            Vector(337, 290), R.string.name_zavimenimalisheva,
            R.string.desc_zavimenimalisheva
        ),
        Point(
            Vector(360, 313), R.string.name_turboatom,
            R.string.desc_turboatom
        ),
        Point(
            Vector(383, 337), R.string.name_palacsporta,
            R.string.desc_palacsporta
        ),
        Point(
            Vector(407, 359), R.string.name_armiyska,
            R.string.desc_armiyska
        ),
        Point(
            Vector(419, 383), R.string.name_imosmaselscogo,
            R.string.desc_imosmaselscogo
        ),
        Point(
            Vector(419, 407), R.string.name_traktorzavod,
            R.string.desc_traktorzavod
        ),
        Point(
            Vector(419, 430), R.string.name_industrial,
            R.string.desc_industrial
        )
    ), Color.parseColor("#e34146"))
    )

    // синяя ветка
    mapData.add(
        BranchElement(listOf(
        Point(
            Vector(407, 58), R.string.name_garoivwork,
            R.string.desc_garoivwork
        ),
        Point(
            Vector(384, 81), R.string.name_studenstka,
            R.string.desc_studenstka
        ),
        Point(
            Vector(359, 105), R.string.name_akadempavl,
            R.string.desc_akadempavl
        ),
        Point(
            Vector(336, 127), R.string.name_barabashova,
            R.string.desc_barabashova
        ),
        Point(
            Vector(313, 151), R.string.name_kievska,
            R.string.desc_kievska
        ),
        Point(
            Vector(280, 163), R.string.name_pushkinska,
            R.string.desc_pushkinska
        ),
        Point(
            Vector(196, 163), R.string.name_univer,
            R.string.desc_univer
        ),
        Point(Vector(167, 163), null, -1),
        Point(Vector(163, 167), null, -1),
        Point(
            Vector(162, 197), R.string.name_istormisei,
            R.string.desc_istormisei
        )
    ), Color.parseColor("#147bc7"))
    )

    // пересадки

    mapData.add(
        TransElement(
        Vector(151, 208),
        Vector(162, 197)
    )
    )

    mapData.add(
        TransElement(
        Vector(209, 151),
        Vector(196, 163)
    )
    )

    mapData.add(
        TransElement(
        Vector(290, 278),
        Vector(302, 267)
    )
    )

    return mapData
}