package com.b4motion.geolocation.geolocation.globals.extensions

import android.util.Log
import com.b4motion.geolocation.geolocation.core.Telescope


/**
 * Created by Javier Camarero on 26/10/18.
 * QUADRAM MOBILE SOLUTIONS
 * jcamarero@quadram.mobi
 */

fun log(text: String, tag: String = "GeoB4") {
    if (Telescope.isLogEnabled)
        Log.d(tag, text)
}

