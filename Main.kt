package bullscows

private const val BULL = "bull"
private const val COW = "cow"

private fun validDigits(digits: Int): Boolean {
    return digits <= 36
}

private fun validNumSymbols(numPossibleSymbols: Int, digits: Int) = numPossibleSymbols >= digits

fun main() {
    println("Input the length of the secret code:")
    val inputLength = readln()

    val digits = try {
        inputLength.toInt()
    } catch (e: NumberFormatException) {
        println("Error: \"$inputLength\" isn't a valid number.")
        return
    }

    if(digits <= 0) {
        println("Error: \"$digits\" isn't a valid number.")
        return
    }

    if (validDigits(digits).not()) {
        println("Error: maximum number of possible symbols in the code is 36 (0-9, a-z).")
        return
    }

    println("Input the number of possible symbols in the code:")
    val numPossibleSymbols = readln().toInt()

    if (numPossibleSymbols > 36) {
        println("Error: maximum number of possible symbols in the code is 36 (0-9, a-z).")
        return
    }

    if (validNumSymbols(numPossibleSymbols, digits).not()) {
        println("Error: it's not possible to generate a code with a length of $digits with $numPossibleSymbols unique symbols.")
        return
    }

    val possibleSymbols = generatePossibleSymbols(numPossibleSymbols)

    val secretCode = createSecretCode(digits, possibleSymbols)

    println(generateGameReadyStatement(digits, possibleSymbols))

    var turn = 1
    while (true) {
        println("Turn $turn:")
        val input = readln()
        val grade = grade(secretCode, input)
        println("Grade: %1s.".format(generateBullCowStr(grade)))
        if (grade[BULL] == secretCode.length) {
            println("Congratulations! You guessed the secret code.")
            break
        }
        turn++
    }
}

private fun generatePossibleSymbols(numPossibleSymbols: Int): List<Char> {
    return mutableListOf<Char>().apply {
        if(numPossibleSymbols <= 10) {
            addAll('0' until '0' + numPossibleSymbols)
        } else {
            addAll('0' .. '9')
        }

        if(numPossibleSymbols > 10) {
            addAll('a' until  'a' + numPossibleSymbols - 10)
        }
    }.toList()
}

private fun generateGameReadyStatement(digits: Int, possibleSymbols: List<Char>): String {
    return StringBuilder()
        .append("The secret is prepared: ")
        .append("*".repeat(digits))
        .apply {
            append("(")
            append("0")
            if(possibleSymbols.size in 2..10) {
                append("-")
                append(possibleSymbols.last())
            } else {
                append("-")
                append('9')
            }

            if(possibleSymbols.size > 10) {
                append(", ")
                append("a")
            }

            if(possibleSymbols.size > 11) {
                append("-")
                append(possibleSymbols.last())
            }
            append(")")
        }
        .appendLine()
        .append("Okay, let's start a game!")
        .toString()

}

fun createSecretCode(digits: Int, possibleSymbols: List<Char>): String {
    val secretCodeSet = mutableSetOf<Char>()

    loop@ while (true) {
        val pseudoRandomNumber = possibleSymbols.random()

        if (secretCodeSet.contains(pseudoRandomNumber)) {
            continue
        }

        secretCodeSet.add(pseudoRandomNumber)

        if (secretCodeSet.size == digits) {
            break@loop
        }
    }
    return secretCodeSet.joinToString("")
}

//9305 - 9999
fun grade(secretCode: String, input: String): Map<String, Int> {
    val result = mutableMapOf(
        BULL to 0,
        COW to 0
    )

    //4bulls
    if (secretCode == input) {
        result[BULL] = secretCode.length
        return result
    }

    for (i in secretCode.indices) {
        if (secretCode[i] == input[i]) {
            result[BULL] = result[BULL]!! + 1
        } else if (secretCode.contains(input[i])) {
            result[COW] = result[COW]!! + 1
        }
    }

    return result
}

fun generateBullCowStr(grade: Map<String, Int>): String {
    val bulls = grade[BULL]!!
    val cows = grade[COW]!!

    if (bulls == 0 && cows == 0) {
        return "None"
    }

    val bullsStr = if (bulls != 0) {
        "$bulls bull${if (bulls > 1) "s" else ""}"
    } else {
        ""
    }

    val cowsStr = if (cows != 0) {
        "$cows cow${if (cows > 1) "s" else ""}"
    } else {
        ""
    }

    return "$bullsStr ${if (bullsStr.isNotEmpty() && cowsStr.isNotEmpty()) "and" else ""} $cowsStr".trim()
}