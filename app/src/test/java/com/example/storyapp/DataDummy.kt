package com.example.storyapp

import com.example.storyapp.model.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                "photoUrl $i", "createAt $i", "name $i", "desc $i", i.toString(), "lon $i", "lat $i"
            )
            items.add(story)
        }
        return items
    }

}