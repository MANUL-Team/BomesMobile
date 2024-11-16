package com.MANUL.Bomes.Utils

import okhttp3.Request

private val BomesRequest = Request.Builder().url("wss://bomes.ru:8000").build()
private val TestServerRequest = Request.Builder().url("ws://192.168.31.24:8000").build()

@JvmField
public val NowRequest = TestServerRequest
