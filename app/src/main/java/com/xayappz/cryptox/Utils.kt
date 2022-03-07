package com.xayappz.cryptox

import com.xayappz.cryptox.models.Data
import java.util.*

class SearchingHM<T, U> : HashMap<String, Data?>() {
    override fun put(key: String, value: Data?): Data? {
        return super.put(key.lowercase(Locale.getDefault()), value)
    }

    override fun get(key: String): Data? {
        return super.get(key.lowercase(Locale.getDefault()))
    }
}
