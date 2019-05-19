package momonyan.mahjongrecorder.datalist

class ItemDataClass(
    val id: Int,
    val dName: MutableList<String>,
    val dPoint: MutableList<Int>,
    val dResult: MutableList<Double>,
    val dDate: String
) {
    //Getter
    //Player
    fun getPlayerName(): MutableList<String> {
        return dName
    }

    fun getPlayerName(num :Int):String{
        return dName[num]
    }

    //Point
    fun getpoint(): MutableList<Int> {
        return dPoint
    }

    fun getpoint(num :Int):Int{
        return dPoint[num]
    }

    //Result
    fun getresultPoint(): MutableList<Double> {
        return dResult
    }

    fun getresultPoint(num: Int): Double {
        return dResult[num]
    }

}