package com.simplyapp.slave.ui

import android.os.Bundle
import android.view.KeyEvent
import com.simplyapp.data.data.ControlMove
import com.simplyapp.slave.R
import com.simplyapp.slave.arch.BaseActivity
import com.simplyapp.slave.data.Lose
import com.simplyapp.slave.data.Ready
import com.simplyapp.slave.data.Running
import com.simplyapp.slave.data.SnakeGame
import com.simplyapp.slave.data.SnakeGame.Companion.MOVE_DOWN
import com.simplyapp.slave.data.SnakeGame.Companion.MOVE_LEFT
import com.simplyapp.slave.data.SnakeGame.Companion.MOVE_RIGHT
import com.simplyapp.slave.data.SnakeGame.Companion.MOVE_START
import com.simplyapp.slave.data.SnakeGame.Companion.MOVE_UP
import kotlinx.android.synthetic.main.activity_main.*

class SlaveActivity : BaseActivity<SlaveMvp.Presenter>(), SlaveMvp.View {

    private var mSnakeGame: SnakeGame = SnakeGame()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter = SlavePresenter(this)
        presenter.onCreate()

        mSnakeGame.onCordUpdate = { x, y, lit ->
            presenter.updateXY(x, y, lit)
        }

        mSnakeGame.onStatusUpdate = { update ->
            when (update) {
                is Running -> {
                    score_text.text = "You have ${update.Score} points\n Speed ${(200 - update.Speed) / 2}%"
                }
                is Ready -> {
                    score_text.text = "Ready To Go Press Enter"
                }
                is Lose -> {
                    score_text.text = "You Lost, with ${update.Score} score \n Speed ${(200 - update.Speed) / 2}%"

                }

            }
        }
        score_text.text = "Ready To Go Press Enter"

    }

    override fun onKeyDown(keyCode: Int, msg: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> mSnakeGame.moveSnake(MOVE_UP)
            KeyEvent.KEYCODE_DPAD_RIGHT -> mSnakeGame.moveSnake(MOVE_RIGHT)
            KeyEvent.KEYCODE_DPAD_DOWN -> mSnakeGame.moveSnake(MOVE_DOWN)
            KeyEvent.KEYCODE_DPAD_LEFT -> mSnakeGame.moveSnake(MOVE_LEFT)
            KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_NUMPAD_ENTER -> mSnakeGame.moveSnake(MOVE_START)
            KeyEvent.KEYCODE_F1 -> {
                mSnakeGame.setMode(SnakeGame.Mode.LOSE)
                presenter.resetDisplay()
            }
        }

        return super.onKeyDown(keyCode, msg)
    }

    override fun setIP(ipAddress: String?) {
        slave_ip_textview.text = ipAddress
    }

    override fun setStatus(status: String) {
        slave_status_textview.text = status
    }

    override fun setControl(controlMove: ControlMove) {
        when (controlMove) {
            ControlMove.UP -> mSnakeGame.moveSnake(MOVE_UP)
            ControlMove.RIGHT -> mSnakeGame.moveSnake(MOVE_RIGHT)
            ControlMove.DOWN -> mSnakeGame.moveSnake(MOVE_DOWN)
            ControlMove.LEFT -> mSnakeGame.moveSnake(MOVE_LEFT)
            ControlMove.START -> mSnakeGame.moveSnake(MOVE_START)

        }
    }

}
