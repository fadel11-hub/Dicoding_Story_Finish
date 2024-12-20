package com.dicoding.dicodingstory

import com.dicoding.dicodingstory.data.response.ItemStoryResponse

object DataDummy {

    fun generateDummyQuoteResponse(): List<ItemStoryResponse> {
        val items: MutableList<ItemStoryResponse> = arrayListOf()
        for (i in 0..100) {
            val quote = ItemStoryResponse(
                i.toString(),
                "name + $i",
                "description $i",
                "photoUrl $i",
                "createdAt $i",
            )
            items.add(quote)
        }
        return items
    }
}