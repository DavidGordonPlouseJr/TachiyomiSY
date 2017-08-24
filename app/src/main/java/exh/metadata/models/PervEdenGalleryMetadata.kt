package exh.metadata.models

import android.net.Uri

class PervEdenGalleryMetadata : SearchableGalleryMetadata() {
    var url: String? = null
    var thumbnailUrl: String? = null

    var title: String? = null
    var altTitles: MutableList<String> = mutableListOf()

    var artist: String? = null

    var type: String? = null

    var rating: Float? = null

    var status: String? = null

    var lang: String? = null

    override fun getTitles() = listOf(title).plus(altTitles).filterNotNull()

    private fun splitGalleryUrl()
            = url?.let {
        Uri.parse(it).pathSegments.filterNot(String::isNullOrBlank)
    }

    override fun galleryUniqueIdentifier() = splitGalleryUrl()?.let {
        "PERVEDEN-${lang?.toUpperCase()}-${it.last()}"
    }
}