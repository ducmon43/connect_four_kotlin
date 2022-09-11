package connectfour

fun main() {
    println("Connect Four")
    val name = Name()
    val board = Board()
    board.setSize()
    val game = Game(name.playerOne, name.playerTwo, board.rows, board.columns)
    //println("${name.playerOne} VS ${name.playerTwo}\n" + "${board.rows} X ${board.columns} board")
    //board.printBoard()
    game.play()
}

class Game(val playerOne:String, val playerTwo:String, val rows:Int, val columns:Int) {

    private var playerTurn = 0
    private var player: String = ""
    private var playerSymbol: String = ""
    val builder = StringBuilder()
    var longString:String = ""
    var sym: String = ""
    val PLAYER_ONE_SYMBOL = "o"
    val PLAYER_TWO_SYMBOL = "*"
    val VALID_REGEX = Regex("^\\d+\$")
    val END = Regex("end")
    var WIN_HORIZONTAL = Regex("$sym{4}")
    var WIN_VERTICAL = Regex("(?:$sym.{${columns - 1}}){3}$sym")
    var WIN_DIAGONAL = Regex("((?:$sym.{$columns}){3}$sym)|((?:$sym.{${columns - 2}}){3}$sym)")
    var playerInput: String = ""
    var gamesNumber: String = ""
    val gameTable = MutableList(rows) { MutableList(columns) { "0" } }
    val DUMMY = Regex("0")
    var count = 0

    fun play() {
        while (true) {
            println("Do you want to play single or multiple games?\n" + "For a single game, input 1 or press Enter\n" + "Input a number of games:")
            gamesNumber = readln()
            when {
                Regex("()|1").matches(gamesNumber) -> { //input 1 or empty
                    singlePlay()
                    break
                }
                Regex("(?!0)\\d+").matches(gamesNumber) -> {
                    multiplePlay()
                    break
                }
                else -> println("Invalid input")
            }

        }
    }
    fun singlePlay() {
        println("$playerOne VS $playerTwo\n" + "$rows X $columns board\n" + "Single game")
        printBoard()
        game@ while (true) {
            nextTurn()
            if (checkInput() == false) break
            drawBoard()
            when (checkBoard()) {
                "win" -> {
                    println("Player $player won")
                    break
                }
                "full" -> {
                    println("It is a draw")
                    break
                }
                "next" -> continue
            }
        }
        println("Game over!")
    }

    fun multiplePlay() {
        var playerOneScore = 0
        var playerTwoScore = 0
        println("$playerOne VS $playerTwo\n" + "$rows X $columns board\n" + "Total $gamesNumber games")
        top@for (i in 1..gamesNumber.toInt()) {
            println("Game #$i")
            resetBoard()
            printBoard()
            game@ while (true) {
                nextTurn()
                if (checkInput() == false) break@top
                drawBoard()
                when (checkBoard()) {
                    "win" -> {
                        println("Player $player won")
                        when (player) {
                            playerOne -> playerOneScore = playerOneScore + 2
                            playerTwo -> playerTwoScore = playerTwoScore + 2
                        }
                        println("Score\n" + "$playerOne: $playerOneScore $playerTwo: $playerTwoScore")
                        break
                    }
                    "full" -> {
                        println("It is a draw")
                        playerOneScore++
                        playerTwoScore++
                        println("Score\n" + "$playerOne: $playerOneScore $playerTwo: $playerTwoScore")
                        break
                    }
                    "next" -> continue //do the next turn
                }
            }
        }
        println("Game over!")
    }

    fun nextTurn() {
        if (playerTurn % 2 == 0) {
            player = playerOne
            playerSymbol = PLAYER_ONE_SYMBOL
            sym = "o"
        } else {
            player = playerTwo
            playerSymbol = PLAYER_TWO_SYMBOL
            sym = "\\*"
        }
        WIN_HORIZONTAL = Regex("$sym{4}")
        WIN_VERTICAL = Regex("(?:$sym.{${columns - 1}}){3}$sym")
        WIN_DIAGONAL = Regex("((?:$sym.{$columns}){3}$sym)|((?:$sym.{${columns - 2}}){3}$sym)")
        playerTurn++
    }

    fun checkInput(): Boolean {
        first@ while (true) {
            println("$player's turn:")
            playerInput = readln().toString()
            when {
                (VALID_REGEX.matches(playerInput)) -> {
                    if (playerInput.toInt() in 1..columns) {
                        if (gameTable[0][playerInput.toInt() - 1] == "0") { //column is NOT full condition
                            //set new data point to Data list
                            for (i in rows - 1 downTo 0) { //check empty cell from the bottom
                                if (gameTable[i][playerInput.toInt() - 1] == "0") { //if the cell is empty
                                    gameTable[i].set(playerInput.toInt() - 1, playerSymbol)
                                    break@first //back to play loop, next turn
                                }
                            }
                        } else { //column is full
                            println("Column $playerInput is full")
                            continue
                        }

                    } else {
                        println("The column number is out of range (1 - $columns)")
                        continue
                    }
                }
                END.matches(playerInput) -> {
                    return false //break the play loop and end the game
                }
                else -> {
                    println("Incorrect column number")
                    continue
                }
            }
        }
        return true //continue the play loop and next step
    }

    fun printBoard() {
        for (i in 1..columns) {
            print(" $i")
        }
        println()
        for (i in 1..rows) {
            for (j in 1..columns) print("║ ")
            println("║")
        }
        print("╚")
        for (i in 1 until columns) print("═╩")
        println("═╝")
    }

    fun drawBoard() {
        //take data from 2D list
        //print Board with o and * accordingly
        for (i in 1..columns) {
            print(" $i")
        }
        println()
        for (i in 0..rows - 1) {
            print("║")
            print(gameTable[i].joinToString("║").replace("0", " "))
            println("║")
            //for (j in 1..columns) print("║ ")
            //println("║")
        }
        print("╚")
        for (i in 1 until columns) print("═╩")
        println("═╝")
    }

    fun checkBoard(): String {
        builder.clear()
        for (i in gameTable.indices) {
            builder.append(gameTable[i].joinToString("")) //add all the rows of game table data using String Builder
            if (WIN_HORIZONTAL.containsMatchIn(gameTable[i].joinToString(""))) return "win" //check Horizontal winning condition
        }
        longString = builder.toString() //join game Data to one long string
        if (WIN_VERTICAL.containsMatchIn(longString)) return "win"
        //WIN_DIAGONAL.containsMatchIn(longString)
        for (row in 0..rows - 4) {
            for (col in 0..columns - 4) {
                if (gameTable[row][col] == playerSymbol) {
                    count = 1
                    for (i in 1..3) {
                        if (gameTable[row + i][col + i] == playerSymbol) count++
                        if (count == 4) return "win"
                    }
                }
            }
        } //check for primary diagonal win
        for (row in 0..rows - 4) {
            for (col in 3..columns - 1) {
                if (gameTable[row][col] == playerSymbol) {
                    count = 1
                    for (i in 1..3) {
                        if (gameTable[row + i][col - i] == playerSymbol) count++
                        if (count == 4) return "win"
                    }
                }
            }
        } //check for secondary diagonal win
        if (!(DUMMY.containsMatchIn(longString))) return "full"

        return "next"
    }

    fun resetBoard() {
        for (i in gameTable.indices) {
            gameTable[i].clear()
            gameTable[i].addAll(MutableList(columns) { "0" })
        }
    }
}
class Name {
        var playerOne = "unknown"
        var playerTwo = "unknown"

        init {
            println("First player's name:")
            playerOne = readln()
            println("Second player's name:")
            playerTwo = readln()
        }
    }

class Board(val _rows: Int = 6, val _columns: Int = 7) {
        var rows: Int = 6
        var columns: Int = 7
        var error = 0

        fun setSize() {
            val regex = Regex("\\s*\\d+\\s*[xX]\\s*\\d+\\s*")
            loop@ while (true) {
                println("Set the board dimensions (Rows x Columns)\n" + "Press Enter for default (6 x 7)")
                val inputSize = readln()
                if (inputSize.isEmpty()) {
                    rows = _rows
                    columns = _columns
                    break
                } else when (regex.matches(inputSize)) {
                    true -> {
                        val boardSize = inputSize.lowercase().split("x")
                        rows = boardSize[0].trim().toInt()
                        columns = boardSize[1].trim().toInt()
                        error = 0
                        if (rows !in 5..9) {
                            println("Board rows should be from 5 to 9")
                            error++
                        }
                        if (columns !in 5..9) {
                            println("Board columns should be from 5 to 9")
                            error++
                        }
                        if (error == 0) break@loop
                    }
                    false -> {
                        println("Invalid input")
                    }
                }
            }
        }

        fun printBoard() {
            for (i in 1..columns) {
                print(" $i")
            }
            println()
            for (i in 1..rows) {
                for (j in 1..columns) print("║ ")
                println("║")
            }
            print("╚")
            for (i in 1 until columns) print("═╩")
            println("═╝")
        }

    }
