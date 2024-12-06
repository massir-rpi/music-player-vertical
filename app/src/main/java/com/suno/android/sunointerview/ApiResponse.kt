package com.suno.android.sunointerview
import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName


@Serializable
data class ApiResponse(
    @SerialName("end") val end: Int?,
    @SerialName("page") val page: Int?,
    @SerialName("per_page") val perPage: Int?,
    @SerialName("songs") val songs: List<Song?>?,
    @SerialName("start") val start: Int?,
    @SerialName("total_pages") val totalPages: Int?,
    @SerialName("total_songs") val totalSongs: Int?
)

@Serializable
data class Song(
    @SerialName("audio_url") val audioUrl: String?,
    @SerialName("avatar_image_url") val avatarImageUrl: String?,
    @SerialName("created_at") val createdAt: String?,
    @SerialName("display_name") val displayName: String?,
    @SerialName("handle") val handle: String?,
    @SerialName("id") val id: String?,
    @SerialName("image_large_url") val imageLargeUrl: String?,
    @SerialName("image_url") val imageUrl: String?,
    @SerialName("is_handle_updated") val isHandleUpdated: Boolean?,
    @SerialName("is_liked") val isLiked: Boolean?,
    @SerialName("is_public") val isPublic: Boolean?,
    @SerialName("is_trashed") val isTrashed: Boolean?,
    @SerialName("is_video_pending") val isVideoPending: Boolean?,
    @SerialName("major_model_version") val majorModelVersion: String?,
    @SerialName("metadata") val metadata: Metadata?,
    @SerialName("model_name") val modelName: String?,
    @SerialName("play_count") val playCount: Int?,
    @SerialName("reaction") val reaction: Reaction?,
    @SerialName("status") val status: String?,
    @SerialName("title") val title: String?,
    @SerialName("upvote_count") val upvoteCount: Int?,
    @SerialName("user_id") val userId: String?,
    @SerialName("video_url") val videoUrl: String?
)

@Serializable
data class Metadata(
    @SerialName("concat_history") val concatHistory: List<ConcatHistory?>?,
    @SerialName("duration") val duration: Double?,
    @SerialName("prompt") val prompt: String?,
    @SerialName("tags") val tags: String?,
    @SerialName("type") val type: String?
)

@Serializable
data class Reaction(
    @SerialName("clip") val clip: String?,
    @SerialName("flagged") val flagged: Boolean?,
    @SerialName("play_count") val playCount: Int?,
    @SerialName("reaction_type") val reactionType: String?,
    @SerialName("skip_count") val skipCount: Int?,
    @SerialName("updated_at") val updatedAt: String?
)

@Serializable
data class ConcatHistory(
    @SerialName("continue_at") val continueAt: Double?,
    @SerialName("id") val id: String?
)