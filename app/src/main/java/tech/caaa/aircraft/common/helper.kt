package tech.caaa.aircraft.common

fun <A, B, C, D, E, F, R> ((A, B, C, D, E, F) -> R).curry(a: A): (B, C, D, E, F) -> R =
    { b: B, c: C, d: D, e:E, f:F -> this(a, b, c, d,e, f) }
fun <A, B, C, D, E, R> ((A, B, C, D, E) -> R).curry(a: A): (B, C, D, E) -> R =
    { b: B, c: C, d: D, e:E -> this(a, b, c, d,e) }
fun <A, B, C, D, R> ((A, B, C, D) -> R).curry(a: A): (B, C, D) -> R =
    { b: B, c: C, d: D -> this(a, b, c, d) }

fun <A, B, C, R> ((A, B, C) -> R).curry(a: A): (B, C) -> R = { b: B, c: C -> this(a, b, c) }
fun <A, B, R> ((A, B) -> R).curry(a: A): (B) -> R = { b: B -> this(a, b) }
fun <A, R> ((A) -> R).curry(t: A): () -> R = { this(t) }