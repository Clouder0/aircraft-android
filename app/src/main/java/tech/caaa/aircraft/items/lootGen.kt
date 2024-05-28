package tech.caaa.aircraft.items

import kotlin.random.Random

typealias positionedLootGen = (x:Double,y:Double) -> List<BaseItem>


fun chanceGenWrapper(chance:Double,inner:positionedLootGen):positionedLootGen {
    return fun(x:Double,y:Double):List<BaseItem>{
        if(Random.nextDouble() < chance) {return inner(x,y)}
        return emptyList()
    }
}

fun radiusLootGen(radius:Double,inner:positionedLootGen):positionedLootGen {
    return fun(x:Double,y:Double):List<BaseItem>{
        return inner(x + 2 * radius * (Random.nextDouble() - 0.5), y + 2 * radius * (Random.nextDouble() - 0.5))
    }
}

fun combineGens(vararg args:positionedLootGen):positionedLootGen {
    return fun(x:Double,y:Double):List<BaseItem> {
        val res = ArrayList<BaseItem>()
        for(arg in args) res.addAll(arg(x,y))
        return res
    }
}

fun singleWrapper(inner:(x:Double,y:Double)->BaseItem):positionedLootGen = {x:Double,y:Double->listOf(inner(x,y))}