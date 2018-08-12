package com.ascendantbrain.android.bakingapp.model

import android.database.Cursor
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.provider.BaseColumns

import com.ascendantbrain.android.bakingapp.provider.DatabaseContract.Step.CONTENT_URI
import com.ascendantbrain.android.bakingapp.provider.DatabaseContract.Step.INDEX_STEP_ID
import com.ascendantbrain.android.bakingapp.provider.DatabaseContract.Step.INDEX_DESCRIPTION_SHORT
import com.ascendantbrain.android.bakingapp.provider.DatabaseContract.Step.INDEX_DESCRIPTION
import com.ascendantbrain.android.bakingapp.provider.DatabaseContract.Step.INDEX_THUMBNAIL_URL
import com.ascendantbrain.android.bakingapp.provider.DatabaseContract.Step.INDEX_VIDEO_URL
import com.ascendantbrain.android.bakingapp.provider.DatabaseContract.Step.PROJECTION

class Step(var id: Int, var shortDescription: String, var description: String, var videoURL: String, var thumbnailURL: String) : Parcelable {
    constructor(source: Parcel) : this(
            source.readInt(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(shortDescription)
        writeString(description)
        writeString(videoURL)
        writeString(thumbnailURL)
    }

    companion object {
        fun fromCursor(c: Cursor): Step? {
            // ensure cursor is in a valid state
            if (c.isClosed) return null

            // retrieve data from cursor
            val stepId = c.getInt(INDEX_STEP_ID)
            val shortDescription = c.getString(INDEX_DESCRIPTION_SHORT)
            val description = c.getString(INDEX_DESCRIPTION)
            val thumbnailUrl = c.getString(INDEX_THUMBNAIL_URL)
            val videoUrl = c.getString(INDEX_VIDEO_URL)

            return Step(stepId, shortDescription, description, videoUrl, thumbnailUrl)
        }

        val contentUri: Uri
            get() = CONTENT_URI

        val projection: Array<String>
            get() = PROJECTION

        val sortOrder: String
            get() = BaseColumns._ID + " ASC"

        @JvmField
        val CREATOR: Parcelable.Creator<Step> = object : Parcelable.Creator<Step> {
            override fun createFromParcel(source: Parcel): Step = Step(source)
            override fun newArray(size: Int): Array<Step?> = arrayOfNulls(size)
        }
    }
}
