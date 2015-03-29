module Chronicle

open System

module Time =
    type t = DateTime

    let unixEpoch:t = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc)

    let now () = System.DateTime.UtcNow
    let fromUnixTime (seconds: int64) =
        unixEpoch + TimeSpan.FromSeconds(seconds |> float)

type 'a TimeSeriesPoint = { time: Time.t; value: 'a }

type User = { name: string; trackCount: int }
type Artist = { name: string; mbid: Guid option }
type Track = { name: string; artist: Artist; mbid: Guid option }
type Listen = Track TimeSeriesPoint
