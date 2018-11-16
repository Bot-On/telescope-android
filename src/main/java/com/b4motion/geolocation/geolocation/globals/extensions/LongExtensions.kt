package com.b4motion.geolocation.geolocation.globals.extensions

import com.b4motion.geolocation.geolocation.globals.constants.DATE_ZULU_FORMAT
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Javier Camarero on 14/11/18.
 * QUADRAM MOBILE SOLUTIONS
 * jcamarero@quadram.mobi
 */
fun Long.toZuluFormat(): String {
    val formater = SimpleDateFormat(DATE_ZULU_FORMAT, Locale.getDefault())
    formater.timeZone = TimeZone.getTimeZone("UTC")
    return formater.format(this)
}

