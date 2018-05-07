package com.simplyapp.data.data

enum class ControlMove {
    UP, DOWN, LEFT, RIGHT, START;

    companion object {
        fun fromByte(bytes: ByteArray): ControlMove? {
            val byte = bytes[0]
            for (enum in values()) {
                if (enum.ordinal == byte.toInt())
                    return enum
            }
            return null;  //or throw exception
        }

        fun toByteArray(move: ControlMove): ByteArray = ByteArray(1) {move.ordinal.toByte()}
    }
}