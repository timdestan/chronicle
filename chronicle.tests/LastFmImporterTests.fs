module LastFmImporterTests

open LastFmImporter

open NUnit.Framework
open FsUnit
open Microsoft.FSharp.Control

[<Test>]
let ``Can get a user`` () =
    let user = LastFmImporter.getUser "tj6186" |> Async.RunSynchronously
    user.Name |> should equal "tj6186"
    user.TrackCount |> should be (greaterThan 26000)

[<Test>]
let ``Can get top tracks for a user`` () =
    let tracks = LastFmImporter.getTopTracksForUser "tj6186" |> Async.RunSynchronously |> Seq.toArray
    tracks.Length |> should be (greaterThan 10)

[<Test>]
[<Timeout(600000)>]
let ``Can get all tracks for a user eventually`` () =
    let tracks = LastFmImporter.getAllTracksForUser "tj6186" |> Async.RunSynchronously |> Seq.toArray
    tracks.Length |> should be (greaterThanOrEqualTo 26000)