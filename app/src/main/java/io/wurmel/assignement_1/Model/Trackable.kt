package io.wurmel.assignement_1.Model

/**
 * Created by wurmel_a on 19/7/18.
 */

class   Trackable(tokens: List<String>) {
    private var id: Int = -1
    private var name: String = ""
    private var description: String = ""
    private var url: String = ""
    private var category: String = ""
    private var pictureUrl: String? = null

    init {

    }

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
}