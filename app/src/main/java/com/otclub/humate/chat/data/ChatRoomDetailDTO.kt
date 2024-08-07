package com.otclub.humate.chat.data

data class ChatRoomDetailDTO(
    val chatRoomId : String,
    val participateId : String,
    val postId : String,
    val postTitle : String,
    val matchDate : String,
    val matchBranch : String,
    val isClicked : Int,
    val isMatched : Int,
    val targetNickname : String,
    val targetParticipateId : String,
    val targetProfileImgUrl : String? = null,
    val targetIsClicked : Int
)