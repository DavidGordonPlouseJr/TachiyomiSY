package exh.ui.metadata.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.databinding.DescriptionAdapterTsBinding
import eu.kanade.tachiyomi.ui.base.controller.withFadeTransaction
import eu.kanade.tachiyomi.ui.manga.MangaController
import eu.kanade.tachiyomi.util.system.getResourceColor
import exh.metadata.metadata.TsuminoSearchMetadata
import exh.ui.metadata.MetadataViewController
import java.util.Date
import kotlin.math.roundToInt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.view.clicks

class TsuminoDescriptionAdapter(
    private val controller: MangaController
) :
    RecyclerView.Adapter<TsuminoDescriptionAdapter.TsuminoDescriptionViewHolder>() {

    private val scope = CoroutineScope(Job() + Dispatchers.Main)
    private lateinit var binding: DescriptionAdapterTsBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TsuminoDescriptionViewHolder {
        binding = DescriptionAdapterTsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TsuminoDescriptionViewHolder(binding.root)
    }

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(holder: TsuminoDescriptionViewHolder, position: Int) {
        holder.bind()
    }

    inner class TsuminoDescriptionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            val meta = controller.presenter.meta
            if (meta == null || meta !is TsuminoSearchMetadata) return

            val genre = meta.category
            if (genre != null) {
                val pair = when (genre) {
                    "Doujinshi" -> Pair("#fc4e4e", R.string.doujinshi)
                    "Manga" -> Pair("#e78c1a", R.string.manga)
                    "Artist CG" -> Pair("#dde500", R.string.artist_cg)
                    "Game CG" -> Pair("#05bf0b", R.string.game_cg)
                    "Video" -> Pair("#14e723", R.string.video)
                    else -> Pair("", 0)
                }

                if (pair.first.isNotBlank()) {
                    binding.genre.setBackgroundColor(Color.parseColor(pair.first))
                    binding.genre.text = itemView.context.getString(pair.second)
                } else binding.genre.text = genre
            } else binding.genre.setText(R.string.unknown)

            binding.favorites.text = (meta.favorites ?: 0).toString()
            val drawable = itemView.context.getDrawable(R.drawable.ic_favorite_24dp)
            drawable?.setTint(itemView.context.getResourceColor(R.attr.colorAccent))
            binding.favorites.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

            binding.whenPosted.text = TsuminoSearchMetadata.TSUMINO_DATE_FORMAT.format(Date(meta.uploadDate ?: 0))

            binding.uploader.text = meta.uploader ?: itemView.context.getString(R.string.unknown)
            binding.pages.text = itemView.context.getString(R.string.num_pages, meta.length ?: 0)

            val name = when (((meta.averageRating ?: 100F) * 2).roundToInt()) {
                0 -> R.string.rating0
                1 -> R.string.rating1
                2 -> R.string.rating2
                3 -> R.string.rating3
                4 -> R.string.rating4
                5 -> R.string.rating5
                6 -> R.string.rating6
                7 -> R.string.rating7
                8 -> R.string.rating8
                9 -> R.string.rating9
                10 -> R.string.rating10
                else -> R.string.no_rating
            }
            binding.ratingBar.rating = meta.averageRating ?: 0F
            binding.rating.text = if (meta.userRatings != null) {
                itemView.context.getString(R.string.rating_view, itemView.context.getString(name), (meta.averageRating ?: 0F).toString(), meta.userRatings ?: 0L)
            } else {
                itemView.context.getString(R.string.rating_view_no_count, itemView.context.getString(name), (meta.averageRating ?: 0F).toString())
            }

            binding.moreInfo.clicks()
                .onEach {
                    controller.router?.pushController(
                        MetadataViewController(
                            controller.manga
                        ).withFadeTransaction()
                    )
                }
                .launchIn(scope)
        }
    }
}
