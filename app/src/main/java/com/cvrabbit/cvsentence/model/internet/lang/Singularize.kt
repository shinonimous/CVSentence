/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.model.internet.lang

import java.util.*

/**
 * https://blog.anysense.co.jp/app/androidunittest/
 * https://developer.android.com/studio/test?hl=ja
 */
object Singularize {
    val _singlar_rules = listOf(
        Pair("(?i)(.)ae$", "$1a"),
        Pair("(?i)(.)itis$", "$1itis"),
        Pair("(?i)(.)eaux$", "$1eau"),
        Pair("(?i)(quiz)zes$", "$1"),
        Pair("(?i)(matr)ices$", "$1ix"),
        Pair("(?i)(ap|vert|ind)ices$", "$1ex"),
        Pair("(?i)(alias|status)es$", "$1"),
        Pair("(?i)(octop|vir)i$",  "$1us"),
        Pair("(?i)((ax)|(cris)|(test))es$", "$1is"),
        Pair("(?i)(shoe)s$", "$1"),
        Pair("(?i)(o)es$", "$1"),
        Pair("(?i)(bus)es$", "$1"),
        Pair("(?i)(m|l)ice$", "$1ouse"),
        Pair("(?i)(x|ch|ss|sh)es$", "$1"),
        Pair("(?i)(m)ovies$", "$1ovie"),
        Pair("(?i)(.)ombies$", "$1ombie"),
        Pair("(?i)(s)eries$", "$1eries"),
        Pair("(?i)([^aeiouy]|qu)ies$", "$1y"),
        // -f, -fe sometimes take -ves in the plural
        // (e.g., lives, wolves).
        Pair("([aeo]l)ves$", "$1f"),
        Pair("([^d]ea)ves$", "$1f"),
        Pair("arves$", "arf"),
        Pair("erves$", "erve"),
        Pair("([nlw]i)ves$", "$1fe"),
        Pair("(?i)([lr])ves$", "$1f"),
        Pair("([aeo])ves$", "$1ve"),
        Pair("(?i)(sive)s$", "$1"),
        Pair("(?i)(tive)s$", "$1"),
        Pair("(?i)(hive)s$", "$1"),
        Pair("(?i)([^f])ves$", "$1fe"),
        // -ses suffixes.
        Pair("(?i)(analy)ses$", "$1sis"),
        //Pair("(?i)((a)naly|(b)a|(d)iagno|(p)arenthe|(p)rogno|(s)ynop|(t)he)ses$", "$1$2sis"),
        Pair("(?i)(analy|ba|diagno|parenthe|progno|synop|the)ses$", "$1sis"),
        Pair("(?i)(.)opses$", "$1opsis"),
        Pair("(?i)(.)yses$", "$1ysis"),
        Pair("(?i)(h|d|r|o|n|b|cl|p)oses$", "$1ose"),
        Pair("(?i)(fruct|gluc|galact|lact|ket|malt|rib|sacchar|cellul)oses$", "$1ose"),
        Pair("(?i)(.)oses$", "$1osis"),
        // -a
        Pair("(?i)([ti])a$", "$1um"),
        Pair("(?i)(n)ews$", "$1ews"),
        Pair("(?i)([^s])s$", "$1") // don't make ss singularize to s.
    )

    val _singular_uninflected = setOf(
        "bison", "debris", "headquarters", "pincers", "trout",
        "bream", "diabetes", "herpes", "pliers", "tuna",
        "breeches", "djinn", "high-jinks", "proceedings", "whiting",
        "britches", "eland", "homework", "rabies", "wildebeest",
        "carp", "elk", "innings", "salmon",
        "chassis", "flounder", "jackanapes", "scissors",
        "christmas", "gallows", "mackerel", "series",
        "clippers", "georgia", "measles", "shears",
        "cod", "graffiti", "mews", "species",
        "contretemps", "mumps", "swine",
        "corps", "news", "swiss",
        // Custom added from MD&A corpus
        "api", "mae", "sae", "basis", "india", "media",
    )
    val _singular_uncountable = setOf(
        "advice", "equipment", "happiness", "luggage", "news", "software",
        "bread", "fruit", "information", "mathematics", "progress", "understanding",
        "butter", "furniture", "ketchup", "mayonnaise", "research", "water",
        "cheese", "garbage", "knowledge", "meat", "rice",
        "electricity", "gravel", "love", "mustard", "sand",
    )
    val _singular_ie = setOf(
        "alergie", "cutie", "hoagie", "newbie", "softie", "veggie",
        "auntie", "doggie", "hottie", "nightie", "sortie", "weenie",
        "beanie", "eyrie", "indie", "oldie", "stoolie", "yuppie",
        "birdie", "freebie", "junkie", "^pie", "sweetie", "zombie",
        "bogie", "goonie", "laddie", "pixie", "techie",
        "bombie", "groupie", "laramie", "quickie", "^tie",
        "collie", "hankie", "lingerie", "reverie", "toughie",
        "cookie", "hippie", "meanie", "rookie", "valkyrie",
    )
    val _singular_irregular = listOf(
        Pair("abuses", "abuse"),
        Pair("ads", "ad"),
        Pair("atlantes", "atlas"),
        Pair("atlases", "atlas"),
        Pair("analysis", "analysis"),
        Pair("beeves", "beef"),
        Pair("brethren", "brother"),
        Pair("children", "child"),
        Pair("children", "child"),
        Pair("corpora", "corpus"),
        Pair("corpuses", "corpus"),
        Pair("ephemerides", "ephemeris"),
        Pair("feet", "foot"),
        Pair("ganglia", "ganglion"),
        Pair("geese", "goose"),
        Pair("genera", "genus"),
        Pair("genii", "genie"),
        Pair("graffiti", "graffito"),
        Pair("helves", "helve"),
        Pair("kine", "cow"),
        Pair("leaves", "leaf"),
        Pair("loaves", "loaf"),
        Pair("men", "man"),
        Pair("mongooses", "mongoose"),
        Pair("monies", "money"),
        Pair("moves", "move"),
        Pair("mythoi", "mythos"),
        Pair("numena", "numen"),
        Pair("occipita", "occiput"),
        Pair("octopodes", "octopus"),
        Pair("opera", "opus"),
        Pair("opuses", "opus"),
        Pair("our", "my"),
        Pair("oxen", "ox"),
        Pair("penes", "penis"),
        Pair("penises", "penis"),
        Pair("people", "person"),
        Pair("sexes", "sex"),
        Pair("soliloquies", "soliloquy"),
        Pair("teeth", "tooth"),
        Pair("testes", "testis"),
        Pair("trilbys", "trilby"),
        Pair("turves", "turf"),
        Pair("zoa", "zoon"),
    )
    val _plural_prepositions = setOf(
        "about", "before", "during", "of", "till",
        "above", "behind", "except", "off", "to",
        "across", "below", "for", "on", "under",
        "after", "beneath", "from", "onto", "until",
        "among", "beside", "in", "out", "unto",
        "around", "besides", "into", "over", "upon",
        "at", "between", "near", "since", "with",
        "athwart", "betwixt", "beyond", "but", "by"
    )

    fun singularize(word: String): String{
        // OK Recurse compound words (e.g. mothers-in-law).
        if ("-" in word) {
            val w = word.split("-").toTypedArray()
            if ((w.size > 1) and (w[1] in _plural_prepositions)) {
                val head = singularize(w[0])
                var tail = ""
                for (i in 1 until w.size) { tail += ("-" + w[i]) }
                return head + tail
            }
        }
        // OK dogs' => dog's
        if (word.endsWith("'")){
            return singularize(word.dropLast(1)) + "'s"
        }

        val w = word.toLowerCase(Locale.ENGLISH)

        // OK Check Singular Uninfected Word
        for (x in _singular_uninflected) {
            if (x.endsWith(w)) {
                return word
            }
        }
        // OK Check Uncountable Word
        for (x in _singular_uncountable) {
            if (x.endsWith(w)) {
                return word
            }
        }
        // OK Check Singular ends with "ie"
        for (x in _singular_ie) {
            if (x.startsWith("^")) {
                if (w.matches((x + "s$").toRegex())) {
                    return word.dropLast(1)
                }
            } else {
                if (w.endsWith(x + "s")) {
                    return word.dropLast(1)
                }
            }
        }
        // OK Check Irregular
        for (x in _singular_irregular) {
            if (w.endsWith(x.first)) {
                return word.replace(("(?i)" + x.first + '$').toRegex(), x.second)
            }
        }
        // Check Singular Rules
        for (i in _singlar_rules) {
            //val m = w.matches(i.first.toRegex())
            val inflection = i.second
            val m = i.first.toRegex().find(w)
            if (m != null) {
                println("小文字変換したword:" + w + "  とregex化するsuffix:" + i.first + "  のマッチは" + m.groupValues[0])
                return word.replace(i.first.toRegex(), inflection)
            }
        }

        // OK When Applicable Rules Not Found
        return word
    }
}