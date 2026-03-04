package io.github.xuankaicat.napkat.core.bot

import io.github.xuankaicat.napkat.core.api.INapCatAccount
import io.github.xuankaicat.napkat.core.api.INapCatFile
import io.github.xuankaicat.napkat.core.api.INapCatGroup
import io.github.xuankaicat.napkat.core.api.INapCatKey
import io.github.xuankaicat.napkat.core.api.INapCatMessage
import io.github.xuankaicat.napkat.core.api.INapCatSystem

interface Bot
    : INapCatMessage, INapCatGroup, INapCatAccount, INapCatFile, INapCatSystem, INapCatKey