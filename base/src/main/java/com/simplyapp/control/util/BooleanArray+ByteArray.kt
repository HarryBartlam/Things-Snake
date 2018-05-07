package com.simplyapp.control.util

import java.util.Arrays
import java.util.BitSet

fun ByteArray.toBooleanArray():BooleanArray {
    val bits = BitSet.valueOf(this)
    val bools = BooleanArray(this.size * 8)
    var i = bits.nextSetBit(0)
    while (i != -1) {
        bools[i] = true
        i = bits.nextSetBit(i + 1)
    }
    return bools
}

fun BooleanArray.toByteArray():ByteArray {
    val bits = BitSet(this.size)
    this.indices
            .filter { this[it] }
            .forEach { bits.set(it) }
    val bytes = bits.toByteArray()
    return if (bytes.size * 8 >= this.size) {
        bytes
    } else {
        Arrays.copyOf(bytes, this.size / 8 + if (this.size % 8 == 0) 0 else 1)
    }
}
