package com.simplyapp.slave.data

import android.os.Handler
import android.os.Message
import com.simplyapp.slave.data.SnakeGame.Direction.EAST
import com.simplyapp.slave.data.SnakeGame.Direction.NORTH
import com.simplyapp.slave.data.SnakeGame.Direction.SOUTH
import com.simplyapp.slave.data.SnakeGame.Direction.WEST
import com.simplyapp.slave.data.SnakeGame.Mode.LOSE
import com.simplyapp.slave.data.SnakeGame.Mode.READY
import com.simplyapp.slave.data.SnakeGame.Mode.RUNNING
import timber.log.Timber
import java.util.ArrayList
import java.util.Random

class SnakeGame {

    companion object {
        var MOVE_LEFT = 0
        var MOVE_UP = 1
        var MOVE_DOWN = 2
        var MOVE_RIGHT = 3
        var MOVE_START = 4

        private val SPEED = 200L
    }

    var onCordUpdate: ((x: Int, y: Int, lite: Boolean) -> Unit)? = null
    var onStatusUpdate: ((update: GameUpdate) -> Unit)? = null

    private val SNAKE_BODY = true
    private val SNAKE_HEAD = true
    private val WALL_VALUE = true
    private val APPLE_VALUE = true

    var introLoopCount = 0
    var reDrawApple = false

    /**
     * Everyone needs a little randomness in their life
     */
    private val RNG = Random()

    enum class Mode {
        READY, RUNNING, LOSE
    }

    private var gameState = READY

    enum class Direction {
        NORTH, SOUTH, EAST, WEST;
    }

    private var currentDirection = NORTH
    private var nextDirection = NORTH

    private var score: Int = 0
    private var moveDelay: Long = SPEED // speed

    /**
     * lastMoved: Tracks the absolute time when the snake last moved, and is used to determine if a
     * move should be made based on moveDelay.
     */
    private var lastMoved: Long = 0

    private var statusText: String? = null

    internal var xTileCount = 32
    internal var yTileCount = 32

    /**
     * snekTheDangerNoddle: A list of Coordinates that make up the snake's body
     * appleList: The location of the apples the snake craves.
     */
    private val snekTheDangerNoddle = ArrayList<Coordinate>(900)
    private val appleList = ArrayList<Coordinate>(12)

    private val redrawHandler = RefreshHandler()

    internal inner class RefreshHandler : Handler() {

        override fun handleMessage(msg: Message) {
            this@SnakeGame.updateGame()
        }

        fun sleepForMillis(delayMillis: Long) {
            this.removeMessages(0)
            sendMessageDelayed(obtainMessage(0), delayMillis)
        }
    }

    init {
        updateGame()
    }

    private fun initNewGame() {
        snekTheDangerNoddle.clear()
        appleList.clear()


        snekTheDangerNoddle.add(Coordinate(15, 12))
        snekTheDangerNoddle.add(Coordinate(16, 12))
        snekTheDangerNoddle.add(Coordinate(16, 13))
        snekTheDangerNoddle.add(Coordinate(16, 14))
        snekTheDangerNoddle.add(Coordinate(16, 15))
        snekTheDangerNoddle.add(Coordinate(16, 16))
        nextDirection = WEST

        //apples to start with
        for (i in 0 until 3) {
            addRandomApple()
        }
        reDrawApple = true

        moveDelay = SPEED
        score = 0
    }

    /**
     * Given a ArrayList of coordinates, we need to flatten them into an array of ints before we can
     * stuff them into a map for flattening and storage.
     *
     * @param cvec : a ArrayList of Coordinate objects
     * @return : a simple array containing the x/y values of the coordinates as
     * [x1,y1,x2,y2,x3,y3...]
     */
    //    private int[] coordArrayListToArray(ArrayList<Coordinate> cvec) {
    //        int[] rawArray = new int[cvec.size() * 2];
    //
    //        int i = 0;
    //        for (Coordinate c : cvec) {
    //            rawArray[i++] = c.x;
    //            rawArray[i++] = c.y;
    //        }
    //
    //        return rawArray;
    //    }

    /**
     * Save game state so that the user does not lose anything if the game process is killed while
     * we are in the background.
     *
     * @return a Bundle with this view's state
     */
    //    public Bundle saveState() {
    //        Bundle map = new Bundle();
    //
    //        map.putIntArray("appleList", coordArrayListToArray(appleList));
    //        map.putInt("currentDirection", Integer.valueOf(currentDirection));
    //        map.putInt("nextDirection", Integer.valueOf(nextDirection));
    //        map.putLong("moveDelay", Long.valueOf(moveDelay));
    //        map.putLong("Score", Long.valueOf(Score));
    //        map.putIntArray("snekTheDangerNoddle", coordArrayListToArray(snekTheDangerNoddle));
    //
    //        return map;
    //    }

    /**
     * Given a flattened array of ordinate pairs, we reconstitute them into a ArrayList of
     * Coordinate objects
     *
     * @param rawArray : [x1,y1,x2,y2,...]
     * @return a ArrayList of Coordinates
     */
    //    private ArrayList<Coordinate> coordArrayToArrayList(int[] rawArray) {
    //        ArrayList<Coordinate> coordArrayList = new ArrayList<Coordinate>();
    //
    //        int coordCount = rawArray.length;
    //        for (int index = 0; index < coordCount; index += 2) {
    //            Coordinate c = new Coordinate(rawArray[index], rawArray[index + 1]);
    //            coordArrayList.add(c);
    //        }
    //        return coordArrayList;
    //    }

    /**
     * Restore game state if our process is being relaunched
     *
     * @param icicle a Bundle containing the game state
     */
    //    public void restoreState(Bundle icicle) {
    //        setMode(PAUSE);
    //
    //        appleList = coordArrayToArrayList(icicle.getIntArray("appleList"));
    //        currentDirection = icicle.getInt("currentDirection");
    //        nextDirection = icicle.getInt("nextDirection");
    //        moveDelay = icicle.getLong("moveDelay");
    //        Score = icicle.getLong("Score");
    //        snekTheDangerNoddle = coordArrayToArrayList(icicle.getIntArray("snekTheDangerNoddle"));
    //    }

    /**
     * Handles snake movement triggers from Snake Activity and moves the snake accordingly. Ignore
     * events that would cause the snake to immediately turn back on itself.
     *
     * @param input The desired input of movement
     */

    fun moveSnake(input: Int) {

        if (input == MOVE_UP) {
            if (currentDirection != SOUTH) {
                nextDirection = NORTH
            }
            return
        }

        if (input == MOVE_DOWN) {
            if (currentDirection != NORTH) {
                nextDirection = SOUTH
            }
            return
        }

        if (input == MOVE_LEFT) {
            if (currentDirection != EAST) {
                nextDirection = WEST
            }
            return
        }

        if (input == MOVE_RIGHT) {
            if (currentDirection != WEST) {
                nextDirection = EAST
            }
            return
        }

        if (input == MOVE_START) {
            if ((gameState == READY) or (gameState == LOSE)) {
                /*
                 * At the beginning of the game, or the end of a previous one,
                 * we should start a new game if UP key is clicked.
                 */
                initNewGame()
                setMode(RUNNING)
                return
            }
        }
    }

    fun setMode(newMode: Mode) {
        val oldMode = gameState
        gameState = newMode

        if (newMode == RUNNING && oldMode != RUNNING) {
            clearTiles()
            statusText = ""
            updateWalls()
            onStatusUpdate?.invoke(Running(score, moveDelay.toInt()))
            return
        }
        when (newMode) {
            READY -> {
                statusText = "ready"
                introLoopCount = 0
                moveDelay = SPEED // reset game speed for ready animation
                onStatusUpdate?.invoke(Ready(score, moveDelay.toInt()))

            }
            LOSE -> {
                clearTiles()
                statusText = String.format("You Lost, Score %s \n Speed %s", score, moveDelay)
                loseLoopCount = 0
                moveDelay = SPEED / 2 // reset game speed for lose animation
                onStatusUpdate?.invoke(Lose(score, moveDelay.toInt()))

            }
            else -> {
            }
        }
    }


    private fun addRandomApple() {
        var newCoord: Coordinate? = null
        var found = false
        while (!found) {
            // Choose a new location for our apple
            val newX = 1 + RNG.nextInt(xTileCount - 2)
            val newY = 1 + RNG.nextInt(yTileCount - 2)
            newCoord = Coordinate(newX, newY)

            // Make sure it's not already under the snake
            var collision = false
            val snakelength = snekTheDangerNoddle.size
            for (index in 0 until snakelength) {
                if (snekTheDangerNoddle[index].equals(newCoord)) {
                    collision = true
                }
            }
            // if we're here and there's been no collision, then we have
            // a good location for an apple. Otherwise, we'll circle back
            // and try again
            found = !collision
        }
        if (newCoord == null) {
            Timber.e("When adding apple Somehow ended up with a null newCoord! ")
        } else {
            appleList.add(newCoord)
        }
    }

    /**
     * Handles the basic updateGame loop, checking to see if we are in the running state, determining if
     * a move should be made, updating the snake's location or animation.
     */
    fun updateGame() {
        val now = System.currentTimeMillis()
        if (now - lastMoved > moveDelay) {
            if (gameState == RUNNING) {
                updateSnake()
                updateApples()
                lastMoved = now

            } else if (gameState == LOSE) {
                updateLose()
            } else if (gameState == READY) {
                updateIntro()
            }
        }
        redrawHandler.sleepForMillis(moveDelay)
    }

    var loseLoopCount = 0

    fun updateIntro() {
        introFrames[introLoopCount].coordinateList.forEach { setTile(it.lit, it.x, it.y) }
        introLoopCount++
        if (introLoopCount >= introFrames.size) {
            introLoopCount = 0
        }
    }

    fun updateLose() {
        loseFrames[loseLoopCount].coordinateList.forEach { setTile(it.lit, it.x, it.y) }
        loseLoopCount++
        if (loseLoopCount >= loseFrames.size) {
            setMode(READY)
        }
    }

    fun clearTiles() {
        for (y in 0 until yTileCount - 1) {
            for (x in 0 until xTileCount) {
                setTile(false, x, y)
            }
        }
    }

    private fun updateWalls() {
        for (x in 0 until xTileCount) {
            setTile(WALL_VALUE, x, 0)
            setTile(WALL_VALUE, x, yTileCount - 1)
        }
        for (y in 1 until yTileCount - 1) {
            setTile(WALL_VALUE, 0, y)
            setTile(WALL_VALUE, xTileCount - 1, y)
        }
    }

    private fun updateApples() {
        if (reDrawApple) {
            for (c in appleList) {
                setTile(APPLE_VALUE, c.x, c.y)
            }
            reDrawApple = false
        }
    }

    private fun updateSnake() {
        var growSnake = false

        // Grab the snake by the head
        val head = snekTheDangerNoddle[0]
        var newHead = Coordinate(1, 1)

        currentDirection = nextDirection

        when (currentDirection) {
            EAST -> {
                newHead = Coordinate(head.x + 1, head.y)
            }
            WEST -> {
                newHead = Coordinate(head.x - 1, head.y)
            }
            NORTH -> {
                newHead = Coordinate(head.x, head.y - 1)
            }
            SOUTH -> {
                newHead = Coordinate(head.x, head.y + 1)
            }
        }

        // Collision detection----------------------------------------------------------------------

        // check if the head has hit where the walls are ment to be
        if (newHead.x < 1 || newHead.y < 1 || newHead.x > xTileCount - 2
                || newHead.y > yTileCount - 2) {
            setMode(LOSE)
            return

        }

        // Look for collisions with itself
        val snakelength = snekTheDangerNoddle.size
        for (snakeindex in 0 until snakelength) {
            val c = snekTheDangerNoddle[snakeindex]
            if (c.equals(newHead)) {
                setMode(LOSE)
                return
            }
        }

        // Look for apple collision with new head
        for (appleindex in 0 until appleList.size) {
            val currentApple = appleList[appleindex]
            if (currentApple.equals(newHead)) {
                appleList.remove(currentApple)
                addRandomApple()

                score++
                //ADD SPEED 10%
                moveDelay = (moveDelay * 0.95f).toLong()
                onStatusUpdate?.invoke(Running(score, moveDelay.toInt()))

                growSnake = true
                reDrawApple = true
            }
        }

        // Updating the new position of the snakes "head"
        snekTheDangerNoddle.add(0, newHead)
        setTile(SNAKE_HEAD, newHead.x, newHead.y)

        // removing the head if snake is not growing
        if (!growSnake) {
            setTile(false, snekTheDangerNoddle[snekTheDangerNoddle.size - 1].x, snekTheDangerNoddle[snekTheDangerNoddle.size - 1].y)
            snekTheDangerNoddle.removeAt(snekTheDangerNoddle.size - 1)

        }

    }

    fun setTile(tileindex: Boolean, x: Int, y: Int) {
        onCordUpdate?.invoke(x, y, tileindex)
    }

    private inner class Coordinate(var x: Int, var y: Int) {
        fun equals(other: Coordinate): Boolean = x == other.x && y == other.y
    }
}
