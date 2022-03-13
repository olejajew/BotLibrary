package com.doblack.bot_library.base.models

import java.io.InputStream

data class ImageInputStream(
    val inputStream: InputStream,
    val fileName: String
)