package com.simplyapp.slave.data

sealed class GameUpdate()
data class Lose(val Score: Int, val Speed: Int) : GameUpdate()
data class Running(val Score: Int, val Speed: Int) : GameUpdate()
data class Ready(val Score: Int, val Speed: Int) : GameUpdate()