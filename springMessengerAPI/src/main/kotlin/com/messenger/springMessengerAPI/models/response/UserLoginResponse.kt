package com.messenger.springMessengerAPI.models.response

import com.fasterxml.jackson.annotation.JsonProperty

data class UserLoginResponse(
        @JsonProperty("SuccessfulLogin")
        val successfulLogin: Boolean,

        @JsonProperty("UserId")
        val userId: Int?
)
