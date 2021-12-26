package com.krygodev.singlesplanet.pairs

import com.krygodev.singlesplanet.model.User

sealed class PairsEvent {
    data class GetPairs(val value: User): PairsEvent()
    object GetUserData: PairsEvent()
}
