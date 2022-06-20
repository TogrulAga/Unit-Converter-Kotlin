package converter

import java.util.*

fun main() {
    Converter.convert()
}

object Converter {
    // Pattern for parsing input
    private val pattern = Regex("""^([-0-9.]+)\s+(\w+|degree \w+|degrees \w+)\s+\w+\s+(\w+|degree \w+|degrees \w+)$""")

    // List of all units
    private val distanceUnits = initDistanceUnits()
    private val weightUnits = initWeightUnits()
    private val temperatureUnits = initTemperatureUnits()


    private fun initDistanceUnits(): List<String> {
        val units = mutableListOf<String>()

        for (unit in DistanceUnit.values()) {
            units.addAll(unit.acceptableNames())
        }

        return units
    }

    private fun initWeightUnits(): List<String> {
        val units = mutableListOf<String>()

        for (unit in WeightUnit.values()) {
            units.addAll(unit.acceptableNames())
        }

        return units
    }

    private fun initTemperatureUnits(): List<String> {
        val units = mutableListOf<String>()

        for (unit in TemperatureUnit.values()) {
            units.addAll(unit.acceptableNames().map { it.lowercase(Locale.getDefault()) })
        }

        return units
    }

    private fun getUnitFromName(name: String): String {
        when (name) {
            in distanceUnits -> {
                for (unit in DistanceUnit.values()) {
                    if (unit.acceptableNames().contains(name)) {
                        return unit.plural()
                    }
                }
            }
            in weightUnits -> {
                for (unit in WeightUnit.values()) {
                    if (unit.acceptableNames().contains(name)) {
                        return unit.plural()
                    }
                }
            }
            in temperatureUnits -> {
                for (unit in TemperatureUnit.values()) {
                    if (unit.acceptableNames().map { it.lowercase() }.contains(name)) {
                        return unit.plural()
                    }
                }
            }
            else -> return "???"
        }

        return "???"
    }

    fun convert() {
        while (true) {
            print("Enter what you want to convert (or exit): ")
            val input = readln().lowercase()
            if (input == "exit") {
                break
            }

            val match = pattern.matchEntire(input)
            if (match == null) {
                println("Parse error\n")
                continue
            }

            val number = match.groupValues[1].toDouble()
            val source = match.groupValues[2]
            val destination = match.groupValues[3]

            if (source in distanceUnits && destination in distanceUnits) {
                if (number < 0) {
                    println("Length shouldn't be negative.\n")
                    continue
                }
                convertDistance(number, source, destination)
            } else if (source in weightUnits && destination in weightUnits) {
                if (number < 0) {
                    println("Weight shouldn't be negative.\n")
                    continue
                }
                convertWeight(number, source, destination)
            } else if (source in temperatureUnits && destination in temperatureUnits) {
                convertTemperature(number, source, destination)
            } else {
                println("Conversion from ${getUnitFromName(source)} to ${getUnitFromName(destination)} is impossible\n")
            }

            println()
        }
    }

    private fun convertDistance(number: Double, source: String, destination: String) {
        val sourceUnit = DistanceUnit.values().first { it.acceptableNames().contains(source) }
        val hubNumber = sourceUnit.convertToMeter(number)

        val destinationUnit = DistanceUnit.values().first { it.acceptableNames().contains(destination) }
        val convertedNumber = destinationUnit.convertFromMeter(hubNumber)

        println("$number ${if (number != 1.0) sourceUnit.plural() else sourceUnit.mainName()} is $convertedNumber ${if (convertedNumber != 1.0) destinationUnit.plural() else destinationUnit.mainName()}")
    }

    private fun convertWeight(number: Double, source: String, destination: String) {
        val sourceUnit = WeightUnit.values().first { it.acceptableNames().contains(source) }
        val hubNumber = sourceUnit.convertToGram(number)

        val destinationUnit = WeightUnit.values().first { it.acceptableNames().contains(destination) }
        val convertedNumber = destinationUnit.convertFromGram(hubNumber)

        println("$number ${if (number != 1.0) sourceUnit.plural() else sourceUnit.mainName()} is $convertedNumber ${if (convertedNumber != 1.0) destinationUnit.plural() else destinationUnit.mainName()}")
    }

    private fun convertTemperature(number: Double, source: String, destination: String) {
        val sourceUnit = TemperatureUnit.values().first { value -> value.acceptableNames().map { it.lowercase() }.contains(source) }
        val destinationUnit = TemperatureUnit.values().first { value -> value.acceptableNames().map { it.lowercase() }.contains(destination) }

        val convertedNumber = sourceUnit.convertTo(destinationUnit, number)

        println("$number ${sourceUnit.pluralOrSingle(number)} is $convertedNumber ${destinationUnit.pluralOrSingle(convertedNumber)}")
    }
}


enum class DistanceUnit(private val acceptableNames: MutableList<String>, private val pluralName: String, private val conversionFactor: Double) {
    Meters(mutableListOf("m", "meter", "meters"), "meters", 1.0),
    Kilometers(mutableListOf("km", "kilometer", "kilometers"), "kilometers", 1000.0),
    Centimeters(mutableListOf("cm", "centimeter", "centimeters"), "centimeters", 0.01),
    Millimeters(mutableListOf("mm", "millimeter", "millimeters"), "millimeters", 0.001),
    Miles(mutableListOf("mi", "mile", "miles"), "miles", 1609.35),
    Yards(mutableListOf("yd", "yard", "yards"), "yards", 0.9144),
    Feet(mutableListOf("ft", "foot", "feet"), "feet", 0.3048),
    Inches(mutableListOf("in", "inch", "inches"), "inches", 0.0254);

    fun convertToMeter(number: Double): Double {
        return number * conversionFactor
    }

    fun convertFromMeter(number: Double): Double {
        return number / conversionFactor
    }

    fun acceptableNames(): MutableList<String> {
        return acceptableNames
    }

    fun plural(): String {
        return pluralName
    }

    fun mainName(): String {
        return acceptableNames[1]
    }
}


enum class WeightUnit(private val acceptableNames: MutableList<String>, private val pluralName: String, private val conversionFactor: Double) {
    Grams(mutableListOf("g", "gram", "grams"), "grams", 1.0),
    Kilograms(mutableListOf("kg", "kilogram", "kilograms"), "kilograms", 1000.0),
    Milligrams(mutableListOf("mg", "milligram", "milligrams"), "milligrams", 0.001),
    Pounds(mutableListOf("lb", "pound", "pounds"), "pounds", 453.592),
    Ounces(mutableListOf("oz", "ounce", "ounces"), "ounces", 28.3495);

    fun convertToGram(number: Double): Double {
        return number * conversionFactor
    }

    fun convertFromGram(number: Double): Double {
        return number / conversionFactor
    }

    fun acceptableNames(): MutableList<String> {
        return acceptableNames
    }

    fun plural(): String {
        return pluralName
    }

    fun mainName(): String {
        return acceptableNames[1]
    }
}

enum class TemperatureUnit(private val acceptableNames: MutableList<String>, private val pluralName: String) {
    Celsius(mutableListOf("degree Celsius", "degrees Celsius", "celsius", "dc", "c"), "degrees Celsius"),
    Fahrenheit(mutableListOf("degree Fahrenheit", "degrees Fahrenheit", "fahrenheit", "df", "f"), "degrees Fahrenheit"),
    Kelvin(mutableListOf("kelvin", "kelvins", "k"), "kelvins");

    fun acceptableNames(): MutableList<String> {
        return acceptableNames
    }

    fun pluralOrSingle(number: Double): String {
        return if (number != 1.0) plural() else singular()
    }

    private fun singular(): String {
        return acceptableNames[0]
    }

    fun plural(): String {
        return pluralName
    }

    fun convertTo(other: TemperatureUnit, number: Double): Double {
        when (this) {
            Celsius -> return when (other) {
                Kelvin -> celsiusToKelvins(number)
                Fahrenheit -> celsiusToFahrenheit(number)
                else -> number
            }
            Fahrenheit -> return when (other) {
                Kelvin -> fahrenheitToKelvins(number)
                Celsius -> fahrenheitToCelsius(number)
                else -> number
            }
            Kelvin -> return when (other) {
                Celsius -> kelvinsToCelsius(number)
                Fahrenheit -> kelvinsToFahrenheit(number)
                else -> number
            }
            else -> throw IllegalArgumentException("Conversion from ${this.plural()} to ${other.plural()} is impossible")
        }
    }

    private fun celsiusToFahrenheit(number: Double): Double {
        return  (number * 9 / 5) + 32
    }

    private fun fahrenheitToCelsius(number: Double): Double {
        return (number - 32) * 5 / 9
    }

    private fun kelvinsToCelsius(number: Double): Double {
        return number - 273.15
    }

    private fun celsiusToKelvins(number: Double): Double {
        return number + 273.15
    }

    private fun fahrenheitToKelvins(number: Double): Double {
        return (number + 459.67) * 5 / 9
    }

    private fun kelvinsToFahrenheit(number: Double): Double {
        return number * 9 / 5 - 459.67
    }
}