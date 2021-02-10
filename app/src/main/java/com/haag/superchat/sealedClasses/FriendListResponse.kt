package com.haag.superchat.sealedClasses

import com.haag.superchat.model.User

sealed class FriendListResponse {
    data class Success(val list: List<User>) : FriendListResponse()
    data class Failure(val message: String?, val throwable: Throwable?) : FriendListResponse()
}