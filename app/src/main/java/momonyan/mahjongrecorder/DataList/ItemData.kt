package momonyan.mahjongrecorder.DataList

class ItemData(val dName: ArrayList<String>, val dPoint: ArrayList<Int>, val dResult: ArrayList<Int>) {
    private var playerNames: ArrayList<String> = arrayListOf("", "", "", "")
    private var points: ArrayList<Int> = arrayListOf(0, 0, 0, 0)
    private var resultPoints: ArrayList<Int> = arrayListOf(0, 0, 0, 0)

    //TODO 要検討
    private fun dataSet() {
        playerNames = dName
        points = dPoint
        resultPoints = dResult
    }


    //Getter
    //Player
    fun getPlayerName(): ArrayList<String> {
        return playerNames
    }

    fun getPlayerName(num :Int):String{
        return playerNames[num]
    }

    //Point
    fun getpoint(): ArrayList<Int> {
        return points
    }

    fun getpoint(num :Int):Int{
        return points[num]
    }

    //Result
    fun getresultPoint(): ArrayList<Int> {
        return resultPoints
    }

    fun getresultPoint(num :Int):Int{
        return resultPoints[num]
    }

}