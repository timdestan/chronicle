module Time

open System

type t = DateTime
// A value with a time stamp
type 'a Stamped = { time: t; value: 'a }

let unixEpoch = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc)

let now () = System.DateTime.UtcNow
let fromUnixTime (seconds: int64) =
    unixEpoch + TimeSpan.FromSeconds(seconds |> float)
