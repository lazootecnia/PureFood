package com.lazootecnia.purefood.ui.utils

import org.jetbrains.compose.resources.StringResource
import purefood.composeapp.generated.resources.Res
import purefood.composeapp.generated.resources.category_breakfast
import purefood.composeapp.generated.resources.category_lunch
import purefood.composeapp.generated.resources.category_desserts
import purefood.composeapp.generated.resources.category_dressing
import purefood.composeapp.generated.resources.category_sauce
import purefood.composeapp.generated.resources.category_jam
import purefood.composeapp.generated.resources.category_glutenfree

object CategoryLocalizer {
    private val categoryMap = mapOf(
        "breakfast" to Res.string.category_breakfast,
        "lunch" to Res.string.category_lunch,
        "desserts" to Res.string.category_desserts,
        "dressing" to Res.string.category_dressing,
        "sauce" to Res.string.category_sauce,
        "jam" to Res.string.category_jam,
        "glutenFree" to Res.string.category_glutenfree
    )

    fun getCategoryStringResource(categoryName: String): StringResource? {
        return categoryMap[categoryName]
    }

    fun isCategoryName(text: String): Boolean {
        return categoryMap.containsKey(text)
    }
}
