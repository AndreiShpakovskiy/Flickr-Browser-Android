package com.shpakovskiy.flickrbrowser

import android.os.Bundle
import com.shpakovskiy.flickrbrowser.photo.Photo
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.activity_photo_details.*
import kotlinx.android.synthetic.main.browse_layout.*
import kotlinx.android.synthetic.main.content_photo_details.*

class PhotoDetailsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_details)
        setSupportActionBar(toolbar)

        activateToolbar(true)

        val photo = intent.getParcelableExtra(PHOTO_TRANSFER) as Photo

        photo_title.text = resources.getString(R.string.photo_title_text, photo.title)
        photo_tags.text = resources.getString(R.string.photo_tags_text, photo.tags)
        photo_author.text = resources.getString(R.string.photo_author_text, photo.author)

        Picasso.with(this).load(photo.link)
            .error(R.drawable.default_placeholder_image)
            .placeholder(R.drawable.default_placeholder_image)
            .into(photo_image)
    }
}