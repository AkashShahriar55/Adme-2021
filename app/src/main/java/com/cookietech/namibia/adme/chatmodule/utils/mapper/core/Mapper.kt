package com.cookietech.namibia.adme.chatmodule.utils.mapper.core

interface Mapper<I, O> {
    fun map(input: I): O
}