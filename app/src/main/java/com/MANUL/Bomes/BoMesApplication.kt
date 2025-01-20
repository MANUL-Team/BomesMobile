package com.MANUL.Bomes

import android.app.Application
import com.MANUL.Bomes.Utils.BoMesWebSocket

class BoMesApplication: Application() {
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        BoMesWebSocket.close()
    }
}