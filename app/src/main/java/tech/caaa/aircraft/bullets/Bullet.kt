package tech.caaa.aircraft.bullets

import tech.caaa.aircraft.aircrafts.BaseAircraft


interface Shootable {
    public fun shoot() : List<Bullet>
}
interface Bullet {
    public fun hit(other: BaseAircraft)
}
