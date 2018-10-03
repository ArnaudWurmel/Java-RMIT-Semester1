package io.wurmel.assignement_1.Model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.net.URL

/**
 * Created by wurmel_a on 19/7/18.
 */

class   Trackable {
    private var id: Int = -1
    private var name: String = ""
    private var description: String = ""
    private var url: String = ""
    private var category: String = ""
    private var pictureUrl: String? = null

    constructor(tokens: List<String>) {
        id = tokens[0].toInt()
        name = tokens[1]
        description = tokens[2]
        url = tokens[3]
        category = tokens[4]
        if (tokens.size == 6) {
            setPictureUrl(tokens[5])
        }
    }

    constructor() {}

    fun getId(): Int = this.id

    fun setId(newId: Int) {
        this.id = newId
    }

    fun getName(): String = this.name

    fun setName(newName: String) {
        this.name = newName
    }

    fun getDescription(): String = this.description

    fun setDescription(newDescription: String) {
        this.description = newDescription
    }

    fun getUrl(): String = this.url

    fun setUrl(newUrl: String) {
        this.url = newUrl
    }

    fun getCategory(): String = this.category

    fun setCategory(newCategory: String) {
        this.category = newCategory
    }

    fun getPictureUrl(): String? = this.pictureUrl

    fun setPictureUrl(newPictureUrl: String) {
        this.pictureUrl = newPictureUrl
    }

    fun isPictureProvided(): Boolean = this.pictureUrl != null
}