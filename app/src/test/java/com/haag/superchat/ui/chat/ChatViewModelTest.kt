package com.haag.superchat.ui.chat

import androidx.lifecycle.LiveData
import com.google.common.truth.Truth
import com.google.firebase.firestore.QuerySnapshot
import com.haag.superchat.repository.ChatsRepository
import com.haag.superchat.sealedClasses.FriendListResponse
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ChatViewModelTest {

    val repo: ChatsRepository = mockk()

    lateinit var sut: ChatViewModel

    var f: LiveData<FriendListResponse> = mockk()

    var q: QuerySnapshot = mockk()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        clearAllMocks()
        sut = ChatViewModel(repo)
    }


    // i want to check when api call has been a success to send Success Class through
    @Test
    fun test1() {
    }
}