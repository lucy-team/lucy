package model

data class Member(
    val id: Long,
    val username: String,
    val avatarUrl: String?,
    val name: String,
)