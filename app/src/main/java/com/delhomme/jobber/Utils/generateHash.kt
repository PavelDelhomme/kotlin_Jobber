package com.delhomme.jobber.Utils

import com.google.common.hash.Hashing
import java.nio.charset.StandardCharsets

fun generateHash(data: SyncableData): String {
    return Hashing.sha256().hashString(data.toString(), StandardCharsets.UTF_8).toString()
}
